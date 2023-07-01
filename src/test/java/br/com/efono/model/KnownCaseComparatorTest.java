package br.com.efono.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 01.
 */
public class KnownCaseComparatorTest {

    /**
     * Tests {@link KnownCaseComparator#equals(Object)}.
     */
    @Test
    public void testEquals() {
        System.out.println("testEquals - same instance");
        assertTrue(KnownCaseComparator.EasyWordsFirst.equals(KnownCaseComparator.EasyWordsFirst));

        System.out.println("testEquals - not the same instance");
        assertFalse(KnownCaseComparator.EasyWordsFirst.equals(KnownCaseComparator.HardWordsFirst));

        System.out.println("testEquals - amother class");
        Object obj = null;
        assertFalse(KnownCaseComparator.EasyWordsFirst.equals(obj));
        obj = "EasyWordsFirst";
        assertFalse(KnownCaseComparator.EasyWordsFirst.equals(obj));
    }

}
