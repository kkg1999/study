# Python Notes — Requests/FastAPI, Concurrency, asyncio & Queues

A working reference covering three connected topics:

1. [Request & Response in Python + FastAPI](#part-1--request--response-in-python--fastapi)
2. [Concurrency, Threads, GIL and async/await](#part-2--concurrency-threads-and-asyncawait)
3. [asyncio & queue.Queue building blocks](#part-3--asyncio--queuequeue)

---

# Part 1 — Request & Response in Python + FastAPI

There are **two sides** to this. Keep them separate in your head:

- **Client side** — *you* send a request, someone else answers. (`requests`, `httpx`)
- **Server side** — *you* receive a request, you answer. (FastAPI)

Same concepts, mirrored.

## 1.1 Anatomy of an HTTP request

Every request has four parts:

```
POST  https://api.site.com/users/42/orders?status=paid&limit=10
 |            |              |                    |
method      host           path              query params

Headers:  Authorization: Bearer xyz
          Content-Type: application/json

Body:     {"item": "book", "qty": 2}
```

| Part | What it's for |
|---|---|
| **Method** | Intent: `GET` read, `POST` create, `PUT/PATCH` update, `DELETE` remove |
| **Path** | *Which resource* — `/users/42` |
| **Query params** | *How to filter/shape* the result — `?status=paid&limit=10` |
| **Headers** | Metadata: who you are (auth), what format you're sending/expecting |
| **Body** | The actual payload. **GET requests don't have one** — that's why filters go in the query string |

The response mirrors it: **status code** (200 ok, 404 not found, 500 server broke), **headers**, **body**.

## 1.2 Client side — sending requests

**Sync** (`requests` — the classic):

```python
import requests

r = requests.get("https://api.github.com/users/octocat")
print(r.status_code)   # 200
data = r.json()        # body parsed into a dict
```

**Sync + async** (`httpx` — same API, does both; prefer this today):

```python
import httpx

# sync
r = httpx.get("https://api.github.com/users/octocat")

# async
async with httpx.AsyncClient() as client:
    r = await client.get("https://api.github.com/users/octocat")
```

### When does async actually matter?

Async wins when you're **waiting on the network**, not when you're computing.

```python
# sync: 10 calls x 200ms = 2 seconds
for url in urls:
    httpx.get(url)

# async: 10 calls in parallel ~= 200ms
async with httpx.AsyncClient() as c:
    results = await asyncio.gather(*[c.get(u) for u in urls])
```

One call? Doesn't matter. Many calls? Async is a huge win.

### Building URLs — don't use f-strings

Never hand-glue query strings. Special characters (`&`, spaces, `/`) will break things.

```python
# BAD - breaks if q contains "&" or a space
url = f"https://api.site.com/search?q={q}&page={page}"

# GOOD - let the library encode it
r = httpx.get(
    "https://api.site.com/search",
    params={"q": "hello world & more", "page": 2, "tags": ["a", "b"]},
)
# -> /search?q=hello+world+%26+more&page=2&tags=a&tags=b
```

Same idea for headers and body:

```python
httpx.post(
    "https://api.site.com/orders",
    headers={"Authorization": "Bearer TOKEN"},
    json={"item": "book", "qty": 2},   # json= sets Content-Type automatically
)
```

`json=` → sends JSON. `data=` → sends form-encoded. `params=` → query string. Three different slots, don't mix them up.

## 1.3 Server side — FastAPI

FastAPI's core trick: **it figures out where each argument comes from by its type and name.**

```python
from fastapi import FastAPI, Header
from pydantic import BaseModel

app = FastAPI()

class Order(BaseModel):        # describes the body
    item: str
    qty: int = 1

@app.post("/users/{user_id}/orders")
def create_order(
    user_id: int,                          # in the path -> path param
    status: str = "paid",                  # not in path, simple type -> query param
    order: Order = ...,                    # Pydantic model -> request body
    authorization: str = Header(None),     # explicitly a header
):
    return {"user": user_id, "item": order.item}
```

The rules:

- Name appears in the route path `{...}` → **path param**
- Simple type (`int`, `str`, `bool`), not in path → **query param**
- Pydantic model → **request body**
- Wrapped in `Header(...)` / `Cookie(...)` → from there

You get validation, type coercion (`"42"` → `42`), and auto-generated docs at `/docs` for free.

### `def` vs `async def` in FastAPI

This one trips people up:

```python
@app.get("/a")
def sync_route():         # runs in a threadpool - safe for blocking code
    return requests.get(...).json()

@app.get("/b")
async def async_route():  # runs on the event loop
    async with httpx.AsyncClient() as c:
        return (await c.get(...)).json()
```

**The rule:** use `async def` **only if everything inside it is awaited**. If you call blocking code (`requests`, `time.sleep`, a sync DB driver) inside `async def`, you freeze the entire server for every user.

Plain `def` is *never wrong* — FastAPI offloads it to a thread. When in doubt, use `def`.

## 1.4 JSON in Python

JSON ↔ Python is a direct mapping:

| JSON | Python |
|---|---|
| object `{}` | `dict` |
| array `[]` | `list` |
| string / number | `str` / `int`,`float` |
| `true` / `null` | `True` / `None` |

```python
import json

json.loads(text)    # JSON string -> Python object
json.dumps(obj)     # Python object -> JSON string
r.json()            # shortcut on a response
```

### Nested JSON

It's just dicts inside lists inside dicts. Walk it step by step:

```python
data = {
    "user": {"name": "Ada", "address": {"city": "London"}},
    "orders": [{"id": 1, "items": ["a", "b"]}, {"id": 2, "items": []}],
}

data["user"]["address"]["city"]      # "London"
data["orders"][0]["items"][1]        # "b"
[o["id"] for o in data["orders"]]    # [1, 2]
```

The pain: a missing key raises `KeyError` and crashes you. Two defenses:

**Quick & dirty** — `.get()` with defaults:

```python
city = data.get("user", {}).get("address", {}).get("city")   # None if absent
```

**The real answer** — describe the shape with Pydantic and let it validate:

```python
from pydantic import BaseModel

class Address(BaseModel):
    city: str

class User(BaseModel):
    name: str
    address: Address           # nested model

class Payload(BaseModel):
    user: User
    orders: list[dict] = []

p = Payload(**response.json())
p.user.address.city            # autocomplete, type-checked, validated
```

Now you fail loudly at the boundary with a clear error, instead of mysteriously three functions later. This is the single biggest quality-of-life upgrade when working with APIs.

## 1.5 Pagination

APIs won't send you 50,000 rows. They send a page and tell you how to get the next one. Two common styles:

### a) Offset / page-based

```
GET /items?limit=10&offset=0     -> items 1-10
GET /items?limit=10&offset=10    -> items 11-20
```

Response usually looks like:

```json
{ "items": [], "total": 137, "limit": 10, "offset": 0 }
```

Consuming it — loop until you run dry:

```python
def fetch_all(url):
    offset, out = 0, []
    while True:
        page = httpx.get(url, params={"limit": 100, "offset": offset}).json()
        out.extend(page["items"])
        if len(out) >= page["total"] or not page["items"]:
            break
        offset += 100
    return out
```

### b) Cursor / token-based

The server hands you an opaque pointer to "where you stopped":

```json
{ "items": [], "next_cursor": "eyJpZCI6MTAwfQ" }
```

```python
cursor = None
while True:
    params = {"limit": 100}
    if cursor:
        params["cursor"] = cursor
    page = httpx.get(url, params=params).json()
    yield from page["items"]
    cursor = page.get("next_cursor")
    if not cursor:
        break
```

Cursor is more robust — with offsets, if rows get inserted between calls you can skip or duplicate items. (GitHub's API uses a third variant: a `Link` header with `rel="next"` — same idea, the pointer just lives in a header.)

### Serving pagination in FastAPI

```python
@app.get("/items")
def list_items(limit: int = 10, offset: int = 0):
    return {
        "items": DB[offset : offset + limit],
        "total": len(DB),
        "limit": limit,
        "offset": offset,
    }
```

Always return `total` (or `next`) — otherwise the client has no way to know if it's done.

## 1.6 The mental model to keep

```
        params  ->  query string  (filters, pagination)
path    {id}    ->  which resource
headers         ->  auth + content type
body (json=)    ->  the payload         [POST/PUT/PATCH only]

        | over the wire |
        v               v

status code     ->  did it work?
headers         ->  metadata, sometimes pagination links
body            ->  .json() -> dict -> validate with Pydantic
```

**Defaults worth adopting:** `httpx` over `requests`, `params=`/`json=` over string building, plain `def` in FastAPI unless you're awaiting throughout, and Pydantic models at every boundary where JSON enters your program.

---

# Part 2 — Concurrency, Threads, and async/await

## 2.1 Three words that get confused

| Term | Meaning | Kitchen analogy |
|---|---|---|
| **Concurrency** | Multiple tasks *in progress*, interleaved. Not necessarily at the same instant. | One cook juggling 3 dishes — stirring one while another simmers |
| **Parallelism** | Multiple tasks *executing at the same instant*, on different cores. | Three cooks, three stoves |
| **Async** | A *style* of achieving concurrency: tasks voluntarily hand back control while waiting | The cook who never stands still watching a pot |

Parallelism needs multiple cores. Concurrency doesn't — it just needs tasks that spend time *waiting*.

## 2.2 The axis that decides everything: what is your task waiting on?

```
CPU-bound   ->  burning cycles: math, parsing, compression, training
I/O-bound   ->  waiting on something else: network, disk, database, another API
```

Pick the wrong tool for the wrong kind of work and you get zero speedup — or a slowdown. Almost every Python concurrency mistake is this mismatch.

## 2.3 The GIL — why Python is different

CPython has one **Global Interpreter Lock**. Rule:

> **Only one thread may execute Python bytecode at a time.**

This has no equivalent in Java or C++ — there, 8 threads genuinely run on 8 cores. In Python they take turns.

```
Java threads:    [T1####][T2####][T3####]   <- truly simultaneous
Python threads:  [T1##][T2##][T1##][T3##]   <- interleaved, one at a time
```

**But** — and this is the whole point — the GIL is **released whenever a thread waits on I/O**, and by C extensions that don't touch Python objects (numpy, torch, pandas, database drivers, compression libs).

So:

| Work | Do Python threads help? |
|---|---|
| Downloading 100 URLs | YES — GIL released while waiting on the socket |
| Reading 1000 files | YES — GIL released during the syscall |
| `numpy` matrix multiply | YES — the C code releases the GIL |
| Pure-Python `for` loop summing numbers | NO — pure bytecode, GIL held throughout |

For that last row, you need **processes**, not threads.

*(Side note: Python 3.13+ ships an optional free-threaded build with no GIL. Real, but not yet the default — the mental model above is still the one to hold.)*

## 2.4 Threads and thread pools

A Python thread is a **real OS thread** — same as Java's. Only the GIL differs.

Raw threads are rarely what you want:

```python
import threading
t = threading.Thread(target=download, args=(url,))
t.start(); t.join()
```

Use a **pool** instead. It's nearly identical to Java's `ExecutorService`:

```python
from concurrent.futures import ThreadPoolExecutor

with ThreadPoolExecutor(max_workers=10) as pool:
    futures = [pool.submit(download, u) for u in urls]   # Future, like Java's
    results = [f.result() for f in futures]              # blocks until done
```

Why pools:

- Threads cost ~1MB stack each — you can't have 10,000
- Reusing workers avoids creation cost
- `max_workers` naturally **caps concurrency** (don't hammer an API with 500 parallel calls)

Swap `ThreadPoolExecutor` → `ProcessPoolExecutor` and the *exact same code* runs on multiple cores — because processes each get their own interpreter and their own GIL. The cost: arguments and return values must be **pickled** and copied between processes, so it only pays off for chunky CPU work.

## 2.5 Locks and semaphores — why shared state hurts

The danger isn't threads, it's **shared mutable state**. This looks atomic but isn't:

```python
counter += 1
# actually: read counter -> add 1 -> write counter
# a thread switch between those steps loses an update
```

**Lock** — mutual exclusion, one holder at a time. Same idea as Java's `synchronized` or C++'s `std::mutex`:

```python
lock = threading.Lock()

with lock:            # exactly one thread inside this block
    counter += 1
```

**Semaphore** — a counter of permits. Allows *N* holders. Same as Java's `Semaphore`:

```python
sem = threading.Semaphore(5)   # at most 5 concurrent

with sem:
    call_rate_limited_api()
```

| | Purpose |
|---|---|
| **Lock** | Protect **correctness** — keep data consistent |
| **Semaphore** | Limit **capacity** — throttle how many at once |
| **Queue** | Avoid the problem — pass messages instead of sharing memory |

A connection pool *is* a semaphore. A rate limiter *is* a semaphore.

**The best defense is avoiding shared state entirely.** `queue.Queue` is already thread-safe; so is handing each worker its own data and merging results at the end. Locks are for when you genuinely can't avoid sharing.

Two classic hazards worth knowing by name: **deadlock** (A holds lock1 wanting lock2 while B holds lock2 wanting lock1 — avoid by always acquiring in the same order), and **race condition** (correctness depends on timing).

## 2.6 The event loop and `async`/`await`

### The idea

Threads are *preemptive*: the OS interrupts you whenever it likes, so a switch can happen between any two bytecodes — hence locks.

Async is **cooperative**: a task runs until it *voluntarily* says "I'm waiting, take over". That word is `await`. Between two awaits, your code cannot be interrupted — which is why async code needs far less locking.

The **event loop** is a single thread running this forever:

```
loop:
    ask the OS: which of my sockets/timers are ready?   (epoll / kqueue / IOCP)
    for each ready one:
        resume the task that was waiting on it
    repeat
```

One thread, thousands of concurrent connections — because the thread is never idle-blocked.

### What `await` actually does under the hood

An `async def` function **does not run when you call it**. It returns a *coroutine object* — a paused, resumable state machine. This is the same machinery as generators.

```python
async def fetch():
    print("A")
    data = await client.get(url)   # <- suspension point
    print("B")

c = fetch()      # nothing printed yet! just an object
await c          # now it runs
```

Mechanically:

1. `await` on something that isn't ready → the coroutine **yields** control (literally, `yield`-like) all the way up to the event loop, carrying a **Future** ("wake me when this resolves").
2. The event loop notes: *task T is waiting on this socket*. It then runs **other** tasks.
3. The OS says the socket has data. The loop resolves the Future and calls `coro.send(value)` — resuming the function **exactly where it stopped**, with local variables intact.

So one `async def` compiles into something like:

```
state 0: run until first await, save locals, return "waiting on X"
state 1: on resume, continue until next await...
```

If you know C++20 coroutines, this is the same transformation — the compiler splits a function into resumable states. If you know JavaScript, it's the identical model. Java's closest modern analogue is **virtual threads** (Loom), which chase the same goal — cheap concurrency for I/O — but hide it, so you write ordinary blocking code.

### The rules that follow from this

```python
# A coroutine only runs when awaited or scheduled
await one()                              # sequential: one, then two
await asyncio.gather(one(), two())       # concurrent: both in flight
task = asyncio.create_task(one())        # start now, await later
```

`gather` / `create_task` are where concurrency actually comes from. Plain `await` in a loop is just... a loop.

**And the cardinal sin:**

```python
async def handler():
    time.sleep(5)          # BAD  - blocks the loop - EVERY user waits 5s
    await asyncio.sleep(5) # GOOD - yields - other tasks run meanwhile
```

Any blocking call (`requests`, sync DB driver, heavy CPU loop, `time.sleep`) inside `async def` freezes the entire application. There is no OS preemption to save you. If you must, push it out:

```python
await asyncio.to_thread(blocking_function, arg)
```

Async also has its own `asyncio.Lock` / `asyncio.Semaphore` — needed not for bytecode races but to protect state **across await points**, and to throttle concurrency.

## 2.7 Choosing — the one table to remember

| Situation | Tool |
|---|---|
| Many network calls, library is sync | `ThreadPoolExecutor` |
| Many network calls, library is async | `asyncio` + `gather` |
| Thousands of concurrent connections | `asyncio` (threads won't scale) |
| Heavy pure-Python CPU work | `ProcessPoolExecutor` |
| CPU work in numpy/torch/pandas | Just call it — it already parallelizes in C |
| One-off blocking call inside async code | `asyncio.to_thread` |

Rough intuition: threads scale to *hundreds*, async tasks to *tens of thousands*, processes to *number of cores*.

## 2.8 Where this shows up in real work

### Machine learning

- **Training** is CPU/GPU-bound in C/CUDA. PyTorch releases the GIL, so its internal thread pools genuinely parallelize. You don't manage this.
- **Data loading** is the bottleneck you *do* manage — decoding JPEGs and augmenting is pure-Python-ish CPU work, so `DataLoader(num_workers=8)` uses **processes**, not threads. That's the GIL rule in action.
- **Inference serving** is I/O-bound at the edges (receiving requests) and CPU-bound in the middle. Typical shape: async web layer + a bounded worker pool for the model, with a **semaphore** capping in-flight inferences so you don't blow up GPU memory.
- **Batch feature fetching** from many APIs → `asyncio.gather`.

### File handling

- Reading 5,000 small files: **threads**. The GIL is released during each syscall, so a pool of 16 is a big win.
- Parsing/compressing them afterwards: that's CPU work → **processes**.
- Honest caveat: local disk I/O has no true async on most OSes. `aiofiles` just runs a thread pool under the hood. For files, threads are the real answer — async is for *sockets*.

### Postgres / databases

This is where async pays off most, because a query is 99% waiting.

```python
# sync:  psycopg           -> use with `def` routes / threads
# async: asyncpg or SQLAlchemy async -> use with `async def` routes
```

Key points:

- A **connection pool** is mandatory — opening a Postgres connection is expensive. The pool is a semaphore: `max_size` connections, everyone else queues.
- Your real concurrency ceiling is the pool, not your thread count. 200 threads sharing a 10-connection pool = 190 threads waiting.
- **Never** use a sync driver inside `async def` — one slow query stalls the whole server.
- Transactions must live on one connection — don't `gather` queries that belong to the same transaction.

### FastAPI — putting it together

```python
@app.get("/sync")
def route():          # runs in FastAPI's threadpool (~40 threads)
    return blocking_db_call()

@app.get("/async")
async def route():    # runs on the event loop
    return await async_db_call()
```

- `def` route → offloaded to a thread. Safe with any blocking code. Ceiling ≈ threadpool size.
- `async def` route → on the loop. Enormous scale, but **one blocking line poisons everything**.
- Mixed reality: an endpoint calling 3 external APIs → `async def` + `gather` turns 600ms into 200ms.
- CPU-heavy endpoint (PDF generation, image resize) → don't do it in the request at all. Hand it to a process pool or a task queue (Celery/RQ) and return a job ID.
- Multiple **workers** (`uvicorn --workers 4`) gives you real multi-core parallelism at the process level — that's how a single-threaded event loop still uses your whole CPU.

## 2.9 The compressed version

1. **Ask what you're waiting on.** I/O → threads or async. CPU → processes.
2. **The GIL blocks Python bytecode, not waiting.** That single sentence explains every rule above.
3. **Threads are preemptive** → you need locks. **Async is cooperative** → you mostly don't, but one blocking call kills you.
4. **`await` = "save my place, wake me when ready."** It's a resumable state machine, driven by a loop watching your sockets.
5. **Concurrency comes from `gather`/`create_task`/`submit`**, never from `async` alone.
6. **Locks protect correctness; semaphores cap capacity.** Best of all: don't share state — use queues.

---

# Part 3 — asyncio & queue.Queue

## 3.1 asyncio: three objects, in order of abstraction

| Object | What it is | How you get one |
|---|---|---|
| **Coroutine** | A paused function. Inert — does nothing until driven. | Calling `async def f()` |
| **Task** | A coroutine *scheduled on the loop*. Runs on its own. | `create_task(coro)` |
| **Future** | A placeholder for a value that arrives later + a list of "call me when it lands" callbacks. | Usually created *for* you |

The chain: **Future = the promise. Task = the driver. Coroutine = the code.**
(`Task` is literally a subclass of `Future`.)

A coroutine only becomes concurrent when it becomes a **Task**. That's the single most important sentence in asyncio.

## 3.2 The loop, mechanically

The event loop holds two things and one syscall:

```
ready     : deque of callbacks to run right now
scheduled : min-heap of timers (soonest first)
selector  : epoll/kqueue/IOCP - "tell me which sockets are ready"

each iteration:
  1. timeout = time until the next timer
  2. selector.select(timeout)   <- the only place the thread sleeps
  3. move ready I/O + expired timers -> ready
  4. run everything in ready to completion
```

Step 4 is why blocking code is fatal: a callback runs **to completion**, uninterrupted. `time.sleep(5)` inside step 4 means steps 1–3 don't happen for 5 seconds.

## 3.3 How `await` wires into that

```python
result = await something
```

1. Task calls `coro.send(None)`, coroutine runs until this line.
2. `something` isn't ready → it yields a **Future** up to the Task.
3. Task registers itself as a **done-callback** on that Future, then returns. The loop is free.
4. Later, I/O completes → `future.set_result(x)` → its callbacks get pushed onto `ready`.
5. Loop runs the callback → `coro.send(x)` → your function resumes on the next line, locals intact.

So `await` = *"register a continuation and get out of the way."*

**Cancellation** falls out of the same design: `task.cancel()` throws `CancelledError` **into** the coroutine at its suspension point. That's why cancellation only happens at `await` — and why your `finally` blocks still run.

## 3.4 The asyncio functions worth knowing

**Entry point**

```python
asyncio.run(main())
```

Creates a loop, runs `main` to completion, cancels leftovers, closes the loop. One per program, at the top.

**Making things concurrent** — this is the whole game:

```python
await one()                              # sequential
task = asyncio.create_task(one())        # starts now, in background
await task                               # collect later
```

`create_task` schedules immediately; `await` just collects. The gap between them is where concurrency lives.

```python
a, b = await asyncio.gather(fetch(x), fetch(y))   # both in flight, results in order
```

Under the hood: wraps each in a Task, returns one Future, decrements a counter on each completion. Caveat: if one fails, the others keep running (orphans).

```python
async with asyncio.TaskGroup() as tg:     # 3.11+, prefer this
    t1 = tg.create_task(fetch(x))
    t2 = tg.create_task(fetch(y))
```

Same concurrency, but the block **cannot exit until all children finish**, and a failure **cancels the siblings**. Structured concurrency — no orphans. Use it as your default.

**Time & control**

```python
await asyncio.sleep(1)      # a timer on the heap - never blocks the thread
await asyncio.sleep(0)      # "yield now" - reschedule me at the back of ready

async with asyncio.timeout(5):   # timer that calls .cancel() on you
    ...

for coro in asyncio.as_completed(tasks):   # results in finish order, not submit order
    result = await coro
```

**Escape hatches**

```python
await asyncio.to_thread(blocking_fn, arg)   # push blocking work to a thread, keep the loop free
loop.run_in_executor(process_pool, cpu_fn)  # same idea, for CPU work -> processes
```

**Coordination** — `asyncio.Lock`, `Semaphore`, `Event`, `Queue`. Same names as `threading`, different purpose:

```python
sem = asyncio.Semaphore(10)
async with sem:            # cap in-flight requests
    await fetch(url)
```

These aren't protecting against bytecode races (async is cooperative — no preemption). They protect **invariants across `await` points** and **cap capacity**. Never use the `threading` versions in async code: they block the loop.

## 3.5 `queue.Queue` — what it's for

`queue.Queue` is the answer to the locking problem from Part 2:

> **Don't share memory to communicate — communicate to share memory.**

It's a thread-safe handoff pipe. Producers `put`, consumers `get`, nobody touches anybody's variables.

```python
q = queue.Queue(maxsize=100)

# producer thread
q.put(item)

# consumer thread
item = q.get()      # BLOCKS until something arrives
```

## 3.6 Under the hood (it's simpler than you'd guess)

```
deque                      <- the actual storage
mutex      : Lock          <- one lock guarding the deque
not_empty  : Condition     <- consumers sleep here
not_full   : Condition     <- producers sleep here
```

`get()` is roughly:

```
with mutex:
    while deque is empty:
        not_empty.wait()      # releases mutex + sleeps until notified
    item = deque.popleft()
    not_full.notify()
```

A **Condition** is the key primitive: *atomically release the lock and sleep; reacquire on wake.* That's what makes blocking cheap — a waiting consumer uses **zero CPU**, no polling. Java's `wait()`/`notify()` is the identical mechanism; `queue.Queue` ≈ `BlockingQueue`.

## 3.7 The Queue API that matters

| Call | Behaviour |
|---|---|
| `put(x)` | Blocks if full |
| `get()` | Blocks if empty |
| `put(x, timeout=2)` / `get(timeout=2)` | Blocks, then raises `Full`/`Empty` |
| `get_nowait()` | Never blocks; raises `Empty` |
| `task_done()` | Consumer says "finished that one" |
| `join()` | Blocks until every item has been `task_done()`'d |

`join()` works off an `unfinished_tasks` counter — `put` increments, `task_done` decrements, hitting zero wakes the joiners. Note: **`qsize()` is a lie** the moment you read it — another thread may have changed it. Don't branch on it.

## 3.8 Two patterns you'll actually use

**`maxsize` = backpressure.** This is the underrated feature. If the producer reads a 50GB file faster than consumers process it, an unbounded queue eats all your RAM. With `maxsize`, `put()` blocks and the producer is *automatically throttled to consumer speed*. Always set a maxsize.

**Sentinel (poison pill) to shut down.** Blocked consumers need a way out:

```python
for _ in workers:
    q.put(None)          # one sentinel per worker
# worker: if item is None: break
```

Variants: `LifoQueue` (stack), `PriorityQueue` (heap — put `(priority, item)` tuples).

## 3.9 The three queues — don't mix them up

| Queue | Safe across | Blocking style | Cost |
|---|---|---|---|
| `queue.Queue` | threads | `get()` blocks the thread | free (shared memory) |
| `asyncio.Queue` | coroutines, **one thread** | `await q.get()` yields | free |
| `multiprocessing.Queue` | processes | blocks | **pickles + pipes** every item |

WARNING: `asyncio.Queue` is **not thread-safe** and `queue.Queue.get()` **blocks the event loop**. Crossing the thread↔async boundary needs `asyncio.to_thread` or `loop.call_soon_threadsafe` — never a raw call.

## 3.10 When to reach for what

| Situation | Use |
|---|---|
| Many network calls, async libs available | `asyncio` + `TaskGroup`/`gather` |
| Many network calls, sync libs only | `ThreadPoolExecutor` |
| Pipeline: one producer → N workers → collector | `queue.Queue` + threads |
| Need to throttle a fast producer | Queue with `maxsize` |
| Rate-limit an API to N in flight | `Semaphore` (async or threading) |
| Fire-and-forget background job | `create_task` + keep a reference (or `TaskGroup`) |
| Blocking call stuck inside async | `asyncio.to_thread` |
| Heavy pure-Python CPU work | `ProcessPoolExecutor` |
| Just running N independent functions | `ThreadPoolExecutor.map` — skip queues entirely |

**Queue vs. thread pool:** a pool *is* a queue plus workers. Use a pool when you have a list of jobs upfront; use a raw `Queue` when work arrives continuously, workers are long-lived, or you need a multi-stage pipeline (each stage joined by its own queue).

**In FastAPI:** an in-process `Queue` is fine for buffering/logging, but for real background jobs it dies with the process — reach for Celery/RQ/Redis. Same shape, durable.

## 3.11 Compressed

1. **Coroutine is inert; Task is alive.** Concurrency starts at `create_task`/`gather`/`TaskGroup`.
2. **`await` = register a continuation, release the thread.** The loop resumes you when your Future resolves.
3. **The loop runs callbacks to completion** — so one blocking line stalls everything. `to_thread` is the escape hatch.
4. **`queue.Queue` = deque + lock + condition variables.** Blocking is free; waiting threads consume nothing.
5. **`maxsize` is backpressure**, not a limit you set casually.
6. **Sentinels stop workers**; `task_done`/`join` tell you when the work is drained.
7. **Three queues, three worlds** — threads, coroutines, processes. Never cross them by accident.
