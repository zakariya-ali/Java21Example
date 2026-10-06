package org.example.solid.isp;

/**
 * I - Interface Segregation Principle.
 *
 * <p>"Clients should not be forced to depend on methods they do not use."
 * Prefer several small, focused interfaces over one big "fat" one.</p>
 *
 * <p>BAD: {@link MachineBad} forces EVERY device to implement print, scan and fax.
 * A simple printer has to write dummy/throwing methods for scan and fax.</p>
 *
 * <p>GOOD: split into {@link Printer}, {@link Scanner} and {@link Fax}. Each device
 * implements only what it really supports, and code that only needs to print
 * depends only on {@link Printer}.</p>
 */
public class InterfaceSegregationDemo {

    // ------------------------------------------------------------------ BAD
    interface MachineBad {
        void print(String doc);

        void scan(String doc);

        void fax(String doc);
    }

    static class SimplePrinterBad implements MachineBad {
        public void print(String doc) {
            System.out.println("  Printing " + doc);
        }

        public void scan(String doc) {
            throw new UnsupportedOperationException("Can't scan");   // forced to implement
        }

        public void fax(String doc) {
            throw new UnsupportedOperationException("Can't fax");    // forced to implement
        }
    }

    // ----------------------------------------------------------------- GOOD
    interface Printer {
        void print(String doc);
    }

    interface Scanner {
        void scan(String doc);
    }

    interface Fax {
        void fax(String doc);
    }

    static class SimplePrinter implements Printer {
        public void print(String doc) {
            System.out.println("  SimplePrinter printing " + doc);
        }
    }

    static class OfficeMachine implements Printer, Scanner, Fax {
        public void print(String doc) {
            System.out.println("  OfficeMachine printing " + doc);
        }

        public void scan(String doc) {
            System.out.println("  OfficeMachine scanning " + doc);
        }

        public void fax(String doc) {
            System.out.println("  OfficeMachine faxing " + doc);
        }
    }

    /** Only needs printing, so only depends on Printer - works with both devices. */
    static void printReport(Printer printer) {
        printer.print("report.pdf");
    }

    public static void run() {
        System.out.println("\n--- I: Interface Segregation Principle ---");
        printReport(new SimplePrinter());
        printReport(new OfficeMachine());
        new OfficeMachine().scan("contract.pdf");
    }
}
