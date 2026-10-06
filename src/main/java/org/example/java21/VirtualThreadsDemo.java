package org.example.java21;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private static void simulatedIoCall() {
        try {
            Thread.sleep(Duration.ofMillis(100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static long runWith(ExecutorService executor) {
        Instant start = Instant.now();
        // try-with-resources: ExecutorService is AutoCloseable since Java 19,
        // close() waits for all submitted tasks to finish.
        try (executor) {
            IntStream.range(0, TASKS).forEach(i -> executor.submit(VirtualThreadsDemo::simulatedIoCall));
        }
        return Duration.between(start, Instant.now()).toMillis();
    }

    public static void run() throws InterruptedException {
        Demo.title("JEP 444 - Virtual Threads");

        // 1. Creating a single virtual thread
        Thread vt = Thread.ofVirtual().name("my-virtual-thread").start(() ->
                System.out.println("Hello from " + Thread.currentThread()));
        vt.join();
        System.out.println("Is virtual? " + vt.isVirtual());

        // 2. Shorthand
        Thread.startVirtualThread(() -> System.out.println("Started with Thread.startVirtualThread")).join();

        // 3. Comparing a fixed pool of 100 platform threads vs one virtual thread per task
        System.out.printf("Running %,d tasks that each block for 100ms...%n", TASKS);

        long platformMs = runWith(Executors.newFixedThreadPool(100));
        System.out.printf("Platform thread pool (100 threads): %,d ms%n", platformMs);

        long virtualMs = runWith(Executors.newVirtualThreadPerTaskExecutor());
        System.out.printf("Virtual thread per task          : %,d ms%n", virtualMs);
    }
}
