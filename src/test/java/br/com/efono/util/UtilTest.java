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
     * Tests {@link Util#getFinalCoda(String)}.
     */
    @Test
    public void testGetFinalCoda() {
        // OCME and CF
        String transcription = "[ʼʃifɾis]";
        System.out.println("testGetFinalCoda - valid tests for Final Codas: " + transcription);
        Phoneme expected = new Phoneme("s", Phoneme.POSITION.CF);
        Phoneme result = Util.getFinalCoda(transcription);
        assertEquals(expected, result);

        // CM and OCME and CF
        transcription = "[iskɾe’veɾ]";
        System.out.println("testGetFinalCoda - valid tests for Final Codas: " + transcription);
        expected = new Phoneme("ɾ", Phoneme.POSITION.CF);
        result = Util.getFinalCoda(transcription);
        assertEquals(expected, result);
    }

    /**
     * Tests {@link Util#replaceLast(String, String, String)}.
     */
    @Test
    public void testReplaceLast() {
        System.out.println("testReplaceLast - null parameters");
        assertNull(Util.replaceLast(null, null, null));
        assertNull(Util.replaceLast("foo AB bar BAAB AB done", null, null));
        assertNull(Util.replaceLast("foo AB bar BAAB AB done", null, ""));
        assertNull(Util.replaceLast("foo AB bar BAAB AB done", "AB", null));
        assertNull(Util.replaceLast(null, "AB", null));
        assertNull(Util.replaceLast(null, null, ""));
        String expected = "foo AB bar BAAB done";

        System.out.println("testReplaceLast valid test");
        String result = Util.replaceLast("foo AB bar BAAB ABdone", "AB", "");
        assertEquals(expected, result);

        expected = "foo AB bar BAAB AB done";
        result = Util.replaceLast("foo AB bar BAAB AB doneAB", "AB", "");
        assertEquals(expected, result);
    }

    /**
     * Tests {@link Util#getConsonantPhonemes(String)}.
     *
     * Tests only the phonemes, not their positions in the transcriptions.
     */
    @Test
    public void testGetConsonantPhonemes() {
        System.out.println("testGetConsonantPhonemes - null and empty parameters");
        assertTrue(Util.getConsonantPhonemes(null).isEmpty());
        assertTrue(Util.getConsonantPhonemes("").isEmpty());
        assertTrue(Util.getConsonantPhonemes("  ").isEmpty());

        // TODO: tests givin a transcription with UNICODE: [\u2019lu.vẽj̃] // read from file as well
        String transcription = "[ba.χi.'gui.ɲə]";
        List<Phoneme> expected = Arrays.asList(new Phoneme[]{
            new Phoneme("b", Phoneme.POSITION.OI),
            new Phoneme("χ", Phoneme.POSITION.OM),
            new Phoneme("g", Phoneme.POSITION.OM),
            new Phoneme("ɲ", Phoneme.POSITION.OM)});

        System.out.println("testGetConsonantPhonemes: " + transcription);
        List<Phoneme> result = Util.getConsonantPhonemes(transcription);
        assertArrayEquals(expected.toArray(), result.toArray());

        transcription = "[ba .χi.' g ui   .ɲə]";
        System.out.println("testGetConsonantPhonemes: tests with transcription with blank spaces " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        assertArrayEquals(expected.toArray(), result.toArray());

        transcription = "[anɛw'ziɲu]";
        System.out.println("testGetConsonantPhonemes: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("n", Phoneme.POSITION.OM), // transcription doesn't have OI
            new Phoneme("z", Phoneme.POSITION.OM),
            new Phoneme("ɲ", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        // tests for test for consonant clusters and codas
        // OCI and CM
        transcription = "[’bɾĩnko]";
        System.out.println("testGetConsonantPhonemes: test for Initial Complex Onset (bɾ) and Medial Coda (n): " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("bɾ", Phoneme.POSITION.OCI),
            new Phoneme("n", Phoneme.POSITION.CM),
            new Phoneme("k", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        // OCME
        transcription = "[ʃi’klƐʧi]";
        System.out.println("testGetConsonantPhonemes: test for Initial Medial Complex Onset (kl): " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("ʃ", Phoneme.POSITION.OI),
            new Phoneme("kl", Phoneme.POSITION.OCME),
            new Phoneme("ʧ", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        // OCME and CF
        transcription = "[ʼʃifɾis]";
        System.out.println("testGetConsonantPhonemes: test for Initial Medial Complex Onset (fɾ) and Final Coda (s): " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("ʃ", Phoneme.POSITION.OI),
            new Phoneme("fɾ", Phoneme.POSITION.OCME),
            new Phoneme("s", Phoneme.POSITION.CF)});
        assertArrayEquals(expected.toArray(), result.toArray());

        // CM and OCME and CF
        transcription = "[iskɾe’veɾ]";
        System.out.println("testGetConsonantPhonemes: test for Medial Coda (s) followed by Medial Complex Onset (kɾ): " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("s", Phoneme.POSITION.CM),
            new Phoneme("kɾ", Phoneme.POSITION.OCME),
            new Phoneme("v", Phoneme.POSITION.OM),
            new Phoneme("ɾ", Phoneme.POSITION.CF)});
        assertArrayEquals(expected.toArray(), result.toArray());

        // CM and OM
        transcription = "[es'peʎu]";
        System.out.println("testGetConsonantPhonemes: test for Medial Coda (s) followed by Medial Onset (kɾ): " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("s", Phoneme.POSITION.CM),
            new Phoneme("p", Phoneme.POSITION.OM),
            new Phoneme("ʎ", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());
        
        // TODO: testar esse metodo com todas as palavras do gabarito.json
    }

}
