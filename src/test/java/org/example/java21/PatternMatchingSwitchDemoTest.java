package org.example.java21;

import org.example.java21.PatternMatchingSwitchDemo.Circle;
import org.example.java21.PatternMatchingSwitchDemo.Rectangle;
import org.example.java21.PatternMatchingSwitchDemo.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternMatchingSwitchDemoTest {

    @Test
    void areaUsesRecordPatterns() {
        assertEquals(Math.PI, PatternMatchingSwitchDemo.area(new Circle(1)), 1e-9);
        assertEquals(4, PatternMatchingSwitchDemo.area(new Square(2)));
        assertEquals(10, PatternMatchingSwitchDemo.area(new Rectangle(2, 5)));
    }

    @Test
    void switchHandlesNullAndGuards() {
        assertTrue(PatternMatchingSwitchDemo.classify(null).startsWith("It's null"));
        assertEquals("Negative integer: -1", PatternMatchingSwitchDemo.classify(-1));
        assertEquals("Zero", PatternMatchingSwitchDemo.classify(0));
        assertEquals("Blank string", PatternMatchingSwitchDemo.classify("  "));
    }
}
