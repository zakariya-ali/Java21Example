package org.example.java21;

/**
 * JEP 440 - Record Patterns (final in Java 21).
 *
 * <p>Records (Java 16) are simple immutable data carriers. Record patterns let us
 * "deconstruct" a record straight into its components inside {@code instanceof}
 * and {@code switch}, instead of calling the accessor methods one by one.
 * Patterns can also be NESTED, so we can pull data out of records inside records.</p>
 */
public class RecordPatternsDemo {

    record Point(int x, int y) {
    }

    enum Colour { RED, GREEN, BLUE }

    record ColouredPoint(Point point, Colour colour) {
    }

    record Rectangle(ColouredPoint topLeft, ColouredPoint bottomRight) {
    }

    // ---- BEFORE Java 21 --------------------------------------------------
    static String describeOldWay(Object obj) {
        if (obj instanceof Point) {
            Point p = (Point) obj;          // manual cast
            int x = p.x();                  // manual accessor calls
            int y = p.y();
            return "Point at (" + x + ", " + y + ")";
        }
        return "Unknown";
    }

    // ---- Java 16: pattern matching for instanceof (removes the cast) -----
    static String describeJava16(Object obj) {
        if (obj instanceof Point p) {
            return "Point at (" + p.x() + ", " + p.y() + ")";
        }
        return "Unknown";
    }

    // ---- Java 21: record pattern (removes the accessors too) -------------
    static String describeJava21(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            return "Point at (" + x + ", " + y + ")";
        }
        return "Unknown";
    }

    // ---- Java 21: NESTED record patterns ---------------------------------
    // We reach two levels deep (Rectangle -> ColouredPoint -> Point) in one line.
    static int width(Object obj) {
        if (obj instanceof Rectangle(ColouredPoint(Point(var x1, var y1), var c1),
                                     ColouredPoint(Point(var x2, var y2), var c2))) {
            return Math.abs(x2 - x1);
        }
        return 0;
    }

    public static void run() {
        Demo.title("JEP 440 - Record Patterns");

        Object p = new Point(3, 4);
        System.out.println("Old way    : " + describeOldWay(p));
        System.out.println("Java 16 way: " + describeJava16(p));
        System.out.println("Java 21 way: " + describeJava21(p));

        Rectangle rect = new Rectangle(
                new ColouredPoint(new Point(1, 1), Colour.RED),
                new ColouredPoint(new Point(11, 6), Colour.BLUE));
        System.out.println("Nested pattern -> width of rectangle = " + width(rect));
    }
}
