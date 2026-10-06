package org.example.solid.dip;

/**
 * D - Dependency Inversion Principle.
 *
 * <p>"High-level modules should not depend on low-level modules. Both should depend on
 * abstractions (interfaces)."</p>
 *
 * <p>BAD: {@link OrderServiceBad} (business logic) creates a concrete {@link EmailSenderBad}
 * with {@code new}. It's hard-wired to email: switching to SMS means editing OrderService,
 * and you can't test it without sending real emails.</p>
 *
 * <p>GOOD: {@link OrderService} depends on the {@link NotificationSender} interface and receives
 * an implementation through its constructor ("constructor injection"). This is exactly what
 * frameworks like Spring do for you with {@code @Autowired} / constructor injection.</p>
 */
public class DependencyInversionDemo {

    // ------------------------------------------------------------------ BAD
    static class EmailSenderBad {
        void send(String msg) {
            System.out.println("  EMAIL: " + msg);
        }
    }

    static class OrderServiceBad {
        private final EmailSenderBad sender = new EmailSenderBad();    // tightly coupled

        void placeOrder(String item) {
            sender.send("Order placed for " + item);
        }
    }

    // ----------------------------------------------------------------- GOOD
    /** The abstraction both sides depend on. */
    public interface NotificationSender {
        void send(String message);
    }

    static class EmailSender implements NotificationSender {
        public void send(String message) {
            System.out.println("  EMAIL: " + message);
        }
    }

    static class SmsSender implements NotificationSender {
        public void send(String message) {
            System.out.println("  SMS  : " + message);
        }
    }

    /** High-level business logic - knows nothing about email or SMS. */
    public static class OrderService {
        private final NotificationSender sender;

        public OrderService(NotificationSender sender) {               // dependency is injected
            this.sender = sender;
        }

        public void placeOrder(String item) {
            // ... save order, take payment, etc.
            sender.send("Order placed for " + item);
        }
    }

    public static void run() {
        System.out.println("\n--- D: Dependency Inversion Principle ---");
        new OrderService(new EmailSender()).placeOrder("Laptop");
        new OrderService(new SmsSender()).placeOrder("Phone");
    }
}
