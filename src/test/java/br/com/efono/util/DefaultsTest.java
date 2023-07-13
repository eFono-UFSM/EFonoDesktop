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

}
