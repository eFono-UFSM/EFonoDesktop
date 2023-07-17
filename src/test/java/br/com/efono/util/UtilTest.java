package br.com.efono.util;

import br.com.efono.model.KnownCase;
import br.com.efono.model.Phoneme;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

    /**
     * Just to ensure if java is recognizing the phonemes.
     */
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
        /**
         * Just to ensure if java is recognizing the phonemes.
         */
        System.out.println("testConstants");
        String enc = System.getProperty("file.encoding");
        System.out.println("file encoding: " + enc);
//        System.setProperty("file.encoding", "UTF-8");

        System.out.println("--vowels--");
        for (String phoneme : Phoneme.VOWELS) {
            System.out.println("phoneme: [" + phoneme + "]");
        }
        System.out.println("--semivowels--");
        for (String phoneme : Phoneme.SEMI_VOWELS) {
            System.out.println("phoneme: [" + phoneme + "]");
        }
        System.out.println("--specials--");
        for (String phoneme : Util.SPECIAL) {
            System.out.println("phoneme: [" + phoneme + "]");
        }
        fail();
    }

    /**
     * Tests for {@link Util#replaceSpecialPhonemes(String)}.
     */
    @Test
    public void testReplaceSpecialPhonemes() {
        // What is labialization? https://pt.wikipedia.org/wiki/Labializa%C3%A7%C3%A3o
        System.out.println("testLabialization - null parameter");
        assertNull(Util.replaceSpecialPhonemes(null));

        System.out.println("testLabialization - empty parameter");
        assertTrue(Util.replaceSpecialPhonemes("").isBlank());
        assertTrue(Util.replaceSpecialPhonemes("  ").isBlank());

        String transcription = "['lĩngʷa]";
        System.out.println("testLabialization - labialization of /g/: gʷ -> " + transcription);

        int gIndex = Arrays.asList(Phoneme.LABIALIZATION).indexOf("gʷ");
        assertTrue(gIndex >= 0); // index exists
        String expected = "['lĩn" + gIndex + "a]";
        assertEquals(expected, Util.replaceSpecialPhonemes(transcription));

        // [lĩkʷɐ]
        transcription = "[lĩkʷɐ]";
        System.out.println("testLabialization - labialization of /k/: kʷ -> " + transcription);

        int kIndex = Arrays.asList(Phoneme.LABIALIZATION).indexOf("kʷ");
        assertTrue(kIndex >= 0); // index exists
        expected = "[lĩ" + kIndex + "ɐ]";
        assertEquals(expected, Util.replaceSpecialPhonemes(transcription));

        // do not remove it
        transcription = "[lĩkʷɐ]['lĩngʷa][lĩkʷɐ]";
        System.out.println("testLabialization - just to make sure that all labialization phonemes are replaced: " + transcription);
        expected = "[lĩ" + kIndex + "ɐ]['lĩn" + gIndex + "a][lĩ" + kIndex + "ɐ]";
        assertEquals(expected, Util.replaceSpecialPhonemes(transcription));

        // nuvizis̃
        transcription = "['nu.vi.zis̃]";
        int sIndex = Arrays.asList(Phoneme.SPECIAL_CONSONANTS).indexOf("s̃") + Phoneme.LABIALIZATION.length;
        System.out.println("testLabialization - special phonemes must be replaced: " + transcription);
        expected = "['nu.vi.zi" + sIndex + "]";
        assertEquals(expected, Util.replaceSpecialPhonemes(transcription));
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
        assertTrue(Util.cleanTranscription("[.'\".'.'‘..’]ʼø").isEmpty());
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

        System.out.println("testCleanTranscription - /nh/ in the given transcription: should be replaced by /ɲ/");
        assertEquals("kaziɲə", Util.cleanTranscription("[‘kazinhə]"));

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

        System.out.println("testCheckPhoneme - labialization phonemes");
        for (String labialization : Phoneme.LABIALIZATION) {
            Phoneme[] result = Util.checkPhoneme(labialization);

            assertEquals(1, result.length);
            assertEquals(labialization, result[0].getPhoneme());
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
     * Tests {@link Util#getConsonantPhonemes(String)}.Tests only the phonemes, not their positions in the
     * transcriptions.
     *
     * @throws java.net.URISyntaxException Exception reading file.
     * @throws java.io.IOException Exception reading file.
     */
    @Test
    public void testGetConsonantPhonemes() throws URISyntaxException, IOException {
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

        // labialization tests: https://pt.wikipedia.org/wiki/Labializa%C3%A7%C3%A3o
        // labialization after CM
        transcription = "['lĩngʷa]";
        System.out.println("testGetConsonantPhonemes: test when transcription contains a Labialization after CM: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("l", Phoneme.POSITION.OI),
            new Phoneme("n", Phoneme.POSITION.CM),
            new Phoneme("gʷ", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        // labialization after OI // fogʷeɾə
        transcription = "[fo.'gʷe.ɾə]";
        System.out.println("testGetConsonantPhonemes: test when transcription contains a Labialization after OI: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("f", Phoneme.POSITION.OI),
            new Phoneme("gʷ", Phoneme.POSITION.OM),
            new Phoneme("ɾ", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        /**
         * Tests for diphthong:
         * https://pt.wikipedia.org/wiki/Fonologia_da_l%C3%ADngua_portuguesa#Classifica%C3%A7%C3%A3o_das_vogais
         */
        // semi vowel: parte de ditongo /ej/ (ditongo oral)
        transcription = "[’bej.ʒu]";
        System.out.println("testGetConsonantPhonemes: test when transcription contains an oral diphthong: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("b", Phoneme.POSITION.OI),
            new Phoneme("ʒ", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        // semi vowel: parte de ditonto /ẽj̃/ (ditongo nasal)
        transcription = "[’nuvẽj̃]";
        System.out.println("testGetConsonantPhonemes: test when transcription contains a nasal diphthong: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("n", Phoneme.POSITION.OI),
            new Phoneme("v", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        transcription = "blibliotlɛkə";
        System.out.println("testGetConsonantPhonemes: test transcription: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("bl", Phoneme.POSITION.OCI),
            new Phoneme("bl", Phoneme.POSITION.OCME),
            new Phoneme("tl", Phoneme.POSITION.OCME),
            new Phoneme("k", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        // /ʃ/ som de ch  https://pt.wikipedia.org/wiki/Fonema
        transcription = "bisʃkɛtə";
        System.out.println("testGetConsonantPhonemes: test transcription: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("b", Phoneme.POSITION.OI),
            new Phoneme("s", Phoneme.POSITION.CM),
            new Phoneme("ʃ", Phoneme.POSITION.OM),
            new Phoneme("k", Phoneme.POSITION.OM),
            new Phoneme("t", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        transcription = "[si’kɾʧi]"; // sikɾʧi
        System.out.println("testGetConsonantPhonemes: test transcription: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("s", Phoneme.POSITION.OI),
            new Phoneme("kɾ", Phoneme.POSITION.OCME),
            new Phoneme("ʧ", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        transcription = "[na’vw]"; // navw
        System.out.println("testGetConsonantPhonemes: test transcription: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("n", Phoneme.POSITION.OI),
            new Phoneme("v", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        transcription = "[’nu.ʤĩ]"; // nuvem -> nuʤĩ
        System.out.println("testGetConsonantPhonemes: test transcription: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("n", Phoneme.POSITION.OI),
            new Phoneme("ʤ", Phoneme.POSITION.OM)});
        assertArrayEquals(expected.toArray(), result.toArray());

        // special consonant phoneme s̃
        transcription = "['nu.vi.zis̃]";
        System.out.println("testGetConsonantPhonemes: test transcription: " + transcription);
        result = Util.getConsonantPhonemes(transcription);
        expected = Arrays.asList(new Phoneme[]{
            new Phoneme("n", Phoneme.POSITION.OI),
            new Phoneme("v", Phoneme.POSITION.OM),
            new Phoneme("z", Phoneme.POSITION.OM),
            new Phoneme("s̃", Phoneme.POSITION.CF)});
        assertArrayEquals(expected.toArray(), result.toArray());

        System.out.println("testGetConsonantPhonemes: tests with all correct known cases");
        File allCorrect = new File(UtilTest.class.getResource("/data/allCorrect.json").toURI());
        List<KnownCase> cases = KnownCase.loadFile(allCorrect);
        for (KnownCase c : cases) {
            expected = c.getPhonemes();
            result = Util.getConsonantPhonemes(c.getRepresentation());

            System.out.println("case: " + c.getRepresentation() + " word: " + c.getWord() + "\n\texpected: " + Arrays.toString(expected.toArray()) + ""
                    + "\n\tresult: " + Arrays.toString(result.toArray()));

            assertArrayEquals(expected.toArray(), result.toArray());
        }
    }

    /**
     * Tests {@link Util#getCaseFromWord(List, String)}.
     */
    @Test
    public void testGetCaseFromWord() {
        System.out.println("testGetCaseFromWord - invalid parameters");
        assertNull(Util.getCaseFromWord(null, null));
        assertNull(Util.getCaseFromWord(null, "batom"));
        assertNull(Util.getCaseFromWord(new ArrayList<>(), null));
        assertNull(Util.getCaseFromWord(new ArrayList<>(), "batom"));

        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));

        assertNull(Util.getCaseFromWord(Arrays.asList(batom, dedo, cama), "terra"));

        System.out.println("testGetCaseFromWord - ignoreCase");
        assertEquals(dedo, Util.getCaseFromWord(Arrays.asList(batom, dedo, cama), "dedo"));
        assertEquals(dedo, Util.getCaseFromWord(Arrays.asList(batom, dedo, cama), "DEDO"));
        assertEquals(dedo, Util.getCaseFromWord(Arrays.asList(batom, dedo, cama), "Dedo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyConstructor() {
        KnownCase emptyCase = new KnownCase("  ", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
    }

    /**
     * Tests {@link Util#getTargetPhonemes(List)}.
     */
    @Test
    public void testGetTargetPhonemes() {
        System.out.println("testGetTargetPhonemes - parameters");
        assertTrue(Util.getTargetPhonemes(null).isEmpty());
        assertTrue(Util.getTargetPhonemes(new ArrayList<>()).isEmpty());

        System.out.println("testGetTargetPhonemes - real case");
        KnownCase passarinho = new KnownCase("Passarinho", "[pasa’ɾiɲo]", true, Arrays.asList(
                new Phoneme("p", Phoneme.POSITION.OI),
                new Phoneme("s", Phoneme.POSITION.OM),
                new Phoneme("ɾ", Phoneme.POSITION.OM),
                new Phoneme("ɲ", Phoneme.POSITION.OM)));

        KnownCase passaro = new KnownCase("Passarinho", "[pasa’ɾiɲo]", true, Arrays.asList(
                new Phoneme("p", Phoneme.POSITION.OI),
                new Phoneme("s", Phoneme.POSITION.OM),
                new Phoneme("ɾ", Phoneme.POSITION.OM)));

        List<Phoneme> expected = Arrays.asList(
                new Phoneme("p", Phoneme.POSITION.OI),
                new Phoneme("s", Phoneme.POSITION.OM),
                new Phoneme("ɾ", Phoneme.POSITION.OM));

        assertEquals(expected, Util.getTargetPhonemes(Arrays.asList(passarinho, passaro)));
        assertEquals(expected, Util.getTargetPhonemes(Arrays.asList(passaro, passarinho)));

        System.out.println("testGetTargetPhonemes - other tests");
        /**
         * Only the phonemes matters, all the other parameters here are useless.
         */
        KnownCase test = new KnownCase("Cama", "test", false, Arrays.asList(
                new Phoneme("a", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("c", Phoneme.POSITION.OM)));

        KnownCase test2 = new KnownCase("Cama", "test", true, Arrays.asList(
                new Phoneme("a", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("c", Phoneme.POSITION.OM),
                new Phoneme("d", Phoneme.POSITION.OM)));

        KnownCase test3 = new KnownCase("Cama", "test", false, Arrays.asList(
                new Phoneme("d", Phoneme.POSITION.OI),
                new Phoneme("e", Phoneme.POSITION.OM),
                new Phoneme("f", Phoneme.POSITION.OM),
                new Phoneme("g", Phoneme.POSITION.OM)));

        System.out.println("testGetTargetPhonemes - no common phonemes");
        assertTrue(Util.getTargetPhonemes(Arrays.asList(test, test2, test3)).isEmpty());
        assertTrue(Util.getTargetPhonemes(Arrays.asList(test, test3, test2)).isEmpty());
        assertTrue(Util.getTargetPhonemes(Arrays.asList(test2, test, test3)).isEmpty());
        assertTrue(Util.getTargetPhonemes(Arrays.asList(test2, test3, test)).isEmpty());
        assertTrue(Util.getTargetPhonemes(Arrays.asList(test3, test, test2)).isEmpty());
        assertTrue(Util.getTargetPhonemes(Arrays.asList(test3, test2, test)).isEmpty());

        expected = Arrays.asList(
                new Phoneme("a", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("c", Phoneme.POSITION.OM));
        System.out.println("testGetTargetPhonemes - common phonemes");

        assertEquals(expected, Util.getTargetPhonemes(Arrays.asList(test, test2)));
        assertEquals(expected, Util.getTargetPhonemes(Arrays.asList(test2, test)));

        System.out.println("testGetTargetPhonemes - no common phoneme");
        assertTrue(Util.getTargetPhonemes(Arrays.asList(test2, test3)).isEmpty());
        assertTrue(Util.getTargetPhonemes(Arrays.asList(test3, test2)).isEmpty());
    }

}
