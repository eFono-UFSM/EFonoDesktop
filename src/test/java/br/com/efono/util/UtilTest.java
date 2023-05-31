package br.com.efono.util;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, May 28.
 */
public class UtilTest {

    @Test
    public void testChars() {
        System.out.println("test string length with special chars");
        String expected = "[’lu.vẽj̃]";
        assertEquals(10, expected.length());
    }

    /**
     * Just print all the possible phonemes in transcriptions: {@link Util#VOWELS} and {@link Util#SPECIAL}
     */
    @Test
    @Ignore
    public void testConstants() {
        System.out.println("testConstants");
        String enc = System.getProperty("file.encoding");
        System.out.println("file encoding: " + enc);
        System.setProperty("file.encoding", "UTF-8");

        for (String phoneme : Util.VOWELS) {
            System.out.println("phoneme: [" + phoneme + "]");
        }
        System.out.println("--specials--");
        for (String phoneme : Util.SPECIAL) {
            System.out.println("phoneme: [" + phoneme + "]");
        }
        assertFalse(true);
        fail();
    }

    /**
     * Tests {@link Util#readTranscriptions(File)}.
     *
     * @throws java.net.URISyntaxException Exception.
     */
    @Test
    public void testReadTranscriptions() throws URISyntaxException {
        System.out.println("testReadTranscriptions - null file");
        List<String> readTranscriptions = Util.readTranscriptions(null);
        assertTrue(readTranscriptions.isEmpty());

        System.out.println("testReadTranscriptions - empty file");
        File empty = new File(UtilTest.class.getResource("/data/empty.txt").toURI());
        readTranscriptions = Util.readTranscriptions(empty);
        assertTrue(readTranscriptions.isEmpty());

        System.out.println("testReadTranscriptions - invalid path - FileNotFoundException");

        File resDir = new File(UtilTest.class.getResource("/data").toURI());
        File invalid = new File(resDir, "invalid.txt");
        readTranscriptions = Util.readTranscriptions(invalid);
        assertTrue(readTranscriptions.isEmpty());

        // TODO: inserir no arquivo txt outras variantes, com unicode
        String expected = "[’lu.vẽj̃]";
        System.out.println("testReadTranscriptions - all variants of " + expected);
        File file = new File(UtilTest.class.getResource("/data/transcriptions.txt").toURI());

        List<String> lines = Util.readTranscriptions(file);
        assertEquals(2, lines.size()); // make sure that test file has content
        for (String line : lines) {
            System.out.println("expected: " + expected + " line: " + line);
            assertEquals(expected, line);
        }
    }

    /**
     * Tests {@link Util#cleanTranscription(String)}.
     */
    @Test
    public void testCleanTranscription() {
        System.out.println("testCleanTranscription - null and empty");
        assertTrue(Util.cleanTranscription(null).isEmpty());
        assertTrue(Util.cleanTranscription("[..'.]").isEmpty());
        assertTrue(Util.cleanTranscription("[.'\".'ʷ.'‘..’]ʼø").isEmpty());
        assertTrue(Util.cleanTranscription("     ").isEmpty());

        System.out.println("testCleanTranscription - valid parameters");
        assertEquals("baχiguiɲə", Util.cleanTranscription("[ba.χi.'gui.ɲə]"));
        /**
         * This string has even and odd number of spaces on purpose, do not remove this test. The method must remove
         * trailing spaces at middle of the string.
         */
        assertEquals("baχiguiɲə", Util.cleanTranscription(" [ba.χi.'gui .   ɲ    ə]   "));
        assertEquals("anɛwziɲu", Util.cleanTranscription("[anɛw'ziɲu]"));
    }

    /**
     * Tests {@link Util#getInitialOnset(String)}.
     */
    @Test
    public void testGetInitialOnset() {
        System.out.println("testGetInitialOnset - invalid transcriptions");
        assertNull(Util.getInitialOnset(null));
        assertNull(Util.getInitialOnset(""));
        assertNull(Util.getInitialOnset("  "));
        assertNull(Util.getInitialOnset("[..'.]"));

        System.out.println("testGetInitialOnset - transcription has consonant phoneme at Initial Onset");
        assertEquals("b", Util.getInitialOnset("[ba.χi.'gui.ɲə]"));

        System.out.println("testGetInitialOnset - transcription withou consonant phoneme at Initial Onset");
        assertNull(Util.getInitialOnset("[anɛw'ziɲu]"));
    }

    @Test
    public void test() {
        Util.decomposeTranscription("[ba.χi.'gui.ɲə]");

        Util.decomposeTranscription("[anɛw'ziɲu]");

//        fail();
    }

}
