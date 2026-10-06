package org.example.java21;

/**
 * Smaller API additions in Java 21 that are handy day to day.
 */
public class LibraryAdditionsDemo {

    public static void run() {
        Demo.title("Smaller Java 21 API additions");

        // Math.clamp - keep a value within a min/max range (before: Math.max(min, Math.min(max, value)))
        System.out.println("Math.clamp(150, 0, 100) = " + Math.clamp(150, 0, 100));
        System.out.println("Math.clamp(-5, 0, 100)  = " + Math.clamp(-5, 0, 100));

        // StringBuilder.repeat - append something N times
        StringBuilder sb = new StringBuilder("Loading");
        sb.repeat('.', 3).repeat(" done", 1);
        System.out.println("StringBuilder.repeat     = " + sb);

        // String.indexOf(ch, beginIndex, endIndex) - search only inside a range
        String text = "banana";
        System.out.println("\"banana\".indexOf('a', 2, 4) = " + text.indexOf('a', 2, 4));

        // String.splitWithDelimiters - split but keep the delimiters
        String[] parts = "a+b-c".splitWithDelimiters("[+-]", 0);
        System.out.println("splitWithDelimiters      = " + String.join(" | ", parts));

        // Character.isEmoji (and friends)
        int smiley = "😀".codePointAt(0);
        System.out.println("Character.isEmoji(U+1F600) = " + Character.isEmoji(smiley));
    }
}
