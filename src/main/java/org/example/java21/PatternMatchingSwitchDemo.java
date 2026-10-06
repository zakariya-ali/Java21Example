package org.example.java21;

/**
 * JEP 441 - Pattern Matching for switch (final in Java 21).
 *
 * <p>A {@code switch} can now test the TYPE of a value (not just constants),
 * deconstruct records, add extra conditions with {@code when} (called "guards"),
 * and handle {@code null} directly. Combined with SEALED interfaces (Java 17),
 * the compiler can check that every possible case is covered - so no
 * {@code default} branch is needed, and adding a new subtype causes a compile error
 * everywhere it isn't handled yet.</p>
 */
public class PatternMatchingSwitchDemo {

    // A sealed interface: ONLY these three records are allowed to implement Shape.
    sealed interface Shape permits Circle, Square, Rectangle {
    }

    record Circle(double radius) implements Shape {
    }

    record Square(double side) implements Shape {
    }

    record Rectangle(double width, double height) implements Shape {
    }

    // ---- BEFORE Java 21: chain of if/else + instanceof -------------------
    static double areaOldWay(Shape shape) {
        if (shape instanceof Circle c) {
            return Math.PI * c.radius() * c.radius();
        } else if (shape instanceof Square s) {
            return s.side() * s.side();
        } else if (shape instanceof Rectangle r) {
            return r.width() * r.height();
        }
        throw new IllegalArgumentException("Unknown shape");
    }

    // ---- Java 21: switch with type patterns + record patterns ------------
    // No default needed: the compiler knows Shape is sealed and all 3 cases are covered.
    static double area(Shape shape) {
        return switch (shape) {
            case Circle(double r) -> Math.PI * r * r;
            case Square(double side) -> side * side;
            case Rectangle(double w, double h) -> w * h;
        };
    }

    // ---- Java 21: guards ("when") and null handling ----------------------
    static String classify(Object obj) {
        return switch (obj) {
            case null -> "It's null (before Java 21 this threw NullPointerException)";
            case Integer i when i < 0 -> "Negative integer: " + i;
            case Integer i when i == 0 -> "Zero";
            case Integer i -> "Positive integer: " + i;
            case String s when s.isBlank() -> "Blank string";
            case String s -> "String of length " + s.length();
            case Shape s -> "A shape with area " + String.format("%.2f", area(s));
            default -> "Something else: " + obj.getClass().getSimpleName();
        };
    }

    public static void run() {
        Demo.title("JEP 441 - Pattern Matching for switch");

        Shape[] shapes = {new Circle(1), new Square(2), new Rectangle(2, 5)};
        for (Shape s : shapes) {
            System.out.printf("%-35s old=%.2f  new=%.2f%n", s, areaOldWay(s), area(s));
        }

        Object[] things = {null, -5, 0, 42, "   ", "hello", new Square(3), 3.14};
        for (Object o : things) {
            System.out.println(classify(o));
        }
    }
}
