package org.example.solid;

import org.example.solid.dip.DependencyInversionDemo.NotificationSender;
import org.example.solid.dip.DependencyInversionDemo.OrderService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Shows WHY Dependency Inversion matters: because OrderService depends on an interface,
 * we can pass in a fake sender and test it without sending real emails/SMS.
 */
class DependencyInversionDemoTest {

    @Test
    void orderServiceSendsNotification() {
        List<String> sent = new ArrayList<>();
        NotificationSender fake = sent::add;            // a fake implementation in one line

        new OrderService(fake).placeOrder("Book");

        assertEquals(List.of("Order placed for Book"), sent);
    }
}
