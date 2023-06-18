package br.com.efono.model;

import br.com.efono.util.Util;
import br.com.efono.util.UtilTest;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
    public void testLoadFileException() throws URISyntaxException, IOException {
        System.out.println("testLoadFile - invalid path - FileNotFoundException");

        File resDir = new File(UtilTest.class.getResource("/data").toURI());
        File invalid = new File(resDir, "invalid.json");
        List<KnownCase> cases = KnownCase.loadFile(invalid);
        assertTrue(cases.isEmpty());
    }

    /**
     * Tests KnownCase constructor for wrong cases inserted by the user.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testConstructorWithJSON() throws URISyntaxException, IOException {
        /**
         * kazinhə (wrong transcription from database). The /nh/ should be replaced by /ɲ/. The class KnownCase should
         * deal with that type of mistake.
         */
        System.out.println("testConstructorWithJSON - tests for wrong cases inserted by the user: "
                + "/nh/ in transcription should be replaced by /ɲ/");
        File allCorrect = new File(UtilTest.class.getResource("/data/specialCases.json").toURI());
        List<KnownCase> cases = KnownCase.loadFile(allCorrect);
        assertEquals(3, cases.size());

        assertEquals("floziɲa", cases.get(0).getRepresentation());
        assertEquals("kaziɲə", cases.get(1).getRepresentation());
        assertEquals("anɛwziɲu", cases.get(2).getRepresentation()); // all normal
    }

    /**
     * Tests {@link KnownCase#KnownCase(java.lang.String, java.lang.String, boolean)} with /nh/ phoneme in the given
     * transcription.
     */
    @Test
    public void testConstructorWithTreat() {
        System.out.println("testConstructorWithTreat");

        KnownCase instance = new KnownCase("Flor", "floziɲa", false);
        assertEquals("floziɲa", instance.getRepresentation());
    }

    /**
     * Tests {@link KnownCase#KnownCase(KnownCase)}.
     */
    @Test
    public void testConstructorCopy() {
        System.out.println("testConstructorCopy");

        KnownCase instance = new KnownCase("Flor", "flozinha", false);
        assertEquals("floziɲa", instance.getRepresentation());

        KnownCase copy = new KnownCase(instance);
        assertTrue(instance.equals(copy));
        assertEquals("floziɲa", copy.getRepresentation());
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
     * Tests {@link KnownCase#buildKnownCases(File)}.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testBuildKnownCases() throws URISyntaxException {
        System.out.println("testBuildKnownCases - .csv file containing valid and invalid cases");
        File file = new File(UtilTest.class.getResource("/data/testBuildCases.csv").toURI());

        List<KnownCase> list = KnownCase.buildKnownCases(file);
        assertEquals(2, list.size());

        assertEquals("Igreja", list.get(0).getWord());
        assertEquals("Gritar", list.get(1).getWord());
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
        Map<String, List<KnownCase>> mapCases = new LinkedHashMap<>();// all the cases are saved for each word, for now...

        // vou testar palavra por palavra e ir avançando pra ver o resultado
        List<String> enableWords = Arrays.asList(new String[]{"Anel", "Barriga", "Batom", "Bebê", "Beijo", "Biblioteca",
            "Bicicleta", "Bolsa", "Brinco", "Bruxa", "Cabelo", "Cachorro", "Caixa", "Calça", "Cama", "Caminhão", "Casa",
            "Cavalo", "Chapéu", "Chiclete", "Chifre", "Chinelo", "Cobra", "Coelho", "Colher", "Cruz", "Dado", "Dedo",
            "Dente", "Dragão", "Escrever", "Espelho", "Estrela", "Faca", "Flor", "Floresta", "Fogo", "Folha", "Fralda",
            "Fruta", "Galinha", "Garfo", "Gato", "Girafa", "Grama", "Gritar", "Igreja", "Jacaré", "Jornal", "Lápis",
            "Letra", "Língua", "Livro", "Magro", "Mesa", "Microfone", "Nariz", "Navio"});
        enableWords.forEach(w -> mapCases.put(w, new ArrayList<>()));

        /**
         * TODO: essas transcrições estão corretas? ʒoɾnal
         * 
         * conferir casos com as fonos: mɾago, mɾikofoni.
         * 
         * Alguns casos estão no diminutivo como incorretos, tipo "naɾiziɲu", não deveria ser considerado correto?
         */
        // just reading the cases
        for (KnownCase c : casesFromCSV) {
            if (enableWords.contains(c.getWord())) {
                List<KnownCase> list = mapCases.get(c.getWord());
                // only word and representation are considered
                if (!list.contains(c)) {
                    c.putPhonemes(Util.getConsonantPhonemes(c.getRepresentation()));

                    list.add(c);
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
         * 1. gera um output com os casos de cada palavra 2. valida esse output 3. copia todo o conteudo de
         * allCases.json para o arquivo em src/main/resources/data/allCases.json (commitado) 3.1 não pode ter nenhuma
         * diferença nos casos anteriores já validados
         */
        File resDir = new File(KnownCaseTest.class.getResource("/cases").toURI());
        // TODO: esta enviando para o target/, tem como enviar para o sources?
        File outAll = new File(resDir, "allCases.json");

        List<KnownCase> allCases = new LinkedList<>();
        mapCases.values().forEach(val -> allCases.addAll(val));

        KnownCase.saveKnownCases(allCases, outAll);

        // just to print all the files path
        fail();
    }

}
