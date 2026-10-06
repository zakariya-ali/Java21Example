# What is Java 21?

Java 21 was released in **September 2023** and is an **LTS (Long-Term Support)** release.
LTS versions (8, 11, 17, 21, 25) get years of security updates, so they are the versions
companies actually upgrade to. Java 21 is the next LTS after Java 17, so moving 17 → 21
brings in everything from Java 18, 19, 20 and 21.

Some features are **final** (ready for production) and some are **preview** (complete, but may
still change; must be enabled with `--enable-preview`, which this project's `pom.xml` does).

| Feature | JEP | Status in 21 | Demo class |
|---|---|---|---|
| Virtual Threads | 444 | Final | `VirtualThreadsDemo` |
| Record Patterns | 440 | Final | `RecordPatternsDemo` |
| Pattern Matching for `switch` | 441 | Final | `PatternMatchingSwitchDemo` |
| Sequenced Collections | 431 | Final | `SequencedCollectionsDemo` |
| Unnamed Patterns & Variables (`_`) | 443 | Preview (final in 22) | `UnnamedPatternsDemo` |
| Small API additions | – | Final | `LibraryAdditionsDemo` |

Other Java 21 features not demoed here: Generational ZGC (JEP 439, a faster garbage collector
mode), String Templates (JEP 430, preview, later **withdrawn**), Structured Concurrency and
Scoped Values (JEPs 453/446, preview), and the Key Encapsulation Mechanism API (JEP 452).

All code is in `src/main/java/org/example/java21`. Run it with:

```
mvn compile exec:exec -Dmode=java21
```

---

## 1. Virtual Threads (JEP 444) — the headline feature

**Problem.** A normal Java thread ("platform thread") maps 1-to-1 onto an operating-system
thread. OS threads are heavy (~1 MB stack each), so you can only have a few thousand. In a web
server, most requests spend their time *waiting* (database, HTTP calls). While waiting, they
hold an expensive thread doing nothing. The old workaround was reactive/async code
(`CompletableFuture`, WebFlux), which is fast but hard to read and debug.

**Solution.** Virtual threads are lightweight threads managed by the JVM. When one blocks
(e.g. `Thread.sleep`, a socket read), the JVM unmounts it and lets the real OS thread
(the *carrier*) run another virtual thread. You can have **millions** of them, while still
writing plain, blocking, top-to-bottom code.

```java
Thread.ofVirtual().start(() -> ...);                // create one
Thread.startVirtualThread(() -> ...);               // shorthand
Executors.newVirtualThreadPerTaskExecutor();        // one virtual thread per task
```

**What the demo shows.** 10,000 tasks that each wait 100 ms:

* Fixed pool of 100 platform threads: ~**11 seconds** (only 100 can wait at once → 100 batches)
* Virtual thread per task: ~**0.5 seconds** (all 10,000 wait at the same time)

**Talking points**
* Virtual threads make *waiting* cheap. They don't make CPU-heavy work faster.
* Don't pool virtual threads — they're cheap; create one per task.
* In Spring Boot 3.2+ you can turn them on with `spring.threads.virtual.enabled=true`.

## 2. Record Patterns (JEP 440)

Records (Java 16) are immutable data classes: `record Point(int x, int y) {}` gives you the
constructor, getters, `equals`, `hashCode` and `toString` for free.

Record patterns let you **deconstruct** a record into its parts in one step:

```java
// Before (Java 8 style)
if (obj instanceof Point) { Point p = (Point) obj; int x = p.x(); int y = p.y(); ... }
// Java 16
if (obj instanceof Point p) { ... p.x() ... }
// Java 21
if (obj instanceof Point(int x, int y)) { ... x ... }
```

They can be **nested** — the demo reaches through `Rectangle → ColouredPoint → Point`
in one pattern. `var` can be used for component types.

## 3. Pattern Matching for `switch` (JEP 441)

`switch` used to only work on constants (numbers, strings, enums). Now it can:

* match on **type**: `case Integer i ->`
* deconstruct **records**: `case Circle(double r) ->`
* add **guards**: `case Integer i when i < 0 ->`
* handle **null**: `case null ->` (previously `switch(null)` always threw `NullPointerException`)

Combined with **sealed interfaces** (Java 17: `sealed interface Shape permits Circle, Square, Rectangle`),
the compiler knows every possible subtype, so the switch is **exhaustive** — no `default`
needed. If someone adds a new `Triangle` shape later, every switch that doesn't handle it
becomes a **compile error** instead of a runtime bug. This is the big safety win.

Order matters: more specific cases (with guards) must come before general ones, otherwise
the compiler reports the later case as "dominated".

## 4. Sequenced Collections (JEP 431)

Before Java 21, "get the last element" was different for every collection type
(`list.get(list.size()-1)`, `deque.getLast()`, and for `LinkedHashSet` there was no simple way).

Java 21 adds new interfaces to the collection hierarchy — `SequencedCollection`,
`SequencedSet`, `SequencedMap` — implemented by `List`, `Deque`, `LinkedHashSet`,
`TreeSet`, `LinkedHashMap`, `TreeMap`, etc. They all now share:

`getFirst()`, `getLast()`, `addFirst()`, `addLast()`, `removeFirst()`, `removeLast()`, `reversed()`

and maps get `firstEntry()`, `lastEntry()`, `putFirst()`, `putLast()`, `pollFirstEntry()`, `pollLastEntry()`.

`reversed()` returns a **view**, not a copy — changes to it show up in the original.

## 5. Unnamed Patterns and Variables (JEP 443, preview)

Use `_` for something you must declare but don't use:

```java
if (obj instanceof Line(Point(var x, _), _)) { ... }   // only care about x
for (String _ : list) count++;                         // loop variable not needed
catch (NumberFormatException _) { ... }                // exception not needed
```

It makes intent clear ("ignored on purpose") and stops unused-variable warnings.
Preview in 21, final in Java 22.

## 6. Smaller API additions

* `Math.clamp(value, min, max)` — keep a value inside a range.
* `StringBuilder.repeat(...)` — append something N times.
* `String.indexOf(ch, begin, end)` — search within a range.
* `String.splitWithDelimiters(regex, limit)` — split but keep the delimiters.
* `Character.isEmoji(codePoint)` and related emoji methods.
