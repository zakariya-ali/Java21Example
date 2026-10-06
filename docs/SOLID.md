# SOLID Principles

SOLID is five object-oriented design principles (popularised by Robert C. Martin, "Uncle Bob").
Following them makes code **easier to change, test and understand**. They are guidelines, not
laws — the goal is to avoid code where one small change breaks many unrelated things.

| Letter | Principle | One-liner |
|---|---|---|
| **S** | Single Responsibility | A class should have one reason to change |
| **O** | Open/Closed | Extend behaviour by adding code, not editing existing code |
| **L** | Liskov Substitution | Subclasses must be usable wherever the parent is expected |
| **I** | Interface Segregation | Many small interfaces beat one big one |
| **D** | Dependency Inversion | Depend on interfaces, not concrete classes |

Code is in `src/main/java/org/example/solid`, one package per principle. **Every example has a
`BAD` section and a `GOOD` section** so you can show the problem and then the fix.
Run with:

```
mvn compile exec:exec -Dmode=solid
```

---

## S — Single Responsibility Principle (`srp/SingleResponsibilityDemo`)

> A class should have only one reason to change.

**Bad:** `InvoiceBad` calculates the total, prints the invoice *and* saves it to the database.
That's three reasons to change: tax/maths rules, print layout, and the storage technology. A change
to the printing could accidentally break the maths, and you can't unit-test the maths without
the database code coming along.

**Good:** split into three classes:
* `Invoice` — data + calculation (`total()`)
* `InvoicePrinter` — formatting only
* `InvoiceRepository` — storage only

**How to explain it:** "If I describe what a class does and I need the word *and*, it probably
has too many responsibilities."

## O — Open/Closed Principle (`ocp/OpenClosedDemo`)

> Open for extension, closed for modification.

**Bad:** `DiscountCalculatorBad` has an `if/else` on a `String` customer type. Adding a
student discount means editing that method, which is already tested and in production.

**Good:** a `DiscountPolicy` interface with one class per discount (`NoDiscount`,
`VipDiscount`, `StudentDiscount`). `DiscountCalculator` just calls `policy.apply(price)`.
`StudentDiscount` was added *without touching any existing class* — that's the principle in action.

**How to explain it:** "New feature = new class, not editing an old `if` chain." This is the
Strategy pattern.

## L — Liskov Substitution Principle (`lsp/LiskovSubstitutionDemo`)

> If `S` is a subtype of `T`, you can replace a `T` with an `S` without breaking anything.

**Bad:** `BirdBad` has a `fly()` method. `PenguinBad extends BirdBad` but can't fly, so it throws
`UnsupportedOperationException`. Any code that loops over birds and calls `fly()` crashes when it
meets a penguin — the demo prints `CRASH: Penguins can't fly!`.

**Good:** `Bird` only contains what *every* bird can do (`eat()`). Flying is a separate
`Flyer` interface that only `Sparrow` implements. Now the compiler won't even let you ask a
penguin to fly.

**How to explain it:** "If a subclass has to throw an exception or do nothing in an overridden
method, the inheritance is probably wrong." Classic example: `Square extends Rectangle`.

## I — Interface Segregation Principle (`isp/InterfaceSegregationDemo`)

> Clients shouldn't be forced to depend on methods they don't use.

**Bad:** `MachineBad` has `print`, `scan` and `fax`. A `SimplePrinterBad` must implement all
three and throws for the two it can't do (which is also an LSP violation — the principles
are connected).

**Good:** three small interfaces — `Printer`, `Scanner`, `Fax`. `SimplePrinter` implements
only `Printer`; `OfficeMachine` implements all three. `printReport(Printer p)` only asks for what it
needs, so it accepts either device.

**How to explain it:** "Small, focused interfaces. Java's own `Runnable`, `Comparable`,
`AutoCloseable` are good examples — one method each."

## D — Dependency Inversion Principle (`dip/DependencyInversionDemo`)

> High-level modules shouldn't depend on low-level modules; both should depend on abstractions.

**Bad:** `OrderServiceBad` (business logic) does `new EmailSenderBad()` inside itself. It's welded
to email; switching to SMS means editing `OrderService`, and testing it would send real emails.

**Good:** `OrderService` depends on the `NotificationSender` interface and receives an
implementation through its **constructor** (dependency injection). We can pass `EmailSender`,
`SmsSender`, or — in tests — a fake.

**Proof:** `src/test/java/org/example/solid/DependencyInversionDemoTest` tests `OrderService` with
a one-line fake (`sent::add`) that records messages in a list. No email, no mocking library.

**How to explain it:** "This is what Spring does for us. When we write a constructor that takes
an interface and Spring passes in the bean, that's dependency inversion + injection."

---

## How the principles relate to each other

* **SRP** makes classes small → easier to apply the others.
* **OCP** is usually *achieved through* **DIP** (depend on an interface, plug in new implementations).
* **ISP** violations often cause **LSP** violations (classes forced to throw for methods they can't support).
* **LSP** is what makes **OCP** safe — new implementations must behave correctly when plugged in.
