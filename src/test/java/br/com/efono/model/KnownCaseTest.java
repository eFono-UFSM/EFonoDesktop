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
     */
    @Test
    public void testSaveKnownCases() throws URISyntaxException {
        // TODO:
        System.out.println("testSaveKnownCases - test for invalid parameters");

        System.out.println("testSaveKnownCases - valid parameters");
        File file = new File(UtilTest.class.getResource("/data/allKnownCases.csv").toURI());
        List<KnownCase> allCases = KnownCase.buildKnownCases(file);

        // word -> list with known cases
        Map<String, List<KnownCase>> mapCases = new HashMap<>(); // all the cases are saved for each word, for now...

        // vou testar palavra por palavra e ir avançando pra ver o resultado
        List<String> enableWords = Arrays.asList(new String[]{"Anel"});
        enableWords.forEach(w -> mapCases.put(w, new ArrayList<>()));

        // just reading the cases
        for (KnownCase c : allCases) {
            if (enableWords.contains(c.getWord())) {
                List<Phoneme> consonantPhonemes = Util.getConsonantPhonemes(c.getRepresentation());

                System.out.println(c.getRepresentation());
                System.out.println("\t" + consonantPhonemes);

                c.putPhonemes(consonantPhonemes);

                mapCases.get(c.getWord()).add(c);
            } else {
                break;
            }
        }

        Iterator<Map.Entry<String, List<KnownCase>>> it = mapCases.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<KnownCase>> next = it.next();

            System.out.println(next.getKey() + " -> " + next.getValue().size());
            // TODO: usar um arquivo no target/ do projeto
            File out = new File("C:\\Users\\Joao\\Documents\\mestrado\\Java\\output-" + next.getKey() + ".json");

            System.out.println("testSaveKnownCases - saving cases for word " + next.getKey());
            KnownCase.saveKnownCases(next.getValue(), out);
        }

        fail();
    }

}
