package org.example.java21;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedCollection;
import java.util.SequencedMap;

/**
 * JEP 431 - Sequenced Collections (Java 21).
 *
 * <p>Before Java 21 there was no common way to get the first/last element of an
 * ordered collection - every type did it differently:</p>
 * <pre>
 *   List           list.get(0)               list.get(list.size() - 1)
 *   Deque          deque.getFirst()          deque.getLast()
 *   LinkedHashSet  set.iterator().next()     (no easy way at all!)
 * </pre>
 *
 * <p>Java 21 adds three new interfaces into the collections hierarchy:
 * {@code SequencedCollection}, {@code SequencedSet} and {@code SequencedMap}. They give
 * every ordered collection the same methods: {@code getFirst()}, {@code getLast()},
 * {@code addFirst()}, {@code addLast()}, {@code removeFirst()}, {@code removeLast()}
 * and {@code reversed()}.</p>
 */
public class SequencedCollectionsDemo {

    // One method that works for List, Deque, LinkedHashSet, TreeSet... because they are all SequencedCollections.
    static <T> String ends(SequencedCollection<T> c) {
        return "first=" + c.getFirst() + ", last=" + c.getLast();
    }

    public static void run() {
        Demo.title("JEP 431 - Sequenced Collections");

        List<String> list = new ArrayList<>(List.of("b", "c", "d"));
        System.out.println("Old way last element: " + list.get(list.size() - 1));
        System.out.println("New way last element: " + list.getLast());

        list.addFirst("a");
        list.addLast("e");
        System.out.println("After addFirst/addLast: " + list);
        System.out.println("Reversed view         : " + list.reversed());

        LinkedHashSet<Integer> set = new LinkedHashSet<>(List.of(10, 20, 30));
        System.out.println("LinkedHashSet " + ends(set));
        System.out.println("ArrayList     " + ends(list));

        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.putFirst("zero", 0);
        System.out.println("Map: " + map);
        System.out.println("firstEntry=" + map.firstEntry() + ", lastEntry=" + map.lastEntry());
        System.out.println("pollLastEntry -> " + map.pollLastEntry() + ", map now " + map);
    }
}
