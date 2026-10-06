# Java 21 Example

A Maven project with two independent parts:

1. **What's new in Java 21** — runnable examples of the main Java 21 features (`org.example.java21`)
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
mvn compile exec:exec -Dmode=java21    # Java 21 features only
mvn compile exec:exec -Dmode=solid     # SOLID examples only
mvn test                               # unit tests
```

In IntelliJ you can also just run `Main`, but add `--enable-preview` to the run configuration's
VM options (needed for the unnamed-variables preview feature).

## Project structure

```
src/main/java/org/example
├── Main.java
├── java21
│   ├── Java21Showcase.java            runs all Java 21 demos
│   ├── VirtualThreadsDemo.java        JEP 444
│   ├── RecordPatternsDemo.java        JEP 440
│   ├── PatternMatchingSwitchDemo.java JEP 441
│   ├── SequencedCollectionsDemo.java  JEP 431
│   ├── UnnamedPatternsDemo.java       JEP 443 (preview)
│   └── LibraryAdditionsDemo.java      smaller API additions
└── solid
    ├── SolidShowcase.java             runs all SOLID demos
    ├── srp  Single Responsibility
    ├── ocp  Open/Closed
    ├── lsp  Liskov Substitution
    ├── isp  Interface Segregation
    └── dip  Dependency Inversion
```
