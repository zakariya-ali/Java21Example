package org.example;

import org.example.java21.Java21Showcase;
import org.example.solid.SolidShowcase;

/**
 * Entry point. The project has two independent parts:
 * <ul>
 *     <li>{@code org.example.java21} - what's new in Java 21</li>
 *     <li>{@code org.example.solid}  - the SOLID design principles</li>
 * </ul>
 *
 * <p>Run everything:       {@code mvn compile exec:exec}</p>
 * <p>Run only Java 21:     {@code mvn compile exec:exec -Dmode=java21}  (or -Dmode=solid)</p>
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        String mode = args.length > 0 ? args[0] : "all";
        switch (mode) {
            case "java21" -> Java21Showcase.run();
            case "solid" -> SolidShowcase.run();
            default -> {
                Java21Showcase.run();
                SolidShowcase.run();
            }
        }
    }
}
