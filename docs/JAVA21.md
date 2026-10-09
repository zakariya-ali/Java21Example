# Java 21 and Virtual Threads

## What is Java 21?

Java 21 was released in **September 2023** and is an **LTS (Long-Term Support)** release.
LTS versions (8, 11, 17, 21, 25) get years of security updates, so they are the versions
companies actually upgrade to. Java 21 is the next LTS after Java 17.

It has several new features (record patterns, pattern matching for `switch`, sequenced
collections and more), but the **headline feature is Virtual Threads (JEP 444)**, and that's
what this project focuses on.

Code: `src/main/java/org/example/java21/VirtualThreadsDemo.java`. Run it with:

```
mvn compile exec:exec -Dmode=java21
```

---

## The problem virtual threads solve

Until Java 21, every Java thread was a **platform thread**: a thin wrapper around an
operating-system thread, mapped 1-to-1.

* OS threads are **expensive**: about 1 MB of stack memory each, and slow to create.
* So an application can only have a **few thousand** of them.

In a typical backend (for example a Spring Boot REST API) each request gets its own thread,
and most of that request's time is spent **waiting**: on the database, on another HTTP
service, on a file. While it waits, it holds onto an expensive OS thread that is doing nothing.

That means the server runs out of threads long before it runs out of CPU. Up to now, the two options were:

1. **Thread pools**, which limit how many requests run at once.
2. **Reactive/async code** (`CompletableFuture`, WebFlux, callbacks). This scales, but it's
   hard to read, hard to debug, and stack traces become useless.

## The solution

A **virtual thread** is a thread managed by the **JVM**, not by the operating system.

* It's tiny (a few hundred bytes to start, grows as needed), so you can have **millions**.
* Virtual threads run *on top of* a small pool of platform threads called **carrier threads**
  (by default, one per CPU core).
* When a virtual thread hits a **blocking** operation (`Thread.sleep`, socket read, JDBC call...),
  the JVM **unmounts** it from its carrier and saves its stack to the heap. The carrier is
  immediately free to run another virtual thread.
* When the blocking operation finishes, the virtual thread is **mounted** again on any free
  carrier and carries on from where it stopped.

So you write normal, simple, **blocking** code, and you get the scalability that used to
require reactive code.

```
   Virtual threads (millions)    VT1  VT2  VT3  VT4  VT5  ...  VT100000
                                  │    │    │
   Carrier threads (≈ CPU cores) [C1] [C2] [C3] ... [C8]
                                  │    │    │
   OS threads                    the operating system only sees these few
```

## Walking through the demo

### 1) Creating virtual threads: `creatingVirtualThreads()`

```java
Thread.ofVirtual().name("my-virtual-thread").start(task);   // builder
Thread.startVirtualThread(task);                             // shorthand
Thread.ofVirtual().name("worker-", 0).factory();             // ThreadFactory for libraries
Executors.newVirtualThreadPerTaskExecutor();                 // what you'll use most
```

It's still the same `java.lang.Thread` class. Existing code, `join()`, `interrupt()`,
`ThreadLocal` and so on all keep working. `isVirtual()` tells you which kind of thread you have.

### 2) They are cheap: `startManyThreads()`

The demo starts **100,000** virtual threads, each sleeping for 1 second. They all finish
in a few seconds total. Doing that with platform threads would usually crash with
`OutOfMemoryError: unable to create native thread`, or freeze the machine.

### 3) Platform pool vs virtual threads: `comparePlatformVsVirtual()`

The demo runs 10,000 tasks that each block for 100 ms.

| Executor | Result on my machine | Why |
|---|---|---|
| `newFixedThreadPool(100)` | **~11,000 ms** | Only 100 tasks can wait at once → 100 rounds × 100 ms |
| `newVirtualThreadPerTaskExecutor()` | **~300–600 ms** | All 10,000 wait at the same time → about 1 round |

Same code, same tasks. Only the executor changed.

### 4) Under the hood: `carrierThreads()`

A virtual thread's `toString()` shows which carrier it's running on, for example
`VirtualThread[#110150]/runnable@ForkJoinPool-1-worker-1`. The demo prints this before and after a
sleep. Often the worker number **changes** (`worker-1` → `worker-5`), which proves the thread was
unmounted during the sleep and resumed on a different carrier.

### Tests: `VirtualThreadsDemoTest`

* `virtualThreadIsVirtual`: `Thread.ofVirtual()` vs `Thread.ofPlatform()`.
* `virtualThreadsRunBlockingTasksConcurrently`: 1,000 tasks × 200 ms finish in under 5 seconds
  (run one after another, they would take 200 seconds).

## Points worth mentioning to your senior

* **Virtual threads make waiting cheap. They don't make computing faster.** For CPU-heavy work
  (number crunching, image processing) they give no benefit; you're still limited by cores.
* **Don't pool them.** Pools exist because platform threads are expensive. Virtual threads are
  cheap, so create one per task and throw it away.
* **Limit resources with a `Semaphore`, not a thread pool.** If the database only allows
  20 connections, use a semaphore (or the connection pool) to limit access, not the thread count.
* **Pinning (Java 21 caveat).** If a virtual thread blocks while inside a `synchronized`
  block, it gets "pinned" to its carrier and can't unmount, which reduces scalability. In Java 21
  the advice is to use `ReentrantLock` instead of `synchronized` around long blocking calls.
  (This was fixed in Java 24, JEP 491.)
* **Be careful with big `ThreadLocal`s.** With millions of threads, per-thread caches add up.
* **Spring Boot 3.2+**: turn them on with `spring.threads.virtual.enabled=true`, and then
  every incoming request is handled on a virtual thread.
