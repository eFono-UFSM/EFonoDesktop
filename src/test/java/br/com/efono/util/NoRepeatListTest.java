package br.com.efono.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Ago 27.
 */
public class NoRepeatListTest {

    /**
     * Tests {@link NoRepeatList#add(java.lang.Object)}.
     */
    @Test
    public void testAdd() {
        System.out.println("testAdd - null parameter");
        List<String> instance = new NoRepeatList<>();
        assertFalse(instance.add(null));

        List<String> expected = Arrays.asList("Test");
        assertTrue(instance.add("Test"));
        assertEquals(expected.size(), instance.size());
        assertTrue(expected.containsAll(instance));

        System.out.println("testAdd - repeated element");
        assertFalse(instance.add("Test"));
        assertEquals(expected.size(), instance.size());
        assertTrue(expected.containsAll(instance));

        System.out.println("testAdd - just add another element");
        assertTrue(instance.add("Testando"));
        expected = Arrays.asList("Test", "Testando");
        assertEquals(expected.size(), instance.size());
        assertTrue(expected.containsAll(instance));

        expected = Arrays.asList("Testando", "Test"); // not in the same order: valid as well.
        assertEquals(expected.size(), instance.size());
        assertTrue(expected.containsAll(instance));
    }

    /**
     * Tests {@link NoRepeatList#addAll(java.util.Collection)}.
     */
    @Test
    public void testAddAll() {
        System.out.println("testAddAll - null parameter");
        List<String> instance = new NoRepeatList<>();
        assertFalse(instance.addAll(null));
        assertTrue(instance.isEmpty());

        System.out.println("testAddAll - validation");
        List<String> expected = Arrays.asList("Test", "Testando");
        assertTrue(instance.addAll(Arrays.asList("Test", "Testando")));
        assertEquals(expected.size(), instance.size());
        assertTrue(expected.containsAll(instance));

        System.out.println("testAddAll - addAll with repeated elements in first positions");
        expected = Arrays.asList("Test", "Testando", "Outro");
        assertTrue(instance.addAll(Arrays.asList("Test", "Outro")));
        assertEquals(expected.size(), instance.size());
        assertTrue(expected.containsAll(instance));

        System.out.println("testAddAll - addAll with repeated elements in last positions");
        expected = Arrays.asList("Test", "Testando", "Outro", "Novo");
        assertTrue(instance.addAll(Arrays.asList("Testando", "Novo")));
        assertEquals(expected.size(), instance.size());
        assertTrue(expected.containsAll(instance));
        
        System.out.println("testAddAll - all elements repeated");
        expected = Arrays.asList("Test", "Testando", "Outro", "Novo");
        assertFalse(instance.addAll(Arrays.asList("Testando", "Novo")));
        assertEquals(expected.size(), instance.size());
        assertTrue(expected.containsAll(instance));
        
        System.out.println("testAddAll - empty parameter");
        assertFalse(instance.addAll(new ArrayList<>()));
        expected = Arrays.asList("Test", "Testando", "Outro", "Novo");
        assertEquals(expected.size(), instance.size());
        assertTrue(expected.containsAll(instance));
    }

}
