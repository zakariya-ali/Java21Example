# Java 21 Example

A Maven project with two independent parts:

1. **Java 21 Virtual Threads** — the headline feature of Java 21, with runnable examples (`org.example.java21`)
2. **SOLID principles** — a bad vs. good example of each principle (`org.example.solid`)

Detailed explanations:
* [docs/JAVA21.md](docs/JAVA21.md)
* [docs/SOLID.md](docs/SOLID.md)

## Requirements

* JDK 21
* Maven 3.9+

## Running

```bash
mvn compile exec:exec                  # run everything
mvn compile exec:exec -Dmode=java21    # Java 21 virtual threads only
mvn compile exec:exec -Dmode=solid     # SOLID examples only
mvn test                               # unit tests
```

In IntelliJ you can also just run `Main`.

## Project structure

```
src/main/java/org/example
├── Main.java
├── java21
│   ├── Java21Showcase.java            runs the Java 21 demo
│   └── VirtualThreadsDemo.java        JEP 444 - Virtual Threads
└── solid
    ├── SolidShowcase.java             runs all SOLID demos
    ├── srp  Single Responsibility
    ├── ocp  Open/Closed
    ├── lsp  Liskov Substitution
    ├── isp  Interface Segregation
    └── dip  Dependency Inversion
```
