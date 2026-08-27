# C++ Interview Notes — Fundamentals, Reordered for Learning

> **How to use this file**
>
> This is ordered **bottom-up**: every topic's prerequisites come before it. Read straight through.
>
> - **⭐** marks a high-frequency interview point or a common trap.
> - **☠️** marks Undefined Behaviour or a bug.
> - Each section ends with a **🎯 Interview** block: the questions actually asked and how to answer.
> - **Part 10** is a reverse index mapping every question from the source question-lists to its section.
> - Code is deliberately minimal — just enough to make the concept concrete.

---

## Table of Contents

**[PART 0 — Language & Type Foundations](#part-0--language--type-foundations)**
0.1 What is C++ · 0.2 Advantages · 0.3 C vs C++ · 0.4 Tokens · 0.5 Data types · 0.6 Is `std::string` primitive · 0.7 Overflow · 0.8 Loops · 0.9 Two starter programs

**[PART 1 — Memory & Indirection](#part-1--memory--indirection)**
1.1 Stack vs heap · 1.2 Pointers · 1.3 Pointer operations · 1.4 `new`/`delete` and `delete[]` · 1.5 References · 1.6 References vs pointers · 1.7 Pass by value vs reference · 1.8 Garbage collection

**[PART 2 — Core Keywords & Scope](#part-2--core-keywords--scope)**
2.1 Storage classes · 2.2 `static` · 2.3 `const` · 2.4 `constexpr` / `consteval` / `constinit` · 2.5 `static_assert` and `if constexpr` · 2.6 `auto` · 2.7 `inline` · 2.8 `volatile` · 2.9 Scope resolution `::` · 2.10 Namespaces

**[PART 3 — Classes: Mechanics](#part-3--classes-mechanics)**
3.1 Class · 3.2 Object · 3.3 `struct` vs `class` · 3.4 Constructors · 3.5 Default constructor · 3.6 Destructor · 3.7 RAII · 3.8 `this` pointer · 3.9 `const` member functions · 3.10 Static members · 3.11 Function overloading · 3.12 Operator overloading

**[PART 4 — The Four Pillars of OOP](#part-4--the-four-pillars-of-oop)**
4.1 What is OOP · 4.2 Encapsulation · 4.3 Access specifiers · 4.4 `friend` · 4.5 Ways to access private fields · 4.6 Abstraction · 4.7 Abstract class, pure virtual, interface · 4.8 Inheritance · 4.9 Multiple inheritance · 4.10 Polymorphism · 4.11 Virtual functions & the vtable · 4.12 Early vs late binding · 4.13 Overriding vs overloading vs hiding · 4.14 Virtual destructor · 4.15 Can a constructor be virtual · 4.16 Virtual call from a constructor · 4.17 Virtual inheritance · 4.18 Diamond problem

**[PART 5 — Value Semantics: Copy, Move, Smart Pointers](#part-5--value-semantics-copy-move-smart-pointers)**
5.1 Copy constructor · 5.2 Rule of 3/5/0 · 5.3 Preventing copying · 5.4 lvalues & rvalues · 5.5 `std::move` / `std::forward` · 5.6 Smart pointers overview · 5.7 `unique_ptr` · 5.8 `shared_ptr` & `weak_ptr` · 5.9 Passing `unique_ptr`

**[PART 6 — Templates](#part-6--templates)**

**[PART 7 — Exceptions & Exception Safety](#part-7--exceptions--exception-safety)**
7.1 Basics & stack unwinding · 7.2 Catch rules · 7.3 Standard hierarchy · 7.4 `noexcept` · 7.5 Safety guarantees · 7.6 Copy-and-swap · 7.7 Exceptions in ctor/dtor · 7.8 Cost model · 7.9 Exceptions & threads

**[PART 8 — STL: Containers, Iterators, Algorithms](#part-8--stl-containers-iterators-algorithms)**
8.1 `vector` vs `list` · 8.2 `map` vs `unordered_map` · 8.3 Iterator invalidation · 8.4 Custom types as keys · 8.5 Iterator categories · 8.6 Member vs free algorithms · 8.7 erase–remove · 8.8 Complexity · 8.9 Comparators · 8.10 Lambdas & `std::function` · 8.11 Insert iterators · 8.12 Ranges · 8.13 Gotchas

**[PART 9 — Master Cheat Sheets](#part-9--master-cheat-sheets)**

**[PART 10 — Question Index](#part-10--question-index)**

**[PART 11 — Not Covered Here](#part-11--not-covered-here)**

---
---

# PART 0 — Language & Type Foundations

---

## 0.1 What is C++?

**C++** is a general-purpose, **compiled**, **statically typed**, **multi-paradigm** language — supporting procedural, object-oriented, generic (template), and functional styles.

Created by **Bjarne Stroustrup** at Bell Labs in **1979** as *"C with Classes"*, renamed C++ in 1983. Standardized as **C++98 → 03 → 11 → 14 → 17 → 20 → 23 → 26**, on a 3-year release cycle.

**⭐ The two design principles that define the language — quote these:**

1. **Zero-overhead principle:** *"What you don't use, you don't pay for. And what you do use, you couldn't hand-code any better."*
2. **Direct mapping to hardware**, with **abstractions that cost nothing at runtime**.

Almost every C++ design decision (why `virtual` is opt-in, why there's no GC, why templates are compile-time) falls out of these two rules.

---

## 0.2 Advantages of C++

- **Performance** — compiles to native machine code. No VM, no interpreter, no GC pauses. Predictable latency.
- **Abstraction without overhead** — templates, inlining, and RAII all resolve at compile time.
- **Manual, deterministic memory control** — essential for games, HFT, embedded, OS kernels, browsers, databases.
- **RAII** — deterministic destruction manages **any** resource (files, sockets, locks, DB connections), not just memory. C++'s single best idea.
- **Multi-paradigm** — pick the right tool per problem.
- **Huge ecosystem + portability** — runs on essentially every platform.

**Be ready for the honest downsides** (interviewers respect this): steep learning curve, enormous surface area, **undefined-behaviour footguns**, slow compile times, no memory safety by default, and historical baggage from C compatibility.

---

## 0.3 C vs C++

| | C | C++ |
|---|---|---|
| Paradigm | Procedural | Multi-paradigm |
| Data & functions | Separate | Bundled in classes |
| Encapsulation / access control | ❌ | ✅ |
| Constructors / destructors | ❌ | ✅ → RAII |
| Overloading | ❌ | ✅ functions + operators |
| Templates / generics | ❌ (macros) | ✅ |
| Exceptions | ❌ (error codes) | ✅ |
| References | ❌ | ✅ |
| Namespaces | ❌ | ✅ |
| Memory | `malloc`/`free` | `new`/`delete` + smart pointers |
| Standard library | small C stdlib | + **STL** (containers, algorithms) |
| I/O | `printf`/`scanf` (not type-safe) | `iostream` (type-safe) |
| Name mangling | ❌ | ✅ (hence `extern "C"`) |

### 🎯 Interview

- **⭐ "Is C++ a superset of C?"** → **No — a common misconception.** It's *mostly* backward-compatible with C89, but not a strict superset:
  - `void*` → `T*` requires an **explicit cast** in C++, implicit in C.
  - `sizeof('a')` is **1 in C++** (`char`) but `sizeof(int)` in C.
  - C++ adds keywords (`class`, `new`, `template`, …) that are valid identifiers in C.
  - C99/C11 features (VLAs, `restrict`, designated initializers pre-C++20) aren't in standard C++.
- **"What is `extern "C"` for?"** → Disables **name mangling** so C code can link against a C++-compiled symbol. Required for C interop and stable ABIs.

---

## 0.4 Tokens

The smallest individual units the compiler recognizes. **Five categories:**

1. **Keywords** — reserved words: `int`, `class`, `return`, `virtual`… (~95 in C++20)
2. **Identifiers** — names you create: `myVar`, `Widget`
3. **Literals** — `42`, `3.14`, `'a'`, `"hi"`, `true`, `nullptr`
4. **Operators** — `+`, `<<`, `->`, `::`, `?:`
5. **Punctuators / separators** — `;`, `{}`, `()`, `,`

*(Whitespace and comments are not tokens — they separate tokens.)*

This is a warm-up question. Answer in 15 seconds and move on.

---

## 0.5 Data types

```
                    C++ types
        ┌──────────────┼──────────────┐
   Fundamental      Derived      User-defined
   ├ void           ├ array      ├ class / struct
   ├ bool           ├ pointer    ├ union
   ├ char family    ├ reference  ├ enum / enum class
   ├ int family     └ function   └ typedef / using
   ├ float family
   └ std::nullptr_t
```

- **Integer family:** `short`, `int`, `long`, `long long` — each `signed` (default) or `unsigned`.
- **Char family:** ⭐ `char`, `signed char`, `unsigned char` are **three distinct types** (unlike `int` / `signed int`, which are the same type). Plus `wchar_t`, `char8_t` (C++20), `char16_t`, `char32_t`.
- **Floating:** `float`, `double`, `long double`.

**⭐ Sizes are NOT fixed by the standard.** Only these guarantees hold:

```
sizeof(char) == 1        (by definition — 1 "byte", which is at least 8 bits)
1 == sizeof(char) <= sizeof(short) <= sizeof(int) <= sizeof(long) <= sizeof(long long)
char >= 8 bits,  short >= 16,  int >= 16,  long >= 32,  long long >= 64
```

**⭐ The detail that impresses:** `long` is **4 bytes on 64-bit Windows (LLP64)** but **8 bytes on 64-bit Linux/macOS (LP64)**. This breaks a lot of supposedly portable code.

**Fix:** use `<cstdint>` when width matters — `int32_t`, `uint64_t`, `int_fast32_t`, `intptr_t`, plus `size_t` and `ptrdiff_t`.

### 🎯 Interview

- **⭐ "Is `char` signed or unsigned?"** → **Implementation-defined!** Signed on x86 GCC/MSVC, **unsigned on ARM**. A genuine source of portability bugs. Use `signed char` / `unsigned char` explicitly when it matters.
- **"`enum` vs `enum class`?"** → Plain `enum` leaks its enumerators into the enclosing scope and implicitly converts to `int`. **`enum class`** (C++11) is scoped and strongly typed — no implicit conversion, no name collisions. Always prefer it.
- **"Why is `sizeof(bool)` 1 and not 1 bit?"** → Bytes are the smallest addressable unit; you cannot take the address of a bit.
- **"Struct size and padding?"** → Members are aligned to their natural boundaries; the compiler inserts padding, and the struct's total size is a multiple of its largest member alignment. **Reordering members largest → smallest reduces size.** Common follow-up.

---

## 0.6 Is `std::string` a primitive type?

**No.** C++ has **no built-in string type.** The built-in representation is a **null-terminated `char` array** (`char*` / `char[]`) inherited from C.

`std::string` is a **class** from the standard library — specifically `std::basic_string<char>`. It manages a heap buffer (with **SSO**, Small String Optimization, storing short strings inline with no allocation) and provides `size()`, concatenation, comparison, and automatic memory management.

```cpp
const char* c = "hello";      // primitive-ish: pointer to a static char array
std::string s = "hello";      // a class object with a destructor
```

**Follow-up worth knowing:** `std::string_view` (C++17) is a **non-owning view** (pointer + length). Use it for read-only string parameters to avoid copies — but beware **dangling** if the underlying string dies first.

---

## 0.7 Overflow error ⭐⭐

Overflow = a value exceeds what its type can represent. C++ treats signed and unsigned **completely differently**, and this is where the interview goes.

```cpp
int x = INT_MAX;
x + 1;                  // ☠️ SIGNED overflow = UNDEFINED BEHAVIOUR

unsigned int u = UINT_MAX;
u + 1;                  // ✅ WELL-DEFINED: wraps to 0 (modulo 2^n)
```

**⭐ Why signed overflow being UB actually matters** — the compiler is allowed to *assume it never happens*:

```cpp
if (x + 1 > x)  { ... }           // compiler may optimize this to `if (true)`
for (int i = 0; i <= n; ++i)      // may be assumed never to wrap → infinite loop possible
```

So the bug isn't "you get a wrong number" — it's that **the compiler deletes your safety check**. That's the answer they're looking for.

**The three "overflows" — don't confuse them:**

| Term | Meaning |
|---|---|
| **Integer overflow** | Arithmetic exceeds the type's range |
| **Stack overflow** | Call stack exhausted (deep recursion / huge locals) → crash |
| **Buffer overflow** | Writing past an array's end → memory corruption, **security vulnerability** |

**Classic bugs to be able to name:**

```cpp
int mid = (low + high) / 2;        // ☠️ overflows for large indices (the famous binary-search bug)
int mid = low + (high - low) / 2;  // ✅ safe

for (size_t i = v.size() - 1; i >= 0; --i)   // ☠️ size_t is UNSIGNED
    // if v is empty: 0-1 wraps to SIZE_MAX; and i >= 0 is ALWAYS true → infinite loop

if (v.size() - 1 < someInt)        // ☠️ signed/unsigned comparison — int converted to unsigned
```

**Detection / prevention:** compare against `std::numeric_limits<T>::max()` *before* the operation · `__builtin_add_overflow` (GCC/Clang) · `-ftrapv` · **UBSan** (`-fsanitize=undefined`) · C++20 `std::cmp_less` and friends for safe signed/unsigned comparison · use a wider type.

### 🎯 Interview

- **⭐ "Is unsigned overflow UB?"** → **No** — it is defined to wrap modulo 2ⁿ. Only **signed** overflow is UB. Very frequently asked, very frequently missed.
- **"Why does `-1 < v.size()` evaluate to false?"** → The usual arithmetic conversions promote `-1` to a huge `size_t`. Compile with `-Wsign-compare`.
- **"What is UB and why does it exist?"** → Behaviour on which the standard imposes **no requirements**. It exists to permit aggressive optimization and to avoid mandating runtime checks (zero-overhead principle). Practical consequence: UB isn't "a random result" — it can "time-travel" and delete surrounding code.

---

## 0.8 Loops

```cpp
for (int i = 0; i < n; ++i) { }        // counting
while (cond) { }                        // condition checked first
do { } while (cond);                    // body runs AT LEAST ONCE
for (const auto& x : container) { }     // ⭐ range-based for (C++11)
```

**Range-based `for`** is the one worth discussing. It works with anything providing `begin()`/`end()` — containers, raw arrays, initializer lists, and your own types.

**⭐ The trap:**

```cpp
for (auto x : bigVector)        // ☠️ COPIES every element
for (const auto& x : bigVector) // ✅ read-only, no copy
for (auto& x : bigVector)       // ✅ modify in place
```

**Rule: `const auto&` by default; `auto&` when you need to modify.**

Also: `break`, `continue`, `goto` (avoid), and C++20's `for (init; decl : range)`.

---

## 0.9 Two starter programs

```cpp
#include <iostream>
#include <string>

int main() {
    std::cout << "Hello world\n";

    std::string s;
    std::getline(std::cin, s);          // ⭐ reads a whole line, INCLUDING spaces
    std::cout << "Length: " << s.length() << "\n";   // O(1); size() is identical
}
```

**These are really "do you know the idioms?" tests:**

- **⭐ `std::getline(std::cin, s)` vs `std::cin >> s`** → `>>` stops at **whitespace**; `getline` reads the whole line. Mixing them requires `std::cin.ignore()` to discard the leftover newline. Very common trap.
- **⭐ `"\n"` vs `std::endl`** → `endl` **also flushes the stream** — needless and slow inside loops. Prefer `'\n'`.
- **`length()` vs `size()`** → Identical for `std::string`; `size()` is the generic-container spelling.
- **`strlen` vs `.length()`** → `strlen` is **O(n)** (scans for `\0`); `std::string::length()` is **O(1)** (stored).
- **`main` returns `int`**, and `return 0;` is implicit **for `main` only**. `void main()` is not standard C++.

---
---

# PART 1 — Memory & Indirection

---

## 1.1 Stack vs heap allocation

Two of C++'s **storage durations**. (There are four: automatic, dynamic, static, thread-local.)

```cpp
void f() {
    int x = 42;                       // STACK — automatic storage
    std::array<int, 100> arr;         // STACK — size fixed at compile time

    int* p = new int(42);             // HEAP — dynamic storage
    std::vector<int> v(1000);         // vector OBJECT on stack, its BUFFER on heap
    delete p;
}                                     // x, arr, v destroyed automatically here
```

| | Stack | Heap (free store) |
|---|---|---|
| Managed by | Compiler, automatically | You (or a smart pointer) |
| Allocation cost | ~1 instruction (move the stack pointer) | Expensive — allocator bookkeeping, possible lock/syscall |
| Lifetime | Ends at scope exit | Until `delete` |
| Size | **Small & fixed** (~1 MB/thread on Windows, ~8 MB on Linux) | Large — limited by RAM/address space |
| Size known at | Compile time | Runtime |
| Layout | Contiguous LIFO → **cache-friendly** | Scattered → cache misses |
| Fragmentation | Impossible | Possible |
| Failure mode | **Stack overflow** (crash) | `std::bad_alloc` / leak / dangling |
| Thread-safety | Each thread has its own | Shared → allocator must synchronize |

**Why the heap exists:** you need it when (a) the size isn't known until runtime, (b) the object must **outlive the scope** that created it, or (c) it's too big for the stack.

**⭐ Key insight:** the stack isn't just "faster allocation" — it's faster because the hot region of the stack is essentially always in L1 cache, and deallocation is free (one pointer move).

### 🎯 Interview

- **⭐ "Default preference?"** → **Stack by default.** Use the heap only when required — and then through **RAII** (`std::vector`, `std::string`, `std::unique_ptr`), never bare `new`/`delete`.
- **"Where do globals/statics live?"** → Neither stack nor heap — the **static storage** area (`.data` for initialized, `.bss` for zero-initialized), allocated for the whole program duration.
- **"What causes a stack overflow?"** → Deep/infinite recursion, or a huge local array (`int buf[10'000'000];`).
- **Pedantic bonus:** the standard says "**free store**" for `new`/`delete` and "**heap**" for `malloc`/`free`; usually the same memory, but they need not be.

---

## 1.2 What is a pointer?

A **pointer** is a variable whose value is a **memory address**. It has its own storage, its own address, and can be reassigned.

```cpp
int x = 42;
int* p = &x;      // p holds x's address
*p = 100;         // dereference — writes through the pointer; x is now 100
p = nullptr;      // pointers can be null
```

**Kinds to be able to name:** null pointer (`nullptr`), **dangling** pointer (points to freed memory), **wild** pointer (uninitialized), `void*` (typeless, must cast before use), function pointer, pointer-to-member.

**⭐ Use `nullptr`, not `NULL` or `0`.** `NULL` is `0`, which is an `int` — so `f(NULL)` may call `f(int)` instead of `f(char*)`. `nullptr` has its own type, `std::nullptr_t`, and resolves correctly.

---

## 1.3 Operations permitted on pointers

```cpp
int arr[5] = {10,20,30,40,50};
int* p = arr;              // ✅ assignment (array decays to pointer)
*p;                        // ✅ dereference → 10
&p;                        // ✅ address-of the pointer itself (int**)
p++;                       // ✅ increment → now points to arr[1]
p = p + 3;                 // ✅ pointer + integer
int* q = &arr[4];
q - p;                     // ✅ pointer - pointer → ptrdiff_t (ELEMENT count, not bytes!)
p < q;                     // ✅ relational comparison (same array only)
p == nullptr;              // ✅ equality
```

**❌ NOT allowed:** `p + q` (adding two pointers), `p * 2`, `p / 2`, `p % 2`, `p & q`, `p + 1.5`.

**⭐ Pointer arithmetic scales by `sizeof(T)`:**

```cpp
int* p = arr;      // suppose address 0x1000
p + 1;             // → 0x1004  (advances 4 bytes, not 1)

char* c = (char*)arr;
c + 1;             // → 0x1001
```

**Validity rules (UB territory):**

- The valid range is `[begin, one-past-the-end]`. You may **form** the one-past-end pointer but **must not dereference** it.
- Going further, or comparing with `<` pointers into *different* arrays, is **UB**.
- `void*` cannot be dereferenced and has no arithmetic (GCC extension pretends element size 1).
- Arithmetic on a `nullptr` is UB.

### 🎯 Interview

- **"Difference between `p++`, `++p`, `*p++`, `(*p)++`?"** → `*p++` = dereference, then advance the *pointer*. `(*p)++` = increment the *value*. Precedence question; common in written tests.
- **"Dangling pointer / wild pointer / memory leak?"** → Dangling = points to freed memory. Wild = uninitialized. Leak = allocated but no pointer to it remains. Fix all three with RAII + always initializing to `nullptr`.
- **⭐ "Array vs pointer?"** → An array **is not** a pointer; it *decays* to one in most expressions. `sizeof(arr)` = 20, `sizeof(p)` = 8. An array name isn't assignable. Decay **loses the size** — which is why `void f(int a[])` is really `void f(int* a)`.
- **"Function pointer?"** → `int (*fp)(int, int) = &add;` — be ready to read the declaration. Modern replacement: lambdas / `std::function`.

---

## 1.4 `new` / `delete`, and `delete` vs `delete[]`

```cpp
int* a = new int(5);        // →  delete a;      single object
int* b = new int[100];      // →  delete[] b;    array
```

**They must match `new` vs `new[]`. Mismatching is Undefined Behaviour.**

**⭐ Why they're different:** `delete[]` must run the **destructor for every element**, so it needs the element count. Implementations store that count in a hidden "**cookie**" just before the returned block, and `delete[]` looks backwards to find it. Calling plain `delete` on that pointer:

- destroys only the first element (the other 99 destructors never run → resource leaks), **and**
- frees the wrong address (the cookie offset is missed) → **heap corruption**.

For `int` (no destructor) it often *appears* to work — still UB, still able to corrupt the heap.

**`new`/`delete` vs `malloc`/`free`:**

| | `new`/`delete` | `malloc`/`free` |
|---|---|---|
| Calls ctor/dtor | ✅ | ❌ |
| Return type | typed `T*` | `void*` (needs a cast) |
| Size | computed automatically | you compute it |
| On failure | throws `std::bad_alloc` | returns `NULL` |
| Overloadable | ✅ (`operator new`) | ❌ |

**Never mix them** — `free()` on a `new`'d pointer is UB.

**Modern answer to give:** *"I don't write either. `std::vector<T>` for arrays, `std::make_unique<T>()` for single objects, `std::make_unique<T[]>(n)` if I truly need a raw array."*

---

## 1.5 Reference variables

A **reference** is an **alias** — another name for an existing object. Not an object itself.

```cpp
int a = 10;
int& r = a;       // r IS a
r = 20;           // a is now 20
```

Types: **lvalue reference** `T&`, **const lvalue reference** `const T&`, **rvalue reference** `T&&` (see 5.4).

**Why references exist:** C-style code passed pointers just to avoid copies, which forced null checks and `*`/`->` noise everywhere. References give pass-by-reference with a **non-null guarantee** and clean syntax. They also make **operator overloading** possible (`a = b` returning `T&`) and enable **move semantics** (`T&&`).

**Under the hood** a reference is usually compiled as a pointer — but the standard doesn't require storage, and the compiler often optimizes it away entirely.

---

## 1.6 References vs pointers

```cpp
int a = 10, b = 20;

int* p = &a;   // p is an object holding a's address
p = &b;        // ✅ reseat: p now points to b
p = nullptr;   // ✅ can be null

int& r = a;    // r IS a, forever
r = b;         // ⚠️ NOT reseating — this assigns b's VALUE into a
// int& r2;    // ❌ must be initialized
```

| | Pointer | Reference |
|---|---|---|
| Must be initialized | ❌ | ✅ always |
| Can be null | ✅ | ❌ (a "null reference" is UB) |
| Can be reseated | ✅ | ❌ |
| Arithmetic (`p++`) | ✅ | ❌ |
| Address-of | `&p` = the pointer's own address | `&r` = address of the **referent** |
| `sizeof` | size of a pointer | size of the **referent** |
| Arrays of them | ✅ `int* a[10]` | ❌ no array of references |
| Levels of indirection | `int**` ✅ | `int& &` ❌ (only via reference collapsing) |
| Needs deref syntax | `*p`, `p->` | no — use directly |

### 🎯 Interview

- **⭐ "Which do you prefer and when?"** The practical rule:
  - Parameter that **must** exist → **reference** (`const T&` read-only, `T&` out-param).
  - Parameter that **may be absent** → **pointer** (`nullptr` = "none") or `std::optional`.
  - **Ownership** → **smart pointer**, never a raw owning pointer.
  - Reseating / iteration / arrays → pointer.
- **⭐ "Is `int& r = *ptr;` safe when `ptr` is null?"** → **No, UB immediately** at the dereference, even though nothing crashes yet. Never say "a null reference".
- **"Can you have a reference member in a class?"** → Yes, but it must be initialized in the **member initializer list**, and it makes the class **non-assignable** (the implicit `operator=` is deleted, since references can't be reseated). Usually a pointer or `std::reference_wrapper` is better.
- **⭐ "`const T&` and temporaries"** → `const T&` **binds to temporaries and extends their lifetime** to the reference's lifetime. Non-const `T&` does not bind to a temporary at all.
- **"Dangling reference?"** → A reference to a destroyed object — classic bug: returning a reference to a local.

---

## 1.7 Pass by value vs pass by reference

```cpp
void byValue(int x)        { x = 100; }   // copies; caller unaffected
void byReference(int& x)   { x = 100; }   // caller's variable IS modified
void byPointer(int* x)     { *x = 100; }  // same, but explicit & nullable
void byConstRef(const BigObject& o);      // no copy, no modification ← the workhorse
```

| | By value | By reference | By pointer |
|---|---|---|---|
| Copies the argument | ✅ | ❌ | ❌ (copies the pointer) |
| Can modify caller's object | ❌ | ✅ | ✅ |
| Can be null / optional | ❌ | ❌ | ✅ |
| Syntax at call site | `f(x)` | `f(x)` — **invisible!** | `f(&x)` — visible |
| Best for | small cheap types | large objects, out-params | optional params, C APIs |

**⭐ Modern parameter-passing guidelines — what a senior interviewer wants:**

| Situation | Signature |
|---|---|
| Small/cheap type (`int`, `double`, pointer, `string_view`) | **by value** `f(int)` |
| Large object, read-only | **`const T&`** |
| Need to modify the caller's object | **`T&`** |
| The function will **store/keep a copy** ("sink" param) | **by value + `std::move`** |
| Optional argument | `const T*` or `std::optional<T>` |
| Perfect-forwarding template | `T&&` + `std::forward` |

```cpp
class Person {
    std::string name_;
public:
    Person(std::string n) : name_(std::move(n)) {}   // sink: by value + move
};
```

### 🎯 Interview

- **⭐ "Is C++ pass-by-value or pass-by-reference?"** → **Everything is pass-by-value at the core** — even a reference parameter conceptually passes the address by value. But at *language level*, references give true reference semantics. Say both halves.
- **"What happens when you pass an array?"** → It **decays to a pointer**; the size is lost. That's why you also pass a length, or use `std::span` (C++20) / `std::array` / `std::vector`.
- **"Why is `const T&` not always best?"** → For small types the indirection costs more than the copy; and for a **sink parameter**, by-value + move is faster (one move instead of a copy).

---

## 1.8 Does C++ have garbage collection?

**No** — C++ has **no automatic garbage collector**. Memory management is manual, but modern C++ makes it automatic *and deterministic* via **RAII** and smart pointers.

**⭐ Why C++ deliberately rejects GC** (this is the real question):

1. **Determinism** — you know *exactly* when a destructor runs. A GC gives no timing guarantee.
2. **RAII generalizes beyond memory** — destructors release **files, sockets, mutexes, DB connections**. A GC only reclaims memory, so those need `try/finally` or `using` blocks in GC languages.
3. **No pause times** — critical for games, HFT, real-time, embedded.
4. **Zero-overhead principle** — you shouldn't pay for a GC you didn't ask for.
5. **Memory footprint** — GC heaps typically need 2–5× the live-set size.

```cpp
{
    std::lock_guard<std::mutex> lk(m);        // acquired
    auto p = std::make_unique<Widget>();
}   // ← lock released and Widget deleted RIGHT HERE, deterministically
```

**Historical footnote worth dropping:** C++11 added a minimal GC-support API (`std::declare_reachable`, `std::pointer_safety`), which **nobody implemented** and which was **removed in C++23**.

**Follow-up: "So how do you avoid leaks?"** → RAII, `unique_ptr`/`shared_ptr`, containers instead of raw arrays, the **Rule of Zero**, and tools: **Valgrind, AddressSanitizer (`-fsanitize=address`), LeakSanitizer**.

---
---

# PART 2 — Core Keywords & Scope

These keywords answer **different questions** and are constantly confused:

| Keyword | The question it answers |
|---|---|
| `const` | *"Can I **modify** it?"* → read-only |
| `constexpr` | *"**Can** it be computed at compile time?"* → permitted, not required |
| `consteval` | *"**Must** it be computed at compile time?"* → mandatory (C++20) |
| `constinit` | *"Is it **initialized** at compile time?"* → but still mutable (C++20) |
| `static` | *"Where does it **live**, and who can **see** it?"* → storage duration + linkage |
| `volatile` | *"Can it change **behind the compiler's back**?"* → don't optimize accesses |
| `inline` | *"May this be **defined in multiple TUs**?"* → ODR relaxation (+ an inlining hint) |
| `auto` | *"What **type** is this?"* → deduce it from the initializer |

---

## 2.1 Storage classes

A storage class specifies **storage duration** (how long it lives) and **linkage** (how it's seen across translation units).

| Keyword | Effect | Status |
|---|---|---|
| `auto` | Automatic storage (the default) | ⚠️ **Repurposed in C++11** for type deduction — no longer a storage class |
| `register` | Hint to keep in a CPU register | ☠️ Deprecated C++11, **removed in C++17** (still a reserved word) |
| `static` | Static storage duration; internal linkage at namespace scope | ✅ |
| `extern` | Declares something defined in another TU (external linkage) | ✅ |
| `mutable` | Member modifiable inside a `const` method | ✅ |
| `thread_local` | One instance **per thread** (C++11) | ✅ |

```cpp
int counter() {
    static int n = 0;      // persists across calls, initialized ONCE
    return ++n;
}

static int fileLocal = 5;  // internal linkage — invisible to other .cpp files
extern int globalCfg;      // declaration; defined in another .cpp

thread_local int tlsId;    // each thread gets its own copy
```

**⭐ The depth answer — separate three orthogonal concepts:**

| Concept | Question it answers | Values |
|---|---|---|
| **Storage duration** | How long does it live? | automatic, static, thread, dynamic |
| **Linkage** | Can other TUs see this name? | none, internal, external |
| **Scope** | Where is the name visible? | block, function, class, namespace, global |

---

## 2.2 `static` — four different meanings

The most overloaded keyword in C++. **Name the context, then the meaning:**

```cpp
// 1. NAMESPACE/FILE SCOPE → internal linkage (file-private)
static int fileLocal = 5;              // invisible to other translation units
static void helper();

// 2. LOCAL VARIABLE → static storage duration
int counter() {
    static int n = 0;                  // initialized ONCE, on first execution; persists
    return ++n;                        // ⭐ thread-safe init guaranteed since C++11
}

// 3. CLASS DATA MEMBER → one shared copy, belongs to the class
class Widget {
    static int count_;                 // declaration
    static inline int total_ = 0;      // ⭐ C++17: declare + define in the header
    static constexpr int kMax = 100;   // implicitly inline since C++17
};
int Widget::count_ = 0;                // definition — in exactly one .cpp

// 4. CLASS MEMBER FUNCTION → no `this`, callable without an object
class Widget { public: static Widget create(); };
Widget::create();
```

Case 1 changes **linkage**. Case 2 changes **storage duration**. Cases 3–4 change **ownership** (class vs object).

### ⭐ Is a static field initialized in the constructor? — **No.**

A static data member belongs to the **class**, not to any object. There is exactly **one** copy, with **static storage duration** — it exists before any object is created and after all are destroyed. The constructor runs **per object**, so it cannot be where a per-class entity is initialized.

```cpp
class Counter {
    static int count_;            // DECLARATION only — no storage yet
public:
    Counter() { ++count_; }       // ✅ the ctor may MODIFY it...
    // static int count_ = 0;     // ❌ ...but cannot INITIALIZE it (pre-C++17)
};

int Counter::count_ = 0;          // ✅ DEFINITION — in exactly one .cpp file
```

**When does it actually get initialized?**
- **Zero-initialized** first (before anything else runs).
- Then **constant-initialized** at compile time if the initializer is a constant expression.
- Otherwise **dynamically initialized** before `main()` — but the order *across translation units is unspecified* → the **static initialization order fiasco**.

**Modern alternatives:**
```cpp
class C {
    static inline int count_ = 0;              // ✅ C++17 — declare + define in the header
    static constexpr int kMax = 100;           // ✅ constant, implicitly inline since C++17
};
```

### 🎯 Interview

- **⭐ "Why must a static member be defined outside the class?"** → The in-class line is a **declaration**; headers are included in many TUs, so defining storage there would violate the ODR. (C++17 `inline` solves this.)
- **⭐ "What's the static initialization order fiasco, and how do you fix it?"** → Statics in *different* translation units are initialized in an **unspecified order**, so one can be used before it's constructed. Fix with the **Construct-On-First-Use / Meyers Singleton** idiom:
  ```cpp
  Logger& logger() {
      static Logger instance;   // initialized on FIRST CALL, thread-safe since C++11
      return instance;
  }
  ```
- **⭐ "When is a function-local `static` initialized, and is it thread-safe?"** → Lazily, on **first execution** of the declaration; **thread-safe since C++11** ("magic statics" — the compiler emits a guard variable). Hidden cost: a guard check on every call (usually a cheap load).
- **"Can a static member function access non-static members?"** → No — it has no `this`. It can access other statics, or non-statics of an object explicitly passed in.
- **"Can a static member be `const`? `virtual`?"** → `const` ✅. `virtual` ❌ (no object → no dynamic dispatch).
- **"Can a static member be of the same type as its class?"** → ✅ Yes (`static Widget instance;`), because it isn't part of the object layout — unlike a non-static member, which would be infinitely recursive.
- **"`static` vs anonymous namespace for file-local?"** → Both give internal linkage; the **anonymous namespace is preferred** in C++ (it also works for types).

---

## 2.3 `const`

`const` means **"this name may not be used to modify the object."** A *compile-time* restriction on the name, not a property of the memory.

**Read declarations right-to-left:**

```cpp
const int  x = 5;          // x is a const int
int const  y = 5;          // ✅ identical — const binds left, unless it's leftmost

const int* p;              // pointer to const int  → *p is read-only, p is not
int* const q = &n;         // const pointer to int  → q is read-only, *q is not
const int* const r = &n;   // both read-only
```

**⭐ Top-level vs low-level `const`** — this distinction drives `auto` and template deduction:

- **Top-level** = the object *itself* is const (`const int x`, `int* const p`)
- **Low-level** = what it *points to* is const (`const int* p`)
- **Rule:** top-level const is **stripped** in copy-initialization and by `auto`/template deduction; low-level const is **preserved**.

```cpp
const int ci = 10;
auto a = ci;        // int  — top-level const dropped
const int* pci = &ci;
auto b = pci;       // const int* — low-level const KEPT
```

**Where `const` appears:**

```cpp
const int SIZE = 10;                       // 1. const variable
void f(const std::string& s);              // 2. const parameter (no modification, no copy)
int  get() const;                          // 3. const member function (see 3.9)
const int& ref();                          // 4. const return
class C { const int id_; };                // 5. const member — MUST use init list; class not assignable
static const int kMax = 100;               // 6. class constant
```

**`const_cast` — and the rule that gets tested:**

```cpp
void legacy(char* s);                       // old API, doesn't modify but isn't const-correct
void wrapper(const std::string& s) {
    legacy(const_cast<char*>(s.c_str()));   // ✅ legal IF legacy really doesn't write
}

const int ci = 10;
int* p = const_cast<int*>(&ci);
*p = 20;                                    // ☠️ UNDEFINED BEHAVIOUR
```

> **⭐ The rule:** `const_cast` + modify is legal **only** if the object was **not originally declared `const`**. Modifying a genuinely-const object through a cast is UB — the compiler may have placed it in read-only memory or constant-folded its value.

**Physical (bitwise) vs logical constness:** the compiler enforces **bitwise**; what you want is **logical**. `mutable` bridges the gap (caches, mutexes). A `T* member` is the hole in the other direction — a const method can freely modify `*member`.

### 🎯 Interview

- **"Why is const-correctness worth it?"** → Compile-time bug prevention, self-documenting APIs, enables optimizations, and — practically — **it's viral**: retrofitting `const` into a large codebase is painful, so do it from day one.
- **"What's `const` return-by-value good for?"** → Mostly a legacy trick (`const T operator+`) to prevent `(a+b) = c`. Today it's discouraged because it **blocks move semantics**.

---

## 2.4 `constexpr`, `consteval`, `constinit`

### `constexpr` — computable at compile time

**On a variable:** the value **must** be a compile-time constant. Stronger than `const`.

```cpp
const     int a = getRuntimeValue();   // ✅ runtime-initialized, then read-only
constexpr int b = getRuntimeValue();   // ❌ ERROR — must be a constant expression
constexpr int c = 5 * 10;              // ✅

int arr[c];                            // ✅ needs a compile-time constant
template<int N> struct S {};  S<c> s;  // ✅ template argument
static_assert(c == 50);                // ✅
```

**On a function:** *"this **may** be evaluated at compile time — **if** the arguments are constant expressions."*

```cpp
constexpr int factorial(int n) {
    return (n <= 1) ? 1 : n * factorial(n - 1);
}

constexpr int x = factorial(5);   // ✅ computed AT COMPILE TIME → 120 baked in
int n = readInput();
int y = factorial(n);             // ✅ same function, runs at RUNTIME
```

> **⭐ THE most common misconception: `constexpr` does NOT guarantee compile-time evaluation.** It only means the function is *eligible*. Force it by assigning to a `constexpr` variable, or use `consteval`.

**⭐ Pointer trap — `constexpr` is always top-level:**

```cpp
const     int* p1 = &x;   // pointer to const int
constexpr int* p2 = &x;   // int* const — const applies to the POINTER, not the pointee!
```

**What `constexpr` functions may contain — it has loosened enormously:**

| Standard | Allowed |
|---|---|
| **C++11** | Essentially a **single `return`** statement. Very restrictive. |
| **C++14** | Loops, `if`, local variables, multiple statements, mutation of locals. |
| **C++17** | `if constexpr`; lambdas implicitly `constexpr`. |
| **C++20** | `try`/`catch`, **`virtual`** calls, **dynamic allocation** (must be freed in the same evaluation), `std::vector`/`std::string`, `constexpr` destructors. |
| **C++23** | `static` locals, `goto`, non-literal variables, more. |

**Other facts:** `constexpr` functions are **implicitly `inline`** · `constexpr` **constructors** let you build compile-time objects (of *literal types*) · a `constexpr` member function is **not** implicitly `const` (that was true only in C++11).

### `consteval` (C++20) — immediate functions

**"Must be evaluated at compile time, always."** Calling it with runtime arguments is a **compile error**.

```cpp
consteval int square(int n) { return n * n; }

constexpr int a = square(5);   // ✅ 25, at compile time
int n = readInput();
int b = square(n);             // ❌ COMPILE ERROR — not a constant expression
```

**Use it when** compile-time evaluation is a *correctness requirement*, not an optimization — e.g. compile-time format-string validation (`std::format` uses this), compile-time hashing, generating type metadata. It guarantees **zero code is ever emitted** for the function.

### `constinit` (C++20) — guaranteed static initialization

**"This static/thread-local variable must be initialized at compile time — but it stays mutable."**

```cpp
constinit int counter = 0;      // guaranteed constant-initialized, but modifiable
int main() { counter++; }       // ✅ legal — constinit is NOT const

constinit int bad = getRuntime();  // ❌ error — not constant-initialized
```

**Why it exists:** it **eliminates the static initialization order fiasco** for that variable by proving at compile time that no dynamic initialization is needed. `const`/`constexpr` would also fix the fiasco but forbid mutation. `constinit` gives the guarantee **without** immutability.

| | Compile-time init? | Mutable? |
|---|---|---|
| `const` | not necessarily | ❌ |
| `constexpr` | ✅ guaranteed | ❌ |
| `constinit` | ✅ guaranteed | ✅ |

### The comparison summary

```cpp
const     int a = f();      // runtime OK, read-only
constexpr int b = 42;       // compile-time required, read-only
constinit int c = 42;       // compile-time init required, MUTABLE  (static/thread only)
consteval int g() { ... }   // function: compile-time ONLY
constexpr int h() { ... }   // function: compile-time IF possible, else runtime
static    int d;            // storage duration / linkage — orthogonal to all the above
```

### 🎯 Interview

- **⭐ "`const` vs `constexpr`?"** → `const` = **read-only**, may be initialized at runtime. `constexpr` = **compile-time constant**, and implies `const` for variables. Every `constexpr` variable is `const`; not every `const` is `constexpr`.
- **⭐ "Does a `constexpr` function always run at compile time?"** → **No.** Only in a constant-expression context with constant arguments. Use `consteval` to force it.
- **⭐ "`#define` vs `const` vs `constexpr`?"**
  | | `#define` | `const` | `constexpr` |
  |---|---|---|---|
  | Type-safe | ❌ | ✅ | ✅ |
  | Scoped | ❌ (global text) | ✅ | ✅ |
  | Visible to debugger | ❌ | ✅ | ✅ |
  | Usable as array size / template arg | ✅ | only if const-int-initialized by a constant | ✅ always |

  **Never use `#define` for constants.**
- **"When must you use `constexpr` rather than `const`?"** → Array sizes, template non-type arguments, `static_assert`, `case` labels, bitfield widths, `enum` initializers, `alignas`.
- **"Doesn't the optimizer already do this?"** → It *may*; `constexpr` **guarantees** it and makes the value usable where the language *requires* a constant. It also moves errors from runtime to compile time.

---

## 2.5 `static_assert` and `if constexpr`

```cpp
static_assert(sizeof(int) == 4, "unexpected int size");   // compile-time assertion, C++11
                                                          // message optional since C++17

template <typename T>
void print(T v) {
    if constexpr (std::is_pointer_v<T>)   // ⭐ C++17: the FALSE branch is DISCARDED,
        std::cout << *v;                  //    not just skipped — it needn't even compile
    else
        std::cout << v;
}
```

`if constexpr` replaced most tag-dispatch and SFINAE boilerplate — a strong modern-C++ signal.

---

## 2.6 `auto`

**Then and now:** in C++98 `auto` meant "automatic storage duration" — the default, so it was useless. **C++11 repurposed it** for **type deduction**: the compiler infers the type from the initializer.

```cpp
auto i = 42;                   // int
auto d = 3.14;                 // double
auto s = std::string("hi");    // std::string
auto it = v.begin();           // std::vector<int>::iterator — huge win
auto lam = [](int x){ return x*2; };   // lambda type is UNNAMEABLE — auto is mandatory
```

**⭐ Deduction rules (where the questions are):** `auto` uses **template argument deduction**, which **strips references and top-level `const`**.

```cpp
const std::vector<int> v{1,2,3};
auto        a = v;      // std::vector<int>        — a COPY, const stripped!
auto&       b = v;      // const std::vector<int>& — reference, const preserved
const auto& c = v;      // const std::vector<int>&
auto&&      d = v;      // forwarding reference — binds to anything
```

**⭐ The classic bug it prevents:**

```cpp
std::map<std::string, int> m;

for (const std::pair<std::string, int>& p : m)   // ☠️ SILENT COPY every iteration!
                                                  //    real type is pair<const string, int>
for (const auto& p : m)                           // ✅ no copy
```

**Related forms:**
- **`decltype(auto)`** (C++14) — deduces *exactly*, **preserving** references and const. For perfect-forwarding return types.
- **Return type deduction** (C++14): `auto f() { return 42; }`
- **Trailing return type:** `auto f(int x) -> int`
- **Generic lambdas** (C++14): `[](auto x){ ... }`
- **Structured bindings** (C++17): `auto [key, value] = *m.begin();`
- **Abbreviated function templates** (C++20): `void f(auto x)`

**Restrictions:** must be initialized · can't be a non-static data member · can't be a function parameter before C++20 · **`auto x = {1,2,3}` deduces `std::initializer_list<int>`**, a special-case exception to the template rules.

### 🎯 Interview

- **⭐ "Does `auto` hurt readability?"** → Balanced answer: use it when the type is **obvious from the initializer** (`auto p = std::make_unique<Widget>()`), **unnameable** (lambdas), or **noisy** (iterators). Spell out the type when it carries real information. Mention **"Almost Always Auto" (Herb Sutter)** and that it's debated.
- **⭐ "Does `auto` slow anything down?"** → **No.** Pure compile-time deduction — zero runtime cost. Static typing is fully preserved; this is not dynamic typing.
- **"`auto` vs `decltype`?"** → `auto` deduces from an initializer and decays; `decltype(expr)` reports the **declared type** of an expression exactly, including references. `decltype((x))` with extra parens yields `int&` — famous puzzle.

---

## 2.7 `inline` functions

**The textbook answer:** `inline` requests that the compiler replace the call with the function body, eliminating call overhead (stack frame setup, jump, return).

```cpp
inline int square(int x) { return x * x; }
int y = square(5);            // may become:  int y = 5 * 5;
```

**⭐ The modern, correct answer — and the one that separates candidates:**

> **`inline` today is primarily about the One Definition Rule, not optimization.**

Two independent facts:

1. **`inline` is only a *hint* for optimization** — the compiler freely ignores it, and just as freely inlines functions you never marked (especially with LTO). It has no obligation either way.
2. **What `inline` actually *guarantees***: the function may be **defined in multiple translation units** without an ODR violation; the linker merges the definitions. **This is why you put function definitions in headers.** A hard language rule, not a hint.

**Implicitly inline:**
- Functions **defined inside a class body**
- `constexpr` functions
- `inline` **variables** (C++17) — how you put a global in a header
- (Templates have an analogous ODR relaxation)

**Trade-offs:**

| Pros | Cons |
|---|---|
| No call overhead | **Code bloat** → worse I-cache → can be *slower* |
| Enables further optimization (constant folding across the boundary) | Longer compile times |
| | **Every caller must recompile** when the body changes |

**Poor candidates for inlining:** large functions, recursive functions, virtual calls through a base pointer (target unknown unless devirtualized).

### 🎯 Interview

- **⭐ "Does `inline` guarantee inlining?"** → **No.** It's a suggestion; the ODR effect is the guarantee. Compiler-specific force: `__forceinline` (MSVC), `__attribute__((always_inline))` (GCC).
- **⭐ "Inline function vs macro?"** → Inline functions are **type-safe**, **scoped**, evaluate arguments once (`MAX(i++, j++)` is the classic macro bug), and are **debuggable**. Always prefer `inline`/`constexpr`/templates over function-like macros.
- **"Why do template definitions go in headers?"** → Same ODR reasoning — the compiler needs the definition at every instantiation point.
- **"`constexpr` vs `inline`?"** → `constexpr` means it *can* be evaluated at compile time (and implies `inline`). `consteval` (C++20) means it **must** be.

---

## 2.8 `volatile`

`volatile` tells the compiler: **"this object's value can change by means outside this program's control — so never optimize away, cache, or reorder accesses to it."**

```cpp
volatile int* statusRegister = (int*)0xFFFF0000;
while (*statusRegister == 0) { }     // ✅ re-reads memory every iteration

int flag = 0;
while (flag == 0) { }                // ☠️ compiler may hoist the load → infinite loop
```

Every read must produce a real load; every write a real store. Access order among volatiles is preserved.

**Legitimate uses — there are only three:**

1. **Memory-mapped hardware registers** (embedded / device drivers)
2. **Variables modified by a signal handler** (with `volatile sig_atomic_t`)
3. **`setjmp`/`longjmp`** — locals that must survive a long jump

**⭐ The one thing you must say: `volatile` is NOT for multithreading.**

| | `volatile` | `std::atomic` |
|---|---|---|
| Prevents the compiler optimizing away accesses | ✅ | ✅ |
| **Atomicity** (indivisible read-modify-write) | ❌ | ✅ |
| **Memory ordering / fences** (stops CPU + compiler reordering) | ❌ | ✅ |
| Prevents data races | ❌ | ✅ |

`volatile int x; x++;` is still a **data race** — it's a non-atomic read-modify-write. Use `std::atomic<int>`.

**⭐ Language trap:** in **Java and C#**, `volatile` *does* have memory-ordering semantics. In **C++ it does not.** Interviewers with a Java background love this one.

**Also:** `volatile` can qualify a member function (`void f() volatile`), and combines as `const volatile` (e.g. a read-only hardware register). C++20 **deprecated** some volatile uses like `v++` and compound assignment on volatiles.

---

## 2.9 Scope resolution operator `::`

Six uses — be able to list them:

```cpp
int value = 10;                        // global
void f() {
    int value = 20;
    std::cout << ::value;              // 1. access the shadowed GLOBAL → 10
}

std::cout;                             // 2. namespace member
void MyClass::method() { }             // 3. define a member outside the class
MyClass::staticCount;                  // 4. static member of a class
Base::foo();                           // 5. disambiguate / call a specific base version
Outer::Inner obj;                      // 6. nested type
```

**Notes:** highest precedence in C++ · **cannot be overloaded** · `A::B::C` chains. It's the tool for resolving *every* kind of name ambiguity (diamond problem, name hiding, shadowing).

---

## 2.10 Namespaces

A **namespace** is a named scope that groups related declarations, preventing **name collisions** across libraries. Without them, two libraries each defining `Logger` cannot be linked together.

```cpp
namespace network {
    class Socket {};
    void connect();

    namespace detail {              // nested — implementation details
        void handshake();
    }
}

namespace net = network;            // namespace alias

network::Socket s;
net::detail::handshake();
```

**Key features:**

- **Open** — you can reopen and add to a namespace anywhere (unlike a class).
- **Nestable**; C++17 allows `namespace a::b::c { }`.
- **Aliases** shorten long nested names.
- **`using`:**
  - `using network::Socket;` — *declaration*, brings in one name. Fine.
  - `using namespace network;` — *directive*, brings in everything. ⭐ **Never in a header**, and avoid at file scope in large projects — it silently poisons every translation unit that includes it and can change overload resolution.
- **Unnamed (anonymous) namespace** — the modern replacement for file-`static`; gives **internal linkage**, so names are invisible to other translation units:
  ```cpp
  namespace { int counter = 0; void helper(); }   // this .cpp only
  ```
- **`inline namespace`** — members are visible in the enclosing namespace; used for **ABI versioning** (`std::literals` etc.).
- **`::x`** — the global namespace qualifier.

### 🎯 Interview

- **⭐ "Why is `using namespace std;` bad?"** → It pulls thousands of names into scope. Real breakages: your `count`/`distance`/`data`/`begin` colliding with `std::`, and new standard versions silently hijacking your calls. Absolutely never in a header, since every includer inherits it.
- **⭐ "What is ADL / Koenig lookup?"** → For an **unqualified** function call, the compiler *also* searches the namespaces of the **argument types**. That's why `std::cout << x;` finds `operator<<` and why `swap(a, b)` (unqualified, after `using std::swap;`) finds a user-defined `swap`. Expect the follow-up: *"Why does `std::cout << "hi"` work without qualifying `operator<<`?"* → ADL.
- **"Namespace vs class?"** → Namespace: pure naming scope, open, no instances, no access control. Class: a type, closed, instantiable, has access specifiers, can be a template. Use a namespace for grouping free functions — **not** a class full of `static` methods (that's a Java habit).
- **"Anonymous namespace vs `static`?"** → Both give internal linkage; the anonymous namespace also works for **types** and is the preferred C++ style.

---
---

# PART 3 — Classes: Mechanics

---

## 3.1 What is a class?

A **class** is a user-defined type — a **blueprint** that bundles **data members** (state) with **member functions** (behaviour), and controls access to them.

```cpp
class BankAccount {
    double balance_;                  // data hidden
public:
    void deposit(double a) {          // the only legal way in
        if (a > 0) balance_ += a;     // invariant enforced here
    }
    double balance() const { return balance_; }
};
```

A class is a **compile-time** concept: it occupies no memory by itself.

---

## 3.2 What is an object?

An **object** is an **instance** of a class — an actual region of storage with a lifetime, holding concrete values for the class's data members.

```cpp
BankAccount a;        // object 'a' — has storage, a lifetime, its own balance_
BankAccount b;        // a separate object with its own state
```

**Class : Object :: Blueprint : Building.** One class, many objects; each has its own copy of non-static data members but they **share** the class's member functions and static members.

---

## 3.3 `struct` vs `class`

**Only two differences**, and they're the same difference twice:

| | `struct` | `class` |
|---|---|---|
| Default member access | `public` | `private` |
| Default inheritance access | `public` | `private` |

**Everything else is identical.** A `struct` can have constructors, destructors, member functions, virtual functions, inheritance, access specifiers, templates — all of it.

**Convention (not a rule):** use `struct` for passive aggregates of public data (POD-like), `class` when there are invariants to protect.

---

## 3.4 Constructors

A special member function that **initializes** an object. Runs automatically at creation.

**Rules:** same name as the class · **no return type** (not even `void`) · can be overloaded · can have default arguments · **cannot** be `virtual`, `static`, or `const` · not inherited (unless `using Base::Base;`).

**Types:**

```cpp
class Widget {
    int a_; std::string s_;
public:
    Widget() : a_(0), s_("") {}                                // 1. DEFAULT
    Widget(int a, std::string s) : a_(a), s_(std::move(s)) {}   // 2. PARAMETERIZED
    Widget(const Widget& o) = default;                          // 3. COPY
    Widget(Widget&& o) noexcept = default;                      // 4. MOVE (C++11)
    Widget(int a) : Widget(a, "default") {}                     // 5. DELEGATING (C++11)
    explicit Widget(double d);                                  // 6. CONVERTING — blocked by explicit
};
```

**⭐ Member initializer list vs assignment in the body — know the difference:**

```cpp
Widget(int a, std::string s) : a_(a), s_(s) {}   // ✅ INITIALIZATION — one construction
Widget(int a, std::string s) { a_ = a; s_ = s; } // ❌ default-construct THEN assign — two steps
```

The init-list isn't just faster — it's **mandatory** for: `const` members, **reference** members, base classes, and members that have no default constructor.

**⭐ Members initialize in DECLARATION order**, not in initializer-list order. Reordering the list changes nothing but emits a warning — and is a classic bug source.

**Construction / destruction order:**
- Construction: **Base → members (in declaration order) → Derived body**
- Destruction: exact **reverse**.

**⭐ `explicit`** prevents implicit conversion:

```cpp
class Buffer { public: Buffer(int size); };
Buffer b = 100;      // 😬 compiles! implicit conversion int → Buffer
// with explicit:  ❌ error — must write Buffer b(100);
```

**Rule: mark every single-argument constructor `explicit`** unless you deliberately want the conversion.

### 🎯 Interview

- **"Can a constructor be `private`?"** → ✅ Yes — singletons, factory-only construction, the builder pattern.
- **"Can a constructor be `explicit` / `constexpr` / `= default` / `= delete`?"** → All ✅.
- **"Can a constructor be virtual?"** → ❌ See 4.15.
- **"Can a constructor throw?"** → ✅ It's the correct way to signal failure. See 7.7.
- **"Can you inherit from a class with a private constructor?"** → Yes, if the derived class is a `friend`; otherwise no. Pre-C++11 that was how you made a class non-inheritable; the modern way is `final`.

---

## 3.5 Default constructor

A constructor **callable with no arguments** — either it takes none, or all its parameters have defaults.

**⭐ The rule that gets tested:** the compiler generates an implicit default constructor **only if you declare NO constructors at all.** Declare *any* constructor and the free one disappears.

```cpp
class A { int x; };                   // ✅ implicit default ctor exists
A a;                                  // OK

class B { int x; public: B(int v) : x(v) {} };
// B b;                               // ❌ ERROR — no default constructor!
class C { int x; public: C(int v) : x(v) {}  C() = default; };  // ✅ restored
```

**⭐ Second trap — the implicit default constructor does NOT zero your members:**

```cpp
struct P { int x; int y; };
P p1;        // ☠️ default-initialized → x, y are INDETERMINATE garbage
P p2{};      // ✅ value-initialized → x = 0, y = 0
P p3 = P();  // ✅ value-initialized
```

It **default-initializes** members, which for **built-in types means "do nothing"**. Class-type members get *their* default constructors called.

**Where a default constructor is required:** `T arr[10];` · `std::vector<T> v(10);` · `map[key]` (via `operator[]`) · most container `resize()`.

---

## 3.6 Destructor

`~ClassName()` — runs automatically when an object is destroyed, to release its resources.

**Rules:** name is `~Class` · **no arguments, no return type** · **cannot be overloaded** — exactly one per class · can be `virtual` (and should be, for polymorphic bases) · implicitly **`noexcept`** since C++11.

**When it's called:**

| Object kind | Destroyed when |
|---|---|
| Local (automatic) | scope exits — **including via an exception** (stack unwinding) |
| Heap | `delete` / `delete[]` |
| Temporary | end of the **full expression** |
| Member / base sub-object | when the containing object is destroyed |
| Static / global | after `main()` returns, in **reverse order of construction** |

**Order (reverse of construction):** derived destructor **body** → derived **members** (reverse declaration order) → **base** destructors (reverse order).

### 🎯 Interview

- **⭐ "Can you call a destructor explicitly?"** → Yes: `obj.~T();`. **Only** valid with **placement `new`** (custom allocators, memory pools). Calling it on a normal object causes **double destruction** → UB.
- **"Does a destructor free the object's memory?"** → No — it destroys the *members/resources*; `operator delete` releases the storage afterwards.
- **"Can a destructor throw?"** → Effectively no — see 7.7.
- **"Can a destructor be pure virtual?"** → ✅ Yes, and it **must still have a definition**. See 4.14.

---

## 3.7 RAII — Resource Acquisition Is Initialization

**The single most important idiom in C++.** Tie a resource's lifetime to an object's lifetime:

- **Acquire** the resource in the **constructor**.
- **Release** it in the **destructor**.

Because the destructor is called automatically on **every** exit path — normal return, early return, **and exception unwinding** — cleanup is guaranteed.

```cpp
class FileHandle {
    std::FILE* f_;
public:
    explicit FileHandle(const char* p) : f_(std::fopen(p, "r")) {
        if (!f_) throw std::runtime_error("cannot open file");
    }
    ~FileHandle() { if (f_) std::fclose(f_); }    // ← guaranteed cleanup
    FileHandle(const FileHandle&) = delete;       // a unique resource: don't copy
    FileHandle& operator=(const FileHandle&) = delete;
};
```

**Standard-library RAII types:** `std::vector`, `std::string`, `std::unique_ptr`, `std::shared_ptr`, `std::lock_guard`, `std::fstream`, `std::jthread`.

**⭐ "Why is RAII better than `try/finally`?"** → Cleanup is written **once, in the class**, not at every use site — and it runs automatically on *every* exit path. GC languages need `try/finally` or `using` because a GC only reclaims memory, not files/locks/sockets.

---

## 3.8 The `this` pointer

**`this`** is an implicit pointer, available inside every **non-static member function**, pointing to the object the function was called on.

```cpp
class Widget {
    int value_;
public:
    void setValue(int value) {
        this->value_ = value;         // disambiguate member from parameter
    }
    Widget& operator=(const Widget& o) {
        if (this == &o) return *this; // ✅ self-assignment check
        value_ = o.value_;
        return *this;                 // ✅ enables chaining: a = b = c
    }
    Widget& setA(int a) { value_ = a; return *this; }   // fluent interface
};
w.setA(1).setA(2);                    // method chaining
```

**Type of `this`:**

| Method | Type of `this` |
|---|---|
| `void f()` | `Widget* const` |
| `void f() const` | `const Widget* const` |
| `void f() volatile` | `volatile Widget* const` |

Note the **`const` after the star is always there** — you can never reassign `this`.

**Uses (list these):**

1. Distinguish a member from a same-named parameter
2. **Return `*this`** for chaining / assignment operators
3. **Self-assignment check** in `operator=`
4. Pass the current object to another function: `registry.add(this);`
5. Access a member hidden by a local

**Not available in:** `static` member functions (no object), free functions, or a lambda that hasn't captured it.

### 🎯 Interview

- **⭐ "Can you modify `this`?"** → No — it's a prvalue of type `T* const`.
- **⭐ "Is `delete this;` legal?"** → **Yes, but dangerous.** Legal only if: the object was heap-allocated with `new`, no member is touched afterwards, and no one uses the object again. Used in COM / reference-counted objects (`Release()`). Never in normal code.
- **⭐ "What is `this` inside a lambda in a member function?"** → `[this]` captures the **pointer**, so the lambda holds a reference to the object — a classic dangling bug if the lambda outlives it. C++17 added `[*this]` to capture a **copy** of the object.
- **"When is `this->` required?"** → When a parameter shadows the member — and, importantly, in templates to access a member of a **dependent base class** (`this->baseMember`).
- **C++23 bonus:** "deducing `this`" — an explicit object parameter `void f(this Self&& self)` that unifies const/non-const/ref-qualified overloads.

---

## 3.9 `const` member functions

Appending `const` to a member function declares it **does not modify the object's observable state**. Inside it, `this` has type `const T*`.

```cpp
class Rect {
    int w_, h_;
    mutable int cachedArea_ = -1;      // mutable → modifiable even in const methods
public:
    int width() const { return w_; }   // ✅ const method
    void setWidth(int w) { w_ = w; }   // non-const

    int area() const {
        if (cachedArea_ < 0) cachedArea_ = w_ * h_;   // ✅ legal: mutable
        return cachedArea_;
    }
};

const Rect r{3, 4};
r.width();       // ✅ const objects can only call const methods
// r.setWidth(5) // ❌ compile error
```

**What a const method may / may not do:**
- ❌ modify non-`mutable` data members
- ❌ call non-const member functions
- ✅ modify `mutable` members, `static` members, and objects reached *through* a pointer member (the pointer is const, the pointee isn't)

**⭐ `const` participates in overload resolution:**

```cpp
class Buf {
    std::vector<int> d_;
public:
    int&       operator[](size_t i)       { return d_[i]; }   // non-const object
    const int& operator[](size_t i) const { return d_[i]; }   // const object
};
```

This is the standard pattern used everywhere in the STL (`begin()`/`cbegin()`, `at()`, `data()`).

### 🎯 Interview

- **⭐ "What is `mutable` for?"** → Members that don't affect the object's *logical* state: **caches**, **memoized results**, **mutexes**, hit counters. `std::mutex m_;` in a class with const methods that lock it is the canonical example.
- **⭐ "Bitwise vs logical constness?"** → The compiler enforces **bitwise** (no bytes of the object change). What you actually want is **logical** (the observable value doesn't change). `mutable` bridges the gap; a `T* ptr_` member is the leak in the other direction.
- **"Can two overloads differ only by constness?"** → ✅ Yes — the constness of `this` is part of the signature. (Contrast with a *by-value parameter*, where top-level `const` is ignored.)
- **⭐ "Is a const method thread-safe?"** → Not automatically. But **the standard library guarantees `const` == safe for concurrent reads** for its own types, and users are expected to honour the same contract. So a const method mutating a cache must guard it with a mutex.

---

## 3.10 Static members (recap in class context)

- **Static data member:** one shared copy for the whole class. Declared in-class, **defined outside** (or `static inline` since C++17). See 2.2 for the full treatment.
- **Static member function:** no `this`, callable as `ClassName::f()`, cannot be `virtual`, cannot access non-static members.

---

## 3.11 Function overloading

Multiple functions with the **same name** in the **same scope**, distinguished by their **parameter list**. Resolved at compile time via **overload resolution**.

```cpp
void print(int);
void print(double);
void print(const std::string&);
void print(int, int);
```

**Valid differentiators:** number of params · types of params · order of params · `const`/`volatile` on the *object* (`f() const`) · ref-qualifiers (`f() &` / `f() &&`).

**❌ NOT valid differentiators:**

- **⭐ Return type alone** — `int f(); double f();` is an error. (Because `f();` as a statement would be ambiguous.)
- Top-level `const` on a **by-value** parameter — `f(int)` and `f(const int)` are the *same* function.
- Default arguments — `f(int)` and `f(int, int = 0)` compile but make `f(1)` ambiguous.
- Typedefs/aliases — `f(int)` vs `f(int32_t)` is a redefinition.

**Name mangling** encodes parameter types into the linker symbol — which is why C++ supports overloading and C doesn't, and why C interop needs `extern "C"`.

**⭐ Overload resolution ranking:** Exact match > promotion (`char→int`, `float→double`) > standard conversion (`int→double`) > user-defined conversion > ellipsis. Ambiguity is a compile error, not a silent pick.

---

## 3.12 Operator overloading

Giving operators a meaning for user-defined types. It is **syntactic sugar for a function call**: `a + b` → `operator+(a, b)` or `a.operator+(b)`.

```cpp
class Complex {
    double re_, im_;
public:
    Complex(double r = 0, double i = 0) : re_(r), im_(i) {}

    Complex& operator+=(const Complex& o) {          // member: modifies *this
        re_ += o.re_; im_ += o.im_; return *this;
    }
    friend Complex operator+(Complex a, const Complex& b) {  // non-member
        return a += b;                               // implement in terms of +=
    }
    friend std::ostream& operator<<(std::ostream& os, const Complex& c) {
        return os << c.re_ << " + " << c.im_ << "i";
    }
};
```

**Rules:**

- Cannot invent new operators (`**`); cannot change **arity**, **precedence**, or **associativity**.
- At least one operand must be a user-defined type (can't overload for two `int`s).
- **⭐ Cannot be overloaded:** `::` `.` `.*` `?:` `sizeof` `typeid` `alignof` and the named casts.
- **Must be members:** `=` `[]` `()` `->` and conversion operators.
- **Should be non-members:** symmetric binary operators (`+`, `==`, `<`) — so implicit conversion applies to the *left* operand too. `<<` / `>>` **must** be non-members.

### 🎯 Interview

- **⭐ "Function overloading vs operator overloading?"** → Both are **compile-time polymorphism**. Function overloading = same *function name*, different parameter lists, resolved by overload resolution. Operator overloading = giving an *operator symbol* a meaning for your type; it's just a function with a special name (`operator+`), and it obeys the same overload-resolution rules but with extra restrictions (fixed arity/precedence, some operators forbidden, at least one user-defined operand).
- **⭐ "Pre-increment vs post-increment overload?"**
  ```cpp
  Counter& operator++();      // ++x  — returns reference, no copy
  Counter  operator++(int);   // x++  — dummy int param disambiguates; returns old value by value
  ```
  Prefer `++x` for non-trivial types: `x++` must make a copy.
- **"Why should `operator+` return by value and `operator+=` return a reference?"** → `+` creates a new object; `+=` mutates and returns `*this` to allow chaining. Canonical: implement `+=` first, define `+` in terms of it.
- **"Overloading `&&`, `||`, `,`?"** → Legal but a **bad idea**: you lose short-circuit evaluation and sequencing guarantees, since they become ordinary function calls.
- **Guiding principle they love:** *"Do as the ints do"* — overloaded operators should behave unsurprisingly. Don't make `+` delete a file.

---
---

# PART 4 — The Four Pillars of OOP

---

## 4.1 What is OOP? Main features

**OOP** = organizing a program around **objects** (data + the operations on that data bundled together) instead of around procedures operating on loose data.

**Why it exists:** in procedural code, data is global-ish and any function can corrupt it. As programs grow, "who can change this?" becomes unanswerable. OOP gives you *ownership boundaries* and *substitutability*.

**The 4 pillars:**

| Pillar | One-line meaning | C++ mechanism |
|---|---|---|
| **Abstraction** | Expose *what*, hide *how* | abstract classes, pure virtual |
| **Encapsulation** | Bundle data + methods, control access | `class` + access specifiers |
| **Inheritance** | Reuse + "is-a" relationship | `: public Base` |
| **Polymorphism** | One interface, many behaviours | overloading, templates, `virtual` |

### 🎯 Interview

- **⭐ "Is C++ a pure OOP language?"** → **No.** It's *multi-paradigm* (procedural, OO, generic, functional). `main()` is a free function; you can write full programs with zero classes. Java/C# are closer to "pure"; Smalltalk is pure.
- **"Difference between class and object?"** → Class = blueprint / type (compile-time). Object = instance with storage (runtime).
- **Trap:** don't say "abstraction and encapsulation are the same." Have the distinction ready (4.6).

---

## 4.2 Encapsulation

**Encapsulation** = binding data and the functions that operate on it into a single unit, *and* restricting direct access to internal state so the class can guarantee its own **invariants**.

**Two parts (interviewers probe this):**

1. **Bundling** — data + behaviour in one unit.
2. **Data hiding** — restricting access via `private`/`protected`.

Data hiding is a *technique used to achieve* encapsulation; they are not synonyms.

**How C++ achieves it:**

- `class` / `struct` as the bundling unit
- `private` / `protected` access specifiers
- Public **member functions** as the controlled gateway
- `const` member functions for read-only access
- **Pimpl idiom** for compile-time encapsulation (hides implementation from the header entirely)

```cpp
class Temperature {
    double celsius_;
public:
    void setCelsius(double c) {
        if (c < -273.15) throw std::out_of_range("below absolute zero");
        celsius_ = c;                 // invariant can never be violated
    }
    double getCelsius() const { return celsius_; }
};
```

### 🎯 Interview

- **⭐ "Doesn't a public getter/setter pair defeat encapsulation?"** → Yes, largely — a naked `setX` that just assigns is no better than a public member. Real encapsulation exposes **behaviour**, not accessors: `account.withdraw(100)` instead of `account.setBalance(account.getBalance() - 100)`.
- **"Benefits?"** → Invariants guaranteed, implementation swappable without touching callers, easier debugging (few write points), thread-safety can be added inside the class.
- **⭐ "Encapsulation vs abstraction?"** → Encapsulation is *implementation-level* (hide the data, restrict access). Abstraction is *design-level* (hide complexity, expose a simplified model). Encapsulation is one way to **enforce** abstraction.
- **⭐ Gotcha:** `private` is a **compile-time** restriction, not a security feature. A `reinterpret_cast` or a memory dump reads it fine. It protects against *mistakes*, not *attackers*.

---

## 4.3 Access specifiers

Three keywords controlling **who may name a member**: `public`, `private`, `protected`.

| Specifier | Same class | Derived class | Outside world |
|---|---|---|---|
| `public` | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ❌ |
| `private` | ✅ | ❌ | ❌ |

```cpp
class A {
public:    int pub;
protected: int prot;
private:   int priv;
};

class B : public A {
    void f() {
        pub = 1;   // OK
        prot = 2;  // OK
        // priv = 3;  // ERROR
    }
};
```

**Key features / rules:**

- Defaults: `class` → `private`; `struct` → `public`.
- Can appear **any number of times, in any order**.
- `friend` bypasses all of them.
- They also apply to **inheritance mode**, which *caps* the inherited access:

```cpp
class B : public    A {};  // public→public,    protected→protected
class C : protected A {};  // public→protected, protected→protected
class D : private   A {};  // public→private,   protected→private
```

In **all** cases, `A`'s `private` members are inaccessible in the derived class (they still *exist* in the object, just unnameable).

### 🎯 Interview

- **⭐ Classic gotcha: access is per-CLASS, not per-OBJECT.** A member function can touch the privates of *any* object of its own class:
  ```cpp
  class X {
      int v;
  public:
      bool bigger(const X& o) const { return v > o.v; }  // o.v is legal!
  };
  ```
  This is why copy constructors and `operator==` work without friends.
- **"private vs protected?"** → `protected` is visible to derived classes. Advice: `protected` *data* is a smell (it becomes part of your derived-class contract forever); prefer `protected` member functions.
- **⭐ Subtle `protected` rule:** a derived class may access a protected member only **through an object of its own type (or further derived)** — not through a `Base&`:
  ```cpp
  class B : public A {
      void f(A& a, B& b) {
          // a.prot = 1;  // ERROR
          b.prot = 1;     // OK
      }
  };
  ```
- **"private inheritance vs composition?"** → Both mean "implemented-in-terms-of". Prefer composition; use `private` inheritance only when you need to override a virtual or exploit the empty-base optimization.

---

## 4.4 `friend` function / `friend` class

A **friend** is a non-member function, another class, or another class's member function that is granted access to the `private` and `protected` members of the class that declares it.

**Why it exists:** some operations are conceptually part of the class's interface but *cannot* be members — most famously `operator<<`, whose left operand must be `std::ostream`.

```cpp
class Vec {
    double x_, y_;
public:
    Vec(double x, double y) : x_(x), y_(y) {}
    friend std::ostream& operator<<(std::ostream& os, const Vec& v);
    friend class Renderer;                 // whole class is a friend
    friend void Logger::dump(const Vec&);  // just one member function
};

std::ostream& operator<<(std::ostream& os, const Vec& v) {
    return os << '(' << v.x_ << ", " << v.y_ << ')';   // touches privates
}
```

**Privileges:** full access to `private` + `protected` members — exactly as if it were a member.

**⭐ Non-privileges (this is what gets asked):**

- ❌ Not a member — has **no `this` pointer**, is not called with `obj.f()`.
- ❌ Cannot be `virtual`, `const`-qualified, or `static`.
- ❌ **Not inherited** — a friend of `Base` is not a friend of `Derived`.
- ❌ **Not transitive** — "friend of my friend" ≠ my friend.
- ❌ **Not reciprocal/symmetric** — `A` befriending `B` does not let `A` see `B`'s privates.
- ❌ Cannot be granted by yourself — friendship is *given*, not *taken*. (The key safety property.)

### 🎯 Interview

- **⭐ "Does `friend` break encapsulation?"** → Best answer: *No — it extends the class's interface deliberately.* The class author writes the `friend` declaration, so the boundary is still under the class's control. It's only a violation if used to bolt on unrelated access from outside.
- **"Where can you declare a friend inside a class?"** → Anywhere; `public:` / `private:` sections have **no effect** on friend declarations.
- **"When is `friend` genuinely necessary?"** → (a) `operator<<` / `operator>>`; (b) symmetric binary operators where you want implicit conversion on *both* operands; (c) tightly coupled pairs like a container and its iterator; (d) test fixtures.
- **Alternative to know:** often a public member function or the **Attorney–Client idiom** is cleaner than a whole `friend class`.

---

## 4.5 Ways to access private fields of a class

Ordered from legitimate to criminal — a good interviewer wants to see you distinguish them.

**✅ Legitimate:**

1. **Public member functions** — accessors or, better, behaviour methods. The intended way.
2. **`friend` function or class** — access granted explicitly by the class author.
3. **Another object of the same class** — access is **per-class, not per-object**:
   ```cpp
   bool operator<(const X& o) const { return v_ < o.v_; }   // o.v_ is legal
   ```
4. **A nested class** — since C++11, a nested class has access to the enclosing class's private members.
5. **Derived class** — for `protected` members only (not `private`).
6. **The Attorney–Client idiom** — a `friend` proxy that re-exposes only a *selected subset* of privates.

**⚠️ Legal but ugly:**

7. **Pointer-to-member trick via explicit template instantiation** — the one genuine *standard-conforming* loophole: **access checking is not performed on explicit instantiation declarations**.
   ```cpp
   template <typename Tag, typename Tag::type M> struct Rob {
       friend typename Tag::type get(Tag) { return M; }
   };
   struct Xf { using type = int Secret::*; friend type get(Xf); };
   template struct Rob<Xf, &Secret::hidden_>;   // legal!
   ```

**☠️ Undefined behaviour / hacks (know them, never ship them):**

8. **`#define private public`** before the `#include` — an ODR violation; different TUs see different layouts. Seen in legacy test code.
9. **`reinterpret_cast` + offset arithmetic** — reading the bytes directly. Works for standard-layout types, UB in general.
10. **`memcpy` / raw memory inspection.**

### 🎯 Interview

- **⭐ The point they want:** *"`private` is a **compile-time** enforcement of design intent — it protects against **accident**, not against **malice**. There is no runtime enforcement; the bytes are right there."*
- **"How would you test private methods?"** → In order: (a) test through the public interface — if you can't, that's a design smell; (b) extract the logic into a separate class or a `detail::` free function; (c) `friend class MyClassTest;` — pragmatic and explicit. Avoid `#define private public`.
- **"Getter or friend?"** → If *everyone* needs it, a public getter. If *one specific collaborator* needs it, `friend` keeps the public interface clean.

---

## 4.6 Abstraction

**Abstraction** = presenting only the essential features of an entity and hiding the irrelevant detail. It's about *modelling*: "what does this thing do?" not "how is it done?"

**How C++ achieves it:**

1. **Header/implementation split** — `.h` declares, `.cpp` defines (abstraction at file level).
2. **Access specifiers** — hide internals.
3. **Abstract classes with pure virtual functions** — the strongest, runtime-polymorphic form.
4. **Templates / concepts** — compile-time abstraction over types.

**⭐ Abstraction vs encapsulation** — expect this question. Abstraction = **design**, hides complexity, "what". Encapsulation = **implementation**, hides data, "how it's protected".

---

## 4.7 Abstract class, pure virtual function, and "interface"

```cpp
class Shape {                     // abstract base class (ABC)
public:
    virtual double area() const = 0;   // pure virtual → no implementation required
    virtual ~Shape() = default;        // ⭐ virtual destructor
};

class Circle : public Shape {
    double r_;
public:
    explicit Circle(double r) : r_(r) {}
    double area() const override { return 3.14159 * r_ * r_; }
};
```

- A **pure virtual function** is declared `= 0`. It has no required implementation and **must** be overridden by a concrete derived class.
- A class with **≥1 pure virtual function** is **abstract** → **cannot be instantiated**. You *can* have pointers/references to it.
- A derived class that doesn't override every pure virtual stays abstract.

**Interface in C++:** C++ has **no `interface` keyword**. The idiom is an ABC with:
- only pure virtual functions,
- no data members,
- a public virtual (or protected non-virtual) destructor.

```cpp
class ISerializable {
public:
    virtual std::string serialize() const = 0;
    virtual void deserialize(const std::string&) = 0;
    virtual ~ISerializable() = default;
};
```

Multiple inheritance of several such interfaces is safe and idiomatic.

**Abstract class vs interface:**

| | Abstract class | Interface (idiom) |
|---|---|---|
| Definition | Has ≥1 **pure virtual** function | **All** functions pure virtual |
| Data members | ✅ may have state | ❌ none |
| Implemented methods | ✅ may provide some | ❌ none (besides the dtor) |
| Constructor | ✅ (protected, for derived use) | Trivial / none |
| Relationship | **"is-a"** | **"can-do"** / capability |
| Multiple inheritance of it | ⚠️ risky (diamond, duplicated state) | ✅ safe & idiomatic |
| Purpose | Share common implementation + force customization | Pure contract / decoupling |

```cpp
// INTERFACE — pure contract, no state
class IDrawable {
public:
    virtual void draw() const = 0;
    virtual ~IDrawable() = default;
};

// ABSTRACT CLASS — shares state and implementation, forces one customization point
class Shape : public IDrawable {
protected:
    Color color_;                              // state
public:
    explicit Shape(Color c) : color_(c) {}
    Color color() const { return color_; }     // shared implementation
    virtual double area() const = 0;           // must be provided by derived
};
```

### 🎯 Interview

- **⭐ "Can a pure virtual function have a body?"** → **Yes!** `virtual void f() = 0;` then `void Base::f() { /* shared code */ }`, callable via `Base::f()`. The class stays abstract. Useful for a default that derived classes must *explicitly* opt into. This trips up most candidates and is the key "abstract class ≠ interface" nuance.
- **⭐ "Can a destructor be pure virtual?"** → Yes, and it **must** be given a definition, because derived destructors always call it. Used to make a class abstract when there's no other natural pure virtual.
- **"When do you choose which?"** → Interface when you want **decoupling** and the implementations have nothing in common (or a class needs several unrelated capabilities). Abstract class when there is **genuine shared state or logic** and a real "is-a" hierarchy.
- **"Why is multiple inheritance of interfaces safe but of abstract classes risky?"** → Interfaces are **stateless**, so a diamond duplicates nothing. Abstract classes with data duplicate that data and force virtual inheritance.
- **"C++20 alternative?"** → **Concepts** give you *compile-time* interfaces (static polymorphism, no vtable, no inheritance).

---

## 4.8 Inheritance and its types

**Inheritance** = deriving a new class from an existing one, acquiring its members. Models an **"is-a"** relationship and enables runtime polymorphism.

**Five types (by structure):**

```
1. Single          2. Multilevel     3. Hierarchical    4. Multiple      5. Hybrid
   A                  A                   A               A   B            A
   |                  |                 / | \              \ /            / \
   B                  B                B  C  D              C            B   C
                      |                                                   \ /
                      C                                                    D
```

| Type | Description |
|---|---|
| **Single** | One base, one derived: `B : A` |
| **Multilevel** | Chain: `A → B → C` |
| **Hierarchical** | Many derived from one base |
| **Multiple** | One derived, multiple bases: `C : A, B` |
| **Hybrid** | Combination of the above (typically multiple + hierarchical) → causes the **diamond** |

```cpp
class Animal { public: virtual void speak() const { std::cout << "..."; }
               virtual ~Animal() = default; };
class Dog : public Animal { public: void speak() const override { std::cout << "Woof"; } };
```

### 🎯 Interview

- **⭐ "What is NOT inherited?"** → Constructors*, destructor, `operator=`, and friendships. (*C++11 `using Base::Base;` lets you *inherit constructors* explicitly.)
- **⭐ "Members initialization order?"** → Members are initialized in **declaration order in the class**, *not* the order in the member-initializer list.
- **"Is-a vs has-a."** → Inheritance = is-a; composition = has-a. **Prefer composition over inheritance** — and justify: inheritance is the tightest coupling in the language.
- **"What does `final` do?"** → `class D final` = cannot be derived from. `void f() override final` = cannot be further overridden. Also enables devirtualization optimizations.
- **⭐ Liskov Substitution Principle:** anywhere a `Base` is expected, a `Derived` must work correctly. If it doesn't, your inheritance is wrong (classic `Square : Rectangle` violation).

---

## 4.9 Can a class inherit multiple classes?

**Yes** — C++ is one of the few mainstream languages with true **multiple inheritance** (Java/C# allow only multiple *interfaces*).

```cpp
class Serializable { public: virtual std::string serialize() const = 0;
                     virtual ~Serializable() = default; };
class Drawable     { public: virtual void draw() const = 0;
                     virtual ~Drawable() = default; };

class Sprite : public Drawable, public Serializable {
public:
    void draw() const override { /*...*/ }
    std::string serialize() const override { return "sprite"; }
};
```

**Rules:**

- **Construction order = the order bases are listed** in the class definition (not the initializer-list order). Destruction is the reverse.
- Each polymorphic base contributes its own vptr, so the object contains **multiple vptrs**.
- **Ambiguity** if two bases have a same-named member → resolve with `Base::member`, or override in the derived class.
- **Diamond** if two bases share a common base → duplicated data; fix with `virtual` inheritance (4.17).
- A `Derived*` → `Base2*` conversion involves a **pointer adjustment** (non-zero offset), unlike single inheritance. This is why `static_cast` is correct and `reinterpret_cast` is broken between them.

### 🎯 Interview

- **⭐ "Is multiple inheritance good or bad?"** The nuanced answer:
  - **✅ Good and safe:** inheriting multiple **stateless interfaces** (pure-virtual ABCs, no data). No diamond problem, no duplication. The mainstream use.
  - **⚠️ Risky:** inheriting multiple classes **with state/implementation** — diamonds, ambiguity, fragile base class, confusing construction order.
  - Rule: *"Inherit from at most one concrete class; any number of interfaces."*
- **"Why did Java drop it?"** → To avoid the diamond problem; interfaces (stateless) give most of the benefit with none of the risk.
- **"What's the mixin pattern?"** → Small classes providing orthogonal capabilities, combined via multiple inheritance (often with CRTP). A legitimate, powerful use.
- **"Does the order of base classes matter?"** → Yes: construction/destruction order, memory layout, and which base sits at offset 0 (that one gets the "free" pointer conversion).

---

## 4.10 Polymorphism and its types

**Polymorphism** = "many forms" — one name/interface, multiple behaviours depending on type.

### A. Compile-time (static) polymorphism
Resolved by the compiler. Zero runtime cost.
- **Function overloading**
- **Operator overloading**
- **Templates** (incl. CRTP — "static polymorphism")
- Default arguments (weak form)

### B. Runtime (dynamic) polymorphism
Resolved at runtime via **virtual functions**; requires a **pointer or reference** to a base.

```cpp
class Shape { public: virtual void draw() const { std::cout << "shape\n"; }
              virtual ~Shape() = default; };
class Circle : public Shape { public: void draw() const override { std::cout << "circle\n"; } };

void render(const Shape& s) { s.draw(); }   // picks the right one at runtime
```

### 🎯 Interview

- **⭐ "Why doesn't polymorphism work with objects by value?"** → **Object slicing.** `Shape s = circle;` copies only the `Shape` sub-object; the vptr stays `Shape`'s. Virtual dispatch requires a pointer or reference.
- **"Can a constructor be virtual?"** → No (4.15). **"Can a static member function be virtual?"** → No — `static` has no `this`.
- **"What is `dynamic_cast`?"** → Safe downcast, checked at runtime via RTTI; returns `nullptr` for pointers, throws `std::bad_cast` for references. Requires a polymorphic (≥1 virtual) type.

---

## 4.11 Virtual functions and the vtable

A **virtual method** is a member function whose call is resolved by the **dynamic (runtime) type** of the object rather than the static type of the pointer/reference.

```cpp
class Base {
public:
    virtual void speak() const { std::cout << "Base\n"; }
    void normal()        const { std::cout << "Base normal\n"; }
    virtual ~Base() = default;
};

class Derived : public Base {
public:
    void speak() const override { std::cout << "Derived\n"; }
    void normal() const         { std::cout << "Derived normal\n"; }  // HIDES, not overrides
};

Base* p = new Derived;
p->speak();    // "Derived"       ← dynamic dispatch
p->normal();   // "Base normal"   ← static binding, resolved by p's TYPE
```

**⭐ Mechanism — the vtable (be able to draw this):**

- Every class with ≥1 virtual function gets a **vtable**: a **static, per-class** array of function pointers.
- Every *object* of such a class gets a hidden **vptr** (usually the first member) pointing at its class's vtable.
- A virtual call becomes: load vptr → index a fixed slot → indirect call. Roughly 2 extra memory loads.
- Cost: **+8 bytes per object** (one vptr, regardless of how many virtual functions), plus an indirect call that usually **cannot be inlined**.

**Rules:**

- `virtual` is **inherited** — once virtual, always virtual; you needn't repeat the keyword (but do use `override`).
- **Pure virtual** `= 0` makes the class abstract.
- **Overriding requires an exact signature match** (name, params, `const`, ref-qualifier). Return type may differ only by **covariance** (`Base*` → `Derived*`).

### 🎯 Interview

- **⭐ "Vtable per class or per object?"** → **Vtable per class, vptr per object.** Very commonly asked, easily fumbled.
- **⭐ "Always use `override`."** Without it, a `const` mismatch or typo silently creates a *new* function; your call goes to the base and the bug is invisible. `override` turns it into a compile error.
- **"Can a virtual function be `private`?"** → ✅ Yes, and it's a real pattern: **NVI (Non-Virtual Interface)** — public non-virtual functions calling private virtual hooks. Access control is checked on the *static* type at the call site; dispatch is independent of it.
- **"Can a virtual function be `inline`?"** → You can mark it, but calls through a base pointer generally can't be inlined. Direct calls on a known concrete object can (devirtualization; `final` helps).
- **⭐ "Default arguments in virtual functions?"** → **Danger.** Default args are bound **statically** (by the pointer's type), the function body **dynamically**. You get derived code with base defaults. Never redefine default args in an override.
- **"When is the vptr set?"** → At the start of each constructor, progressively — which is why virtual calls in constructors don't dispatch to derived classes (4.16).

---

## 4.12 Early vs late binding

**Binding** = associating a function *call* with the function *body* to execute.

| | **Early (static) binding** | **Late (dynamic) binding** |
|---|---|---|
| Resolved at | **Compile time** | **Runtime** |
| Based on | the **static type** of the pointer/reference | the **dynamic type** of the object |
| Mechanism | direct call to a fixed address | **vptr → vtable → indirect call** |
| Applies to | normal functions, overloads, templates, non-virtual members | **`virtual` functions** |
| Cost | none; inlinable | 1 extra indirection; usually not inlinable |

```cpp
Base* p = new Derived;
p->normalFn();    // EARLY  → Base::normalFn  (decided by p's type)
p->virtualFn();   // LATE   → Derived::virtualFn (decided by the object)
```

**How late binding is implemented:** the compiler gives each polymorphic class a **vtable** and each object a hidden **vptr**. A virtual call compiles to *"load vptr → fetch slot N → call"*. Slot **indices** are fixed at compile time; only the **table** varies at runtime.

**⭐ Key point:** C++ defaults to **early binding** — you must opt in with `virtual`. Java is the opposite (methods are virtual by default; `final` opts out). That's the zero-overhead principle at work.

---

## 4.13 Overriding vs overloading vs hiding

| | Where | Signature | Dispatch |
|---|---|---|---|
| **Overloading** | Same scope | **Different** | Compile-time |
| **Overriding** | Base vs Derived | **Identical** + `virtual` | Runtime |
| **⭐ Hiding (shadowing)** | Base vs Derived | Any | Compile-time — a derived name hides **all** base overloads |

```cpp
class Base { public: void f(int); void f(double); };
class Derived : public Base {
public:
    void f(int);       // ⚠️ HIDES BOTH Base::f overloads
    // using Base::f;  // ✅ the fix — brings them back into scope
};
```

**Function overriding** specifically: redefining a **virtual** function in a derived class with the **same signature**, so a call through a base pointer/reference executes the derived version. That's the mechanism of runtime polymorphism.

---

## 4.14 Virtual destructor — why we need it

Because **deleting a derived object through a base-class pointer with a non-virtual destructor is Undefined Behaviour** — in practice the derived destructor never runs, so derived resources leak.

```cpp
class Base { public: ~Base() { std::cout << "~Base\n"; } };          // ☠️ not virtual
class Derived : public Base {
    std::vector<int> big_{1'000'000};
public:
    ~Derived() { std::cout << "~Derived\n"; }
};

Base* p = new Derived;
delete p;              // prints only "~Base" → big_ LEAKS
```

With `virtual ~Base()`, `delete p` dispatches through the vtable to `~Derived()`, which then chains to `~Base()`. Output: `~Derived`, `~Base`.

**The rule:** *If a class has **any** virtual function — or is ever deleted through a base pointer — give it a **virtual destructor**.*

**⭐ The counter-rule (this separates good candidates):** **don't** add a virtual destructor to a class that isn't polymorphic. It adds a vptr (+8 bytes), destroys **trivial copyability** and **standard layout**, and can wreck performance for small value types like a `Point`.

**Alternative when you want inheritance but forbid polymorphic deletion:**

```cpp
class Base {
protected:
    ~Base() = default;    // non-virtual, but protected → `delete basePtr` won't compile
};
```

### 🎯 Interview

- **⭐ "What exactly happens without it?"** Precise answer: *"It's undefined behaviour. In every real implementation, the base destructor runs but the derived destructor does not — so derived members are never destroyed and their resources leak. With multiple inheritance or an adjusted pointer, `operator delete` may even get the wrong address."*
- **⭐ "Does `shared_ptr` need a virtual destructor?"** → **No!** `shared_ptr<Base> p = make_shared<Derived>();` works correctly even without one, because `shared_ptr` **type-erases the deleter** at construction, capturing "delete as `Derived`" in the control block. `unique_ptr<Base>` **does** need it (the deleter is a static part of the type). Excellent discriminator question.
- **"Is the destructor 'overridden'?"** → Not in the usual sense — destructors have different names. Dispatch is virtual, but each destructor still automatically runs its own base's destructor afterwards.

---

## 4.15 Can a constructor be virtual?

**No.** Two independent reasons — give both:

1. **Mechanical:** virtual dispatch uses the **vptr**, and it's the *constructor's job* to set up the vptr. Before the constructor runs, the object has no vptr — nothing to dispatch through. Chicken and egg.
2. **Logical:** virtual dispatch means "pick the behaviour based on the object's actual type." When constructing, the object **has no type yet** — you're the one deciding it. You must name the concrete type to allocate the right amount of memory.

**Destructors, by contrast, can and should be virtual** — by then the object exists and has a known dynamic type.

**"Virtual construction" when you actually need it — the `clone()` idiom:**

```cpp
class Shape {
public:
    virtual std::unique_ptr<Shape> clone() const = 0;   // "virtual copy constructor"
    virtual ~Shape() = default;
};

class Circle : public Shape {
public:
    std::unique_ptr<Shape> clone() const override {
        return std::make_unique<Circle>(*this);         // real copy ctor called here
    }
};

std::unique_ptr<Shape> copy = someShape->clone();       // right type, without knowing it
```

Or a **factory function** for "virtual default construction": `std::unique_ptr<Shape> create(ShapeType t);`

**⭐ Follow-up: "So how do you copy an object when you only have a `Base*`?"** → The `clone()` idiom. Naming it *"the virtual copy constructor"* scores points.

---

## 4.16 Can a virtual function be called from a constructor?

**Yes, it compiles and runs — but it does NOT dispatch to the derived class.** A top-tier interview question.

```cpp
class Base {
public:
    Base() { log(); }                         // calls Base::log, ALWAYS
    virtual void log() { std::cout << "Base\n"; }
    virtual ~Base() = default;
};

class Derived : public Base {
    std::string name_ = "derived-data";
public:
    void log() override { std::cout << "Derived: " << name_ << "\n"; }
};

Derived d;   // prints "Base", NOT "Derived"
```

**Why:** objects are built **base-first**. While `Base::Base()` runs, the `Derived` part doesn't exist yet — its members are uninitialized. So the compiler sets the **vptr to `Base`'s vtable** during `Base`'s constructor, and switches it to `Derived`'s vtable only when `Derived`'s constructor begins. The rule protects you: dispatching to `Derived::log()` would read `name_` before it was constructed.

**Same applies to destructors, in reverse.** During `~Base()`, the derived part is already destroyed, so the vptr has been reset to `Base`.

**⭐ The dangerous case:** if the function is **pure virtual with no definition**, the call is **undefined behaviour** — typically a runtime crash: `pure virtual method called`.

```cpp
class B { public: B() { f(); } virtual void f() = 0; };  // ☠️ UB
```

**Rule:** *During construction and destruction, the object's dynamic type is the class currently being constructed/destructed.*

**Workarounds:** two-phase initialization (`obj.init()`), a **factory function** that constructs then calls the virtual, or passing the needed behaviour in as a constructor argument (strategy/callback).

### 🎯 Interview

- State it crisply: *"It compiles, but virtual dispatch is disabled — you get the current class's version. With a pure virtual and no body, it's UB."*
- **"Can you call a virtual function *indirectly* from a constructor?"** → Yes, and the same rule applies — anything reached from the ctor, however deep, still sees the base's dynamic type.
- **"Is it safe to call a non-virtual member function from a constructor?"** → Yes, provided it only touches already-initialized members.

---

## 4.17 Virtual inheritance

`class B : virtual public A` tells the compiler: **however many paths lead to `A` in this hierarchy, keep only one shared `A` sub-object.**

```cpp
class Animal { public: int age; };
class Mammal : virtual public Animal {};
class WingedAnimal : virtual public Animal {};
class Bat : public Mammal, public WingedAnimal {};   // exactly ONE Animal

Bat b;
b.age = 5;   // unambiguous
```

**Cost / mechanics:**

- Objects gain a **vbptr** (virtual base pointer) or offset table, because the virtual base's location isn't a fixed compile-time offset.
- ⭐ **The most-derived class is responsible for constructing the virtual base**, directly — intermediate classes' initializers for the virtual base are *ignored*.
  ```cpp
  class Bat : public Mammal, public WingedAnimal {
  public:
      Bat() : Animal(), Mammal(), WingedAnimal() {}  // Bat inits Animal itself
  };
  ```
  Consequence: a virtual base **must** have an accessible default constructor, or every most-derived class must initialize it explicitly.
- Construction order: virtual bases first (depth-first, left-to-right), then non-virtual bases, then members.
- ⭐ `static_cast` from a **virtual base** down to derived is **illegal** — you must use `dynamic_cast`.

### 🎯 Interview

- **"When do you use it?"** → Almost only to solve the diamond. In practice, prefer interface-only multiple inheritance (stateless ABCs), where the diamond causes no data duplication problem.
- **⭐ "Does virtual inheritance have anything to do with virtual functions?"** → No, unrelated mechanisms — the keyword is just reused.

---

## 4.18 The diamond problem

Arises with hybrid/multiple inheritance:

```
      Animal          Bat gets TWO copies of Animal
      /    \          → Bat::Mammal::Animal::age
  Mammal  WingedAnimal   Bat::WingedAnimal::Animal::age
      \    /
       Bat
```

```cpp
class Animal { public: int age; };
class Mammal : public Animal {};
class WingedAnimal : public Animal {};
class Bat : public Mammal, public WingedAnimal {};

Bat b;
// b.age = 5;   // ❌ ERROR: ambiguous
```

**Two problems:** (1) **ambiguity** at the call site, (2) **data duplication** — two independent `age` fields that can silently disagree.

**Resolutions:**

1. **Virtual inheritance** — the real fix; one shared sub-object.
   ```cpp
   class Mammal : virtual public Animal {};
   class WingedAnimal : virtual public Animal {};
   ```
2. **Explicit scope resolution** — fixes ambiguity only, duplication remains: `b.Mammal::age = 5;`
3. **Redesign** — usually best: replace multiple inheritance with composition, or make the shared base a **stateless interface** (no data → no duplication problem).

### 🎯 Interview

- **"Java has no diamond problem — why?"** → No multiple *class* inheritance; only multiple *interface* inheritance (stateless). Default methods reintroduce a mild version, resolved by explicit override.
- **⭐ "Virtual inheritance solves data duplication — does it solve function ambiguity?"** → Only for the *shared base's* functions. If `Mammal` and `WingedAnimal` both override `speak()`, calling `b.speak()` is **still ambiguous**; `Bat` must override it and pick.
- **Trade-off answer they want:** virtual inheritance costs size + indirection + constructor complexity. Don't reach for it reflexively.

---
---

# PART 5 — Value Semantics: Copy, Move, Smart Pointers

---

## 5.1 Copy constructor

A constructor that creates a new object as a copy of an existing one of the same type.

```cpp
class Buffer {
    int* data_;
    size_t size_;
public:
    Buffer(size_t n) : data_(new int[n]{}), size_(n) {}

    Buffer(const Buffer& other)                     // ⭐ copy constructor
        : data_(new int[other.size_]), size_(other.size_) {
        std::copy(other.data_, other.data_ + size_, data_);   // DEEP copy
    }
    ~Buffer() { delete[] data_; }
};
```

**Signature:** `T(const T&)`.

**⭐ Why must the parameter be a reference?** Passing by value would itself require a copy → which calls the copy constructor → infinite recursion. The compiler rejects `T(T)` outright.

**When is it called?**

1. `T b = a;` or `T b(a);` — initialization from an existing object
2. Passing an argument **by value**
3. Returning by value (often elided)
4. Throwing/catching an exception by value

**Not called** for `T b; b = a;` — that's the **copy assignment operator**.

**⭐ Shallow vs deep copy:** the compiler-generated copy constructor is **memberwise** — for a raw pointer it copies the *address*, so two objects own the same memory → **double free** on destruction. If your class owns a resource, you must write a deep copy (or use RAII members that copy correctly).

### 🎯 Interview

- **⭐ "Copy constructor vs assignment operator?"** → The copy ctor builds a *new* object from raw storage. Assignment overwrites an *existing*, already-constructed object → must free old resources and handle **self-assignment** (`if (this == &other) return *this;`); copy-and-swap handles both for free (7.6).
- **"Why `const` in `const T&`?"** → So you can copy from temporaries and `const` objects, and to promise you won't modify the source.
- **⭐ "Will the copy ctor always run when returning by value?"** → No — **copy elision / RVO**. Since **C++17**, elision of prvalue returns is *mandatory*. Don't put observable side effects in a copy constructor and expect a fixed count.
- **⭐ "When does the compiler NOT generate one?"** → It's implicitly **deleted** if a member/base has a deleted or inaccessible copy ctor (e.g. `std::unique_ptr`, `std::mutex`), or if you user-declare a **move** constructor/assignment. It's generated but **deprecated** if you declare a destructor or copy assignment.
- **"Copy vs move constructor?"** → `T(T&&)` steals the resource from an rvalue (cheap pointer transfer) instead of duplicating; it must leave the source valid and destructible. Mark it `noexcept` so `std::vector` will actually use it on reallocation.

---

## 5.2 Rule of Three / Five / Zero

- **⭐ Rule of Three:** if you need any one of **destructor**, **copy constructor**, **copy assignment**, you almost certainly need all three (they all indicate manual resource ownership).
- **⭐ Rule of Five (C++11):** add **move constructor** and **move assignment**.
- **⭐ Rule of Zero (best):** design so you need **none** — use `std::string`, `std::vector`, `std::unique_ptr` as members and let the compiler generate everything correctly.

**The five special member functions:**

```cpp
class T {
    ~T();                          // destructor
    T(const T&);                   // copy constructor
    T& operator=(const T&);        // copy assignment
    T(T&&) noexcept;               // move constructor
    T& operator=(T&&) noexcept;    // move assignment
};
```

**⭐ Generation rules (a favourite deep question):**
- Declaring **any** copy operation or a **destructor** suppresses implicit generation of the **move** operations.
- Declaring **any** move operation **deletes** the copy operations.
- So: declare none, or declare all five explicitly.

---

## 5.3 How to protect an object from copying

**Modern C++ (C++11+) — `= delete`:**

```cpp
class NonCopyable {
public:
    NonCopyable() = default;
    NonCopyable(const NonCopyable&)            = delete;
    NonCopyable& operator=(const NonCopyable&) = delete;
};
```

Keep them **`public`** — a deleted-but-public function produces the clear error *"call to deleted constructor"*, whereas private ones may report the confusing *"is private"* first.

**Pre-C++11 idiom (you'll see it in legacy code):** declare them `private` and **never define them** — member/friend uses fail at *link* time, everyone else at *compile* time. Or inherit from `boost::noncopyable`.

**⭐ The critical subtlety most candidates miss — moves:**

Declaring (even deleting) a copy constructor **suppresses the implicit generation of the move constructor and move assignment**. So the class above is not just non-copyable — it's **non-movable** too, and `T x = std::move(y);` binds to the *deleted copy ctor* and fails.

If you want **move-only** (usually what you actually want — like `unique_ptr` or `std::thread`):

```cpp
class MoveOnly {
public:
    MoveOnly() = default;
    MoveOnly(const MoveOnly&)            = delete;
    MoveOnly& operator=(const MoveOnly&) = delete;
    MoveOnly(MoveOnly&&) noexcept            = default;   // ✅ explicitly restore moves
    MoveOnly& operator=(MoveOnly&&) noexcept = default;
};
```

**Easiest route of all:** give the class a **move-only member** (e.g. `std::unique_ptr`) — copying is then implicitly deleted for you and moves are generated automatically. The **Rule of Zero** in action.

### 🎯 Interview

- **⭐ "Why `= delete` instead of private?"** → (a) Clearer compiler errors; (b) works even for *members and friends*, which private does not; (c) a compile-time error, not a link-time one; (d) `= delete` works on **any** function, e.g. banning an unwanted overload: `void f(char) = delete;`.
- **"Why would you want a non-copyable class?"** → Unique resources: mutexes, file handles, sockets, threads, singletons, `unique_ptr`. Copying them is meaningless or actively harmful (double-close).
- **Real STL examples to cite:** `std::mutex` (non-copyable **and** non-movable), `std::unique_ptr` and `std::thread` (move-only).

---

## 5.4 lvalues and rvalues

These are **value categories** — a property of every *expression* (not of types or variables). They answer: *"does this expression name a persistent object, or is it a temporary I'm allowed to plunder?"*

**The simple mental model:**
- **lvalue** = has a **name/identity**, you can take its **address**, it persists beyond the expression. *(Historically: can appear on the Left of `=`.)*
- **rvalue** = a **temporary**, no name, about to be destroyed — safe to steal from.

```cpp
int x = 10;

x            // lvalue — named, &x is valid
10           // rvalue (prvalue) — literal, &10 is invalid
x + 1        // rvalue — temporary result
getValue()   // rvalue if it returns by value
getRef()     // lvalue if it returns T&
std::move(x) // rvalue (xvalue) — an lvalue cast to look like a temporary
"hello"      // ⚠️ lvalue! string literals are arrays with static storage
++x          // lvalue (returns x itself)
x++          // rvalue (returns a copy of the old value)
```

**Binding rules:**

```cpp
int&  lr  = x;      // ✅ lvalue ref  ← lvalue
// int& lr2 = 10;   // ❌ lvalue ref cannot bind to rvalue
const int& cr = 10; // ✅ const lvalue ref CAN bind to rvalue (lifetime extended)
int&& rr = 10;      // ✅ rvalue ref  ← rvalue
// int&& rr2 = x;   // ❌ rvalue ref cannot bind to lvalue
```

**Why this matters:** it's the entire foundation of **move semantics**. If the compiler knows an expression is an rvalue, it knows nobody will look at that object again, so instead of a deep copy it can **steal the internals** (swap pointers) — turning an O(n) copy into an O(1) move.

**The full C++11 taxonomy** (know it if they push):

```
        expression
        /        \
   glvalue      rvalue          glvalue = has identity
    /    \      /    \          rvalue  = can be moved from
lvalue    xvalue    prvalue
```

- **lvalue** — identity, **not** movable (`x`, `*p`, `arr[0]`)
- **xvalue** ("eXpiring") — identity **and** movable (`std::move(x)`, a function returning `T&&`)
- **prvalue** ("pure rvalue") — no identity, movable (`10`, `x+1`, `T()`)

### 🎯 Interview

- **⭐ THE classic question: "Is a named rvalue reference an lvalue or an rvalue?"**
  ```cpp
  void f(std::string&& s) {   // s is declared as an rvalue REFERENCE
      g(s);                   // but s ITSELF is an LVALUE (it has a name!)
      g(std::move(s));        // ✅ this is how you forward it as an rvalue
  }
  ```
  **"If it has a name, it's an lvalue."** Memorize this line. Almost everyone gets it wrong.
- **"Why can `const T&` bind to a temporary but `T&` can't?"** → Binding a non-const reference to a temporary would let you modify an object about to vanish — almost always a bug. `const&` is read-only, so it's safe, and the temporary's lifetime is extended.
- **"Why is `"hello"` an lvalue?"** → It's a `const char[6]` with **static storage duration** — it has an address and outlives the expression.
- **"What does `T&&` mean in `template<typename T> void f(T&& x)`?"** → **Not** an rvalue reference — a **forwarding (universal) reference**, because `T` is deduced. See 5.5.

---

## 5.5 `std::move` and `std::forward`

**Neither one moves or forwards anything. Both are just casts**, evaluated at compile time, generating zero instructions.

### `std::move` — unconditional cast to rvalue

```cpp
template <typename T>
constexpr std::remove_reference_t<T>&& move(T&& t) noexcept {
    return static_cast<std::remove_reference_t<T>&&>(t);
}
```

It says: *"I no longer care about this object — feel free to steal from it."* A **permission slip**, not an action. The stealing happens inside the move constructor that overload resolution then selects.

```cpp
std::string a = "a very long string that heap-allocates";
std::string b = a;             // COPY — a still valid and unchanged
std::string c = std::move(a);  // MOVE — c steals a's buffer; a is valid-but-unspecified
```

### `std::forward` — conditional cast, preserving value category

Solves the **perfect forwarding** problem: a wrapper must pass its argument on *exactly as it received it* — lvalues stay lvalues, rvalues stay rvalues, `const` stays `const`.

```cpp
template <typename T>
void wrapper(T&& arg) {           // T&& in a DEDUCED context = forwarding reference
    target(std::forward<T>(arg)); // lvalue in → lvalue out; rvalue in → rvalue out
}
```

Without `forward`, `arg` (being named) is always an **lvalue**, so `target` would always copy — even when the caller passed a temporary.

**⭐ The machinery — reference collapsing:**

| Deduced `T` | `T&&` collapses to | Meaning |
|---|---|---|
| `X&` (caller passed an **lvalue**) | `X& &&` → **`X&`** | `forward<X&>` casts to lvalue ref → copy |
| `X` (caller passed an **rvalue**) | `X&&` | `forward<X>` casts to rvalue ref → move |

Rule: **`&` wins.** `& &`, `& &&`, `&& &` → `&`. Only `&& &&` → `&&`.

**When to use which:**

| Situation | Use |
|---|---|
| Concrete rvalue reference parameter `T&&` (non-deduced) | `std::move` |
| Forwarding reference `T&&` in a template (deduced) | `std::forward<T>` |
| A local variable you're done with | `std::move` |

```cpp
class Widget {
    std::string name_;
public:
    Widget(std::string n) : name_(std::move(n)) {}   // ✅ take by value + move
};
```

### 🎯 Interview

- **⭐ "Does `std::move` move anything?"** → **No.** A `static_cast` to `T&&`. Zero runtime cost, generates no code. Scott Meyers: it should have been called `rvalue_cast`.
- **⭐ "What happens if you `std::move` a `const` object?"**
  ```cpp
  const std::string s = "hi";
  std::string t = std::move(s);   // silently COPIES!
  ```
  `std::move(s)` yields `const std::string&&`, which cannot bind to `string(string&&)` — but *can* bind to `string(const string&)`. **You get a silent copy, no error.**
- **⭐ "What's the state of a moved-from object?"** → **Valid but unspecified.** You may destroy it or assign to it; you must **not** assume its value. (`std::string` may be empty — or, with SSO, may still hold the old characters. `unique_ptr`/`shared_ptr` are specified to be null.)
- **⭐ "Why do move constructors need `noexcept`?"** → `std::vector` reallocation must be **strongly exception-safe**. If your move ctor could throw mid-way, the vector has half-moved elements and cannot roll back — so it falls back to **copying** (`std::move_if_noexcept`). Mark move ops `noexcept` or lose the performance you wrote them for.
- **"When would you use `std::forward` outside a `T&&` parameter?"** → Essentially never. Using `forward` on a non-deduced type, or `move` on a forwarding reference, are both bugs.

---

## 5.6 Smart pointers — what exists

**Smart pointers** are RAII wrappers around a raw pointer: they own the resource and release it in their destructor. Cleanup becomes **automatic and exception-safe** — the #1 cause of leaks is an early `return` or a thrown exception skipping your `delete`.

| Smart pointer | Ownership | Cost | Use for |
|---|---|---|---|
| **`std::unique_ptr<T>`** | **Exclusive** — exactly one owner | Zero overhead (same size/speed as a raw ptr) | The default. ~90% of cases. |
| **`std::shared_ptr<T>`** | **Shared** — reference counted | 2 pointers wide + atomic refcount | Genuinely shared ownership, unclear lifetime |
| **`std::weak_ptr<T>`** | **None** — non-owning observer | 2 pointers wide | Breaking `shared_ptr` **cycles**; caches |
| ~~`std::auto_ptr`~~ | Broken (copy = transfer) | — | ☠️ Deprecated C++11, **removed in C++17** |

```cpp
auto u = std::make_unique<Widget>(args);    // C++14
auto s = std::make_shared<Widget>(args);    // C++11
std::weak_ptr<Widget> w = s;                // observes, doesn't keep alive
```

**Why `auto_ptr` was removed:** its *copy constructor* silently transferred ownership, so `f(myAutoPtr)` left your variable null. It also broke horribly in STL containers, which copy freely. `unique_ptr` + move semantics fixed this properly.

### 🎯 Interview

- **⭐ "Why prefer `make_unique`/`make_shared` over `new`?"** Three reasons:
  1. **Exception safety.** `f(unique_ptr<A>(new A), g())` — pre-C++17, the compiler could evaluate `new A`, then `g()` (which throws), then the `unique_ptr` ctor → **leak**. `make_unique` is a single call, so no such window.
  2. **No repetition** of the type.
  3. `make_shared` does **one allocation** for object + control block instead of two → faster, better locality.
- **⭐ "Downside of `make_shared`?"** → Object and control block share one allocation, so the object's memory can't be released until the last **`weak_ptr`** dies too. Bad for large objects with long-lived weak refs. Also **can't use a custom deleter** with `make_shared`.
- **"Do smart pointers have overhead?"** → `unique_ptr` with the default deleter: **zero**. `shared_ptr`: 2× size plus **atomic** refcount ops on copy/destroy.
- **"When do you still use a raw pointer?"** → As a **non-owning** observer/parameter. `void draw(Widget* w)` is fine and idiomatic. Raw pointers are only banned for *ownership*.

---

## 5.7 `unique_ptr` — implementation and how single ownership is enforced

Conceptually about 20 lines:

```cpp
template <typename T, typename Deleter = std::default_delete<T>>
class unique_ptr {
    T* ptr_;
    Deleter del_;                              // usually empty → EBO, costs 0 bytes
public:
    explicit unique_ptr(T* p = nullptr) noexcept : ptr_(p) {}
    ~unique_ptr() { if (ptr_) del_(ptr_); }    // RAII

    // ⭐ THE ENFORCEMENT:
    unique_ptr(const unique_ptr&)            = delete;
    unique_ptr& operator=(const unique_ptr&)  = delete;

    // Move = ownership TRANSFER
    unique_ptr(unique_ptr&& o) noexcept : ptr_(o.ptr_) { o.ptr_ = nullptr; }
    unique_ptr& operator=(unique_ptr&& o) noexcept {
        if (this != &o) { reset(); ptr_ = o.ptr_; o.ptr_ = nullptr; }
        return *this;
    }

    T& operator*()  const { return *ptr_; }
    T* operator->() const { return ptr_; }
    T* get()        const noexcept { return ptr_; }
    explicit operator bool() const noexcept { return ptr_ != nullptr; }

    T* release() noexcept { T* t = ptr_; ptr_ = nullptr; return t; }  // give up ownership
    void reset(T* p = nullptr) { T* old = ptr_; ptr_ = p; if (old) del_(old); }
};
```

**⭐ How single ownership is guaranteed — two mechanisms working together:**

1. **Compile-time:** the copy constructor and copy assignment are `= delete`. Any attempt to duplicate the pointer is a **compiler error**. No runtime check, no runtime cost.
2. **Runtime:** the move operations **null out the source**. So after a move, exactly one `unique_ptr` holds a non-null pointer — the invariant "at most one owner" is preserved by construction.

**Why it's zero-overhead:** `sizeof(std::unique_ptr<T>) == sizeof(T*)` when the deleter is stateless, thanks to the **Empty Base Optimization** (the deleter is an empty base, not a member). A stateful deleter (a capturing lambda, a function pointer) makes it bigger.

### 🎯 Interview

- **⭐ "Is the deleter part of the type?"** → **Yes**: `unique_ptr<T, D>`. That's why `unique_ptr<FILE, decltype(&fclose)>` is a different type from `unique_ptr<FILE>`. Contrast with `shared_ptr`, where the deleter is **type-erased** into the control block, so `shared_ptr<T>` is one type regardless of deleter.
- **"`release()` vs `reset()`?"** → `release()` returns the raw pointer and **stops owning it — no deletion** (you're now responsible). `reset()` **deletes** the current object and optionally adopts a new one.
- **"`unique_ptr<T[]>`?"** → A partial specialization that calls `delete[]` and provides `operator[]` but not `operator*`/`operator->`. (In practice, prefer `std::vector`.)
- **"Can `unique_ptr` be stored in a `std::vector`?"** → ✅ Yes — since C++11 containers only require **movable** elements. This is *the* idiomatic polymorphic collection: `std::vector<std::unique_ptr<Shape>>`.
- **"Can you convert `unique_ptr` → `shared_ptr`?"** → ✅ `shared_ptr<T> s = std::move(u);` — one-way only; the reverse is impossible (you can't prove you're the last owner).

---

## 5.8 `shared_ptr` and `weak_ptr`

`shared_ptr` implements **shared ownership**: the object lives until the *last* owner goes away, via a **reference count** in a heap-allocated **control block**.

```
 sp1 ──┐                     ┌──────────── CONTROL BLOCK ───────────┐
       ├──▶ [ Widget ]  ◀────│ strong count : 2   (# of shared_ptr) │
 sp2 ──┘                     │ weak count   : 1   (# of weak_ptr)   │
                             │ deleter, allocator (type-erased)     │
 wp1 ───────────────────────▶└──────────────────────────────────────┘
```

**A `shared_ptr` object is two pointers wide:** one to the managed object, one to the control block. (`sizeof(shared_ptr<T>) == 16` on 64-bit.)

**Lifecycle rules:**
- Copy a `shared_ptr` → **strong count ++**
- Destroy/reset a `shared_ptr` → **strong count --**
- **strong count hits 0** → the **object is destroyed** (deleter runs)
- **weak count also hits 0** → the **control block** itself is freed

**⭐ How the counter is synchronized (the actual question):**

The reference counts are **`std::atomic`** integers, updated with atomic increment/decrement (typically `fetch_add` with `memory_order_relaxed` for increments, `acq_rel` for the final decrement so the destructor sees all prior writes). This is a **lock-free hardware-level** operation (`lock xadd` on x86), not a mutex.

**This gives exactly one guarantee — and you must state the limits:**

| What | Thread-safe? |
|---|---|
| The **reference count** itself | ✅ Yes — atomic |
| Copying/destroying **distinct** `shared_ptr` objects pointing to the same target | ✅ Yes |
| The **pointed-to object** `T` | ❌ **No** — you must synchronize it yourself |
| Reading *and* writing the **same** `shared_ptr` instance from multiple threads | ❌ **No** — data race. Use `std::atomic<std::shared_ptr<T>>` (C++20) or `std::atomic_load/store` (deprecated C++20) |

### 🎯 Interview

- **⭐ "What's the cost of `shared_ptr`?"** → 2× pointer size, a heap allocation for the control block, and **atomic RMW on every copy and destruction**. Atomics are expensive on multi-core (cache-line ping-pong). Hence: *`unique_ptr` by default; `shared_ptr` only when ownership is genuinely shared.*
- **⭐ "What is a circular reference and how do you fix it?"**
  ```cpp
  struct Node {
      std::shared_ptr<Node> next;
      std::shared_ptr<Node> prev;   // ☠️ cycle → counts never reach 0 → LEAK
  };
  ```
  Fix: make the back-pointer a **`std::weak_ptr`**. Classic: parent holds `shared_ptr` to children, children hold `weak_ptr` to parent.
- **"How do you use a `weak_ptr`?"** → It can't be dereferenced. Call **`lock()`**, which atomically returns a `shared_ptr` (null if the object is gone). `expired()` alone is racy — always use `lock()`.
- **⭐ "What happens here?"**
  ```cpp
  Widget* raw = new Widget;
  std::shared_ptr<Widget> a(raw);
  std::shared_ptr<Widget> b(raw);   // ☠️ TWO independent control blocks → DOUBLE FREE
  ```
  **Never create a `shared_ptr` from a raw pointer more than once.** Always copy an existing `shared_ptr`. A favourite trick question.
- **⭐ "What is `enable_shared_from_this`?"** → How a class safely hands out a `shared_ptr` to itself. Returning `shared_ptr<T>(this)` creates a second control block (same double-free bug). Inherit from `std::enable_shared_from_this<T>` and call `shared_from_this()` — it reuses the existing control block. Caveat: only valid *after* the object is already owned by a `shared_ptr`.
- **"Is `use_count()` reliable?"** → Only for debugging. In multithreaded code it's stale the instant it returns.

---

## 5.9 Can we copy a `unique_ptr` or pass it around?

**Copy: no. Move: yes.** That's the entire point of the type.

```cpp
auto a = std::make_unique<Widget>();

// auto b = a;                      // ❌ compile error: copy ctor is deleted
auto b = std::move(a);              // ✅ ownership transferred; a is now nullptr

if (!a) std::cout << "a is empty\n";   // true
```

**How to pass it — the four idioms (what they're really testing):**

```cpp
// 1. TRANSFER ownership into the function → by value
void take(std::unique_ptr<Widget> p);
take(std::move(a));                  // must std::move at the call site

// 2. Just USE the object, no ownership → raw pointer or reference ✅ preferred
void use(Widget& w);
void use(const Widget* w);
use(*a);                             // a still owns it

// 3. RETURN ownership → by value, no std::move needed
std::unique_ptr<Widget> make() {
    auto p = std::make_unique<Widget>();
    return p;                        // implicit move / NRVO — do NOT write std::move(p)
}

// 4. REASSIGN the caller's pointer → unique_ptr&
void replace(std::unique_ptr<Widget>& p);
```

**Rule of thumb:** if a function doesn't care about ownership, it should take `Widget&` or `Widget*` — **not** a smart pointer. Taking `const unique_ptr<Widget>&` is a code smell.

### 🎯 Interview

- **⭐ "Why is `return std::move(p);` bad?"** → It **disables NRVO** (the compiler can no longer construct directly into the return slot) and turns a potential zero-copy into a forced move. Returning a local by value already moves implicitly.
- **"State of a moved-from `unique_ptr`?"** → Guaranteed **`nullptr`**. (`unique_ptr`/`shared_ptr` are the exception — for most types, moved-from is "valid but *unspecified*".)
- **"Can `unique_ptr` be a class member?"** → Yes, and it makes the class **move-only** automatically. If you need it copyable, write a copy ctor that deep-copies (often via a virtual `clone()`).
- **"Why does `std::move` work if you can't copy?"** → `std::move` doesn't move — it **casts** to `unique_ptr&&`, selecting the move constructor instead of the deleted copy constructor.

---
---

# PART 6 — Templates

**Templates** = blueprints for generating functions or classes parameterized by **type** (or value). They give **generic, type-safe code with zero runtime overhead** — the alternatives being macros (no type safety) or `void*` (no type safety, plus indirection).

**Function template:**

```cpp
template <typename T>
T maxOf(const T& a, const T& b) { return (a > b) ? a : b; }

maxOf(3, 7);            // T deduced as int
maxOf<double>(1, 2.5);  // explicit
```

**Class template:**

```cpp
template <typename T, size_t N>       // type param + non-type param
class Array {
    T data_[N];
public:
    T& operator[](size_t i) { return data_[i]; }
    constexpr size_t size() const { return N; }
};

Array<int, 10> a;
```

**⭐ How it works:** the compiler performs **instantiation** — generating a separate concrete function/class for each set of template arguments actually used. Nothing is generated for unused templates. This is why:

- **⭐ Template definitions normally must live in the header**, not a `.cpp`. The compiler needs the full definition at the point of instantiation. (Alternative: explicit instantiation in the `.cpp`.)
- Error messages are notoriously long — errors surface at instantiation, deep inside library code.
- Code bloat is possible (many instantiations).

**Specialization:**

```cpp
template <typename T> class Store { /* generic */ };
template <> class Store<bool> { /* FULL specialization for bool */ };
template <typename T> class Store<T*> { /* PARTIAL specialization for pointers */ };
```

### 🎯 Interview

- **⭐ "Templates vs macros?"** → Templates are type-safe, scoped, debuggable, understood by the compiler (not the preprocessor), and participate in overload resolution.
- **⭐ "Can you partially specialize a function template?"** → **No.** Class templates support partial specialization; function templates support only **full** specialization — use **overloading** instead (that's the idiomatic answer).
- **⭐ "Templates vs runtime polymorphism?"** → Templates = compile-time, duck-typed, inlinable, zero cost, but code bloat + header-only + types must be known at compile time. Virtuals = runtime flexibility, one copy of code, but vtable indirection and no inlining.
- **"`typename` vs `class` in a template parameter?"** → Interchangeable there. But `typename` is **required** to disambiguate a **dependent type**: `typename T::iterator it;`.
- **⭐ "What's SFINAE?"** → "Substitution Failure Is Not An Error": if substituting template args produces an invalid signature, that candidate is silently dropped instead of erroring. Basis of `std::enable_if`. **C++20 replaces most of this with concepts** (`template <std::integral T>`), which are readable and give sane errors.
- **Know the names:** variadic templates (`template <typename... Args>`), perfect forwarding (`T&&` + `std::forward`), **CRTP** (`class D : public Base<D>` — static polymorphism), `constexpr` / `if constexpr`, template template parameters.

---
---

# PART 7 — Exceptions & Exception Safety

---

## 7.1 Basics and stack unwinding

```cpp
double divide(int a, int b) {
    if (b == 0) throw std::invalid_argument("division by zero");
    return double(a) / b;
}

try {
    divide(1, 0);
}
catch (const std::invalid_argument& e) { std::cerr << e.what(); }
catch (const std::exception& e)        { std::cerr << e.what(); }  // base — must come LAST
catch (...)                            { std::cerr << "unknown"; }
```

**⭐ What `throw` actually does — "stack unwinding":**

1. A copy of the thrown object is placed in a special exception area.
2. The runtime walks up the call stack looking for a matching handler.
3. **For every frame it leaves, it runs the destructors of all fully-constructed automatic objects** — this is the mechanism that makes RAII work.
4. If no handler is found anywhere → **`std::terminate()`** → `abort()`.

> ⭐ **Only automatic (stack) objects are cleaned up.** Anything held in a raw pointer **leaks**. This single fact is the entire argument for RAII.

**Matching rules:** handlers are tried **in source order**, and the first *compatible* one wins — there's no "best match" like overload resolution. So **derived classes must be caught before base classes**, or the base handler shadows them (most compilers warn).

Only these conversions apply in matching: derived→base, non-const→const, array/function decay. **No arithmetic conversions** — `throw 42;` will *not* be caught by `catch (long)`.

---

## 7.2 The catch rules that get asked

**⭐ Always catch by `const&`.**

```cpp
catch (std::exception e)         // ☠️ SLICING — a derived exception is truncated to the base,
                                 //    so e.what() gives the wrong message. Also copies.
catch (const std::exception& e)  // ✅ no slice, no copy, polymorphic what()
```

**⭐ `throw;` vs `throw e;` — the rethrow trap:**

```cpp
catch (const std::exception& e) {
    log(e);
    throw;      // ✅ rethrows the ORIGINAL exception object, preserving its dynamic type
    // throw e; // ☠️ throws a COPY sliced to std::exception — type information LOST
}
```

**`catch (...)`** catches everything but gives you no object. Use it for cleanup + `throw;`, or as a last-resort barrier at a thread/`main` boundary. Never swallow silently.

---

## 7.3 The standard exception hierarchy

```
std::exception                          ← catch (const std::exception&) catches all of these
├── std::logic_error          (bugs — preventable by checking preconditions)
│   ├── invalid_argument
│   ├── domain_error
│   ├── length_error
│   └── out_of_range          ← vector::at(), map::at()
├── std::runtime_error        (conditions only detectable at runtime)
│   ├── range_error
│   ├── overflow_error / underflow_error
│   └── std::system_error     ← thrown by std::thread, filesystem
├── std::bad_alloc            ← operator new
├── std::bad_cast             ← dynamic_cast on a reference
├── std::bad_typeid
├── std::bad_function_call
└── std::bad_weak_ptr
```

`what()` is `virtual const char* what() const noexcept`.

**⭐ Custom exceptions — inherit from `std::runtime_error`, not `std::exception`:**

```cpp
class ConfigError : public std::runtime_error {
public:
    explicit ConfigError(const std::string& msg)
        : std::runtime_error("config: " + msg) {}   // base stores/manages the string for you
};
```

Deriving straight from `std::exception` forces you to manage the message buffer yourself — and if `what()` allocates, it can throw, which is forbidden (`what()` is `noexcept`).

---

## 7.4 `noexcept`

```cpp
void f() noexcept;                       // promises not to throw
void g() noexcept(sizeof(int) == 4);     // conditional
static_assert(noexcept(f()));            // the noexcept OPERATOR — compile-time query
```

**If a `noexcept` function throws → `std::terminate()` immediately.** No unwinding is guaranteed, so destructors may not run.

**Why it matters (three concrete payoffs):**

1. **⭐ `std::vector` reallocation** uses your move constructor **only if it's `noexcept`** — otherwise it copies, to preserve the strong guarantee. Forgetting `noexcept` on a move ctor silently costs you all your move performance.
2. The compiler can **omit unwinding tables** for the function → smaller, faster code.
3. It's part of the interface contract.

**Implicitly `noexcept`:** destructors, and implicitly-generated special members when their subobjects' are.

**Mark `noexcept`:** destructors, move operations, `swap`, and simple accessors. **Don't** mark something `noexcept` you can't guarantee — you can't loosen it later without breaking callers.

> Historical note: C++98 **dynamic exception specifications** (`void f() throw(int);`) were deprecated in C++11 and **removed in C++17**. Only `throw()` survived as a synonym for `noexcept`, and it's gone in C++20.

---

## 7.5 ⭐ Exception safety guarantees — the core of this topic

Four levels, from strongest to weakest. **Know the names and be able to classify code.**

| Guarantee | Meaning |
|---|---|
| **1. No-throw (nofail)** | The operation **never throws**. Always succeeds. *Required for: destructors, `swap`, move ops, deallocation.* |
| **2. Strong** | **Commit-or-rollback.** If it throws, the program state is **exactly as before** — no side effects. *e.g. `vector::push_back`.* |
| **3. Basic** | If it throws, **no leaks and all invariants hold** — but the state may have changed in an unspecified (still valid) way. **The minimum acceptable level.** |
| **4. None** | Anything may happen: leaks, corrupted invariants. **Unacceptable.** |

```cpp
// BASIC only — if the second push_back throws, the first element is still added
void addPair(std::vector<int>& v, int a, int b) {
    v.push_back(a);
    v.push_back(b);
}

// STRONG — work on a copy, then commit with a no-throw swap
void addPairStrong(std::vector<int>& v, int a, int b) {
    std::vector<int> tmp = v;    // may throw — v untouched
    tmp.push_back(a);            // may throw — v untouched
    tmp.push_back(b);            // may throw — v untouched
    v.swap(tmp);                 // ✅ noexcept → the commit point
}
```

**⭐ The universal recipe for the strong guarantee:** *do all the throwing work on a copy/temporary, then commit with a **non-throwing** operation (`swap` or a pointer assignment).*

---

## 7.6 Copy-and-swap

The canonical strongly-safe assignment operator:

```cpp
class Buffer {
    int* data_; size_t size_;
public:
    void swap(Buffer& o) noexcept {           // ✅ must be noexcept
        std::swap(data_, o.data_);
        std::swap(size_, o.size_);
    }
    Buffer& operator=(Buffer rhs) {           // ⭐ BY VALUE — the copy happens here
        swap(rhs);                            //    if it throws, *this is untouched
        return *this;
    }                                         //    rhs's destructor frees the old data
};
```

**Three wins at once:** **strong exception safety**, **self-assignment safety for free**, and it handles **both copy and move assignment** (the by-value parameter is move-constructed from an rvalue).

---

## 7.7 Exceptions in constructors and destructors

### Constructor: throwing is **correct and idiomatic**

A constructor has no return value, so **throwing is the only way to report failure**. The alternative — a half-built "zombie" object requiring an `isValid()` check — is far worse.

```cpp
class File {
    std::FILE* f_;
public:
    explicit File(const char* path) : f_(std::fopen(path, "r")) {
        if (!f_) throw std::runtime_error("cannot open file");
    }
    ~File() { if (f_) std::fclose(f_); }
};
```

**⭐ The critical consequence:** if a constructor throws, **the object was never fully constructed, so its destructor is NEVER called.** However:

- All **fully-constructed members and base classes ARE destroyed** (in reverse order).
- Anything the constructor body allocated into a **raw pointer leaks**.

```cpp
class Bad {
    int* a_; int* b_;
public:
    Bad() {
        a_ = new int[100];
        b_ = new int[100];   // if this throws → a_ LEAKS (~Bad never runs)
    }
};

class Good {
    std::vector<int> a_{100};   // ✅ RAII members clean themselves up
    std::vector<int> b_{100};
};
```

**The prevention is RAII**, not try/catch.

To catch a failure from a **member initializer list**, use a **function-try-block** — but note it **must** rethrow (you cannot "recover" into a valid object):

```cpp
Widget::Widget() try : member_(risky()) { }
catch (...) { log(); throw; }   // implicit rethrow anyway
```

### Destructor: throwing is **effectively forbidden**

Since **C++11, destructors are implicitly `noexcept`**. Throwing from one calls **`std::terminate()`** — instant program death.

**Why the rule exists:** during **stack unwinding** from an exception, destructors run. If one of them throws, you now have two exceptions in flight and the language has no way to choose → `terminate`.

**How to prevent it:**

```cpp
~Connection() noexcept {
    try { flush(); close(); }
    catch (...) { /* log and swallow — never propagate */ }
}
```

And for operations that *can* legitimately fail (like closing a file), provide an **explicit `close()`** the user can call and check, with the destructor as a silent last-resort fallback. (Exactly what `std::ofstream` does.)

### 🎯 Interview

- **⭐ "If a constructor throws, is the destructor called?"** → **No** for that object; **yes** for its already-constructed bases and members. Highest-frequency question in this area.
- **"What about `new`?"** → `Widget* p = new Widget;` — if the *constructor* throws, `operator delete` is called automatically to release the raw memory, and `p` is never assigned. No leak of the allocation, but any resources the ctor grabbed still leak.
- **"Can you throw in a destructor if you really want to?"** → You must mark it `~T() noexcept(false)`. Almost always a design error, and it breaks all standard containers.
- **"Two-phase construction?"** → The anti-pattern of an empty ctor + `init()`. Avoid: it produces invalid objects, defeats RAII, and every user must remember to call `init()`.

---

## 7.8 What do exceptions cost?

Modern implementations use the **"zero-cost" / table-driven** model:

- **When no exception is thrown: essentially zero runtime cost** — no checks on the happy path. The cost is *binary size* (unwind tables in a separate section) and some lost optimization opportunity.
- **When one IS thrown: very expensive** — table lookup, stack walking, RTTI type matching. Think microseconds — orders of magnitude slower than a return.

**Conclusion:** exceptions are for **exceptional** conditions, not control flow. Never `throw` in a hot loop for an expected outcome.

**Alternatives to know:** return codes (easy to ignore, pollute signatures) · `std::optional<T>` (C++17, "no value") · `std::expected<T,E>` (C++23, "value or error" without exceptions) · `std::error_code`. Some domains (games, embedded, HFT) compile with `-fno-exceptions` — mention the trade-off: then `new` can't report failure and the STL is largely unusable.

---

## 7.9 Exceptions and threads

**⭐ An exception cannot propagate across a thread boundary.** If it escapes the thread function, `std::terminate` is called.

```cpp
std::exception_ptr ep;
std::thread t([&]{
    try { risky(); }
    catch (...) { ep = std::current_exception(); }   // capture
});
t.join();
if (ep) std::rethrow_exception(ep);                  // rethrow on this thread
```

**`std::async`/`std::future` do this for you** — an exception in the task is stored in the shared state and **rethrown when you call `future::get()`**. A strong argument for `async` over a raw `thread`.

### 🎯 Interview (Part 7 summary questions)

- **⭐ "Why catch by const reference?"** → Avoids **slicing**, avoids a copy, preserves polymorphism.
- **⭐ "`throw;` vs `throw e;`?"** → Original object vs **sliced copy**.
- **⭐ "What is stack unwinding?"** → Destroying automatic objects frame-by-frame while searching for a handler. Why RAII works and why raw `new` leaks.
- **⭐ "Explain the exception safety guarantees."** → The four-level table, plus: *"I aim for **basic** everywhere and **strong** where it's cheap; destructors, swap and moves must be **no-throw**."*
- **⭐ "How would you make `operator=` exception safe?"** → **Copy-and-swap.** Have the code ready.
- **"What happens if an exception is never caught?"** → `std::terminate` → `std::abort`. Whether the stack is unwound first is **implementation-defined** — so your destructors may never run. Catch at `main`/thread boundaries if cleanup matters.
- **"Does `new` throw?"** → Yes, `std::bad_alloc`. `new (std::nothrow) T` returns `nullptr` instead. Checking `if (!p)` after a plain `new` is dead code.
- **"Can you throw in a `noexcept` function?"** → It compiles, but at runtime → `std::terminate`.
- **"Exceptions vs error codes?"** → Exceptions: can't be ignored, don't pollute the return type, work in constructors/operators, zero cost on the happy path. Error codes: predictable cost, no unwind machinery, better for expected failures and `-fno-exceptions`. Modern middle ground: `std::expected`.

---
---

# PART 8 — STL: Containers, Iterators, Algorithms

---

## 8.1 `vector` vs `list`

| | `std::vector` | `std::list` |
|---|---|---|
| Structure | **Contiguous** dynamic array | **Doubly-linked** list |
| Random access `v[i]` | ✅ **O(1)** | ❌ not supported (O(n) traversal) |
| `push_back` | **O(1) amortized** | O(1) |
| `push_front` | ❌ O(n) | ✅ O(1) |
| Insert/erase in middle | O(n) — must shift | **O(1)** *given an iterator* |
| Memory per element | element only | element **+ 2 pointers** + allocator overhead |
| Allocations | few (geometric growth) | **one per element** |
| Cache locality | ✅ **excellent** | ❌ terrible (pointer chasing) |
| Iterator stability | ❌ realloc invalidates all | ✅ **stable** — only the erased one dies |
| `splice` (O(1) merge/move) | ❌ | ✅ |

**⭐ The answer interviewers actually want:** *"Use `vector` by default — nearly always."* The Big-O table lies. On modern hardware memory latency dominates: `vector` walks contiguous memory with hardware prefetching, while `list` chases pointers across the heap, missing cache almost every step. Benchmarks consistently show **`vector` beats `list` even for middle insertion** up to surprisingly large sizes, because the O(n) `memmove` is far cheaper than the O(n) traversal needed to *find* the insertion point, plus one allocation per node.

**Use `list` only when:** you need **iterator/reference stability** across insertions and erasures, you need **O(1) `splice`**, or elements are huge/non-movable.

### 🎯 Interview

- **⭐ "Why is `vector::push_back` O(1) *amortized*?"** → When `size() == capacity()` it allocates a bigger buffer (**typically 2× — MSVC uses 1.5×**) and moves everything. Doubling means a reallocation of cost n happens only every n pushes, so the cost **averages to O(1) per push**. A single push can still be O(n).
- **"How do you avoid reallocation?"** → `reserve(n)` up front. **`reserve` changes capacity, not size**; `resize` changes size (and constructs elements).
- **"How do you actually free a vector's memory?"** → `clear()` does **not** release capacity. Use `shrink_to_fit()` (non-binding) or the swap trick: `std::vector<T>().swap(v);`.
- **"What about `deque`?"** → Chunked storage: O(1) random access **and** O(1) push_front/push_back. push_front/push_back don't invalidate **references** (though they do invalidate iterators). Good middle ground; slightly slower indexing than vector.
- **"`forward_list`?"** → Singly linked; one pointer per node, no `size()`, forward-only. Use when memory is truly tight.
- **⭐ "What's special about `std::vector<bool>`?"** → It's a **bit-packed specialization**, not a real container: `operator[]` returns a **proxy object**, not `bool&`. So `auto x = v[0];` gives a proxy, `&v[0]` isn't a `bool*`, and it breaks generic code. Use `std::deque<bool>`, `std::vector<char>`, or `std::bitset`. Famous gotcha — great to volunteer unprompted.

---

## 8.2 `map` vs `unordered_map`

| | `std::map` | `std::unordered_map` |
|---|---|---|
| Structure | **Red-black tree** (balanced BST) | **Hash table** (buckets + chaining) |
| Ordering | ✅ **sorted by key** | ❌ arbitrary |
| Lookup / insert / erase | **O(log n)** guaranteed | **O(1) average**, ⚠️ **O(n) worst** |
| Key requirement | `operator<` (strict weak ordering) | `std::hash` **+** `operator==` |
| Range queries | ✅ `lower_bound`, `upper_bound`, ordered iteration | ❌ |
| Memory | ~3 pointers + colour per node | bucket array + node per element (usually more) |
| Iterator invalidation | **Never** on insert; only the erased one | **Rehash invalidates all iterators** (but **not** references/pointers to elements) |
| Since | C++98 | C++11 |

**How to choose:**
- Need **sorted order**, range queries, or `lower_bound`? → **`map`**.
- Need **raw lookup speed**, order irrelevant? → **`unordered_map`**.
- Need **worst-case guarantees** (real-time, or adversarial/untrusted keys)? → **`map`** — hash tables degrade to O(n) on collisions.
- **Small n (say < 30)?** → `map` often *wins*, and a sorted `std::vector<std::pair<K,V>>` usually beats both (cache locality again).

### 🎯 Interview

- **⭐ "When is `unordered_map` slower than `map`?"** Three cases: (1) **small n**, where hashing costs more than a couple of comparisons; (2) **expensive/poor hash function** — a bad hash collapses everything into one bucket → O(n); (3) during a **rehash**, which is O(n) and unpredictable (latency spikes).
- **⭐ "What is a hash collision and how does `unordered_map` handle it?"** → The standard mandates **separate chaining** (buckets of linked nodes) — which is *why* references stay valid across a rehash: only the bucket array is rebuilt, the nodes are relinked, not moved.
- **"What triggers a rehash?"** → When `load_factor() = size/bucket_count` exceeds `max_load_factor()` (default 1.0). Pre-empt with `reserve(n)`.
- **⭐ "What's the danger of `operator[]` on a map?"** → It **default-constructs and inserts** a value if the key is missing — so it's a *mutating* operation and doesn't exist on a `const` map. `if (m["missing"] == 0)` silently grows your map. Use `find()`, `count()`, `contains()` (C++20), or `at()` (throws) to query.
- **"`insert` vs `emplace` vs `try_emplace`?"** → `emplace` constructs in place; but on a duplicate key it may **construct then discard** (and can move-from your argument). **`try_emplace`** (C++17) doesn't touch the arguments if the key exists — the correct choice for move-only values.
- **"`map` vs `multimap`?"** → `multimap` allows duplicate keys and has no `operator[]`. Same for `set`/`multiset`.
- **⭐ "Is `std::map` a hash map?"** → **No** — a common confusion with Java's `Map` / C#'s `Dictionary`. `std::map` is a **tree**.

---

## 8.3 When does `push_back()` invalidate vector iterators?

**⭐ Precise rule — memorize this:**

> **If `push_back` causes a reallocation (i.e. `size() == capacity()` before the call), ALL iterators, pointers, and references into the vector are invalidated.**
> **If it does NOT reallocate, only the `end()` iterator is invalidated;** all other iterators, pointers, and references remain valid.

```cpp
std::vector<int> v{1,2,3};
std::cout << v.size() << " " << v.capacity();   // e.g. 3 3

auto it = v.begin();
v.push_back(4);            // size == capacity → REALLOCATES
// *it;                    // ☠️ UNDEFINED BEHAVIOUR — dangling
```

```cpp
std::vector<int> v;
v.reserve(100);            // capacity 100
v.push_back(1);
auto it = v.begin();
v.push_back(2);            // no reallocation
std::cout << *it;          // ✅ still valid (but the old end() is not)
```

**Why:** reallocation allocates a *new* buffer, moves elements into it, and frees the old one. Every iterator/pointer still refers to the freed memory.

**The classic bug:**

```cpp
for (auto it = v.begin(); it != v.end(); ++it)
    if (*it == x) v.push_back(*it);   // ☠️ may invalidate `it` AND the cached end()
```

**Fixes:** call `reserve()` first, use **indices** instead of iterators, or collect into a temporary and append afterwards.

**⭐ Full invalidation cheat sheet:**

| Operation | Invalidates |
|---|---|
| `push_back` / `emplace_back` (realloc) | **everything** |
| `push_back` / `emplace_back` (no realloc) | `end()` only |
| `reserve` / `resize` (grow, realloc) | **everything** |
| `insert(pos, …)` | everything if realloc; else everything **at or after `pos`** |
| `erase(pos)` | everything **at or after `pos`** |
| `clear`, `assign`, `operator=` | **everything** |
| `pop_back` | the erased element and `end()` |
| `shrink_to_fit` | **everything** (may reallocate) |
| **`std::list`** insert | **nothing** |
| **`std::list` / `map` / `set`** erase | only the erased element |
| **`unordered_map`** rehash | **all iterators**; references/pointers stay valid |
| **`deque`** push_front/back | **all iterators**; references/pointers stay valid |

### 🎯 Interview

- **⭐ "Does `push_back` invalidate references too?"** → Yes, on reallocation — **iterators, pointers, and references** all die together. Many people only mention iterators.
- **⭐ "Why does `deque::push_back` invalidate iterators but not references?"** → It may allocate a new chunk and grow the internal map of chunk pointers (killing iterators), but the **existing elements never move**, so references stay valid.
- **"How do you erase while iterating?"** → The **erase–remove idiom** (8.7), or C++20 `std::erase(v, x)`. For node-based containers: `it = c.erase(it);`.
- **"What growth factor does `vector` use?"** → Implementation-defined: **libstdc++/libc++ use 2×, MSVC uses 1.5×**. (1.5 allows reusing previously freed blocks; 2 never can.)

---

## 8.4 Making your class usable as a key in `map` and `unordered_map`

The two containers need **completely different things** — that's the point of the question.

### For `std::map` (tree) → needs **ordering**

Provide a **strict weak ordering** via `operator<`, or pass a custom comparator.

```cpp
struct Point {
    int x, y;
    bool operator<(const Point& o) const {
        return std::tie(x, y) < std::tie(o.x, o.y);   // ✅ clean lexicographic compare
    }
};
std::map<Point, std::string> m;   // works
```

Or, without touching the class:

```cpp
struct PointLess {
    bool operator()(const Point& a, const Point& b) const { return a.x < b.x; }
};
std::map<Point, std::string, PointLess> m2;
```

**C++20 shortcut:** `auto operator<=>(const Point&) const = default;` generates all six comparisons.

**⭐ Strict weak ordering requirements** (violating them = UB, often a crash deep in the tree):

- Irreflexive: `!(a < a)`
- Asymmetric: `a < b` ⟹ `!(b < a)`
- Transitive
- Equivalence (`!(a<b) && !(b<a)`) must be transitive
- ⚠️ Common bug: `return x <= o.x;` — `<=` is **not** irreflexive and breaks the container.

> Note: `map` never uses `operator==`. Two keys are "equal" iff **neither is less than the other**.

### For `std::unordered_map` (hash table) → needs **hash + equality**

```cpp
struct Point { int x, y;
    bool operator==(const Point& o) const { return x == o.x && y == o.y; }
};

namespace std {                          // ✅ specializing std::hash is explicitly allowed
    template <> struct hash<Point> {
        size_t operator()(const Point& p) const noexcept {
            size_t h = std::hash<int>{}(p.x);
            h ^= std::hash<int>{}(p.y) + 0x9e3779b9 + (h << 6) + (h >> 2);  // boost hash_combine
            return h;
        }
    };
}
std::unordered_map<Point, std::string> um;   // works
```

Or without touching `std`:

```cpp
struct PointHash { size_t operator()(const Point& p) const noexcept { /*...*/ } };
std::unordered_map<Point, std::string, PointHash> um2;
```

**⭐ The hash–equality contract (must state this):**

> **If `a == b`, then `hash(a) == hash(b)`.**
> The reverse need not hold (that's just a collision).

Break this and lookups silently fail — the item sits in a different bucket than the one searched.

### Summary

| | `map` | `unordered_map` |
|---|---|---|
| Required | `operator<` (or comparator) | `std::hash` **and** `operator==` (or KeyEqual) |
| Property needed | strict weak ordering | hash/equality consistency |
| C++20 shortcut | `= default` on `<=>` | none — hashing is always manual |

### 🎯 Interview

- **⭐ "Why is `^` alone a bad way to combine hashes?"** → `hash(x) ^ hash(y)` is **commutative** (so `{1,2}` and `{2,1}` collide) and self-cancelling (`x ^ x == 0`, so `{5,5}` always hashes to 0). Use `hash_combine`'s golden-ratio mix.
- **"Can you legally specialize `std::hash`?"** → ✅ Yes — specializing a standard template for your **own** type is explicitly permitted. Adding *new* declarations to `namespace std` is not.
- **⭐ "What must a key type be?"** → **Copyable** and, for `map`, comparable — and the key is `const` inside the container. **Never mutate a key in place** (via `const_cast` or a `mutable` field): the container's invariant breaks and lookups fail.
- **"Why `noexcept` on the hash function?"** → `unordered_map` gives stronger exception-safety guarantees during rehash if the hash can't throw.
- **"How do you use your class as a `map` *value*?"** → It just needs to be copyable/movable — plus **default-constructible** if you use `operator[]` (which value-initializes on insert). Use `insert`/`emplace` if it isn't.

---

## 8.5 ⭐ Iterator categories

```
Input ──┐
        ├─▶ Forward ─▶ Bidirectional ─▶ Random Access ─▶ Contiguous (C++20)
Output ─┘
```

| Category | Adds | Operations |
|---|---|---|
| **Input** | read once, single pass | `*it` (read), `++`, `==` |
| **Output** | write once, single pass | `*it = v`, `++` |
| **Forward** | multi-pass | + can copy and re-traverse |
| **Bidirectional** | backwards | + `--` |
| **Random access** | jumps | + `it + n`, `it[n]`, `it1 - it2`, `<` |
| **Contiguous** (C++20) | memory adjacency | elements physically adjacent → `&*(it+n) == &*it + n` |

**⭐ Which container gives which — memorize this:**

| Container | Iterator category |
|---|---|
| `vector`, `array`, `string` | **Contiguous** (random access pre-C++20) |
| `deque` | **Random access** — but ⚠️ **not contiguous** |
| `list`, `set`, `map`, `multiset`, `multimap` | **Bidirectional** |
| `forward_list`, `unordered_*` | **Forward** |
| `istream_iterator` | Input |
| `ostream_iterator`, `back_inserter` | Output |

**Why it matters — the consequences interviewers chase:**

```cpp
std::list<int> l;
// std::sort(l.begin(), l.end());   // ❌ WON'T COMPILE — sort needs RANDOM ACCESS
l.sort();                            // ✅ list's MEMBER sort (merge sort, O(n log n))
```

```cpp
std::set<int> s;
auto it = s.lower_bound(5);               // ✅ O(log n) — uses the tree
std::lower_bound(s.begin(), s.end(), 5);  // ⚠️ O(log n) COMPARISONS but O(n) ITERATOR STEPS
                                          //    → effectively O(n). Same for std::binary_search.
```

**"Write a custom iterator — what's required?"** → The five member typedefs (`iterator_category`, `value_type`, `difference_type`, `pointer`, `reference`), plus `operator*`, `operator++`, `operator==`/`!=`. C++20 replaces the typedefs with **concepts** (`std::input_iterator` etc.).

---

## 8.6 ⭐ Member function vs free algorithm

> **Rule: if a container provides a member function with the same name as an algorithm, the member is (almost always) asymptotically better. Use it.**

| Free algorithm | Container member | Why |
|---|---|---|
| `std::find` — **O(n)** | `set::find`, `map::find` — **O(log n)** | uses the tree |
| `std::find` — **O(n)** | `unordered_map::find` — **O(1)** | uses the hash |
| `std::sort` — needs random access | `list::sort` | merge sort; also **doesn't invalidate iterators** |
| `std::remove` — only reorders | `list::remove` | actually removes nodes |
| `std::lower_bound` — O(n) steps on a set | `set::lower_bound` — O(log n) | tree descent |
| `std::count` — O(n) | `map::count` / `contains` (C++20) | O(log n) |

Using `std::find` on a `std::map` is one of the most-asked "spot the inefficiency" questions.

---

## 8.7 ⭐ The erase–remove idiom

**The key insight: algorithms operate on *iterators*, not containers — so they cannot change a container's size.**

```cpp
std::vector<int> v{1, 2, 3, 2, 4, 2};

std::remove(v.begin(), v.end(), 2);
// v is now {1, 3, 4, ?, ?, ?} and v.size() is STILL 6
//                    ^ returns an iterator to the new logical end
```

`std::remove` **shifts** the surviving elements forward and returns the new logical end. The tail elements are **valid but unspecified** (they were moved-from). Size is unchanged.

```cpp
// The erase-remove idiom:
v.erase(std::remove(v.begin(), v.end(), 2), v.end());          // ✅ actually shrinks
v.erase(std::remove_if(v.begin(), v.end(),
                       [](int x){ return x % 2 == 0; }), v.end());

// ⭐ C++20 — finally a one-liner:
std::erase(v, 2);
std::erase_if(v, [](int x){ return x % 2 == 0; });
```

**The same "doesn't actually remove" behaviour applies to:** `std::unique`, `std::remove_if`, and the partitioning algorithms. ⚠️ **`std::unique` only removes *consecutive* duplicates** — you must `sort` first to deduplicate.

**For node-based containers, erase-while-iterating uses the return value:**

```cpp
for (auto it = m.begin(); it != m.end(); ) {
    if (pred(*it)) it = m.erase(it);   // ✅ erase returns the next valid iterator
    else           ++it;
}
```

---

## 8.8 Complexity guarantees worth quoting

| Algorithm | Complexity | Notes |
|---|---|---|
| `std::sort` | **O(n log n) worst case** (guaranteed since C++11) | **Introsort** = quicksort + heapsort fallback + insertion sort for small ranges. **Not stable.** |
| `std::stable_sort` | O(n log n) with extra memory, else O(n log²n) | merge sort; **stable** |
| `std::partial_sort` | O(n log k) | top-k |
| `std::nth_element` | **O(n) average** | introselect — the right tool for "find the median / k-th" |
| `std::find` / `count` / `for_each` | O(n) | |
| `std::lower_bound` / `binary_search` | O(log n) **comparisons** | but O(n) *increments* on non-random-access iterators |
| `std::remove` / `unique` | O(n) | reorders only |
| `std::accumulate` | O(n) | |

**⭐ `std::sort` is not stable; `std::stable_sort` is.** Very common question.

**⭐ Also worth naming:** `std::sort` is typically **3–5× faster than C's `qsort`**, because the comparator is a **template parameter and gets inlined**, whereas `qsort` makes an indirect call through a function pointer. A great "why templates matter" answer.

---

## 8.9 Comparators and strict weak ordering

```cpp
std::sort(v.begin(), v.end(), [](int a, int b){ return a <= b; });   // ☠️ UB — often CRASHES
std::sort(v.begin(), v.end(), [](int a, int b){ return a <  b; });   // ✅
```

A comparator must be a **strict weak ordering**. Using `<=` breaks irreflexivity, and libstdc++'s introsort will happily run its pointer **past the end of the array** → segfault. A real, frequently-asked war story.

**`priority_queue` inversion** — trips up DSA people constantly:

```cpp
std::priority_queue<int> maxHeap;                                       // default: MAX-heap (less<>)
std::priority_queue<int, std::vector<int>, std::greater<int>> minHeap;  // greater<> → MIN-heap
```

The comparator answers *"is a lower priority than b?"* — so `greater` puts the *smallest* on top.

---

## 8.10 Lambdas, functors, and `std::function`

```cpp
int threshold = 10;
auto pred = [threshold](int x) { return x > threshold; };   // closure: unnamed class with operator()
```

**Capture modes:**

| Syntax | Meaning | Syntax | Meaning |
|---|---|---|---|
| `[x]` | copy | `[&x]` | by reference |
| `[=]` | all used vars by copy | `[&]` | all by reference |
| `[this]` | the enclosing object **by pointer** ⚠️ | `[*this]` | copy of the object (C++17) |
| `[x = expr]` | **init capture** (C++14) — enables `[p = std::move(ptr)]` for move-only captures | | |

**Facts:** `operator()` is **`const` by default** — add `mutable` to modify captures · a **capture-less** lambda implicitly converts to a **function pointer** · each lambda has a **unique, unnameable type**, hence `auto` · generic lambdas (`[](auto x)`) since C++14 · capturing `this` inside `[=]` is **deprecated in C++20**.

**⭐ The performance question — `std::function` vs a lambda:**

| | Lambda as a **template** parameter | `std::function<int(int)>` |
|---|---|---|
| Type | unique concrete type | **type-erased** wrapper |
| Call | direct, **inlinable** ✅ | virtual-like indirect call, **not inlinable** ❌ |
| Allocation | none | may **heap-allocate** if the closure is large |
| Use when | performance matters (all STL algorithms do this) | you must **store** heterogeneous callables |

```cpp
template <typename F> void apply(F f);        // ✅ inlines — how std::for_each works
void apply(std::function<void(int)> f);       // ❌ indirect call every time
```

This is exactly why STL algorithms take the comparator as a **template parameter**.

**⭐ Dangling capture — the #1 lambda bug:**

```cpp
auto makeCounter() {
    int count = 0;
    return [&count]{ return ++count; };   // ☠️ dangling — count dies at return
}
```

---

## 8.11 Insert iterators and the "output range must exist" trap

```cpp
std::vector<int> src{1,2,3}, dst;

std::copy(src.begin(), src.end(), dst.begin());              // ☠️ UB — dst is EMPTY
std::copy(src.begin(), src.end(), std::back_inserter(dst));  // ✅ push_back for each
```

Algorithms **never resize** the destination. Use `std::back_inserter` (push_back), `std::front_inserter` (push_front — `deque`/`list`), `std::inserter(c, pos)` (insert — for `set`/`map`), or `dst.resize(src.size())` first.

---

## 8.12 C++20 Ranges — the modern-C++ signal

```cpp
// Old
std::sort(v.begin(), v.end());
// Ranges
std::ranges::sort(v);                                   // no iterator pair
std::ranges::sort(people, {}, &Person::age);            // ⭐ PROJECTION — sort by a member

// Lazy, composable views — no intermediate containers, evaluated on demand
auto result = v | std::views::filter([](int x){ return x % 2 == 0; })
                | std::views::transform([](int x){ return x * x; })
                | std::views::take(3);
```

**Selling points to state:** no iterator-pair boilerplate · **projections** (sort/find by a member without writing a comparator) · **lazy evaluation** (no temporary vectors) · **composability** via `|` · **concept-checked** parameters → dramatically better error messages. Also `views::iota`, `views::reverse`, `views::split`, `ranges::to` (C++23).

---

## 8.13 Gotcha list (rapid-fire)

```cpp
// 1. accumulate's init value determines the ACCUMULATOR TYPE
std::accumulate(v.begin(), v.end(), 0);      // ☠️ int accumulator — truncates doubles / overflows
std::accumulate(v.begin(), v.end(), 0.0);    // ✅
std::accumulate(v.begin(), v.end(), 0LL);    // ✅ for large sums

// 2. Reverse iterator .base() is OFF BY ONE
//    &*rit == &*(rit.base() - 1)

// 3. std::move — two completely different things
std::move(x);                                    // the CAST (utility)
std::move(src.begin(), src.end(), dst.begin());  // the ALGORITHM (moves a range)

// 4. The two-step swap idiom (enables ADL to find a user-defined swap)
using std::swap;
swap(a, b);

// 5. Naming conventions
//    _if     → takes a predicate        (find_if, remove_if, count_if)
//    _copy   → writes to a new range    (remove_copy, replace_copy)
//    _n      → takes a count            (copy_n, fill_n, for_each_n)

// 6. std::reduce (C++17) is the PARALLEL accumulate — requires an
//    associative & commutative op; order of evaluation is unspecified

// 7. at() vs operator[]
//    vector::at()  → bounds-checked, throws std::out_of_range
//    vector::[]    → unchecked, UB out of range
//    map::at()     → throws if missing
//    map::[]       → INSERTS if missing
```

---
---

# PART 9 — Master Cheat Sheets

---

## 9.1 The "can / cannot" table

| Question | Answer |
|---|---|
| Can a **constructor** be virtual? | ❌ — vptr not set up yet. Use `clone()`. |
| Can a **destructor** be virtual? | ✅ — mandatory if deleting via a base pointer |
| Can a **destructor** be pure virtual? | ✅ — but it **must** have a definition |
| Can a **pure virtual** function have a body? | ✅ — class stays abstract |
| Can a **virtual** function be `static`? | ❌ — no `this` |
| Can a **virtual** function be `private`? | ✅ — the NVI idiom |
| Can a **constructor** be `private`? | ✅ — singleton / factory |
| Can a **constructor** throw? | ✅ — the correct way to signal failure |
| Can a **destructor** throw? | ❌ effectively — implicitly `noexcept` → `terminate` |
| Can you call a **destructor** explicitly? | ✅ — only with placement `new` |
| Can you overload on **return type**? | ❌ |
| Can you **partially specialize** a function template? | ❌ — overload instead |
| Can a **static** member function be virtual? | ❌ |
| Can a **static** member be of its own class type? | ✅ |
| Is `friend` inherited / transitive / mutual? | ❌ / ❌ / ❌ |
| Can `unique_ptr` be copied? | ❌ — move only |
| Can a **reference** be null / reseated? | ❌ / ❌ |
| Can you `std::sort` a `std::list`? | ❌ — use `list::sort` |

## 9.2 The "what actually happens" table

| Situation | Result |
|---|---|
| `delete` a derived object via base ptr, non-virtual dtor | ☠️ UB — derived dtor never runs, leak |
| Virtual call inside a constructor | Runs the **current class's** version; pure virtual + no body → **UB** |
| Constructor throws | Object's dtor **not** called; bases/members **are** destroyed |
| Destructor throws | `std::terminate` |
| Throw inside a `noexcept` function | `std::terminate` |
| Exception never caught | `std::terminate` → `abort`; unwinding **not guaranteed** |
| Exception escapes a thread function | `std::terminate` |
| `throw e;` in a catch block | **Sliced copy** — use `throw;` |
| `catch (std::exception e)` by value | **Slicing** — use `const&` |
| Signed integer overflow | ☠️ **UB** (unsigned wraps — well-defined) |
| `delete` on `new[]` memory | ☠️ UB — heap corruption |
| `push_back` when `size() == capacity()` | **All** iterators/pointers/references invalidated |
| `std::move` on a `const` object | **Silent copy**, no error |
| `return std::move(local)` | Kills NRVO — slower |
| Deleting the copy ctor | Also **suppresses** implicit move generation |
| `map["missing"]` | **Inserts** a default-constructed value |
| `std::remove(...)` | Reorders only — size unchanged |
| Comparator using `<=` in `std::sort` | ☠️ UB — often a segfault |
| `shared_ptr` built twice from the same raw pointer | ☠️ Two control blocks → double free |
| `T x;` for a struct of ints | Members are **indeterminate garbage** (`T x{}` zeroes them) |

## 9.3 The "which one / what's the difference" table

| Pair | Key difference |
|---|---|
| `const` vs `constexpr` | read-only vs **compile-time constant** |
| `constexpr` vs `consteval` | *may* be compile-time vs **must** be |
| `constexpr` vs `constinit` | compile-time + immutable vs compile-time + **mutable** |
| `inline` (hint) vs `inline` (ODR) | the ODR effect is the **guarantee**; inlining is not |
| `volatile` vs `std::atomic` | no optimization vs **atomicity + ordering**. `volatile` ≠ threading |
| Overloading vs overriding vs hiding | same scope/diff params · base-derived/same sig + virtual · derived name hides **all** base overloads |
| Early vs late binding | compile-time, static type vs **runtime, dynamic type** |
| Abstract class vs interface | may have state/impl vs pure contract |
| `unique_ptr` vs `shared_ptr` | exclusive, zero overhead vs shared, atomic refcount |
| `unique_ptr` deleter vs `shared_ptr` deleter | part of the **type** vs **type-erased** in the control block |
| `map` vs `unordered_map` | tree, ordered, O(log n) vs hash, unordered, O(1) avg |
| `vector` vs `list` | contiguous + cache-friendly vs stable iterators + O(1) splice |
| `std::sort` vs `std::stable_sort` | not stable, introsort vs stable, merge sort |
| Copy ctor vs copy assignment | builds a new object vs overwrites an existing one |
| `std::move` vs `std::forward` | unconditional cast vs **conditional**, category-preserving cast |
| `new`/`delete` vs `malloc`/`free` | ctor/dtor + typed + throws vs raw bytes + `NULL` |
| `#define` vs `constexpr` | text substitution, no type safety vs typed, scoped |
| Vtable vs vptr | **per class** vs **per object** |
| Pass by value vs `const T&` | copy vs no copy; small types by value, big by `const&` |
| `std::function` vs template lambda | type-erased, not inlinable vs concrete type, inlined |

## 9.4 Rules to recite

| Rule | Statement |
|---|---|
| **Rule of Zero** | Use RAII members; declare none of the five special members |
| **Rule of Three** | Need one of dtor / copy ctor / copy assign → need all three |
| **Rule of Five** | + move ctor / move assign |
| **Access is per-class** | Not per-object — a method sees any same-class object's privates |
| **Members init in declaration order** | Not initializer-list order |
| **If it has a name, it's an lvalue** | Even a `T&&` parameter |
| **Virtual dtor if any virtual function** | Otherwise `delete base` is UB |
| **Always `override`** | Turns silent signature mismatches into compile errors |
| **`explicit` on single-arg ctors** | Unless you want the implicit conversion |
| **`noexcept` on move ops and swap** | Or `vector` falls back to copying |
| **`const auto&` in range-for** | `auto` copies |
| **Prefer composition over inheritance** | Inheritance is the tightest coupling in the language |
| **`const&` in catch** | Avoids slicing |
| **Strong guarantee recipe** | Work on a copy, commit with a no-throw swap |
| **Hash contract** | `a == b` ⟹ `hash(a) == hash(b)` |
| **Strict weak ordering** | Comparator must use `<`, never `<=` |

---
---

# PART 10 — Question Index

Every question from the source lists, mapped to its section.

## 10.1 OOP question set

| # | Question | Section |
|---|---|---|
| 1 | What is OOP? Main features? | 4.1 |
| 2 | What is encapsulation? How achieved in C++? | 4.2 |
| 3 | What is an access specifier? Features? | 4.3 |
| 4 | What is a friend function/class? Privileges? | 4.4 |
| 5 | What is abstraction? How achieved? What is an interface? | 4.6, 4.7 |
| 6 | What is inheritance? How many types? | 4.8 |
| 7 | What is polymorphism? Types? | 4.10 |
| 8 | What is virtual inheritance? | 4.17 |
| 9 | What is the diamond problem? How to resolve? | 4.18 |
| 10 | Function overloading and operator overloading | 3.11, 3.12 |
| 11 | Can a virtual function be called from a constructor? | 4.16 |
| 12 | What is a copy constructor? | 5.1 |
| 13 | What is a template? | Part 6 |
| 14 | Namespace | 2.10 |

## 10.2 C++ basics / OOP / STL question set

| Question | Section |
|---|---|
| Difference between references and pointers | 1.6 |
| Difference between memory allocation on stack and heap | 1.1 |
| What kinds of smart pointers exist? | 5.6 |
| How is `unique_ptr` implemented? How is single ownership forced? | 5.7 |
| How does `shared_ptr` work? How is the refcount synchronized? | 5.8 |
| Can we copy `unique_ptr` or pass it between objects? | 5.9 |
| What are rvalue and lvalue? | 5.4 |
| What are `std::move` and `std::forward`? | 5.5 |
| Ways to access private fields of a class | 4.5 |
| Can a class inherit multiple classes? | 4.9 |
| Is a static field initialized in the class constructor? | 2.2 |
| Can an exception be thrown in a constructor/destructor? How to prevent? | 7.7 |
| What are virtual methods? | 4.11 |
| Why do we need a virtual destructor? | 4.14 |
| Difference between abstract class and interface | 4.7 |
| Can a constructor be virtual? | 4.15 |
| How is `const` used for class methods? | 3.9 |
| How to protect an object from copying | 5.3 |
| Difference between `vector` and `list` | 8.1 |
| Difference between `map` and `unordered_map` | 8.2 |
| When does `push_back()` invalidate a vector iterator? | 8.3 |
| How to modify your class to use with `map` / `unordered_map` | 8.4 |

## 10.3 The 45-question list

| # | Question | Section |
|---|---|---|
| 1 | What is C++? | 0.1 |
| 2 | Advantages of C++ | 0.2 |
| 3 | Differences between C and C++ | 0.3 |
| 4 | What is a class? | 3.1 |
| 5 | What is an object? | 3.2 |
| 6 | Features/concepts of OOP | 4.1 |
| 7 | Different data types in C++ | 0.5 |
| 8 | Storage classes in C++ | 2.1 |
| 9 | Tokens in C++ | 0.4 |
| 10 | Does C++ have automatic garbage collection? | 1.8 |
| 11 | Types of polymorphism | 4.10 |
| 12 | How is late binding implemented? | 4.12, 4.11 |
| 13 | What is a namespace? | 2.10 |
| 14 | Operations permitted on pointers | 1.3 |
| 15 | Difference between `delete[]` and `delete` | 1.4 |
| 16 | C++ access specifiers | 4.3 |
| 17 | What is a friend function? | 4.4 |
| 18 | What is a virtual function? | 4.11 |
| 19 | What is a destructor? | 3.6 |
| 20 | What is an overflow error? | 0.7 |
| 21 | What is overloading? | 3.11, 3.12 |
| 22 | What is function overriding? | 4.13 |
| 23 | What is virtual inheritance? | 4.17 |
| 24 | What is a constructor? | 3.4 |
| 25 | What is a pointer? | 1.2 |
| 26 | What is the scope resolution operator? | 2.9 |
| 27 | What is a pure virtual function? | 4.7 |
| 28 | Difference between a struct and a class | 3.3 |
| 29 | What is a virtual destructor? | 4.14 |
| 30 | Program: print Hello world | 0.9 |
| 31 | Program: enter a string and find its length | 0.9 |
| 32 | What is the `this` pointer? | 3.8 |
| 33 | Function overloading vs operator overloading | 3.11, 3.12 |
| 34 | What is a static member? | 2.2, 3.10 |
| 35 | What is a reference variable? | 1.5, 1.6 |
| 36 | What is a copy constructor? | 5.1 |
| 37 | Does C++ support String as a primitive type? | 0.6 |
| 38 | What is the diamond problem and where does it occur? | 4.18 |
| 39 | What is an inline function? | 2.7 |
| 40 | Use of the `volatile` keyword | 2.8 |
| 41 | Pass by value and pass by reference | 1.7 |
| 42 | What is the `auto` keyword? | 2.6 |
| 43 | Types of loops in C++ | 0.8 |
| 44 | What is an abstract class? | 4.7 |
| 45 | What is a default constructor? | 3.5 |

## 10.4 Advanced-keyword / exceptions / STL set

| Question | Section |
|---|---|
| `const` / `constexpr` / `consteval` / `constinit` / `static` | 2.2 – 2.5 |
| Exception handling and exception safety | Part 7 |
| STL algorithms and iterators | 8.5 – 8.13 |

---
---

# PART 11 — Not Covered Here

Two topics remain before this set is complete for a mid/senior C++ interview:

### 1. Multithreading & concurrency ⭐⭐ *(see `cpp-concurrency-notes.md`)*
`std::thread` / `std::jthread` · `mutex`, `lock_guard`, `unique_lock`, `scoped_lock`, `shared_mutex` · `condition_variable` and the spurious-wakeup/predicate rule · **data race vs race condition** · deadlock and the four Coffman conditions · `std::atomic` and memory orders · `async` / `future` / `promise` / `packaged_task` · thread pools · false sharing.

This is the single most common "second round" topic after OOP.

### 2. Design patterns in C++
Thread-safe **Singleton** (Meyers), **Factory** / Abstract Factory, **Observer**, **RAII**, **PIMPL**, **CRTP**, **Strategy**, **Visitor** (and `std::variant` + `std::visit` as the modern alternative).

Shorter than the rest, and it reuses everything above — a good final revision pass.

### Smaller things worth a look afterwards
- **Move semantics deep-dive:** `std::exchange`, move-and-swap, when the compiler generates move ops.
- **Type casts:** `static_cast` / `dynamic_cast` / `const_cast` / `reinterpret_cast` — when each is correct, and why C-style casts are banned.
- **RTTI:** `typeid`, `type_info`, cost, why many codebases disable it.
- **Memory layout / alignment:** padding, `alignas`, `alignof`, cache lines, false sharing.
- **Compilation model:** preprocessor → compile → assemble → link; ODR; include guards vs `#pragma once`; translation units; static vs dynamic libraries.
- **`std::optional` / `std::variant` / `std::any` / `std::tuple`** (C++17 vocabulary types).

---

*End of notes.*

