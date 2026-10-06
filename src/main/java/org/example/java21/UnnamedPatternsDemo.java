package org.example.java21;

import java.util.List;

/**
 * JEP 443 - Unnamed Patterns and Variables ({@code _}).  PREVIEW in Java 21.
 *
 * <p>"Preview" means the feature is complete but may still change, so it must be switched on
 * with {@code --enable-preview} (already configured in pom.xml). This one became final in
 * Java 22 without changes.</p>
 *
 * <p>Use {@code _} for a variable or pattern you are forced to declare but never use. It tells
 * the reader (and the compiler) "I deliberately don't care about this".</p>
 */
public class UnnamedPatternsDemo {

    record Point(int x, int y) {
    }

    record Line(Point start, Point end) {
    }

    public static void run() {
        Demo.title("JEP 443 - Unnamed Patterns and Variables (preview)");

        // In a record pattern: we only want x of the start point
        Object obj = new Line(new Point(1, 2), new Point(5, 6));
        if (obj instanceof Line(Point(var x, _), _)) {
            System.out.println("Start x = " + x);
        }

        // In a loop where only the count matters
        int count = 0;
        for (String _ : List.of("a", "b", "c")) {
            count++;
        }
        System.out.println("Counted " + count + " items");

        // In a catch block where the exception isn't used
        try {
            Integer.parseInt("not a number");
        } catch (NumberFormatException _) {
            System.out.println("Not a number - exception variable not needed");
        }
    }
}
