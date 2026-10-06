package org.example.solid.srp;

import java.util.ArrayList;
import java.util.List;

/**
 * S - Single Responsibility Principle.
 *
 * <p>"A class should have only ONE reason to change."</p>
 *
 * <p>BAD: {@link InvoiceBad} calculates totals, formats the invoice for printing AND saves it.
 * Three different reasons to change (tax rules, print layout, storage technology) all hit
 * the same class, and you can't test the maths without also dragging in the persistence.</p>
 *
 * <p>GOOD: split it so each class does one job: {@link Invoice} holds data and does the maths,
 * {@link InvoicePrinter} formats it, {@link InvoiceRepository} stores it.</p>
 */
public class SingleResponsibilityDemo {

    record LineItem(String name, int quantity, double unitPrice) {
        double total() {
            return quantity * unitPrice;
        }
    }

    // ------------------------------------------------------------------ BAD
    static class InvoiceBad {
        private final List<LineItem> items;

        InvoiceBad(List<LineItem> items) {
            this.items = items;
        }

        double total() {                       // responsibility 1: business logic
            return items.stream().mapToDouble(LineItem::total).sum();
        }

        void print() {                         // responsibility 2: presentation
            items.forEach(i -> System.out.println(i.name() + " x" + i.quantity()));
            System.out.println("TOTAL: " + total());
        }

        void saveToDatabase() {                // responsibility 3: persistence
            System.out.println("INSERT INTO invoices ...");
        }
    }

    // ----------------------------------------------------------------- GOOD
    /** Only knows about invoice data and calculations. */
    record Invoice(List<LineItem> items) {
        double total() {
            return items.stream().mapToDouble(LineItem::total).sum();
        }
    }

    /** Only knows how to display an invoice. */
    static class InvoicePrinter {
        void print(Invoice invoice) {
            invoice.items().forEach(i ->
                    System.out.printf("  %-10s x%d  @ %.2f%n", i.name(), i.quantity(), i.unitPrice()));
            System.out.printf("  TOTAL: %.2f%n", invoice.total());
        }
    }

    /** Only knows how to store invoices (here: in memory). */
    static class InvoiceRepository {
        private final List<Invoice> storage = new ArrayList<>();

        void save(Invoice invoice) {
            storage.add(invoice);
            System.out.println("  Saved invoice. Stored count = " + storage.size());
        }
    }

    public static void run() {
        System.out.println("\n--- S: Single Responsibility Principle ---");
        List<LineItem> items = List.of(new LineItem("Keyboard", 1, 49.99), new LineItem("Mouse", 2, 19.50));

        Invoice invoice = new Invoice(items);
        new InvoicePrinter().print(invoice);
        new InvoiceRepository().save(invoice);
    }
}
