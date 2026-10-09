package org.example.java21;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

/**
 * JEP 444 - Virtual Threads (final in Java 21). The headline feature of Java 21.
 *
 * <p><b>The problem:</b> a normal ("platform") thread is a wrapper around an operating-system
 * thread. OS threads are expensive (around 1MB of stack each), so an app can only have
 * a few thousand. In a typical web server each request waits on a database / HTTP call,
 * and while it waits it holds on to that expensive thread doing nothing.</p>
 *
 * <p><b>The solution:</b> virtual threads are lightweight threads managed by the JVM, not
 * the OS. Millions can exist at once. When a virtual thread blocks (sleep, I/O, etc.)
 * the JVM "parks" it and frees the underlying OS thread (the "carrier") to run
 * another virtual thread. We keep writing simple, blocking, easy-to-read code and
 * still get the scalability of async/reactive code.</p>
 *
 * <p>Note: virtual threads make WAITING cheap; they do not make CPU-heavy work faster.</p>
 */
public class VirtualThreadsDemo {

    private static final int TASKS = 10_000;

    /** Simulates a blocking call such as a DB query or HTTP request. */
    static void simulatedIoCall(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Submits {@code tasks} blocking calls to the executor and returns how long they all took. */
    static long timeTasks(ExecutorService executor, int tasks, Duration eachTaskWaits) {
        Instant start = Instant.now();
        // try-with-resources: ExecutorService is AutoCloseable since Java 19,
        // close() waits for all submitted tasks to finish.
        try (executor) {
            IntStream.range(0, tasks).forEach(i -> executor.submit(() -> simulatedIoCall(eachTaskWaits)));
        }
        return Duration.between(start, Instant.now()).toMillis();
    }

    // ---- 1. Ways to create a virtual thread -------------------------------
    static void creatingVirtualThreads() throws InterruptedException {
        System.out.println("\n1) Creating virtual threads");

        // a) Builder - same style as the platform-thread builder Thread.ofPlatform()
        Thread vt = Thread.ofVirtual().name("my-virtual-thread").start(() ->
                System.out.println("   Hello from " + Thread.currentThread()));
        vt.join();
        System.out.println("   isVirtual() = " + vt.isVirtual());

        // b) Shorthand
        Thread.startVirtualThread(() -> System.out.println("   Started with Thread.startVirtualThread")).join();

        // c) Factory - useful for passing to libraries that accept a ThreadFactory
        ThreadFactory factory = Thread.ofVirtual().name("worker-", 0).factory();
        Thread fromFactory = factory.newThread(() -> System.out.println("   Created by factory: " + Thread.currentThread().getName()));
        fromFactory.start();
        fromFactory.join();

        // d) Executor - the one you'll use most in real code: one new virtual thread per task
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> System.out.println("   Running inside newVirtualThreadPerTaskExecutor"));
        }
    }

    // ---- 2. They are cheap: start 100,000 at once -------------------------
    static void startManyThreads() throws InterruptedException {
        System.out.println("\n2) Starting 100,000 virtual threads that each sleep for 1 second");
        // Trying this with platform threads would likely fail with OutOfMemoryError
        // ("unable to create native thread") or bring the machine to a crawl.
        Instant start = Instant.now();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100_000; i++) {
            threads.add(Thread.startVirtualThread(() -> simulatedIoCall(Duration.ofSeconds(1))));
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.printf("   All 100,000 finished in %,d ms (not 100,000 seconds!)%n",
                Duration.between(start, Instant.now()).toMillis());
    }

    // ---- 3. Platform thread pool vs virtual threads -----------------------
    static void comparePlatformVsVirtual() {
        System.out.printf("%n3) Running %,d tasks that each block for 100ms%n", TASKS);

        long platformMs = timeTasks(Executors.newFixedThreadPool(100), TASKS, Duration.ofMillis(100));
        System.out.printf("   Platform thread pool (100 threads): %,6d ms%n", platformMs);

        long virtualMs = timeTasks(Executors.newVirtualThreadPerTaskExecutor(), TASKS, Duration.ofMillis(100));
        System.out.printf("   Virtual thread per task           : %,6d ms%n", virtualMs);
        // With 100 platform threads only 100 tasks can wait at once -> 10,000 / 100 = 100 rounds of 100ms.
        // With virtual threads all 10,000 wait at the same time -> roughly one round.
    }

    // ---- 4. What happens under the hood: mounting and unmounting ----------
    static void carrierThreads() throws InterruptedException {
        System.out.println("\n4) A virtual thread runs ON a platform \"carrier\" thread, and can move between them");
        // toString() of a virtual thread shows its carrier, e.g. VirtualThread[#22]/runnable@ForkJoinPool-1-worker-3
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            threads.add(Thread.startVirtualThread(() -> {
                String before = Thread.currentThread().toString();
                simulatedIoCall(Duration.ofMillis(50));     // blocking -> unmounted from carrier
                String after = Thread.currentThread().toString();
                System.out.println("   before sleep: " + before + "\n   after sleep : " + after);
            }));
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("   (the worker number after '@' can change: the thread was parked and resumed elsewhere)");
    }

    public static void run() throws InterruptedException {
        Demo.title("JEP 444 - Virtual Threads");
        creatingVirtualThreads();
        startManyThreads();
        comparePlatformVsVirtual();
        carrierThreads();
    }
}
