package br.com.efono.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 13.
 */
public class DefaultsTest {

    /**
     * Tests {@link Defaults#getEasyHardWords(String[])}.
     */
    @Test
    public void testGetEasyHardWords() {
        System.out.println("getEasyHardWords - invalid parameters");
        assertEquals(0, Defaults.getEasyHardWords(null).length);
        assertEquals(0, Defaults.getEasyHardWords(new String[]{}).length);

        System.out.println("getEasyHardWords - array with even length");
        String[] expected = new String[]{"Anel", "Cama", "Porta", "Teste"};
        String[] result = Defaults.getEasyHardWords(new String[]{"Anel", "Porta", "Teste", "Cama"});
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }

        System.out.println("getEasyHardWords - array with odd length");
        expected = new String[]{"Anel", "Cama", "Porta", "Janela", "Teste"};
        result = Defaults.getEasyHardWords(new String[]{"Anel", "Porta", "Teste", "Janela", "Cama"});
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }

        System.out.println("getEasyHardWords - array with one element");
        expected = new String[]{"Anel"};
        result = Defaults.getEasyHardWords(new String[]{"Anel"});
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }

        System.out.println("getEasyHardWords - array with two element");
        expected = new String[]{"Anel", "Porta"};
        result = Defaults.getEasyHardWords(new String[]{"Anel", "Porta"});
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }

    }

    /**
     * Tests {@link Defaults#findIndexOf(String)}.
     */
    @Test
    public void testFindIndexOf() {
        System.out.println("testFindIndexOf - null and empty parameters");
        assertEquals(-1, Defaults.findIndexOf(null));
        assertEquals(-1, Defaults.findIndexOf(""));
        assertEquals(-1, Defaults.findIndexOf("  "));

        System.out.println("testFindIndexOf - a word that is not in our database");
        assertEquals(-1, Defaults.findIndexOf("Test"));

        System.out.println("testFindIndexOf - variants of some word that is in our database: all must be recognized");
        String[] words = new String[]{"Jacare", "JACARÉ", "Jacaré", "JACARE", "JAcAre"};
        for (String w : words) {
            assertEquals("Failed in try to find the word " + w, 48, Defaults.findIndexOf(w));
        }
    }

    /**
     * Tests {@link Defaults#findIndexOf(String, String[])}.
     */
    @Test
    public void testAnotherFindIndexOf() {
        System.out.println("testAnotherFindIndexOf - null and empty parameters");
        assertEquals(-1, Defaults.findIndexOf(null, null));
        assertEquals(-1, Defaults.findIndexOf(null, new String[]{}));
        assertEquals(-1, Defaults.findIndexOf("", null));
        assertEquals(-1, Defaults.findIndexOf("  ", null));
        assertEquals(-1, Defaults.findIndexOf("", new String[]{}));

        System.out.println("testAnotherFindIndexOf - a word that is not in our database");
        assertEquals(-1, Defaults.findIndexOf("Test", Defaults.EASY_HARD_WORDS));

        System.out.println("testAnotherFindIndexOf - variants of some word that is in our database: all must be recognized");
        String[] words = new String[]{"Jacare", "JACARÉ", "Jacaré", "JACARE", "JAcAre"};
        for (String w : words) {
            assertTrue("Failed in try to find the word " + w, Defaults.findIndexOf(w, Defaults.EASY_HARD_WORDS) >= 0);
        }

        assertEquals(-1, Defaults.findIndexOf(null, Defaults.EASY_HARD_WORDS));

    }

}
