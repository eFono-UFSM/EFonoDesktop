package br.com.efono.util;

import br.com.efono.model.Phoneme;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
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
//        System.setProperty("file.encoding", "UTF-8");

        for (String phoneme : Phoneme.VOWELS) {
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
    public void testCleanTranscription() throws URISyntaxException {
        System.out.println("testCleanTranscription - null and empty");
        assertTrue(Util.cleanTranscription(null).isEmpty());
        assertTrue(Util.cleanTranscription("[..'.]").isEmpty());
        assertTrue(Util.cleanTranscription("[.'\".'ʷ.'‘..’]ʼø").isEmpty());
        assertTrue(Util.cleanTranscription("     ").isEmpty());

        System.out.println("testCleanTranscription - valid parameters");
        assertEquals("baχiguiɲə", Util.cleanTranscription("[ba.χi.'gui.ɲə]"));
        assertEquals("baχiguiɲə", Util.cleanTranscription("baχiguiɲə"));
        /**
         * This string has even and odd number of spaces on purpose, do not remove this test. The method must remove
         * trailing spaces at middle of the string.
         */
        assertEquals("baχiguiɲə", Util.cleanTranscription(" [ba.χi.'gui .   ɲ    ə]   "));
        assertEquals("anɛwziɲu", Util.cleanTranscription("anɛwziɲu"));

        File file = new File(UtilTest.class.getResource("/data/transcriptions.txt").toURI());
        System.out.println("testCleanTranscription - reading from file");

        List<String> lines = Util.readTranscriptions(file);
        assertEquals(2, lines.size()); // make sure that test file has content
        String expected = "luvẽj̃";
        for (String line : lines) {
            assertEquals(expected, Util.cleanTranscription(line));
        }
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
        Phoneme expected = new Phoneme("b", Phoneme.POSITION.OI);
        Phoneme result = Util.getInitialOnset("[ba.χi.'gui.ɲə]");
        assertEquals(expected, result);
        assertEquals("b", result.getPhoneme());
        assertEquals(Phoneme.POSITION.OI, result.getPosition());

        System.out.println("testGetInitialOnset - transcription withou consonant phoneme at Initial Onset");
        assertNull(Util.getInitialOnset("[anɛw'ziɲu]"));

        System.out.println("testGetInitialOnset - transcription has a consonant cluster at Initial Complex Onset");
        expected = new Phoneme("bɾ", Phoneme.POSITION.OCI);
        result = Util.getInitialOnset("[’bɾĩnko]");
        assertEquals(expected, result);
    }

    /**
     * Tests {@link Util#checkPhoneme(String)}.
     */
    @Test
    public void testCheckPhoneme() {
        System.out.println("testCheckPhoneme - null");
        assertEquals(0, Util.checkPhoneme(null).length);

        System.out.println("testCheckPhoneme - blank");
        assertEquals(0, Util.checkPhoneme("").length);
        assertEquals(0, Util.checkPhoneme("     ").length);

        System.out.println("testCheckPhoneme - consonant clusters");
        for (String cluster : Phoneme.CONSONANT_CLUSTERS) {
            Phoneme[] result = Util.checkPhoneme(cluster);

            assertEquals(1, result.length);
            assertEquals(cluster, result[0].getPhoneme());
        }

        System.out.println("testCheckPhoneme - a single charcater phoneme");
        Phoneme[] result = Util.checkPhoneme("b");
        assertEquals("b", result[0].getPhoneme());
        
        System.out.println("testCheckPhoneme - invalid phoneme - coda followed by Medial Complex Onset");
        result = Util.checkPhoneme("skɾ");
        Phoneme[] expected = new Phoneme[]{
            new Phoneme("s", Phoneme.POSITION.CM), 
            new Phoneme("kɾ", Phoneme.POSITION.OCME)};
        assertArrayEquals(expected, result);
        
        System.out.println("testCheckPhoneme - invalid phoneme - coda followed by Medial Onset");
        result = Util.checkPhoneme("sp");
        expected = new Phoneme[]{
            new Phoneme("s", Phoneme.POSITION.CM), 
            new Phoneme("p", Phoneme.POSITION.OM)};
        assertArrayEquals(expected, result);
    }

    /**
     * Tests {@link Util#checkPhonemes(String[])}.
     */
    @Test
    public void testCheckPhonemes() {
        System.out.println("testCheckPhonemes - null and empty parameter");
        assertEquals(0, Util.checkPhonemes(null).length);
        assertEquals(0, Util.checkPhonemes(new String[]{}).length);

        System.out.println("testCheckPhonemes - valid arrays");
        String[] expected = new String[]{"b", "χ", "g", "ɲ"};
        Phoneme[] result = Util.checkPhonemes(expected);
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i].getPhoneme());
        }

        expected = new String[]{"bɾ", "n", "k"};
        result = Util.checkPhonemes(expected);
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i].getPhoneme());
        }

        expected = new String[]{"ʃ", "kl", "ʧ"};
        result = Util.checkPhonemes(expected);
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i].getPhoneme());
        }

        System.out.println("invalid arrays");
        String[] invalidArray = new String[]{"bɾ", "nk"};
        expected = new String[]{"bɾ", "n", "k"};
        result = Util.checkPhonemes(invalidArray);
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i].getPhoneme());
        }

        System.out.println("invalid arrays - null, empty and blank spaces must be discarted");
        invalidArray = new String[]{"b", null, "", "χ", " ", " g ", "  ", "ɲ"};
        expected = new String[]{"b", "χ", "g", "ɲ"};
        result = Util.checkPhonemes(invalidArray);
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i].getPhoneme());
        }

        // iskɾeveɾ -> skɾ v ɾ (invalid array)
        invalidArray = new String[]{"skɾ", "v", "ɾ"};
        expected = new String[]{"s", "kɾ", "v", "ɾ"};
        result = Util.checkPhonemes(invalidArray);
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i].getPhoneme());
        }

        // TODO: depois, testar com a posição dos fonemas
    }

    /**
     * Tests {@link Util#getConsonantPhonemes(String)}.
     *
     * Tests only the phonemes, not their positions in the transcriptions.
     */
    @Test
    public void testGetConsonantPhonemes() {
        // TODO: tests givin a transcription with UNICODE: [\u2019lu.vẽj̃] // read from file as well
        String transcription = "[ba.χi.'gui.ɲə]";
        List<String> expected = Arrays.asList(new String[]{"b", "χ", "g", "ɲ"});
        System.out.println("testGetConsonantPhonemes: " + transcription);
        List<Phoneme> phonemes = Util.getConsonantPhonemes(transcription);
        assertEquals(expected.size(), phonemes.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), phonemes.get(i).getPhoneme());
        }

        transcription = "[anɛw'ziɲu]";
        System.out.println("testGetConsonantPhonemes: " + transcription);
        phonemes = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new String[]{"n", "z", "ɲ"});
        assertEquals(expected.size(), phonemes.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), phonemes.get(i).getPhoneme());
        }

        // tests for test for consonant clusters and codas
        // OCI and CM
        transcription = "[’bɾĩnko]";
        System.out.println("testGetConsonantPhonemes: test for Initial Complex Onset (bɾ) and Medial Coda (n): " + transcription);
        phonemes = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new String[]{"bɾ", "n", "k"});
        assertEquals(expected.size(), phonemes.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), phonemes.get(i).getPhoneme());
        }

        // OCME
        transcription = "[ʃi’klƐʧi]";
        System.out.println("testGetConsonantPhonemes: test for Initial Medial Complex Onset (kl): " + transcription);
        phonemes = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new String[]{"ʃ", "kl", "ʧ"});
        assertEquals(expected.size(), phonemes.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), phonemes.get(i).getPhoneme());
        }

        // OCME and CF
        transcription = "[ʼʃifɾis]";
        System.out.println("testGetConsonantPhonemes: test for Initial Medial Complex Onset (fɾ) and Final Coda (s): " + transcription);
        phonemes = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new String[]{"ʃ", "fɾ", "s"});
        assertEquals(expected.size(), phonemes.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), phonemes.get(i).getPhoneme());
        }

        transcription = "[iskɾe’veɾ]";
        System.out.println("testGetConsonantPhonemes: test for Medial Coda (s) followed by Medial Complex Onset (kɾ): " + transcription);
        phonemes = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new String[]{"s", "kɾ", "v", "ɾ"});
        assertEquals(expected.size(), phonemes.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), phonemes.get(i).getPhoneme());
        }
        assertEquals(Phoneme.POSITION.CM, phonemes.get(0).getPosition());
        assertEquals(Phoneme.POSITION.OCME, phonemes.get(1).getPosition());

        transcription = "[es'peʎu]";
        System.out.println("testGetConsonantPhonemes: test for Medial Coda (s) followed by Medial Onset (kɾ): " + transcription);
        phonemes = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new String[]{"s", "p", "ʎ"});
        assertEquals(expected.size(), phonemes.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), phonemes.get(i).getPhoneme());
        }
        assertEquals(Phoneme.POSITION.CM, phonemes.get(0).getPosition());
        assertEquals(Phoneme.POSITION.OM, phonemes.get(1).getPosition());
    }

    @Test
    public void test() {
        Util.decomposeTranscription("[ba.χi.'gui.ɲə]");

        Util.decomposeTranscription("[anɛw'ziɲu]");

        fail();
    }

}
