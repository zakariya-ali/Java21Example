package org.example.java21;

/**
 * Small helper so every demo prints a consistent heading.
 */
public final class Demo {

    private Demo() {
    }

    public static void title(String text) {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println(" " + text);
        System.out.println("=".repeat(70));
    }
}
