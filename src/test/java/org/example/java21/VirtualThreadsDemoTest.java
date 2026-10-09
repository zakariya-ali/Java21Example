package org.example.java21;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VirtualThreadsDemoTest {

    @Test
    void virtualThreadIsVirtual() {
        Thread vt = Thread.ofVirtual().unstarted(() -> { });
        Thread platform = Thread.ofPlatform().unstarted(() -> { });

        assertTrue(vt.isVirtual());
        assertFalse(platform.isVirtual());
    }

    @Test
    void virtualThreadsRunBlockingTasksConcurrently() {
        // 1,000 tasks x 200ms. Run one after another this would take 200 seconds.
        long ms = VirtualThreadsDemo.timeTasks(
                Executors.newVirtualThreadPerTaskExecutor(), 1_000, Duration.ofMillis(200));

        assertTrue(ms < 5_000, "Expected all tasks to wait concurrently, took " + ms + " ms");
    }
}
