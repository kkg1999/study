# C++ Concurrency Notes (Interview Revision)

Covering: thread lifecycle, `std::atomic`, mutexes, RAII locks, condition variables,
a full producer–consumer program, C++ version differences, and `using namespace std`.

Each part ends with a **60-second answer** — the compressed version to say out loud in an interview.

---

## Table of contents

1. [Thread lifecycle](#part-1--thread-lifecycle)
2. [std::atomic](#part-2--stdatomic)
3. [Mutex](#part-3--mutex)
4. [Locks (the RAII layer)](#part-4--locks-the-raii-layer)
5. [Condition variables](#part-5--condition-variables)
6. [Full producer–consumer program](#part-6--full-producerconsumer-program)
7. [C++ version differences + parens vs braces](#part-7--c-version-differences--parens-vs-braces)
8. [Why `std::` and not `using namespace std`](#part-8--why-std-and-not-using-namespace-std)
9. [Appendix: cheat sheets](#appendix--cheat-sheets)

---
---

# Part 1 — Thread lifecycle

## 1.1 What a thread actually *is*

A **process** is a memory box: code, globals, heap, open file descriptors, all mapped into one
virtual address space.

A **thread** is just *an execution cursor moving through that box*. Concretely, a thread owns only:

- **Registers** (including instruction pointer `RIP` and stack pointer `RSP`)
- **Its own stack** (locals, return addresses)
- **Thread-local storage** (`thread_local` variables)
- A small kernel bookkeeping struct (TCB — `task_struct` on Linux, `ETHREAD`/`KTHREAD` on Windows)

Everything else — heap, globals, file descriptors, mmap'd regions — is **shared**.
That sharing is the entire source of both the power and the pain (data races).

> A process with 4 threads = one address space, four independent (registers + stack) pairs.

## 1.2 The lifecycle states

```
                    ┌─────────┐
                    │   NEW   │  (kernel obj + stack allocated)
                    └────┬────┘
                         │ added to run queue
                         ▼
      preempted     ┌─────────┐    dispatched    ┌──────────┐
   ┌───────────────►│  READY  │─────────────────►│ RUNNING  │
   │                └─────────┘                  └────┬─────┘
   │                     ▲                            │
   │                     │ event occurs               │ waits on
   │                     │ (wake-up)                  │ mutex/IO/sleep
   │                ┌────┴──────┐                     │
   └────────────────┤  BLOCKED  │◄────────────────────┘
                    └───────────┘                     │
                                                      │ returns / exits
                                                      ▼
                                              ┌───────────────┐
                                              │  TERMINATED   │ (zombie:
                                              └───────┬───────┘  exit status
                                                      │          still held)
                                             join() / detach reap
                                                      ▼
                                                 ┌─────────┐
                                                 │ DESTROY │
                                                 └─────────┘
```

**Most important mental model:** *a thread never wakes itself up.* Something external — a hardware
interrupt, or another thread — moves it from BLOCKED back to READY.

## 1.3 What the OS does at each transition

| Transition | Who triggers it | What the kernel does |
|---|---|---|
| **NEW → READY** | `clone()` / `NtCreateThreadEx` | Allocate `task_struct`, `mmap` a stack (Linux 8 MB *reserved*, lazily faulted; Windows 1 MB), set up TLS, push onto a CPU's run queue |
| **READY → RUNNING** | Scheduler dispatch | **Context switch**: save outgoing registers into its TCB, load incoming registers. No page-table swap needed between threads of the same process — that's why thread switches are cheaper than process switches |
| **RUNNING → READY** | Timer interrupt (quantum expired), `yield()`, higher-priority thread became runnable | Preemption. Back on the run queue, still healthy |
| **RUNNING → BLOCKED** | The thread itself, via a syscall | `read()` on an empty socket, `futex(FUTEX_WAIT)` on a contended mutex, `nanosleep`, `join`. Pulled off the run queue, parked on a **wait queue**. Consumes **zero CPU** |
| **BLOCKED → READY** | Someone else | Another thread unlocks a mutex → `FUTEX_WAKE`; disk controller fires an IRQ; timer expires |
| **RUNNING → TERMINATED** | The thread returns or exits | Stack can be unmapped, but TCB + exit status linger if someone might `join()` |

Two subtleties:

- **Blocked ≠ spinning.** A blocked thread is genuinely off the CPU. A *spinning* thread (busy-wait
  loop) is in RUNNING state, burning a core. Real mutexes usually spin briefly *then* block.
- **Context switches aren't free.** ~1–5 µs direct cost, plus cache/TLB pollution that can cost far
  more. This is why thread-per-request collapses at scale and why thread pools exist.

## 1.4 Where C++ plugs in

`std::thread` is a **thin RAII handle over the OS thread**. It is not the thread.

```cpp
std::thread t(work, 42);
```

1. **Constructor** decay-copies `work` and `42` into a heap-allocated state object.
2. Calls the platform primitive via a C-ABI trampoline: `pthread_create` (libstdc++) or
   `_beginthreadex` (MSVC).
3. `pthread_create` mmaps a stack, initializes TLS, then `clone(CLONE_VM|CLONE_FILES|CLONE_THREAD|…)`.
4. Kernel creates the task → **READY**. `pthread_create` returns immediately; parent and child race.
5. Child enters the trampoline → constructs `thread_local`s on first use → calls your callable.
6. Callable returns → `thread_local` destructors run → state object freed → **TERMINATED**.
7. `t.join()` blocks the *caller* until step 6, then reaps the TCB.

### The rule that trips everyone up

```cpp
{
    std::thread t(work);
}   // ← destructor of a JOINABLE thread calls std::terminate(). Program dies.
```

The destructor deliberately does **not** join. Committee reasoning: silently joining hides a
blocking call in a destructor; silently detaching leaves dangling references to dead stack frames.
Both are subtle disasters, so crashing loudly is the least-bad option.

Before the destructor runs you must pick exactly one:

```cpp
t.join();     // block me until t finishes, then reap it.  joinable() → false
t.detach();   // sever the handle; t runs free, runtime reaps it.  joinable() → false
```

`detach()` is a loaded gun:

```cpp
void bad() {
    int local = 5;
    std::thread t([&]{ std::this_thread::sleep_for(1s); std::cout << local; });
    t.detach();
}   // bad() returns, `local` is gone, thread wakes and reads garbage
```

**C++20 fixes the ergonomics** with `std::jthread` — joins in its destructor *and* carries a
`std::stop_token`:

```cpp
std::jthread t([](std::stop_token st){
    while (!st.stop_requested()) { /* work */ }
});
// destructor: request_stop() then join(). Safe by default.
```

## 1.5 Memory-model guarantees you get for free

- Everything the parent did **before** constructing the thread *happens-before* the new thread's
  first instruction.
- Everything the thread did **before** finishing *happens-before* `join()` returns.

```cpp
int result = 0;
std::thread t([&]{ result = compute(); });   // no data race on `result`
t.join();                                     // ...because join() is a sync point
std::cout << result;                          // guaranteed to see the write
```

Read `result` *without* joining and you have UB — not "maybe stale," genuine undefined behavior.

## 1.6 Death, and how it goes wrong

- **You cannot kill a thread in C++.** No `t.kill()`, by design — abrupt termination leaves mutexes
  locked and destructors unrun. Cancellation must be **cooperative**: an `atomic<bool>` flag or a
  `stop_token` checked periodically. (`pthread_cancel` exists but is effectively unusable in C++ —
  it doesn't reliably unwind destructors.)
- **If `main` returns while threads run**, `exit()` tears down the process. Detached threads are
  killed mid-instruction: no unwinding, no destructors, no flushing.
- **An uncaught exception escaping a thread function** calls `std::terminate()` for the whole
  process — it does *not* propagate to `join()`. Use `std::async`/`std::packaged_task` to capture it
  into a `future` and rethrow at `.get()`.

## ⏱ 60-second answer — thread lifecycle

> A thread is registers + a stack sharing everyone else's memory. The OS shuttles it between READY
> (wants CPU), RUNNING (has CPU), and BLOCKED (waiting on something, costing nothing) until it
> returns, at which point it sits as a zombie until someone reaps it. `std::thread` is a handle to
> that OS thread whose destructor refuses to guess your intent — so you must say `join()` or
> `detach()`. `std::jthread` says `join()` for you.

---
---

# Part 2 — std::atomic

**Header:** `<atomic>` · **Introduced:** C++11

## 2.1 The two problems atomics solve

Conflating these is the #1 interview mistake.

**Problem A — Atomicity (tearing / lost updates).**
`counter++` compiles to *load → add → store*. Two threads interleave, both read 5, both write 6.
One increment vanishes.

**Problem B — Visibility & Ordering.**
Even with indivisible writes, the compiler and CPU **reorder** memory operations. Thread A writes
`data` then `ready = true`; Thread B may see `ready == true` while still reading the *old* `data`.
Nothing was torn — the operations just became visible out of order.

`std::atomic` solves both. `memory_order` is the dial controlling **how much of B** you pay for.

Legal framing: **any two conflicting accesses to the same memory location, at least one a write,
unordered, from different threads = a data race = undefined behavior.**

## 2.2 What `std::atomic<T>` is

A wrapper making reads/writes of `T` **indivisible and race-free**.

```cpp
std::atomic<int> counter{0};
counter++;                      // atomic read-modify-write
int x = counter.load();
counter.store(42);
```

`T` must be trivially copyable, copy-constructible, copy-assignable. So `int`, `bool`, pointers,
small PODs — yes. `std::string`, `std::vector` — no.

**Lock-free vs lock-based:**

```cpp
std::atomic<int>::is_always_lock_free;      // constexpr, C++17 — compile-time
someAtomic.is_lock_free();                  // runtime
```

If `T` is too big for a native atomic instruction, the library **silently falls back to a hidden
mutex pool**. `std::atomic<BigStruct>` still compiles and is still correct — it's just not lock-free,
and you get zero of the performance you wanted. Always check.

> `std::atomic_flag` is the *only* type guaranteed lock-free on every platform.

## 2.3 The operation set

| Operation | Meaning |
|---|---|
| `load()` | atomic read |
| `store(v)` | atomic write |
| `exchange(v)` | write `v`, **return old value** (atomic swap) |
| `fetch_add` / `fetch_sub` | integers & pointers; **returns the OLD value** |
| `fetch_and` / `or` / `xor` | bitwise |
| `compare_exchange_weak` / `strong` | **CAS** — the universal primitive |

**Trap 1 — no atomic multiply, divide, or max.** Only add/sub and bitwise. Anything else needs a CAS loop.

**Trap 2 — this is *not* atomic:**

```cpp
std::atomic<int> a;
a = a + 1;      // ✗ atomic LOAD, then separate atomic STORE. Race in between.
a++;            // ✓ single atomic read-modify-write
a.fetch_add(1); // ✓ same thing
```

## 2.4 CAS — the universal primitive

```cpp
bool compare_exchange_strong(T& expected, T desired);
```

> "If the current value equals `expected`, replace it with `desired` and return true. Otherwise,
> **write the actual current value back into `expected`** and return false."

That write-back is what makes the retry loop clean:

```cpp
// atomic max — the general shape of any custom atomic operation
void atomic_max(std::atomic<int>& a, int val) {
    int cur = a.load(std::memory_order_relaxed);
    while (cur < val &&
           !a.compare_exchange_weak(cur, val))   // on failure, cur is refreshed for free
    { }
}
```

**weak vs strong:**

- `weak` may fail **spuriously** — return false even when the value *did* match. On LL/SC
  architectures (ARM, POWER) a context switch or cache event breaks the reservation.
- `strong` never fails spuriously, but must internally retry on those platforms → slower.

**Rule:** already in a loop → **weak**. Single one-shot check → **strong**.

**The ABA problem** (classic follow-up): a lock-free stack pops A; another thread pops A, pops B,
pushes A back. Your CAS sees A, thinks nothing changed, succeeds — but `next` now points at freed
memory. CAS compares *values*, not *history*. Fixes: tagged pointers (version counter packed
alongside), hazard pointers, or epoch-based reclamation.

## 2.5 Memory ordering — the heart of the interview

Every atomic op takes an optional `std::memory_order`. **Default is `seq_cst`** — safest, slowest.

### `memory_order_relaxed`

Atomicity **only**. No ordering relative to any other variable. Still guarantees per-variable
**coherence**: all threads agree on the modification order of *that one* variable, and values never
go backwards.

```cpp
hitCount.fetch_add(1, std::memory_order_relaxed);   // nobody's decisions depend on it
```

### `release` / `acquire` — the workhorse pair

Be able to draw this on a whiteboard.

```cpp
std::atomic<bool> ready{false};
int data = 0;                    // plain, non-atomic!

// Producer
data = 42;                                          // (1)
ready.store(true, std::memory_order_release);       // (2) ─┐ nothing above
                                                    //      │ can sink below
// Consumer                                                 │
if (ready.load(std::memory_order_acquire)) {        // (3) ◄┘ nothing below
    assert(data == 42);                             // (4)   can hoist above
}
```

- **release store** = "everything I wrote *before* this becomes visible to anyone who acquire-reads
  this value." Prior operations can't move *down* past it.
- **acquire load** = "everything the releasing thread did before its store is now visible to me."
  Subsequent operations can't move *up* past it.

When (3) **reads the value written by** (2), (2) **synchronizes-with** (3). That establishes
**happens-before**: (1) happens-before (4). The assert cannot fire, and there is no data race on
`data` even though it's a plain `int`.

Critical: synchronization exists **only if the acquire actually reads the value the release wrote**.
Half a pair does nothing.

This is exactly how a mutex works: `unlock()` is a release, `lock()` is an acquire.

- **`acq_rel`** — for RMW ops that both consume and publish (e.g. `fetch_sub` in a refcount).
- **`consume`** — a weaker acquire based on data dependency. **Effectively deprecated**; every
  compiler promotes it to acquire. Say that and move on.

### `memory_order_seq_cst`

Everything acquire/release gives, **plus** a single **global total order** all seq_cst operations
across all threads agree on.

Why that matters — the store-buffer / Dekker example:

```cpp
std::atomic<int> x{0}, y{0};
int r1, r2;

Thread 1:  x.store(1);  r1 = y.load();
Thread 2:  y.store(1);  r2 = x.load();
```

- With `seq_cst`: `r1 == 0 && r2 == 0` is **impossible** — one store must come first in the total order.
- With `acquire/release` (or relaxed): **it can happen.** Each CPU's store sits in its own store
  buffer, invisible to the other, and both loads see 0.

**Cost:** on x86-64, loads are already acquire and stores already release *in hardware* — so
relaxed/acquire/release atomics are **free** (same `mov`). Only a **seq_cst store** costs, needing
`xchg` or `mfence` to drain the store buffer. On ARM/POWER (weakly ordered) the differences are real
and measurable everywhere.

### Summary table

| Ordering | Atomic? | Orders other memory? | Global total order? | Typical use |
|---|---|---|---|---|
| `relaxed` | ✓ | ✗ | ✗ | counters, flags nobody gates on |
| `acquire` | ✓ | ✓ (loads side) | ✗ | reading a published flag |
| `release` | ✓ | ✓ (stores side) | ✗ | publishing data |
| `acq_rel` | ✓ | ✓ both | ✗ | RMW in the middle of a chain |
| `seq_cst` | ✓ | ✓ both | ✓ | default; when unsure |

**Practical advice:** default to `seq_cst`. Reach for acquire/release only when you've profiled and
can articulate the exact happens-before edge. Relaxed only for genuinely independent counters.
Hand-tuned orderings are where correct-looking code silently breaks on ARM.

## 2.6 Atomics vs mutex

| | `std::atomic` | `std::mutex` |
|---|---|---|
| Scope | one variable | arbitrary critical section |
| Blocking | usually lock-free | blocks, may context-switch (~1–5 µs) |
| Multi-variable invariants | ✗ | ✓ |
| Contention behavior | cache-line ping-pong, degrades | queues in kernel |
| Deadlock possible | no | yes |

**Atomics are not automatically faster.** Under heavy contention an atomic RMW forces the cache line
into Exclusive state one core at a time — 16 threads hammering one `atomic<int>` can be *slower*
than a mutex. A "lock-free" algorithm is often slower than a well-written locked one; lock-freedom
buys **progress guarantees** (no thread blocks others, immune to priority inversion), not speed.

> **Lock-free** = at least one thread always makes progress.
> **Wait-free** = *every* thread completes in bounded steps.
> **Obstruction-free** = progress if run in isolation.

## 2.7 Gotchas

- **`volatile` is not for threading.** It means "don't optimize away this access" — for MMIO and
  signal handlers. **No atomicity, no ordering.** (Java/C# `volatile` ≠ C++ `volatile`.)
- **False sharing.** Two unrelated atomics on the same 64-byte cache line bounce between cores. Fix:
  `alignas(std::hardware_destructive_interference_size)` (C++17).
- **Default construction pre-C++20 leaves the value uninitialized.** Always `std::atomic<int> a{0};`.
- **`atomic<T*>` makes the *pointer* atomic, not the pointee.**
- **Atomics don't fix logic races.** They eliminate data races (UB); the *when* is still up to you.

**C++20 additions worth name-dropping:**

- `atomic<T>::wait() / notify_one() / notify_all()` — futex-backed blocking without a condvar.
- `std::atomic_ref<T>` — atomic ops on an object you don't own (e.g. one array element).
- `std::atomic<std::shared_ptr<T>>` — replaces the old free-function overloads.
- `std::atomic_flag::test()` — read without setting; `ATOMIC_FLAG_INIT` no longer needed.

## 2.8 Canonical code

**Spinlock** (shows you understand acquire/release *and* their cost):

```cpp
class Spinlock {
    std::atomic_flag flag = ATOMIC_FLAG_INIT;
public:
    void lock() {
        while (flag.test_and_set(std::memory_order_acquire))
            std::this_thread::yield();   // or _mm_pause() / __builtin_ia32_pause()
    }
    void unlock() { flag.clear(std::memory_order_release); }
};
```

**Refcount** (shows you know *why* the orderings differ):

```cpp
void release() {
    if (count.fetch_sub(1, std::memory_order_acq_rel) == 1) {
        delete ptr;   // fetch_sub returns the OLD value → 1 means I was the last
    }
}
```

Increment can be `relaxed` (you already hold a reference; nothing is being published). Decrement
needs `acq_rel`: **release** so your writes are visible to whoever destroys the object, **acquire**
so the destroying thread sees everyone else's writes before it frees.

## ⏱ 60-second answer — std::atomic

> It solves two things: indivisibility of read-modify-write, and control over compiler/CPU
> reordering. Operations are load, store, exchange, fetch_add, and compare_exchange — CAS being the
> universal primitive, with `weak` for loops since it can fail spuriously. Every operation takes a
> memory_order. `relaxed` gives atomicity only. `release`/`acquire` pair up to create a
> happens-before edge, which is how you safely publish non-atomic data behind a flag — same
> mechanism as a mutex. `seq_cst` is the default and additionally imposes a single global total
> order across all threads, which is what prevents the store-buffer reordering in Dekker's
> algorithm. On x86 everything but a seq_cst store is essentially free; on ARM the distinctions cost
> real barriers. Whether it's actually lock-free depends on size — check `is_always_lock_free`,
> since large types silently fall back to a mutex.

---
---

# Part 3 — Mutex

**Header:** `<mutex>` · **Introduced:** C++11

## 3.1 The problem

Atomics protect **one variable**. Most invariants span several:

```cpp
// Bank transfer — needs BOTH updates to appear as one indivisible event
from.balance -= 100;
to.balance   += 100;
```

Make both `atomic<int>` and you've fixed nothing. A reader between the two lines sees $100 vanish.
Atomicity of the *parts* doesn't give atomicity of the *whole*.

```
Thread A: ──[lock]──── critical section ────[unlock]──►
Thread B: ────[lock....blocked....]──── critical section ──[unlock]──►
                      ▲
                      thread is in BLOCKED state — off the CPU, costing nothing
```

## 3.2 What a mutex actually is

**MUT**ual **EX**clusion. A token exactly one thread can hold. Two properties:

1. **Mutual exclusion** — one owner at a time.
2. **Memory ordering** — `unlock()` is a **release**, `lock()` is an **acquire**. Everything written
   inside the critical section is visible to the next thread that locks. Without this a mutex would
   be useless: you'd get exclusive access to *stale* data.

> A mutex is release/acquire synchronization with an ownership protocol on top.

**Key mental correction:** a mutex protects **data**, not code. `std::mutex` doesn't know what it
guards — the association lives in your head, or better, in your class design:

```cpp
class Counter {
    mutable std::mutex m_;   // mutable so const methods can lock
    int value_ = 0;          // guarded by m_ — keep them adjacent
public:
    void increment() { std::lock_guard<std::mutex> lg(m_); ++value_; }
    int  get() const { std::lock_guard<std::mutex> lg(m_); return value_; }
};
```

## 3.3 The raw API (and why you never use it)

```cpp
std::mutex m;
m.lock();      // block until acquired
m.unlock();    // release
m.try_lock();  // acquire or return false immediately — never blocks
```

Never call these directly:

```cpp
m.lock();
doWork();       // ← throws
m.unlock();     // ← never runs. Mutex locked forever. Every other thread deadlocks.
```

Also breaks on early `return`, `break`, `continue`.
**RAII is not a style preference here — it's a correctness requirement.**

Non-obvious rules:

- `std::mutex` is **neither copyable nor movable**. A class containing one is also non-copyable
  unless you write it yourself.
- Locking a `std::mutex` you already hold is **UB** (usually self-deadlock), not an error.
- Unlocking a mutex you don't own is **UB**.
- Destroying a locked mutex is **UB**.

## 3.4 How it's actually implemented (the good answer)

A modern mutex is **hybrid**: a userspace atomic fast path plus a kernel slow path.

```
lock():
  1. CAS the state 0 → 1.  Succeeded? DONE.        ← uncontended: ~20ns, never
                                                     enters the kernel
  2. Contended. Spin briefly (~100s of cycles,
     PAUSE instruction) hoping the owner exits.    ← optimizes short critical sections

  3. Still locked? syscall futex(FUTEX_WAIT).      ← kernel parks the thread on a
     Thread → BLOCKED. Off the run queue.            wait queue: RUNNING → BLOCKED

unlock():
  1. Atomic store state = 0  (release).
  2. If anyone is waiting, futex(FUTEX_WAKE).      ← moves a waiter BLOCKED → READY
```

**futex = "fast userspace mutex."** The kernel is only involved *on contention*. Uncontended
`lock()`/`unlock()` is two atomic operations and zero syscalls — a few nanoseconds. Contended is a
syscall plus a context switch — microseconds, 100–1000× more. (Windows equivalents: `SRWLOCK` /
`WaitOnAddress`.)

**Design consequence:** the fix for a slow mutex is almost never "use atomics" — it's **reduce
contention** (shard the data, shrink the critical section, thread-local accumulation).

## 3.5 The mutex family

| Type | Purpose | Since |
|---|---|---|
| `std::mutex` | Standard. Non-recursive. | C++11 |
| `std::recursive_mutex` | Same thread may lock N times, must unlock N times | C++11 |
| `std::timed_mutex` | `try_lock_for(100ms)`, `try_lock_until(tp)` | C++11 |
| `std::recursive_timed_mutex` | Both | C++11 |
| `std::shared_timed_mutex` | Shared + timed | C++14 |
| `std::shared_mutex` | Reader/writer: many readers **or** one writer | C++17 |

**On `recursive_mutex`** — expected answer: *"It exists, but needing it is usually a design smell."*
It typically means a public locking method calls another public locking method. Clean fix — split
into a locking public wrapper and a non-locking private `_locked()` implementation:

```cpp
void doWork()  { std::lock_guard<std::mutex> lg(m_); doWork_locked(); }   // public: locks
void doOther() { std::lock_guard<std::mutex> lg(m_); doWork_locked(); }   // public: locks
private:
void doWork_locked() { /* assumes m_ held */ }                            // private: never locks
```

**On `shared_mutex`** — read-heavy workloads only. It's more expensive than `std::mutex`
per-operation and can starve writers. If the critical section is short, a plain `mutex` often wins
even when reads dominate. Measure.

## 3.6 Deadlock

**The four Coffman conditions** (all must hold simultaneously):

1. Mutual exclusion
2. Hold and wait
3. No preemption
4. Circular wait

Break any one and deadlock is impossible. In practice you break **#4**.

```cpp
// Thread 1: lock(A) → lock(B)
// Thread 2: lock(B) → lock(A)      ✗ classic deadlock
```

**Solutions, in order of preference:**

1. **Consistent lock ordering** — always acquire in the same global order (e.g. by address). Free,
   but relies on discipline.
2. **`std::scoped_lock` / `std::lock`** — acquires all mutexes with a deadlock-avoidance algorithm
   (try-and-back-off), so the order you write them in doesn't matter:

   ```cpp
   void transfer(Account& a, Account& b, int amt) {
       std::scoped_lock lk(a.m, b.m);      // C++17 — deadlock-free, any order
       a.balance -= amt;
       b.balance += amt;
   }
   ```

   Pre-C++17 equivalent:

   ```cpp
   std::lock(a.m, b.m);
   std::lock_guard<std::mutex> la(a.m, std::adopt_lock);
   std::lock_guard<std::mutex> lb(b.m, std::adopt_lock);
   ```

   Bonus: with `scoped_lock`, `transfer(x,y)` and `transfer(y,x)` concurrently is safe. Edge case —
   `scoped_lock(a.m, a.m)` on the same account is UB; guard with `if (&a == &b) return;`.
3. **Avoid nested locks entirely** — the best fix.
4. **Never call unknown/user code while holding a lock** — a callback might lock something you don't
   know about, or re-enter you.
5. **Use timeouts** (`try_lock_for`) — a detection mechanism, not a real solution.

Also name **livelock** (threads keep responding to each other, never progress — the naive
try-lock-back-off loop) and **starvation** (`std::mutex` makes **no fairness guarantee**).

## 3.7 Gotchas

- **Lock granularity.** Coarse = simple but serializes everything. Fine = concurrent but more
  deadlock risk and more overhead. Hold the lock for the **shortest correct interval** — but don't
  lock/unlock repeatedly in a tight loop.
- **Never hold a lock during I/O, network calls, sleeps, or `std::cout`.** Copy out, unlock, then do
  the slow thing.
- **Returning references defeats the lock:**

  ```cpp
  T& get() { std::lock_guard<std::mutex> lg(m_); return data_; }  // ✗ reference escapes
  T  get() { std::lock_guard<std::mutex> lg(m_); return data_; }  // ✓ return a copy
  ```

  This is why **thread-safe interfaces don't compose**: `if (!s.empty()) s.pop();` is a race even if
  both methods are individually locked. Fix: a combined `try_pop()` — one operation, one lock.
- **One-time initialization.** Double-checked locking with a plain `bool` is the textbook broken
  pattern. Use `std::call_once` with a `std::once_flag`, or just a **function-local static** —
  thread-safe initialization is guaranteed since C++11 ("magic statics"):

  ```cpp
  Singleton& instance() { static Singleton s; return s; }   // ✓ thread-safe
  ```
- **`mutable std::mutex`** — so `const` member functions can lock. Bitwise-const vs logically-const.
- **Priority inversion** — low-priority thread holds a lock a high-priority thread needs; a
  medium-priority thread preempts the low one. Fix: priority inheritance (the Mars Pathfinder bug).
- **Lock convoy** — many threads serialize behind one lock and repeatedly context-switch, so
  throughput collapses below the serial rate.

## 3.8 Canonical code

```cpp
// Thread-safe wrapper — pass the operation IN, data never escapes
template <typename T>
class Guarded {
    mutable std::mutex m_;
    T data_;
public:
    template <typename F>
    auto with(F&& f) const {
        std::lock_guard<std::mutex> lg(m_);
        return f(data_);
    }
};

// Scoped unlock for the slow part
void process() {
    std::unique_lock<std::mutex> lk(m_);
    auto batch = std::move(pending_);   // move out cheaply
    pending_.clear();
    lk.unlock();                        // ← release before the expensive work
    for (auto& item : batch) heavyWork(item);
}
```

## ⏱ 60-second answer — mutex

> A mutex enforces mutual exclusion over a critical section, and just as importantly provides
> release/acquire ordering — `unlock` is a release, `lock` is an acquire — so the next thread sees
> everything the previous one wrote. Implementation is hybrid: an atomic CAS fast path in userspace,
> and only on contention a futex syscall that parks the thread in the BLOCKED state. That's why
> uncontended locks cost nanoseconds and contended ones cost microseconds. You always wrap it in
> RAII — `scoped_lock` or `lock_guard` normally, `unique_lock` when you need deferred/manual unlock
> or a condition variable — because a raw `lock()` leaks on exception. The main hazard is deadlock
> from inconsistent lock ordering, which `std::scoped_lock` solves by acquiring multiple mutexes
> with a back-off algorithm. And a mutex protects data, not code — so the mutex and the data it
> guards should live together in one class, with no references escaping.

---
---

# Part 4 — Locks (the RAII layer)

## 4.1 The naming confusion (fix this first)

| | Examples | What it is |
|---|---|---|
| **Mutex** | `std::mutex`, `std::shared_mutex`, `std::recursive_mutex` | The **resource**. Provides the actual exclusion. |
| **Lock** | `std::lock_guard`, `std::unique_lock`, `std::scoped_lock`, `std::shared_lock` | An **RAII handle** managing *ownership* of a mutex. Owns nothing itself. |

The lock classes contain **no locking machinery whatsoever** — thin templates that call `m.lock()`
in the constructor and `m.unlock()` in the destructor.

> **Mutex = the door. Lock = the RAII object that opens it when created and closes it when destroyed.**

This is why there are "so many locks" — they're not different kinds of exclusion, they're different
**ownership policies** over the same underlying mutex.

## 4.2 The concepts (why they're templates)

Every lock class is `template <typename Mutex>` and never says `std::mutex` anywhere. It requires a
**named requirement**:

| Concept | Must provide | Satisfied by |
|---|---|---|
| **BasicLockable** | `lock()`, `unlock()` | everything |
| **Lockable** | + `try_lock()` | everything except a bare custom BasicLockable |
| **TimedLockable** | + `try_lock_for()`, `try_lock_until()` | `timed_mutex`, `shared_timed_mutex` |
| **SharedLockable** | `lock_shared()`, `unlock_shared()`, `try_lock_shared()` | `shared_mutex` |

Payoff: **your own types work.** The `Spinlock` from Part 2 has `lock()`/`unlock()`, so it's
BasicLockable, so `std::lock_guard<Spinlock> lg(sl);` just compiles.

## 4.3 The four wrappers

| | Since | Added capability | Size | Movable | Early unlock | Multiple mutexes | Tags |
|---|---|---|---|---|---|---|---|
| `lock_guard<M>` | C++11 | Lock in ctor, unlock in dtor. Nothing else. | ptr | ✗ | ✗ | ✗ | adopt only |
| `unique_lock<M>` | C++11 | Defer / try / timed / move / manual unlock | ptr + bool | ✓ | ✓ | ✗ | all three |
| `scoped_lock<M...>` | C++17 | Locks **N mutexes deadlock-free** | ptrs | ✗ | ✗ | ✓ | adopt only |
| `shared_lock<M>` | C++14 | Reader lock for `shared_mutex` | ptr + bool | ✓ | ✓ | ✗ | all three |

**Decision tree:**

```
Need reader/writer semantics?           → shared_lock (with shared_mutex)
Need 2+ mutexes at once?                → scoped_lock
Need condition_variable, early unlock,
  deferred lock, or to move/return it?  → unique_lock
Otherwise (the 90% case)                → lock_guard  (or scoped_lock)
```

**`lock_guard` vs `scoped_lock` for a single mutex** — a real question with a real answer.
`scoped_lock` is strictly more capable and is generally the C++17 default. But it has one sharp edge:

```cpp
std::scoped_lock lk;          // ✓ compiles. Zero mutexes. Locks NOTHING. Silent bug.
std::lock_guard  lg;          // ✗ compile error. Good.
```

So: `scoped_lock` when you might have multiple; `lock_guard` when you want the compiler to catch a
missing argument. Either answer is defensible if you articulate that trade-off.

## 4.4 The three tag types

```cpp
std::unique_lock<std::mutex> lk(m, std::defer_lock);    // don't lock yet — lk.lock() later
std::unique_lock<std::mutex> lk(m, std::try_to_lock);   // attempt now, don't block; check owns_lock()
std::unique_lock<std::mutex> lk(m, std::adopt_lock);    // ALREADY locked by me; just take ownership
```

The critical distinction:

- **`defer_lock`** — "the mutex is **unlocked**; don't lock it." Destructor won't unlock.
- **`adopt_lock`** — "the mutex is **already locked**; assume ownership." Destructor **will** unlock.

Getting these backwards is UB. `lock_guard` and `scoped_lock` accept only `adopt_lock` — they have
no state to represent "not locked."

## 4.5 `unique_lock`, the flexible one

```cpp
std::unique_lock<std::mutex> lk(m);

lk.unlock();            // release the mutex, keep ownership tracking
lk.lock();              // reacquire
lk.try_lock();          // → bool
lk.try_lock_for(50ms);  // needs a TimedLockable mutex
lk.owns_lock();         // → bool  (also: explicit operator bool)
lk.mutex();             // → M*    raw pointer, does NOT transfer ownership
lk.release();           // ⚠ see below
```

### `unlock()` vs `release()` — the #1 gotcha

```cpp
lk.unlock();    // Unlocks the mutex. owns_lock() → false. Mutex is now FREE.
lk.release();   // Returns M*. Gives up OWNERSHIP WITHOUT UNLOCKING.
                // Mutex is STILL LOCKED. You must now unlock it manually.
```

`release()` is "disown, don't unlock" — same semantics as `unique_ptr::release()`. Nine times out of
ten you meant `unlock()`.

### Why the extra bool exists

`unique_lock` can be in **either** state at destruction:

```cpp
std::unique_lock<std::mutex> lk(m, std::try_to_lock);
if (!lk.owns_lock()) return;      // someone else has it — bail out
// ... destructor unlocks only if we actually acquired
```

That bool is the entire cost difference vs `lock_guard`: one byte and a branch in the destructor.
Negligible, but that's the honest answer to "why not always use `unique_lock`?"

### Why `condition_variable` demands it

`cv.wait(lk, pred)` must **unlock → block → relock**. Only `unique_lock` exposes `unlock()`/`lock()`
and can represent the temporarily-unlocked state. `lock_guard` physically cannot.
(`condition_variable_any` accepts any BasicLockable, at a performance cost.)

## 4.6 Multiple mutexes: `scoped_lock` and `std::lock`

```cpp
std::lock(m1, m2);              // FREE FUNCTION — locks both, no RAII, no cleanup
std::scoped_lock lk(m1, m2);    // RAII CLASS — locks both AND unlocks on scope exit
```

Both use the same **deadlock-avoidance algorithm**: lock the first, `try_lock` the rest; on any
failure unlock everything and retry starting from the one that failed. Because it never holds one
lock while *blocking* on another, circular wait (Coffman #4) is impossible.

`std::try_lock(a, b, c)` is the non-blocking sibling: returns `-1` on full success, or the **0-based
index** of the first mutex that failed — and unlocks the ones it did get.

**Note:** all-or-nothing, so `scoped_lock` gives no way to unlock one early. If you need that, use
two `unique_lock`s with `defer_lock` plus `std::lock`.

## 4.7 `shared_lock` (reader/writer)

```cpp
std::shared_mutex m;

{ std::shared_lock<std::shared_mutex> lk(m); return cache_.at(key); }   // many readers at once
{ std::unique_lock<std::shared_mutex> lk(m); cache_[key] = value; }     // exclusive writer
```

Note the asymmetry interviewers probe: **`shared_lock` for readers, `unique_lock` (not `lock_guard`)
for writers** — `lock_guard<shared_mutex>` calls `lock()`, which is exclusive; it works, but
`unique_lock` is the conventional pairing and signals intent.

Two things to know:

1. **There is no lock upgrade in the standard.** You cannot atomically promote a `shared_lock` to a
   `unique_lock`. Doing it by hand (unlock shared, lock unique) has a gap where a writer can slip
   in — you must **re-validate your assumptions** after reacquiring. Boost has `upgrade_lock`; std
   does not.
2. **`shared_mutex` isn't free.** More internal state, higher per-operation cost, possible writer
   starvation. It only wins with **many readers and long critical sections**.

## 4.8 Locks are values (the transfer idiom)

Because `unique_lock` and `shared_lock` are **movable**, ownership can leave a function:

```cpp
class Registry {
    std::mutex m_;
    std::map<int, Data> data_;
public:
    // Caller gets the data AND the lock protecting it — can't touch one without the other
    std::pair<std::unique_lock<std::mutex>, Data&> checkout(int id) {
        std::unique_lock<std::mutex> lk(m_);
        return { std::move(lk), data_.at(id) };
    }
};

auto [lk, ref] = registry.checkout(7);
ref.mutate();                  // safe: lk is still alive and holding the mutex
// lk's destructor unlocks here
```

This solves the "returning a reference escapes the lock" problem: the reference and its lock travel
together. `lock_guard` and `scoped_lock` are non-movable, so they can't do this.

## 4.9 Gotchas

- **The unnamed temporary** — the most common real-world bug:

  ```cpp
  std::lock_guard<std::mutex>(m);      // ✗ see Part 7 — parses as a DECLARATION, hard error
  std::lock_guard<std::mutex>{m};      // ⚠ real temporary: locks then instantly unlocks. Warning only.
  std::lock_guard<std::mutex> lg(m);   // ✓
  ```
- **Most vexing parse:**

  ```cpp
  std::lock_guard<std::mutex> lg();   // ✗ declares a FUNCTION named lg. No lock.
  std::lock_guard<std::mutex> lg{m};  // ✓
  ```
- **CTAD is C++17.** `std::lock_guard lg(m);` needs C++17; before that write the template argument.
- **Locks aren't thread-safe objects.** A `unique_lock` belongs to one thread. Share the *mutex*,
  not the *lock object*.
- **Don't move a lock across a thread boundary casually.** Some mutexes require unlock from the
  owning thread (`recursive_mutex` definitely does).
- **Scope locks tightly with a bare block:**

  ```cpp
  void f() {
      { std::lock_guard<std::mutex> lg(m_); update(); }   // ← unlocked here
      slowIO();                                            // not under the lock
  }
  ```
- **Hierarchical mutex** — the classic design answer for enforcing lock order at *runtime*. A custom
  mutex storing a hierarchy level in a `thread_local` that throws if you acquire a higher-level lock
  while holding a lower one. It's BasicLockable, so it drops straight into `lock_guard`. Good answer
  to "how do you prevent deadlock in a large codebase?"

## ⏱ 60-second answer — locks

> Locks and mutexes are different layers. The mutex is the actual exclusion primitive; the lock
> classes are RAII ownership wrappers that hold no state of their own — they just call `lock()` in
> the constructor and `unlock()` in the destructor, which is what makes them exception-safe. They're
> templated on a Lockable concept, so custom types like a spinlock work too. `lock_guard` is the
> minimal zero-overhead one. `unique_lock` adds a bool ownership flag, which buys you deferred
> locking, early unlock, timed acquisition, and movability — that flag is exactly why
> `condition_variable` requires it, since `wait()` has to unlock and relock. `scoped_lock` is C++17
> and takes multiple mutexes, acquiring them with the same deadlock-avoidance algorithm as
> `std::lock` so acquisition order doesn't matter. `shared_lock` pairs with `shared_mutex` for
> reader/writer. Main traps: the unnamed temporary that locks and instantly unlocks, `release()`
> disowning without unlocking where you almost always meant `unlock()`, and the fact that there's no
> lock upgrade from shared to exclusive in the standard.

---
---

# Part 5 — Condition variables

**Header:** `<condition_variable>` · **Introduced:** C++11

> Terminology: it's **condition variable**, not "conditional variable." Interviewers notice.

## 5.1 The problem

A mutex makes waiting *safe*. It doesn't make waiting *efficient*.

```cpp
while (true) {
    std::unique_lock<std::mutex> lk(m);
    if (!queue.empty()) { auto x = queue.front(); queue.pop(); break; }
    lk.unlock();                       // ✗ busy-wait: burns a full CPU core
}                                      //   doing nothing, and hammers the mutex
```

That thread stays in **RUNNING** state at 100% CPU to repeatedly discover there's nothing to do.
Adding `sleep_for(10ms)` "fixes" the burn but adds latency and is pure guesswork.

What you want: **"put me in BLOCKED state until someone tells me something changed."**
That's a condition variable — not a lock, not a data structure, but a **waiting room with a doorbell**.

```
Consumer: [lock]─[queue empty]─[wait: UNLOCK + SLEEP]········[wake]─[RELOCK]─[take item]─[unlock]
                                       ▲                        ▲
                                       │                        │
Producer:                    ─────[lock]─[push]─[unlock]─[notify]
```

## 5.2 A CV is always part of a trio

| Piece | Role |
|---|---|
| **The shared state** | The actual data — a queue, a `bool ready`, a counter |
| **A mutex** | Protects that state |
| **The condition_variable** | The signalling channel |

The thing you're waiting on — the **predicate** — is a function of the shared state, *not* something
the CV knows about.

**The CV is stateless and has no memory.** It does not remember that a notify happened. If you
notify when nobody is waiting, the notification **vanishes into the void**. Everything meaningful
lives in the shared state; the CV only handles sleep/wake mechanics.

Internalize that — most CV bugs are a failure to believe it.

## 5.3 The API

```cpp
// Waiting  (must hold a std::unique_lock<std::mutex>)
cv.wait(lk);                            // raw — you must write the loop yourself
cv.wait(lk, pred);                      // ✓ preferred — loop built in
cv.wait_for(lk, 100ms);                 // → std::cv_status::timeout / no_timeout
cv.wait_for(lk, 100ms, pred);           // → bool: the predicate's final value
cv.wait_until(lk, timePoint);
cv.wait_until(lk, timePoint, pred);

// Notifying  (lock NOT required)
cv.notify_one();                        // wake one waiter (unspecified which)
cv.notify_all();                        // wake all waiters
```

Return-type asymmetry — a common quiz:

- **Without** a predicate → `cv_status::timeout` or `cv_status::no_timeout`
- **With** a predicate → `bool`, simply `pred()` evaluated one final time. `false` means "timed out
  and the condition still isn't satisfied."

The predicate form handles the loop, spurious wakeups, and deadline arithmetic all at once.
Hand-rolling `wait_for` in a loop is a bug factory — each spurious wake restarts the full timeout
unless you recompute the deadline.

## 5.4 What `wait()` actually does

```
cv.wait(lk):
  1. ATOMICALLY:  unlock the mutex  AND  register on the CV's wait queue
  2. BLOCK        (RUNNING → BLOCKED — off the run queue, zero CPU)
  3. On wake:     RE-LOCK the mutex (may block again if contended), then return
```

- **Why it must unlock.** If `wait()` held the mutex while sleeping, the producer could never
  acquire it to change the state. Instant deadlock.
- **Why it re-locks before returning.** Your next line inspects shared state; it must be under the
  mutex. `wait()` restores the exact invariant that held before you called it: **you hold the lock
  on both sides.**

That's why it must be a `std::unique_lock`.

## 5.5 Spurious wakeups — why the loop is mandatory

**A `wait()` can return without anyone calling `notify`.** Permitted by the standard, and it happens.

Why it's allowed:

- POSIX `pthread_cond_wait` can be interrupted by a **signal**.
- Implementations use a **futex**, and on some platforms multiple waiters get woken by one wake.
- Requiring their absence would force expensive extra synchronization on every wake path.

Waking up is a **hint**, not a guarantee.

Also: even a *genuine* notify may be stale by the time you run. Between the notifier's
`notify_one()` and your reacquiring the mutex, a third thread may have taken the item. This
**"stolen wakeup" is more common than a truly spurious one**, and the fix is identical.

```cpp
// ✗ BROKEN
std::unique_lock<std::mutex> lk(m);
if (!ready) cv.wait(lk);        // wakes spuriously → proceeds with ready == false

// ✓ Correct — always re-check in a loop
while (!ready) cv.wait(lk);

// ✓ Best — identical, but the library writes the loop for you
cv.wait(lk, []{ return ready; });
```

Memorize the equivalence:

```cpp
cv.wait(lk, pred);   ⟺   while (!pred()) cv.wait(lk);
```

The predicate is evaluated **before the first sleep** — if the condition is already true, `wait`
returns immediately without ever blocking. Not an optimization; essential (see next).

## 5.6 The lost wakeup problem

The deepest question in this topic. **Why must the notifier hold the mutex while changing state?**

Imagine `wait()` weren't atomic:

```
Consumer                            Producer
────────                            ────────
lock(m)
check: queue empty  ✓
unlock(m)
                                    lock(m)
                                    push(item)
                                    unlock(m)
                                    notify_one()   ← nobody's waiting yet. Lost forever.
sleep on cv
   ▲
   └── sleeps forever, with an item sitting in the queue
```

The notify fell into the gap between *checking* and *sleeping*.

**The protocol closes this window from both sides:**

1. `wait()` unlocks and enqueues **atomically** — there is no gap.
2. The waiter holds the mutex while evaluating the predicate.
3. The notifier holds the mutex while **modifying** the state.

So the notifier can't change state during the waiter's check-then-sleep — it can't even get the
mutex until the waiter is safely on the wait queue. And if the producer *did* get there first, the
predicate is already true and `wait` returns immediately.

**Rule:** you may notify with or without the lock, but you **must** hold the lock when *modifying*
the predicate's state.

## 5.7 Notify with or without the lock?

Both correct. The trade-off:

```cpp
// (A) Notify while holding the lock
{
    std::lock_guard<std::mutex> lg(m);
    ready = true;
    cv.notify_one();
}   // waiter wakes, immediately blocks on the mutex → "hurry up and wait", one wasted switch

// (B) Unlock first, then notify  ← generally preferred
{
    std::lock_guard<std::mutex> lg(m);
    ready = true;
}
cv.notify_one();    // waiter wakes and can grab the mutex right away
```

(B) is the usual advice; modern implementations mitigate (A) with *wait morphing* (moving the waiter
directly to the mutex queue instead of waking it).

**One real correctness exception:** if the waiter might **destroy** the CV after waking, notifying
after unlocking is a use-after-free. In that pattern, notify while holding the lock.
`std::notify_all_at_thread_exit` (C++11) covers the related case of notifying after thread-local
destructors have run.

## 5.8 `notify_one` vs `notify_all`

| | Wakes | Use when |
|---|---|---|
| `notify_one` | Exactly one waiter (unspecified which) | All waiters are **interchangeable** |
| `notify_all` | Every waiter | Waiters have **different predicates**, or the change satisfies many |

**`notify_one` is the efficient default** — avoids the **thundering herd** (N threads wake, contend
for one mutex, N−1 go straight back to sleep).

**But it's a correctness bug when waiters aren't equivalent.** Classic failure: one CV shared by
producers waiting on "not full" and consumers waiting on "not empty". A `notify_one` may wake the
*wrong class* of thread, whose predicate is false, so it sleeps again — and the signal is consumed.
Deadlock with work available.

Two ways out:

1. **Two condition variables** — `notFull_` and `notEmpty_`. Preferred: precise and efficient.
2. **One CV with `notify_all`** — correct but wasteful.

Always `notify_all` for: **shutdown flags**, **broadcast state changes**, any "release the gate"
pattern.

## 5.9 `condition_variable` vs `condition_variable_any`

Both C++11.

| | Works with | Cost |
|---|---|---|
| `condition_variable` | **Only** `unique_lock<mutex>` | Maps directly onto the native primitive — fastest |
| `condition_variable_any` | **Any BasicLockable** — `shared_lock`, `recursive_mutex`, custom locks | Needs an internal mutex of its own → slower |

**C++20 adds the one compelling reason to reach for `_any`:** interruptible waits via
`std::stop_token`, integrating with `std::jthread`.

```cpp
std::condition_variable_any cv;

std::jthread worker([&](std::stop_token st) {
    std::unique_lock<std::mutex> lk(m);
    // returns false if stop was requested — no more "poison pill" hacks
    if (cv.wait(lk, st, []{ return ready; })) doWork();
});
// jthread's destructor calls request_stop() → the wait unblocks
```

Plain `std::condition_variable` has **no** stop_token overload — this is `_any`-only.

## 5.10 Gotchas

- **All threads waiting on one CV must use the same mutex.** Mixing mutexes on one CV is **UB**.
- **Never `wait()` without holding the lock.** Passing an unlocked `unique_lock` is UB.
- **The shutdown flag must be in every predicate.** The most common real-world CV hang:
  `done_ = true; notify_all();` but a waiter's predicate is only `!q_.empty()` — it wakes, predicate
  is false, sleeps forever, and `join()` never returns.
- **Destroying a CV with waiters attached is UB.**
- **`wait_for` is not exact.** A spurious wake in a hand-written loop restarts the timeout. Use the
  predicate overload (defined in terms of `wait_until` with a fixed deadline).
- **Prefer `steady_clock` for timeouts** — `system_clock` is vulnerable to wall-clock adjustments.
- **CVs are not a queue and not a semaphore.** No memory, no count, no ordering guarantee about
  which waiter wakes. Fairness is not promised.
- **Don't hold the lock across slow work after waking** — copy the item out, unlock, then process.

## 5.11 When a CV is *not* the right tool (C++20 alternatives)

- **`std::latch`** — one-shot "wait for N tasks to finish." Not reusable.
- **`std::barrier`** — reusable rendezvous for a fixed group, phase by phase.
- **`std::counting_semaphore`** — resource counting. Unlike a CV, a semaphore **has memory**: a
  `release()` with no waiter is *not* lost, it increments the count. **Key semantic difference.**
- **`std::atomic::wait/notify`** — lightweight single-variable waiting with no mutex at all.

## ⏱ 60-second answer — condition variables

> A condition variable lets a thread block until some shared state changes, instead of busy-waiting.
> It's always used as a trio: the shared state, a mutex protecting it, and the CV itself. `wait()`
> takes a `unique_lock` — specifically that type, because it has to atomically unlock the mutex and
> enqueue the thread, block, then re-lock before returning; `lock_guard` can't express that. That
> atomic unlock-and-enqueue is what prevents the lost-wakeup race, together with the rule that the
> notifier must hold the mutex while modifying the state. You always wait in a loop on a predicate,
> because wakeups can be spurious — permitted by the standard, since POSIX condvars can be
> interrupted by signals — and because another thread may have consumed the item before you
> reacquire the lock. `cv.wait(lk, pred)` is exactly `while (!pred()) cv.wait(lk);`. `notify_one` is
> the efficient default and avoids a thundering herd, but it's a bug when waiters have different
> predicates — a bounded queue needs two CVs, one for not-full and one for not-empty. The CV itself
> is stateless: a notify with no waiter is lost, which is the key difference from a semaphore. All
> of this is C++11; C++20 adds stop_token-interruptible waits on `condition_variable_any`, plus
> `latch`, `barrier`, `counting_semaphore`, and `atomic::wait`.

---
---

# Part 6 — Full producer–consumer program

**Build:** `g++ -std=c++17 -Wall -pthread producer_consumer.cpp -o pc`

```cpp
// producer_consumer.cpp
#include <chrono>
#include <condition_variable>
#include <iostream>
#include <mutex>
#include <queue>
#include <string>
#include <thread>
#include <vector>

using namespace std::chrono_literals;

// ===================== the queue =====================
template <typename T>
class BoundedQueue {
    std::mutex              m_;
    std::condition_variable notFull_, notEmpty_;
    std::queue<T>           q_;
    size_t                  cap_;
    bool                    done_ = false;
public:
    explicit BoundedQueue(size_t cap) : cap_(cap) {}

    bool push(T item) {
        std::unique_lock<std::mutex> lk(m_);
        notFull_.wait(lk, [this]{ return q_.size() < cap_ || done_; });
        if (done_) return false;
        q_.push(std::move(item));
        lk.unlock();
        notEmpty_.notify_one();
        return true;
    }

    bool pop(T& out) {
        std::unique_lock<std::mutex> lk(m_);
        notEmpty_.wait(lk, [this]{ return !q_.empty() || done_; });
        if (q_.empty()) return false;          // shut down AND fully drained
        out = std::move(q_.front());
        q_.pop();
        lk.unlock();
        notFull_.notify_one();
        return true;
    }

    void shutdown() {
        { std::lock_guard<std::mutex> lg(m_); done_ = true; }
        notFull_.notify_all();
        notEmpty_.notify_all();
    }

    size_t size() {            // demo only - a snapshot, stale the moment it returns
        std::lock_guard<std::mutex> lg(m_);
        return q_.size();
    }
};

// ===================== synchronized printing =====================
// Without this mutex, output from different threads interleaves mid-line.
std::mutex gCoutM;
auto gStart = std::chrono::steady_clock::now();

void say(const std::string& msg) {
    auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(
                  std::chrono::steady_clock::now() - gStart).count();
    std::lock_guard<std::mutex> lg(gCoutM);
    std::cout << "[" << ms << " ms] " << msg << std::endl;
}

// ===================== workers =====================
void producer(BoundedQueue<int>& q, int id, int count) {
    for (int i = 1; i <= count; ++i) {
        int item = id * 100 + i;
        say("  P" + std::to_string(id) + " pushing " + std::to_string(item) + " ...");
        if (!q.push(item)) {
            say("  P" + std::to_string(id) + " push REJECTED (queue shut down)");
            break;
        }
        say("  P" + std::to_string(id) + " pushed  " + std::to_string(item) +
            "   [size=" + std::to_string(q.size()) + "]");
        std::this_thread::sleep_for(200ms);           // producers are FAST
    }
    say("  P" + std::to_string(id) + " finished producing");
}

void consumer(BoundedQueue<int>& q, int id) {
    int item;
    while (q.pop(item)) {                             // blocks here when empty
        say("        C" + std::to_string(id) + " got     " + std::to_string(item) +
            "   [size=" + std::to_string(q.size()) + "]");
        std::this_thread::sleep_for(700ms);           // consumers are SLOW
    }
    say("        C" + std::to_string(id) + " exiting (shut down and drained)");
}

// ===================== orchestrator =====================
int main() {
    const int CAPACITY = 3, NUM_PRODUCERS = 2, NUM_CONSUMERS = 2, ITEMS_EACH = 4;

    BoundedQueue<int> q(CAPACITY);
    std::vector<std::thread> producers, consumers;

    say("=== start: " + std::to_string(NUM_PRODUCERS) + " producers, " +
        std::to_string(NUM_CONSUMERS) + " consumers, capacity " +
        std::to_string(CAPACITY) + " ===");

    for (int i = 1; i <= NUM_PRODUCERS; ++i)
        producers.emplace_back(producer, std::ref(q), i, ITEMS_EACH);
    for (int i = 1; i <= NUM_CONSUMERS; ++i)
        consumers.emplace_back(consumer, std::ref(q), i);

    // 1. wait until every producer has finished producing
    for (auto& t : producers) t.join();
    say("=== all producers done -> shutting down queue ===");

    // 2. announce "no more items are coming" - this wakes blocked consumers
    q.shutdown();

    // 3. consumers drain the leftovers, then pop() returns false and they exit
    for (auto& t : consumers) t.join();
    say("=== all consumers joined - clean exit ===");
}
```

## 6.1 Actual output

```
[0 ms] === start: 2 producers, 2 consumers, capacity 3 ===
[3 ms]   P1 pushing 101 ...
[3 ms]   P1 pushed  101   [size=1]
[4 ms]   P2 pushing 201 ...
[4 ms]   P2 pushed  201   [size=2]
[4 ms]         C1 got     101   [size=1]
[4 ms]         C2 got     201   [size=0]
[203 ms]   P2 pushing 202 ...
[203 ms]   P2 pushed  202   [size=1]
[203 ms]   P1 pushing 102 ...
[203 ms]   P1 pushed  102   [size=2]
[409 ms]   P1 pushing 103 ...
[410 ms]   P1 pushed  103   [size=3]      ← queue now FULL
[411 ms]   P2 pushing 203 ...             ← P2 announces...
[616 ms]   P1 pushing 104 ...             ← P1 announces...
[699 ms]         C2 got     202   [size=2]  ← a slot frees up
[699 ms]   P2 pushed  203   [size=3]        ← ...P2 unblocks, 288 ms later
[699 ms]         C1 got     102   [size=2]
[699 ms]   P1 pushed  104   [size=3]        ← ...P1 unblocks
[905 ms]   P1 finished producing
[905 ms]   P2 pushing 204 ...             ← blocked again...
[1406 ms]   P2 pushed  204   [size=3]     ← ...501 ms later
[1406 ms]         C1 got     103   [size=2]
[1406 ms]         C2 got     203   [size=2]
[1621 ms]   P2 finished producing
[1621 ms] === all producers done -> shutting down queue ===
[2117 ms]         C1 got     104   [size=1]   ← draining after shutdown
[2118 ms]         C2 got     204   [size=0]
[2832 ms]         C2 exiting (shut down and drained)
[2832 ms]         C1 exiting (shut down and drained)
[2832 ms] === all consumers joined - clean exit ===
```

## 6.2 What the output proves

1. **Backpressure works.** At 411 ms P2 says "pushing 203" then *nothing for 288 ms* — parked in
   `notFull_.wait()`, BLOCKED, zero CPU. It resumes at exactly 699 ms, the instant C2's `pop()`
   freed a slot and fired `notFull_.notify_one()`. That gap is the whole point of a **bounded**
   queue: a fast producer cannot run away and exhaust memory.
2. **Consumers idle for free.** Nobody spins; between items each consumer sits in `notEmpty_.wait()`.
3. **Clean shutdown with no lost items.** At 1621 ms production stops, but 104 and 204 are still
   queued. Consumers drain them first; only when the queue is *both* shut down *and* empty does
   `pop()` return `false`. Zero items dropped, no hang.

## 6.3 The orchestration, in three lines

```cpp
for (auto& t : producers) t.join();   // 1. all production complete
q.shutdown();                         // 2. only NOW is it safe to say "no more coming"
for (auto& t : consumers) t.join();   // 3. consumers drain, then exit naturally
```

**Order matters.** Call `shutdown()` before the producers finish and you'd reject valid items. Skip
`shutdown()` entirely and consumers block forever in `notEmpty_.wait()` — `join()` never returns and
the program hangs. That is the single most common bug in this pattern, and exactly why the shutdown
flag appears **inside both predicates**.

## 6.4 Three details worth knowing

- **`std::ref(q)` is mandatory.** `std::thread` *decay-copies* every argument into internal storage.
  Without `std::ref` it tries to copy the `BoundedQueue` — non-copyable because it contains a
  `std::mutex` — so it wouldn't even compile. Same trap as `std::bind`.
- **The `gCoutM` print mutex isn't decoration.** `std::cout` is thread-safe against data races but
  gives no atomicity across `<<` calls; without it you get shredded lines. Note it's a *separate*
  mutex from the queue's, and nothing ever holds both in a cycle — no deadlock.
- **`sleep_for` is outside the lock, always.** `pop()` fully releases the mutex before the 700 ms
  sleep. Sleeping while holding a lock would serialize everything.

---
---

# Part 7 — C++ version differences + parens vs braces

## 7.1 Is that `BoundedQueue` C++11 or C++17?

**C++17 — but only barely.** Exactly **one** feature pushes it past C++11: **CTAD** (Class Template
Argument Deduction) on the lock types.

Verified with g++:

```
original @ -std=c++11  →  error: missing template arguments before 'lk'
original @ -std=c++17  →  compiles clean
```

### Complete audit

| Feature in the code | Since |
|---|---|
| `std::unique_lock lk(m_);` — **CTAD** | **C++17** ← the only offender |
| `std::lock_guard lg(m_);` — **CTAD** | **C++17** ← same |
| `bool done_ = false;` — NSDMI (in-class member init) | C++11 |
| `[this]{ return ...; }` — lambda | C++11 |
| `std::move(item)` — move semantics | C++11 |
| `bool push(T item)` + move — pass-by-value + move idiom | C++11 |
| `std::condition_variable`, `wait(lk, pred)` | C++11 |
| `explicit`, `std::queue`, `std::mutex` | C++98 / C++11 |

### The C++11 version — a two-token change

```cpp
std::unique_lock<std::mutex> lk(m_);      // ← spell out the type
std::lock_guard<std::mutex>  lg(m_);      // ← same
```

That's the *entire* diff.

### What CTAD actually is

Before C++17, class templates could never deduce their arguments from constructor parameters — only
function templates could. So you wrote the type twice:

```cpp
std::pair<int, std::string> p(1, "hi");                // C++11
auto p = std::make_pair(1, std::string("hi"));         // C++11 workaround: a helper function
std::pair p(1, std::string("hi"));                     // C++17 CTAD — no helper needed
```

CTAD is why `std::make_pair`, `std::make_tuple` etc. exist — function-template workarounds for
exactly this gap. C++17 made most obsolete.

**Important:** CTAD is purely **compile-time syntax sugar**. Runtime behavior, generated code, and
performance are byte-for-byte identical.

### What you'd write in each era

| Standard | Idiomatic single-mutex lock |
|---|---|
| C++11 | `std::lock_guard<std::mutex> lg(m_);` |
| C++17 | `std::lock_guard lg(m_);` or `std::scoped_lock lk(m_);` |
| C++20 | Same, plus `std::jthread` + `condition_variable_any` with `stop_token` for shutdown |

If asked "make this C++20," the meaningful upgrade isn't syntax — it's replacing the `done_` flag
with a `std::stop_token`.

## 7.2 `lk(m_)` vs `lk{m_}`

### For locks: **zero difference**

```cpp
std::unique_lock<std::mutex> lk(m_);   // identical
std::unique_lock<std::mutex> lk{m_};   // identical
```

Both are **direct-initialization**, both select `unique_lock(mutex_type&)`, both generate the same
code. `unique_lock` has no `initializer_list` constructor and there's no narrowing, so the two forms
that *usually* differ don't apply.

### Where they genuinely differ

**1. Braces prefer `initializer_list` — the famous trap**

```cpp
std::vector<int> v1(3, 0);   // ← 3 elements: {0, 0, 0}
std::vector<int> v2{3, 0};   // ← 2 elements: {3, 0}   ⚠ completely different object
```

**Rule:** if *any* `initializer_list` constructor is viable, braces pick it — even if a parenthesized
call would be a "better" match. This is why "always use braces" is bad blanket advice.

**2. Braces ban narrowing conversions**

```cpp
int a(3.99);      // ✓ compiles → 3, silently
int b{3.99};      // ✗ compile error: narrowing conversion
long x = 5;
int c{x};         // ✗ error (non-constant narrowing)
```

Braces' best feature — turns silent data loss into a compile error.

**3. Braces immunize against the Most Vexing Parse**

```cpp
std::lock_guard<std::mutex> lg();    // ✗ declares a FUNCTION returning lock_guard. No lock!
Widget w();                          // ✗ function declaration
Widget w{};                          // ✓ value-initialized object
```

MVP only bites when the arguments *could* be parsed as types. Since `m_` is a variable name,
`lk(m_)` is unambiguous and safe.

**4. Braces work for aggregates (parens didn't until C++20)**

```cpp
struct P { int x, y; };
P p{1, 2};    // ✓ all standards
P q(1, 2);    // ✗ pre-C++20;  ✓ C++20 (P0960, parenthesized aggregate init)
```

**5. `auto` + braces is its own hazard**

```cpp
auto a = 1;      // int
auto b{1};       // int in C++17;  std::initializer_list<int> in C++11/14  ⚠
auto c = {1};    // std::initializer_list<int> — always
auto d{1, 2};    // ✗ ill-formed since C++17
```

### The lock-specific twist (verified with g++)

Parens and braces fail **differently**, and parens is the safer accident:

```cpp
std::lock_guard<std::mutex>(m);   // ✗ HARD ERROR
std::lock_guard<std::mutex>{m};   // ⚠ compiles — silent bug (warning only)
```

Actual g++ output:

```
PARENS:  error: no matching function for call to 'lock_guard<mutex>::lock_guard()'
BRACES:  warning: ignoring return value ... declared with attribute 'nodiscard'
```

**Why parens errors:** `std::lock_guard<std::mutex>(m);` is *not* a temporary at all. Redundant
parentheses around a declarator name are legal, so it parses as `std::lock_guard<std::mutex> m;` — a
**local variable named `m`**, default-constructed. `lock_guard` has no default constructor → hard
error. You get saved by accident.

**Why braces compiles:** `{m}` can't be a declarator, so it unambiguously constructs a temporary
that locks and instantly unlocks. libstdc++ marks the constructor `[[nodiscard]]`, so you get a
*warning* — which slips through if warnings are off.

> **Practical takeaway: build with `-Wall -Werror`.** That converts this from a silent race into a
> build failure.

### What to actually do

| Situation | Use |
|---|---|
| Locks, mutexes, threads | `lk(m_)` — parens. Conventional, fails loudly on the temporary bug |
| Containers with a size/count argument | **Parens.** `vector<int> v(3, 0)` |
| Containers with literal contents | **Braces.** `vector<int> v{1, 2, 3}` |
| Aggregates / POD structs | **Braces** |
| Numeric conversions you want checked | **Braces** (narrowing protection) |
| Zero arguments | **Braces** — `T x{}` avoids MVP |

The honest summary: "always braces" is popular but breaks on `std::vector`. The defensible position:
**braces by default for narrowing checks and MVP immunity, parens whenever an `initializer_list`
constructor would hijack the call.**

## ⏱ 60-second answer — versions & init syntax

> The code is C++17 only because of CTAD — `std::unique_lock lk(m_)` without template arguments.
> Write `std::unique_lock<std::mutex> lk(m_)` and it's pure C++11; everything else — the lambdas,
> `std::move`, in-class member initializers, and condition_variable itself — is C++11. CTAD is
> compile-time only, so generated code is identical; it's the same reason `std::make_pair` existed,
> as a function-template workaround for class templates not being able to deduce.
>
> For `lk(m_)` versus `lk{m_}` there's no difference here — both are direct-initialization selecting
> the same constructor, and `unique_lock` has no initializer_list constructor to hijack the braces.
> In general braces differ in three ways: they prefer `initializer_list` constructors, which is why
> `vector<int> v(3,0)` gives three zeros but `v{3,0}` gives two elements; they reject narrowing
> conversions; and they sidestep the most vexing parse. One lock-specific detail: the
> unnamed-temporary bug behaves differently — `lock_guard<mutex>(m);` is actually parsed as
> declaring a variable named `m`, so it's a hard error, while `lock_guard<mutex>{m};` really does
> build a temporary that locks and instantly unlocks, and only warns.

---
---

# Part 8 — Why `std::` and not `using namespace std`

## 8.1 The core idea

Namespaces exist for exactly one reason: **to prevent name collisions in large programs.** `std` is
a box around a few thousand names so they can't fight with yours.

`using namespace std;` opens the box and dumps everything into the enclosing scope. You've
re-introduced the exact problem namespaces were invented to solve — and voluntarily given up the
tool the standard gave you to solve it.

That's the whole argument. Everything below is consequences.

## 8.2 Problem 1 — `std` is full of ordinary English words

`count` · `data` · `size` · `begin` · `end` · `swap` · `find` · `sort` · `min` · `max` ·
`distance` · `transform` · `next` · `prev` · `left` · `right` · `hash` · `ref` · `array` ·
`function` · `less` · `plus` · `equal` · `sample` · `bind` · `byte` · `move` · `copy` · `fill` ·
`merge` · `search` · `unique` · `rotate` · `error_code`

Famous real casualty: POSIX socket code including both `<functional>` and `<sys/socket.h>`. With
`using namespace std`, a plain `bind(sock, addr, len)` becomes ambiguous between the socket function
and `std::bind`. Code that compiled for years, broken by one `using` directive.

## 8.3 Problem 2 — the failure can be *silent*

**The argument that actually matters.**

Usual dismissal: *"if there's a conflict I'll get a compile error and fix it."* But an ambiguity
error is the **best** case — loud, immediate, safe.

The bad case is when there's **no ambiguity at all**, because one candidate is simply a **better
overload match**. Overload resolution doesn't warn you it had a close call. It picks a winner and
moves on. Your call site now quietly binds to a standard-library template instead of your function,
and the program compiles cleanly and behaves differently.

> **The danger isn't the code that breaks. It's the code that keeps compiling.**

Worse with templates and ADL, where the visible candidate set is already non-obvious.

## 8.4 Problem 3 — fragile across standard versions

`std` grows every three years, drawing from the same pool of ordinary words. Your code can break
without you touching a line.

Canonical example: **C++17 added `std::byte`**. Enormous amounts of code had a global
`typedef unsigned char byte;` — legal, zero conflict, for two decades. Add `using namespace std` and
a compiler upgrade, and every use of `byte` is suddenly ambiguous. C++17 also added `std::size`,
`std::data`, and `std::sample`, which broke real codebases the same way.

> `using namespace std` means the C++ committee has an open invitation to break your build.

## 8.5 Problem 4 — in a header, it's indefensible

In a `.cpp` the blast radius stops at that translation unit.

In a header, the blast radius is **everyone who includes you, transitively, forever**. Downstream
authors can't opt out and often have no idea where it came from — they just get a bewildering
ambiguity error in a file that doesn't mention `std` anywhere.

**The one absolute rule: never at namespace scope in a header.** No exceptions.

## 8.6 Problem 5 — you lose free documentation

`std::` isn't noise, it's **provenance**. It tells you at a glance that a name is standard, not
local, not third-party. Matters most where the distinction is subtle: `std::swap` vs a type's own
ADL-found `swap`; `std::move` vs a member `move`; `std::function` vs your framework's.

## 8.7 What it does *not* cost you

Purely a **compile-time** concern. `using namespace std` has **zero runtime cost** — no performance
implication, no binary size difference. Anyone claiming it's slow is wrong. The entire debate is
correctness, maintainability, readability.

## 8.8 The principled exception — literal namespaces

The demo in Part 6 uses `using namespace std::chrono_literals;`. That's deliberate, not inconsistent.

**Literal operator namespaces are designed to be imported.** There's no way to write `200ms` without
bringing `operator""ms` into scope — you can't qualify a literal suffix. And these namespaces
(`std::chrono_literals`, `std::string_literals`, `std::literals`) contain *only* literal operators —
a handful of suffixes like `ms`, `s`, `h`, `sv`. Essentially nothing to collide with.

So it's a targeted import of a tiny, purpose-built namespace, not a bulk dump. Same reasoning for
`using namespace std::placeholders` for `_1`, `_2`.

> **The actual principle: the objection is to the *size and unpredictability* of what you're
> importing, not to the `using` keyword.**

## 8.9 The pragmatic spectrum

| Approach | Verdict |
|---|---|
| Fully qualify — `std::vector` | Always correct. The default. |
| Using-**declaration** for specific names — `using std::string;` | Fine. Imports exactly one name; a conflict is a *hard error*, never silent. |
| `using namespace std;` **inside a function** | Acceptable. Blast radius is one function. |
| `using namespace std::chrono_literals;` etc. | Fine — tiny, purpose-built namespaces. |
| `using namespace std;` at file scope in a `.cpp` | Tolerable in a toy, a bad habit in real code. |
| `using namespace std;` in a **header** | Never. |

**Key distinction to name in an interview:** a using-*declaration* (`using std::swap;`) is a
fundamentally different and safer thing from a using-*directive* (`using namespace std;`). The
declaration names exactly what it imports and turns any conflict into an immediate error. The
directive imports an open-ended set and lets overload resolution silently arbitrate.

## 8.10 Honest summary

Fine for a 30-line exercise, a competitive-programming submission, or a throwaway snippet — code
that will never be included, maintained, or survive a compiler upgrade. It stops being fine the
moment the code has a future.

**Interview framing:** `using namespace std;` at global scope is a minor signal, easily excused. In a
header it's a real red flag. Being able to explain *why* — especially the silent-overload-resolution
point and the `std::byte` version-fragility example — is a meaningfully positive signal, because it
shows you understand name lookup rather than reciting a style rule.

---
---

# Appendix — Cheat sheets

## A.1 Standard version cheat sheet

| Feature | Standard |
|---|---|
| `std::thread`, `std::mutex`, `std::lock_guard`, `std::unique_lock` | C++11 |
| `std::atomic`, `std::atomic_flag`, `memory_order` | C++11 |
| `std::condition_variable`, `condition_variable_any`, `cv_status` | C++11 |
| `std::notify_all_at_thread_exit` | C++11 |
| `std::call_once` / `once_flag`, magic statics | C++11 |
| `std::async`, `future`, `promise`, `packaged_task` | C++11 |
| `std::shared_timed_mutex`, `std::shared_lock` | C++14 |
| `std::shared_mutex` | C++17 |
| `std::scoped_lock` | C++17 |
| **CTAD** (`std::lock_guard lg(m);` with no template args) | C++17 |
| `is_always_lock_free`, `hardware_destructive_interference_size` | C++17 |
| `std::byte`, `std::size`, `std::data`, `std::sample` | C++17 |
| `std::jthread`, `std::stop_token` / `stop_source` | C++20 |
| `condition_variable_any::wait(lk, stop_token, pred)` | C++20 |
| `std::atomic<T>::wait / notify_one / notify_all` | C++20 |
| `std::atomic_ref`, `std::atomic<std::shared_ptr<T>>` | C++20 |
| `std::latch`, `std::barrier`, `std::counting_semaphore` | C++20 |
| Parenthesized aggregate initialization | C++20 |

## A.2 "Which primitive do I reach for?"

| Need | Use |
|---|---|
| One variable, indivisible update | `std::atomic` |
| Multi-variable invariant / critical section | `std::mutex` + RAII lock |
| Many readers, few writers, long critical sections | `std::shared_mutex` + `shared_lock` |
| Wait until shared state changes | `std::condition_variable` |
| Wait for N tasks to finish (one-shot) | `std::latch` (C++20) |
| Reusable phase rendezvous | `std::barrier` (C++20) |
| Count a resource pool; notify must not be lost | `std::counting_semaphore` (C++20) |
| Run a task, get a result or an exception back | `std::async` / `std::future` |
| One-time initialization | magic static, or `std::call_once` |
| Cooperative cancellation | `std::stop_token` + `std::jthread` (C++20) |

## A.3 The top traps, one line each

1. `std::thread` destructor on a joinable thread → `std::terminate()`. Always `join()` or `detach()`.
2. `a = a + 1` on an atomic is **two** operations, not one. Use `a++` / `fetch_add`.
3. `fetch_add` / `fetch_sub` / `exchange` return the **OLD** value.
4. `compare_exchange_weak` can fail spuriously — only use it inside a loop.
5. Never call `m.lock()` directly; an exception leaks the lock forever.
6. `std::lock_guard lg(m);` — a *named* variable. The unnamed temporary is a silent no-op.
7. Deadlock: always `std::scoped_lock` for 2+ mutexes, or a consistent global order.
8. `cv.wait()` **must** be in a predicate loop — spurious and stolen wakeups are real.
9. The notifier **must hold the mutex while modifying** the predicate's state (lost wakeup).
10. `notify_one` with heterogeneous waiters = deadlock. Use two CVs or `notify_all`.
11. Put the shutdown flag **inside every CV predicate**, or `join()` hangs forever.
12. `std::ref` when passing a non-copyable object to `std::thread` — arguments are decay-copied.
13. `volatile` is **not** a threading primitive in C++.
14. Never hold a lock across I/O, sleeps, `std::cout`, or unknown callbacks.
15. Returning a reference from a locked getter defeats the lock — return a copy.
