package br.com.efono.model;

import br.com.efono.util.Util;
import br.com.efono.util.UtilTest;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 03.
 */
public class KnownCaseTest {

    /**
     * Tests {@link KnownCase#loadFile(File)} when invalid path is given.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void test() throws URISyntaxException, IOException {
        System.out.println("testReadTranscriptions - invalid path - FileNotFoundException");

        File resDir = new File(UtilTest.class.getResource("/data").toURI());
        File invalid = new File(resDir, "invalid.json");
        List<KnownCase> cases = KnownCase.loadFile(invalid);
        assertTrue(cases.isEmpty());
    }

    /**
     * Tests construct for null word.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructWord() {
        new KnownCase(null, "", false);
    }

    /**
     * Tests construct for null representation.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructRepresentation() {
        new KnownCase("anel", null, false);
    }

    /**
     * Tests construct for null phonemes.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructPhonemes() {
        new KnownCase("anel", "", false, null);
    }

    /**
     * Tests {@link KnownCase#getWord()}, {@link KnownCase#getRepresentation()}, {@link KnownCase#getPhonemes()} and
     * {@link KnownCase#isCorrect()}.
     */
    @Test
    public void testGetters() {
        System.out.println("testGetters");
        KnownCase instance = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));

        assertEquals("Anel", instance.getWord());
        assertEquals("anɛw", instance.getRepresentation()); // clean representation, always
        assertTrue(instance.isCorrect());
        assertEquals(Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)), instance.getPhonemes());
    }

    /**
     * Tests {@link KnownCase#hashCode()}.
     */
    @Test
    public void testHashCode() {
        System.out.println("testHashCode - only word and representation fields are considered");
        KnownCase instance = new KnownCase("Anel", "[a’nɛw]", true);
        KnownCase other = new KnownCase("Anel", "[a’nɛw]", true);
        assertEquals(instance.hashCode(), other.hashCode());

        other = new KnownCase("Anel", "[a’nɛw]", false);
        assertEquals(instance.hashCode(), other.hashCode());

        other = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        assertEquals(instance.hashCode(), other.hashCode());

        other = new KnownCase("Anel", "[a’nɛw]", false, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        assertEquals(instance.hashCode(), other.hashCode());

        other = new KnownCase("Anel", "[a’nɛw]", false, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OM)));
        assertEquals(instance.hashCode(), other.hashCode());
    }

    /**
     * Tests {@link KnownCase#equals(Object)}.
     */
    @Test
    public void testEquals() {
        System.out.println("testEquals - only word and representation fields are considered");
        System.out.println("testEquals - empty case");
        KnownCase empty = new KnownCase();
        assertTrue(empty.equals(new KnownCase()));

        System.out.println("testEquals - same instance");
        KnownCase instance = new KnownCase("Anel", "[a’nɛw]", true);
        assertTrue(instance.equals(instance));

        System.out.println("testEquals - different classes");
        Object obj = "";
        assertFalse(instance.equals(obj));

        System.out.println("testEquals - null object");
        obj = null;
        assertFalse(instance.equals(obj));

        System.out.println("testEquals - different instances but the same object");
        KnownCase other = new KnownCase("Anel", "[a’nɛw]", true);
        assertTrue(instance.equals(other));

        System.out.println("testEquals - same instance but other with phonemes");
        other = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        assertTrue(instance.equals(other));

        System.out.println("testEquals - same case, but inconsistency in the correctness: won't consider correct field");
        instance = new KnownCase("Anel", "[a’nɛw]", true);
        other = new KnownCase("Anel", "[a’nɛw]", false);
        assertTrue(instance.equals(other));

        System.out.println("testEquals - same word, different representation");
        instance = new KnownCase("Anel", "[a’nɛw]", true);
        other = new KnownCase("Anel", "[anɛw'ziɲu]", true);
        assertFalse(instance.equals(other));

        System.out.println("testEquals - same word, different phonemes");
        instance = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        other = new KnownCase("Anel", "[anɛw'ziɲu]", true, Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM),
                new Phoneme("z", Phoneme.POSITION.OM),
                new Phoneme("ɲ", Phoneme.POSITION.OM)));
        assertFalse(instance.equals(other));

        System.out.println("testEquals - different words, same representation");
        instance = new KnownCase("Anel", "[a’nɛw]", true);
        other = new KnownCase("Barriga", "[a’nɛw]", true);
        assertFalse(instance.equals(other));
    }

    /**
     * Tests {@link KnownCase#loadFile(java.io.File)}.
     *
     * @throws java.net.URISyntaxException Exception.
     * @throws java.io.IOException Exception reading file.
     */
    @Test
    public void testLoadFile() throws URISyntaxException, IOException {
        System.out.println("testLoadFile - tests for valid path but invalid file");
        File empty = new File(UtilTest.class.getResource("/data/empty.txt").toURI());
        List<KnownCase> emptyCases = KnownCase.loadFile(empty);
        assertTrue(emptyCases.isEmpty());

        System.out.println("testLoadFile - test if all cases were read");
        File allCorrect = new File(UtilTest.class.getResource("/data/allCorrect.json").toURI());
        List<KnownCase> cases = KnownCase.loadFile(allCorrect);
        assertEquals(163, cases.size());
    }

    /**
     * Tests {@link KnownCase#saveKnownCases(List, File)}.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testSaveKnownCases() throws URISyntaxException {
        // TODO:
        System.out.println("testSaveKnownCases - test for invalid parameters");

        System.out.println("testSaveKnownCases - valid parameters");
        File file = new File(UtilTest.class.getResource("/data/allKnownCases.csv").toURI());
        List<KnownCase> casesFromCSV = KnownCase.buildKnownCases(file);

        // word -> list with known cases
        Map<String, List<KnownCase>> mapCases = new HashMap<>(); // all the cases are saved for each word, for now...

        // vou testar palavra por palavra e ir avançando pra ver o resultado
        List<String> enableWords = Arrays.asList(new String[]{"Anel"});
        enableWords.forEach(w -> mapCases.put(w, new ArrayList<>()));

        List<KnownCase> allCases = new ArrayList<>();
        // just reading the cases
        for (KnownCase c : casesFromCSV) {
            if (enableWords.contains(c.getWord())) {
                List<KnownCase> list = mapCases.get(c.getWord());
                // only word and representation are considered
                if (!list.contains(c)) {
                    c.putPhonemes(Util.getConsonantPhonemes(c.getRepresentation()));

                    list.add(c);
                    allCases.add(c); // to store later
                }
            } else {
                break;
            }
        }

        Iterator<Map.Entry<String, List<KnownCase>>> it = mapCases.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<KnownCase>> next = it.next();

            System.out.println(next.getKey() + " -> " + next.getValue().size());

            File resDir = new File(KnownCaseTest.class.getResource("/cases").toURI());
            File out = new File(resDir, "output-" + next.getKey() + ".json");
            System.out.println("testSaveKnownCases - saving cases for word " + next.getKey());
            KnownCase.saveKnownCases(next.getValue(), out);
        }

        /**
         * allCases.json contem os casos que foram validados
         * 
         * 1. gera um output com os casos de cada palavra
         * 2. valida esse output
         * 3. copia todo o conteudo de allCases.json para o arquivo em src/main/resources/data/allCases.json (commitado)
         * 3.1 não pode ter nenhuma diferença nos casos anteriores já validados
         */
        File resDir = new File(KnownCaseTest.class.getResource("/cases").toURI());
        File outAll = new File(resDir, "allCases.json");
        KnownCase.saveKnownCases(allCases, outAll);

        // just to print all the files path
        fail();
    }

}
