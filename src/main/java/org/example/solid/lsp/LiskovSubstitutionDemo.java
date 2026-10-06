package org.example.solid.lsp;

import java.util.List;

/**
 * L - Liskov Substitution Principle.
 *
 * <p>"If S is a subtype of T, you must be able to use an S anywhere a T is expected
 * without breaking the program." A subclass must keep the promises of its parent.</p>
 *
 * <p>BAD: {@link BirdBad} promises every bird can {@code fly()}. {@link PenguinBad} can't,
 * so it throws an exception. Any code that loops over birds and calls fly() now crashes
 * when it gets a penguin - the subclass is NOT substitutable.</p>
 *
 * <p>GOOD: only promise what is true for ALL birds. Flying is a separate capability
 * ({@link Flyer}) that only some birds have. Nobody can ask a penguin to fly because the
 * type system won't let them.</p>
 */
public class LiskovSubstitutionDemo {

    // ------------------------------------------------------------------ BAD
    static class BirdBad {
        void fly() {
            System.out.println("  Flying");
        }
    }

    static class PenguinBad extends BirdBad {
        @Override
        void fly() {
            throw new UnsupportedOperationException("Penguins can't fly!");   // breaks the parent's promise
        }
    }

    // ----------------------------------------------------------------- GOOD
    interface Bird {
        String name();

        default void eat() {
            System.out.println("  " + name() + " is eating");
        }
    }

    interface Flyer {
        void fly();
    }

    record Sparrow(String name) implements Bird, Flyer {
        public void fly() {
            System.out.println("  " + name + " is flying");
        }
    }

    record Penguin(String name) implements Bird {
    }

    public static void run() {
        System.out.println("\n--- L: Liskov Substitution Principle ---");

        System.out.println("  BAD version:");
        for (BirdBad b : List.of(new BirdBad(), new PenguinBad())) {
            try {
                b.fly();
            } catch (UnsupportedOperationException e) {
                System.out.println("  CRASH: " + e.getMessage());
            }
        }

        System.out.println("  GOOD version:");
        List<Bird> birds = List.of(new Sparrow("Sparrow"), new Penguin("Penguin"));
        birds.forEach(Bird::eat);                       // every Bird can safely eat

        List<Flyer> flyers = List.of(new Sparrow("Sparrow"));
        flyers.forEach(Flyer::fly);                     // only Flyers are asked to fly
    }
}
