package org.example.java21;

/**
 * Runs every Java 21 feature demo in order.
 */
public class Java21Showcase {

    public static void run() throws InterruptedException {
        RecordPatternsDemo.run();
        PatternMatchingSwitchDemo.run();
        SequencedCollectionsDemo.run();
        VirtualThreadsDemo.run();
        LibraryAdditionsDemo.run();
        UnnamedPatternsDemo.run();
    }
}
