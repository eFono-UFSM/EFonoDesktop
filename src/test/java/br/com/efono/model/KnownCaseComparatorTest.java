package br.com.efono.model;

import java.util.Arrays;
import java.util.List;
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

    /**
     * Tests the use of {@link KnownCaseComparator#EasyHardWords}.
     */
    @Test
    public void testSortEasyHardWords() {
        /**
         * Empty representation. It doesn't matter here.
         */
        KnownCase faca = new KnownCase("Faca", "", true);
        KnownCase dedo = new KnownCase("Dedo", "", true);
        KnownCase travesseiro = new KnownCase("Travesseiro", "", true);
        KnownCase terra = new KnownCase("Terra", "", true);
        KnownCase sapo = new KnownCase("Sapo", "", true);

        List<KnownCase> list = Arrays.asList(faca, dedo, travesseiro, terra, sapo);

        System.out.println("testSortEasyHardWords - EasyWordsFirst");
        list.sort(KnownCaseComparator.EasyWordsFirst.getComparator());
        KnownCase[] expected = new KnownCase[]{dedo, terra, faca, sapo, travesseiro};
        for (int i = 0; i < list.size(); i++) {
            assertEquals(expected[i], list.get(i));
        }

        System.out.println("testSortEasyHardWords - HardWordsFirst");
        list.sort(KnownCaseComparator.HardWordsFirst.getComparator());
        expected = new KnownCase[]{travesseiro, sapo, faca, terra, dedo};
        for (int i = 0; i < list.size(); i++) {
            assertEquals(expected[i], list.get(i));
        }

        System.out.println("testSortEasyHardWords - EasyHardWords");
        list = Arrays.asList(faca, dedo, travesseiro, terra, sapo);
        list.sort(KnownCaseComparator.EasyHardWords.getComparator());
        /**
         * The "easy, hard, easy, hard" in the complete list with words
         * {@link KnownCaseComparator.Defaults#SORTED_WORDS}.
         */
        expected = new KnownCase[]{travesseiro, dedo, terra, faca, sapo};
        for (int i = 0; i < list.size(); i++) {
            assertEquals(expected[i], list.get(i));
        }
    }

}
