package org.example.solid.ocp;

import java.util.List;

/**
 * O - Open/Closed Principle.
 *
 * <p>"Software should be OPEN for extension but CLOSED for modification."
 * You should be able to add new behaviour by adding new code, not by editing
 * existing, already-tested code.</p>
 *
 * <p>BAD: {@link DiscountCalculatorBad} uses an if/else on a String. Every new
 * customer type means opening this class and editing it (and risking breaking it).</p>
 *
 * <p>GOOD: each discount is its own class implementing {@link DiscountPolicy}.
 * A new type (e.g. {@link StudentDiscount}) is a NEW class; {@link DiscountCalculator}
 * never changes.</p>
 */
public class OpenClosedDemo {

    // ------------------------------------------------------------------ BAD
    static class DiscountCalculatorBad {
        double apply(String customerType, double price) {
            if (customerType.equals("REGULAR")) {
                return price;
            } else if (customerType.equals("VIP")) {
                return price * 0.8;
            }
            // Adding "STUDENT" means editing this method...
            return price;
        }
    }

    // ----------------------------------------------------------------- GOOD
    interface DiscountPolicy {
        String name();

        double apply(double price);
    }

    static class NoDiscount implements DiscountPolicy {
        public String name() {
            return "Regular";
        }

        public double apply(double price) {
            return price;
        }
    }

    static class VipDiscount implements DiscountPolicy {
        public String name() {
            return "VIP (20% off)";
        }

        public double apply(double price) {
            return price * 0.8;
        }
    }

    /** Added later WITHOUT touching any existing class - that's the point. */
    static class StudentDiscount implements DiscountPolicy {
        public String name() {
            return "Student (10% off)";
        }

        public double apply(double price) {
            return price * 0.9;
        }
    }

    /** Closed for modification: works with any DiscountPolicy, now or in the future. */
    static class DiscountCalculator {
        double finalPrice(DiscountPolicy policy, double price) {
            return policy.apply(price);
        }
    }

    public static void run() {
        System.out.println("\n--- O: Open/Closed Principle ---");
        DiscountCalculator calculator = new DiscountCalculator();
        for (DiscountPolicy policy : List.of(new NoDiscount(), new VipDiscount(), new StudentDiscount())) {
            System.out.printf("  %-18s 100.00 -> %.2f%n", policy.name(), calculator.finalPrice(policy, 100));
        }
    }
}
