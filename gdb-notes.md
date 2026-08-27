# GDB Notes (C++ on Ubuntu / WSL)

Revision notes covering: line-by-line stepping through a simple program, and modifying
variable values while the program is paused.

---

## Part 1 — Line-by-line stepping

### Step 0 — Install the tools (once)

```bash
sudo apt update
sudo apt install build-essential gdb
```

- `build-essential` gives you `g++`
- `gdb` is the debugger

Verify:

```bash
g++ --version
gdb --version
```

### Step 1 — Write the program

Work inside the Linux filesystem (`~/`), **not** `/mnt/c/...` — it's much faster and
avoids permission oddities.

```bash
mkdir ~/gdbdemo && cd ~/gdbdemo
nano add.cpp
```

```cpp
#include <iostream>

int add(int a, int b) {
    int sum = a + b;
    return sum;
}

int main() {
    int x = 5;
    int y = 7;
    int result = add(x, y);
    std::cout << "Result: " << result << std::endl;
    return 0;
}
```

Save with `Ctrl+O`, `Enter`, exit with `Ctrl+X`.

### Step 2 — Compile with debug symbols

```bash
g++ -g -O0 add.cpp -o add
```

| Flag | Why |
| --- | --- |
| `-g` | Embeds line numbers and variable names into the binary |
| `-O0` | No optimization, so lines execute in the order you wrote them |

Without `-g`, GDB sees only machine addresses — no variable names, no line numbers.

### Step 3 — Start GDB

```bash
gdb ./add
```

You get a `(gdb)` prompt. The program has been *loaded* but has not started running yet.

### Step 4 — Stop at the first line

```
(gdb) start
```

`start` = "set a breakpoint at `main` and run". The program launches and freezes on the
first line of `main`:

```
Temporary breakpoint 1, main () at add.cpp:9
9           int x = 5;
```

Important: that line has **not** run yet — it is *about* to run.

### Step 5 — The stepping loop

| Command | Short | Behavior |
| --- | --- | --- |
| `next` | `n` | Run the current line, stop at the next one. **Skips over** function calls. |
| `step` | `s` | Same, but **goes into** function calls. |

Walking through `add.cpp`:

```
(gdb) n        ->  runs int x = 5;      now on line 10
(gdb) n        ->  runs int y = 7;      now on line 11
(gdb) s        ->  ENTERS add(), now on line 4: int sum = a + b;
(gdb) n        ->  runs sum = a + b;    now on line 5: return sum;
(gdb) n        ->  returns, back in main on line 12
(gdb) n        ->  prints "Result: 12"
```

**Tip:** after typing a command once, just press **Enter** to repeat it. So press `n`,
then Enter, Enter, Enter… to walk the program.

### Step 6 — Look at values while paused

```
(gdb) print x          # or: p x
$1 = 5

(gdb) info locals      # all variables in the current function
x = 5
y = 7
result = 32767         # garbage — not assigned yet

(gdb) bt               # backtrace: where am I in the call chain
```

`result = 32767` before assignment is normal — that's uninitialized memory, and being
able to see it is a big part of why debuggers are useful.

### Step 7 — Auto-print on every step

Typing `print x` after each step gets tedious:

```
(gdb) display x
(gdb) display y
(gdb) display result
```

Now every `n` automatically shows all three values. Closest thing to "watching execution
live".

### Step 8 — See the source as you step (TUI mode)

Press **`Ctrl+X`** then **`A`** (or launch with `gdb -tui ./add`).

The terminal splits: source on top with an arrow marking the current line, `(gdb)` prompt
below. `n` visually moves the arrow down the file. `Ctrl+X A` again toggles it off.

If the display gets garbled (common in WSL): type `refresh` or press `Ctrl+L`.

### Step 9 — Finish up

```
(gdb) continue      # run to completion
(gdb) quit          # exit; confirm with y if prompted
```

### Cheat sheet

| Command | Meaning |
| --- | --- |
| `start` | Run and stop at `main` |
| `n` / `next` | Next line, over calls |
| `s` / `step` | Next line, into calls |
| `finish` | Run until current function returns |
| `p var` | Print a variable |
| `info locals` | Print all local variables |
| `display var` | Auto-print `var` after every step |
| `list` | Show source around current line |
| `bt` | Call stack |
| `c` / `continue` | Resume full speed |
| `q` / `quit` | Exit |
| `Enter` | Repeat last command |

Start with just `start`, `n`, `s`, `p`. Pick up the rest as needed.

---

## Part 2 — Modifying a variable's value while running

### The main command: `set var`

```
(gdb) set var x = 100
```

The variable now holds 100 for the rest of execution, exactly as if the program had
assigned it.

Using `add.cpp`:

```
(gdb) start
(gdb) n              # runs int x = 5;
(gdb) n              # runs int y = 7;
(gdb) p x
$1 = 5
(gdb) set var x = 100
(gdb) p x
$2 = 100
(gdb) n              # calls add(x, y)
(gdb) n
(gdb) p result
$3 = 107             # 100 + 7 — the change took effect
```

### Why `set var` and not just `set`

`set` is also GDB's *configuration* command (`set width`, `set listsize`,
`set print pretty`). If your variable is named `width`, then `set width = 80` silently
changes GDB's terminal width instead of your variable.

**Always type `set var`.** It removes the ambiguity and costs four characters.

### The shortcut: `print` also assigns

```
(gdb) p x = 100
$1 = 100
```

`print` evaluates a C++ expression, and assignment *is* an expression — so it writes the
value and shows the result in one go. Handy, but a typo like `p x = 5` when you meant
`p x == 5` will silently modify your program.

Rule of thumb: `set var` when you mean to **write**, `p` when you mean to **read**.

### What you can modify

| Target | Example |
| --- | --- |
| Local / global | `set var count = 0` |
| Struct or class member | `set var obj.total = 42` |
| Via `this` inside a method | `set var this->count = 7` |
| Pointer target | `set var *ptr = 99` |
| Pointer itself | `set var ptr = 0` (to test null handling) |
| Array element | `set var arr[3] = -1` |
| `std::string` | `set var s = "hello"` — often works, sometimes fussy |
| Function argument | `set var a = 50` while paused inside `add()` |

Expressions work too: `set var x = y * 2 + 1`.

### Three related tricks

**Force a return value** — skip a function's real work entirely:

```
(gdb) finish          # normal: run to end of function
(gdb) return 999      # instead: abandon it now, return 999 to caller
```

GDB asks for confirmation, since it discards the rest of the function.

**Call a function by hand:**

```
(gdb) p add(3, 4)
$1 = 7
```

This genuinely runs your code — side effects are real.

**Jump to a different line** (rarely a good idea, but it exists):

```
(gdb) jump 12         # skip lines, resume at line 12
```

Bypasses initialization and often corrupts state. Prefer `set var`.

### Two things that will bite you

**Optimized builds.** With `-O2`, a variable may live only in a register, or not exist at
all. You'll see `<optimized out>`, or the write silently won't stick. Always debug with
`-g -O0`.

**Const-folded values.** If the compiler already baked a constant into the machine code,
changing the variable won't change behavior — the code no longer reads it. Again, `-O0`
avoids this.

### Why this is powerful

Test edge cases without editing and recompiling:

- Force an error path: `set var fd = -1`
- Trigger an empty-input branch: `set var count = 0`
- Reproduce an overflow: `set var n = 2147483647`
- Confirm a hypothesis: patch the "wrong" value to the right one, continue, and see if
  the bug disappears

That last one is the real workflow — it turns "I think this variable is the problem" into
a proven answer in about five seconds.

---

## Part 3 — Crashes, core dumps, and post-mortem debugging

### Step 1 — A crash is a *signal*, not an error message

The program does something illegal (null deref, divide by zero, walking off an array).
The CPU traps it, tells the kernel, and the kernel sends the process a **signal**:

| Signal | Meaning | Typical cause |
| --- | --- | --- |
| `SIGSEGV` (11) | Segmentation fault | Null/dangling pointer, array way out of bounds, stack overflow |
| `SIGABRT` (6) | Abort | Failed `assert()`, uncaught exception, glibc heap corruption detected |
| `SIGFPE` (8) | Arithmetic fault | Integer divide by zero |
| `SIGBUS` (7) | Bus error | Misaligned / invalid memory mapping |
| `SIGILL` (4) | Illegal instruction | Corrupted function pointer, jumped into garbage |

The default action for all of these: **kill the process and dump core**. Hence
`Segmentation fault (core dumped)`.

Note what you *don't* get: no line number, no stack trace, no variable dump. The kernel
knows nothing about your source code.

### Step 2 — What a core dump actually is

A snapshot of the process at the instant it died, written to disk as an ELF file.
It contains:

- Full contents of process memory (heap, stack, globals)
- All CPU registers, including the **program counter** (instruction being executed) and
  the **stack pointer**
- The signal that killed it
- Thread state for every thread

It does **not** contain your source code, and (mostly) not your debug symbols.

Think of it as a crime-scene photograph: complete, frozen, and unreadable to a human.

### Step 3 — Where GDB comes in

GDB is the translator. You give it two things:

```
core dump  +  the binary that produced it (built with -g)
```

1. Reads the register set from the core, finds the program counter
2. Looks that address up in the binary's debug symbols -> "line 42 of `parser.cpp`"
3. Walks the saved stack frame by frame, mapping return addresses to function names
   -> **the backtrace**
4. Uses each frame's stack pointer + debug info describing variable layout
   -> **local variable values**

The core has the *data*; the binary's `-g` symbols have the *meaning*; GDB joins them.

### The two ways to debug a crash

#### Mode A — Live, under GDB (easy, when reproducible)

```bash
gdb ./app
(gdb) run
```

The program runs at full speed. On crash, GDB catches the signal **before** the process
dies and freezes it there:

```
Program received signal SIGSEGV, Segmentation fault.
0x... in Parser::next () at parser.cpp:42
42          return buf[i]->value;
```

The process is still alive, so everything works: `bt`, `print`, `info locals`, even
`set var` and `continue`. No core dump needed. Always prefer this when you can reproduce
the crash.

#### Mode B — Post-mortem, from a core file (when you can't reproduce)

The crash happened on a server, in CI, or once in a thousand runs. You only have the
corpse:

```bash
gdb ./app core
```

Same commands, one difference: **the process is dead**. You can inspect everything, but
you cannot step, continue, or call functions. A photograph, not a live subject.

### Enabling core dumps (usually off by default)

Most systems set the core size limit to 0, so nothing is written.

```bash
ulimit -c              # check; "0" means disabled
ulimit -c unlimited    # enable, for this shell session only
```

Then decide where cores go — a kernel setting:

```bash
cat /proc/sys/kernel/core_pattern
```

If it starts with `|` (a pipe to `apport` or `systemd-coredump`), the dump is handed to a
crash-reporting daemon. **On WSL that daemon often isn't running, so the core silently
vanishes.** Point it at a plain file instead:

```bash
sudo sysctl -w kernel.core_pattern=/tmp/core.%e.%p
```

`%e` = executable name, `%p` = PID. A crash then writes `/tmp/core.app.12345`.

Both `ulimit` and `sysctl -w` reset on reboot / new shell — fine for learning.

On distros using `systemd-coredump`, cores live in a journal instead:

```bash
coredumpctl list           # recent crashes
coredumpctl gdb            # open the newest one in GDB directly
```

### The first four commands on any crash

```
(gdb) bt
```
**Backtrace** — the call chain that led to the crash, innermost frame first. 80% of
crashes are solved right here.

```
(gdb) frame 1
```
Move up to the caller. Frame 0 is often deep inside `std::` or libc; the interesting
frame is usually yours, one or two levels up.

```
(gdb) info locals
```
All locals in the selected frame. Look for the one that's `0x0`, absurdly large, or
obviously garbage.

```
(gdb) print ptr
```
Confirm the suspect. `$1 = (Node *) 0x0` is the smoking gun.

Bonus: `bt full` = backtrace *plus* locals for every frame in one shot. Verbose, but
often the fastest single command.

### Reading common crash signatures

| What you see | What it usually means |
| --- | --- |
| `bt` shows your code, one pointer is `0x0` | Null dereference — the classic |
| Pointer is a plausible-looking but wrong address | Use-after-free / dangling pointer |
| `bt` is thousands of frames of the same function | Infinite recursion -> stack overflow |
| `SIGABRT`, `bt` shows `abort` <- `__assert_fail` | Your `assert()` fired — read the message |
| `SIGABRT`, `bt` shows `abort` <- `std::terminate` | Uncaught exception |
| `SIGABRT` with `malloc(): corrupted top size` | Heap corruption — the real bug happened earlier |
| `bt` shows `??` and no names | Built without `-g`, or the stack is smashed |

**Important:** the crash location is not always the bug location. Heap corruption and
use-after-free crash somewhere innocent, long after the actual mistake. That's when you
reach for AddressSanitizer:

```bash
g++ -fsanitize=address -g -O0 main.cpp -o app
```

ASan catches the bug at the moment it happens rather than at the eventual crash.

### Rules that make post-mortem debugging work

1. **Build with `-g`.** No debug symbols -> `bt` shows hex addresses and `??`. Useless.
2. **Keep the exact binary.** The core must be paired with the *same* build that produced
   it. Recompile and the addresses no longer line up — GDB shows nonsense.
3. **`-g` doesn't slow you down.** It only adds symbol data to the file. You can ship
   release builds as `-O2 -g` and still get real backtraces. Many production systems do
   exactly this.
4. **Source files must be reachable.** The core doesn't contain your code; GDB reads
   `.cpp` files from disk to display lines. If they moved: `directory /path/to/src`.

### One-line summary

The kernel kills your process and photographs its memory. GDB reads that photograph
against your binary's debug symbols and turns it back into filenames, line numbers, and
variable values.
