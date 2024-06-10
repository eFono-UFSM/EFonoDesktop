package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationInfo;
import static br.com.efono.util.Defaults.SORTED_WORDS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 28.
 */
public class SimulationWordsSequenceTest {

    /**
     * Number of times that the same phoneme in the same position must be produced to be considered in the phonetic
     * inventory. It's 1 only in tests.
     */
    private final int minimum = 1;

    /**
     * Executes before each method.
     */
    @Before
    public void beforeMethod() {
        Defaults.TREE.clear();
        Defaults.TARGET_PHONEMES.clear();
    }

    /**
     * Tests parameters of {@link SimulationWordsSequence#runSimulation(Assessment, KnownCaseComparator, int)}.
     */
    @Test
    public void testRunSimulationParameters() {
        System.out.println("testRunSimulationParameters - all invalid");
        SimulationInfo info = SimulationWordsSequence.runSimulation(null, null, 0);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());

        System.out.println("testRunSimulationParameters - valid minimum");
        info = SimulationWordsSequence.runSimulation(null, null, 1);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());

        // only the comparator can be null, then it'll get the assessment as it is on the database
        System.out.println("testRunSimulationParameters - null comparator");
        KnownCase anel = new KnownCase(SORTED_WORDS[0], "anɛw", true, Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM)));
        info = SimulationWordsSequence.runSimulation(new Assessment(Arrays.asList(anel)), null, 1);

        assertTrue(info.getMapCounter().containsKey(new Phoneme("n", Phoneme.POSITION.OM)));
        assertTrue(info.getWordsRequired().contains(SORTED_WORDS[0]));

        System.out.println("testRunSimulationParameters - empty assessment");
        info = SimulationWordsSequence.runSimulation(new Assessment(), null, 1);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());

        System.out.println("testRunSimulationParameters - null assessment");
        info = SimulationWordsSequence.runSimulation(null, KnownCaseComparator.EasyWordsFirst, 1);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());

        System.out.println("testRunSimulationParameters - invalid minimum");
        info = SimulationWordsSequence.runSimulation(new Assessment(Arrays.asList(anel)),
                KnownCaseComparator.EasyWordsFirst, 0);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());
    }

    /**
     * Tests {@link SimulationWordsSequence#runSimulation(Assessment, KnownCaseComparator, int)}.
     */
    @Test
    public void testRunSimulationEasyFirst() {
        /**
         * The transcription doesn't matter. Only the phonemes.
         */
        KnownCase anel = new KnownCase(SORTED_WORDS[0], "anɛw", true, Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM)));

        KnownCase cabelo = new KnownCase(SORTED_WORDS[16], "kabelu", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM)));

        KnownCase gato = new KnownCase(SORTED_WORDS[21], "gato", true, Arrays.asList(
                new Phoneme("g", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM)));

        KnownCase chinelo = new KnownCase(SORTED_WORDS[33], "ʃinɛlo", true, Arrays.asList(
                new Phoneme("ʃ", Phoneme.POSITION.OI),
                new Phoneme("n", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM)));

        /**
         * 'fakeCase' is harder than 'Cabelo'. But 'fakeCase' has more phonemes than 'Cabelo', so depending on the
         * sequence of the evaluation 'Cabelo' won't be necessary, because 'fakeCase' can test all its phonemes and +1.
         */
        KnownCase fakeCase = new KnownCase(SORTED_WORDS[35], "kajʃə", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM),
                new Phoneme("ɾ", Phoneme.POSITION.OM)));

        Assessment assessment = new Assessment(Arrays.asList(anel, cabelo, gato, chinelo, fakeCase));

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        mapCounterExpected.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        // cabelo
        mapCounterExpected.put(new Phoneme("k", Phoneme.POSITION.OI), 2);
        mapCounterExpected.put(new Phoneme("b", Phoneme.POSITION.OM), 2);
        mapCounterExpected.put(new Phoneme("l", Phoneme.POSITION.OM), 3);
        // gato
        mapCounterExpected.put(new Phoneme("g", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("t", Phoneme.POSITION.OM), 1);
        // chinelo
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 1);
        // fakeCase
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OM), 1);

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final List<String> wordsRequiredExpected = Arrays.asList("Anel", "Cabelo", "Gato", "Chinelo", SORTED_WORDS[35]);

        KnownCaseComparator comp = KnownCaseComparator.EasyWordsFirst;
        SimulationInfo expected = new SimulationInfo(mapCounterExpected, wordsRequiredExpected, assessment, comp,
                SimulationWordsSequence.SPLIT_CONSONANTS);
        SimulationInfo result = SimulationWordsSequence.runSimulation(assessment, comp,
                minimum);

        assertEquals(expected, result);
    }

    /**
     * Tests {@link SimulationWordsSequence#runSimulation(Assessment, KnownCaseComparator, int)}.
     */
    @Test
    public void testRunSimulationHardFirst() {
        /**
         * The transcription doesn't matter. Only the phonemes.
         */
        KnownCase anel = new KnownCase(SORTED_WORDS[0], "anɛw", true, Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM)));

        KnownCase cabelo = new KnownCase(SORTED_WORDS[16], "kabelu", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM)));

        KnownCase gato = new KnownCase(SORTED_WORDS[21], "gato", true, Arrays.asList(
                new Phoneme("g", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM)));

        KnownCase chinelo = new KnownCase(SORTED_WORDS[33], "ʃinɛlo", true, Arrays.asList(
                new Phoneme("ʃ", Phoneme.POSITION.OI),
                new Phoneme("n", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM)));

        /**
         * 'fakeCase' is harder than 'Cabelo'. But 'fakeCase' has more phonemes than 'Cabelo', so depending on the
         * sequence of the evaluation 'Cabelo' won't be necessary, because 'fakeCase' can test all its phonemes and +1.
         */
        KnownCase fakeCase = new KnownCase(SORTED_WORDS[35], "kajʃə", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM),
                new Phoneme("ɾ", Phoneme.POSITION.OM)));

        Assessment assessment = new Assessment(Arrays.asList(anel, cabelo, gato, chinelo, fakeCase));

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        // fakeCase
        mapCounterExpected.put(new Phoneme("k", Phoneme.POSITION.OI), 2);
        mapCounterExpected.put(new Phoneme("b", Phoneme.POSITION.OM), 2);
        mapCounterExpected.put(new Phoneme("l", Phoneme.POSITION.OM), 3);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OM), 1);
        // chinelo
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        // gato
        mapCounterExpected.put(new Phoneme("g", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("t", Phoneme.POSITION.OM), 1);
        // cabelo: no new phonemes to be tested
        // anel: no new phonemes to be tested

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final List<String> wordsRequiredExpected = Arrays.asList(SORTED_WORDS[35], "Chinelo", "Gato");

        KnownCaseComparator comp = KnownCaseComparator.HardWordsFirst;
        SimulationInfo expected = new SimulationInfo(mapCounterExpected, wordsRequiredExpected, assessment, comp,
                SimulationWordsSequence.SPLIT_CONSONANTS);
        SimulationInfo result = SimulationWordsSequence.runSimulation(assessment, comp,
                minimum, true, true);

        assertEquals(expected, result);
    }

    /**
     * Tests {@link SimulationWordsSequence#runSimulation(Assessment, KnownCaseComparator, int)} for consonant clusters.
     */
    @Test
    public void testRunSimulationConsonantClusters() {
        System.out.println("testRunSimulationConsonantClusters - with consonant clusters");
        KnownCase cobra = new KnownCase(SORTED_WORDS[52], "kɔbɾə", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("bɾ", Phoneme.POSITION.OCME)));

        KnownCase fakeCase = new KnownCase(SORTED_WORDS[55], "kɔbɾə", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("bɾ", Phoneme.POSITION.OCME)));

        KnownCase chifre = new KnownCase(SORTED_WORDS[65], "ʃifɾis", true, Arrays.asList(
                new Phoneme("ʃ", Phoneme.POSITION.OI),
                new Phoneme("fɾ", Phoneme.POSITION.OCME),
                new Phoneme("s", Phoneme.POSITION.CF)));
        final Assessment assessment = new Assessment(Arrays.asList(cobra, fakeCase, chifre));

        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        // cobra: bɾ(OCME) -> b(OCME) + ɾ(OCME)
        mapCounterExpected.put(new Phoneme("k", Phoneme.POSITION.OI), 2);
        mapCounterExpected.put(new Phoneme("b", Phoneme.POSITION.OCME), 2);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 3);
        // fakeCase: not needed
        // chifre: fɾ(OCME) -> f(OCME) + ɾ(OCME)
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("f", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("s", Phoneme.POSITION.CF), 1);

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final List<String> wordsRequiredExpected = Arrays.asList(SORTED_WORDS[52], SORTED_WORDS[65]);

        KnownCaseComparator comp = null;
        SimulationInfo expected = new SimulationInfo(mapCounterExpected, wordsRequiredExpected, assessment, comp,
                SimulationWordsSequence.SPLIT_CONSONANTS);
        SimulationInfo result = SimulationWordsSequence.runSimulation(assessment, comp, minimum);

        assertEquals(expected, result);
    }

    /**
     * Tests {@link SimulationWordsSequence#getWordsRequired(List, Map, boolean, int, boolean)} for phonetic inventory.
     */
    @Test
    public void testGetWordsRequiredPhoneticInventory() {
        System.out.println("testGetWordsRequiredPhoneticInventory - invalid parameters");
        assertTrue(SimulationWordsSequence.getWordsRequired(null, null, true, 0, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, new HashMap<>(), true, 0, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, null, false, 0, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, new HashMap<>(), false, 0, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, null, true, -1, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, new HashMap<>(), true, -1, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), null, true, 0, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), new HashMap<>(), true, 0, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), null, false, 0, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), new HashMap<>(), false, 0, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), null, false, 1, true).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), new HashMap<>(), false, 1, true).isEmpty());

        System.out.println("testGetWordsRequiredPhoneticInventory - valid cases");
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", false, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase biblioteca = new KnownCase("Biblioteca", "[biblio’tɛkə]", true, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("bl", Phoneme.POSITION.OCME),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("kɾ", Phoneme.POSITION.OCME)));
        KnownCase bicicleta = new KnownCase("Bicicleta", "[bisi’klɛtə]", true, Arrays.asList(
                new Phoneme("kl", Phoneme.POSITION.OCME))); // nao precisa
        // tanto faz a transcrição aqui, o que importa são os fonemas
        KnownCase jacare = new KnownCase("Jacaré", "[batata]", true, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("t", Phoneme.POSITION.OM)));

        List<KnownCase> list = Arrays.asList(batom, biblioteca, bicicleta, jacare);

        final Map<Phoneme, Integer> expectedMapCounter = new HashMap<>();
        expectedMapCounter.put(new Phoneme("b", Phoneme.POSITION.OI), 3);
        expectedMapCounter.put(new Phoneme("t", Phoneme.POSITION.OM), 4);
        expectedMapCounter.put(new Phoneme("b", Phoneme.POSITION.OCME), 1);
        expectedMapCounter.put(new Phoneme("l", Phoneme.POSITION.OCME), 2);
        expectedMapCounter.put(new Phoneme("k", Phoneme.POSITION.OCME), 2);
        expectedMapCounter.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);

        final Map<Phoneme, Integer> mapCounter = new HashMap<>();

        List<String> expected = Arrays.asList("Batom", "Biblioteca"); // we don't need Bicicleta, because we already have k(OCME) and l(OCME).
        List<String> result = SimulationWordsSequence.getWordsRequired(list, mapCounter, true, 1, true);
        assertTrue(expected.containsAll(result));
        assertTrue(result.containsAll(expected));
        assertEquals(expected, result);

        assertEquals(expectedMapCounter.size(), mapCounter.size());
        assertTrue(mapCounter.keySet().containsAll(expectedMapCounter.keySet()));
        Iterator<Map.Entry<Phoneme, Integer>> it = expectedMapCounter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Phoneme, Integer> next = it.next();
            assertEquals(next.getValue(), mapCounter.get(next.getKey()));
        }

        System.out.println("testGetWordsRequiredPhoneticInventory - not splitting consonant clusters");
        expectedMapCounter.clear();
        expectedMapCounter.put(new Phoneme("b", Phoneme.POSITION.OI), 3);
        expectedMapCounter.put(new Phoneme("t", Phoneme.POSITION.OM), 4);
        expectedMapCounter.put(new Phoneme("bl", Phoneme.POSITION.OCME), 1);
        expectedMapCounter.put(new Phoneme("kɾ", Phoneme.POSITION.OCME), 1);
        expectedMapCounter.put(new Phoneme("kl", Phoneme.POSITION.OCME), 1);

        expected = Arrays.asList("Batom", "Biblioteca", "Bicicleta"); // now, we need Bicicleta, because "kl" does not repeat.
        result = SimulationWordsSequence.getWordsRequired(list, mapCounter, false, 1, true);
        assertTrue(expected.containsAll(result));
        assertTrue(result.containsAll(expected));
        assertEquals(expected, result);

        assertEquals(expectedMapCounter.size(), mapCounter.size());
        assertTrue(mapCounter.keySet().containsAll(expectedMapCounter.keySet()));

        it = expectedMapCounter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Phoneme, Integer> next = it.next();
            assertEquals(next.getValue(), mapCounter.get(next.getKey()));
        }

        System.out.println("testGetWordsRequiredPhoneticInventory - changing the order");
        list = Arrays.asList(jacare, bicicleta, biblioteca, batom);

        expected = Arrays.asList("Jacaré", "Bicicleta", "Biblioteca");
        result = SimulationWordsSequence.getWordsRequired(list, mapCounter, true, 1, true);
        assertTrue(expected.containsAll(result));
        assertTrue(result.containsAll(expected));
        assertEquals(expected, result);
    }

    /**
     * Tests {@link SimulationWordsSequence#getWordsRequired(List, Map, boolean, int, boolean)} for PCC-R.
     */
    @Test
    public void testGetWordsRequiredPCCR() {
        System.out.println("testGetWordsRequiredPCCR - invalid parameters");
        assertTrue(SimulationWordsSequence.getWordsRequired(null, null, true, 0, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, new HashMap<>(), true, 0, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, null, false, 0, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, new HashMap<>(), false, 0, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, null, true, -1, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(null, new HashMap<>(), true, -1, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), null, true, 0, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), new HashMap<>(), true, 0, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), null, false, 0, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), new HashMap<>(), false, 0, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), null, false, 1, false).isEmpty());
        assertTrue(SimulationWordsSequence.getWordsRequired(new ArrayList<>(), new HashMap<>(), false, 1, false).isEmpty());

        System.out.println("testGetWordsRequiredPCCR - valid cases");
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", false, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase biblioteca = new KnownCase("Biblioteca", "[biblio’tɛkə]", true, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("bl", Phoneme.POSITION.OCME),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("kɾ", Phoneme.POSITION.OCME)));
        KnownCase bicicleta = new KnownCase("Bicicleta", "[bisi’klɛtə]", true, Arrays.asList(
                new Phoneme("kl", Phoneme.POSITION.OCME))); // nao precisa
        // tanto faz a transcrição aqui, o que importa são os fonemas
        KnownCase jacare = new KnownCase("Jacaré", "[batata]", true, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("t", Phoneme.POSITION.OM)));

        // target phonemes for each word
        Defaults.TARGET_PHONEMES.put("Batom", Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("s", Phoneme.POSITION.OM))); // testing another phoneme

        // all phonemes from Bicicleta were already tested in Batom
        Defaults.TARGET_PHONEMES.put("Bicicleta", Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("s", Phoneme.POSITION.OM),
                new Phoneme("t", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Biblioteca", Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("bl", Phoneme.POSITION.OCME),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("k", Phoneme.POSITION.OM)));

        // all phonemes from Jacaré were already tested in Batom
        Defaults.TARGET_PHONEMES.put("Jacaré", Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("t", Phoneme.POSITION.OM)));

        List<KnownCase> list = Arrays.asList(batom, biblioteca, bicicleta, jacare);

        // how many times each phoneme was tested
        final Map<Phoneme, Integer> expectedMapCounter = new HashMap<>();
        expectedMapCounter.put(new Phoneme("b", Phoneme.POSITION.OI), 4);
        expectedMapCounter.put(new Phoneme("t", Phoneme.POSITION.OM), 5);
        expectedMapCounter.put(new Phoneme("b", Phoneme.POSITION.OCME), 1);
        expectedMapCounter.put(new Phoneme("l", Phoneme.POSITION.OCME), 1);
        expectedMapCounter.put(new Phoneme("k", Phoneme.POSITION.OM), 1);
        expectedMapCounter.put(new Phoneme("s", Phoneme.POSITION.OM), 2);

        final Map<Phoneme, Integer> mapCounter = new HashMap<>();

        List<String> expected = Arrays.asList("Batom", "Biblioteca");
        List<String> result = SimulationWordsSequence.getWordsRequired(list, mapCounter, true, 1, false);
        assertTrue(expected.containsAll(result));
        assertTrue(result.containsAll(expected));
        assertEquals(expected, result);

        assertEquals(expectedMapCounter.size(), mapCounter.size());
        assertTrue(mapCounter.keySet().containsAll(expectedMapCounter.keySet()));
        Iterator<Map.Entry<Phoneme, Integer>> it = expectedMapCounter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Phoneme, Integer> next = it.next();
            assertEquals(next.getValue(), mapCounter.get(next.getKey()));
        }

        System.out.println("testGetWordsRequiredPCCR - changing the order");
        list = Arrays.asList(jacare, bicicleta, biblioteca, batom);

        expected = Arrays.asList("Jacaré", "Bicicleta", "Biblioteca");
        result = SimulationWordsSequence.getWordsRequired(list, mapCounter, true, 1, false);
        assertTrue(expected.containsAll(result));
        assertTrue(result.containsAll(expected));
        assertEquals(expected, result);
    }

    /**
     * Tests {@link SimulationWordsSequence#getNextWords(List, boolean)}.
     */
    @Test
    public void testGetNextWords() {
        System.out.println("testGetNextWords - parameters");
        assertTrue(SimulationWordsSequence.getNextWords(null, true).isEmpty());
        assertTrue(SimulationWordsSequence.getNextWords(null, false).isEmpty());
        assertTrue(SimulationWordsSequence.getNextWords(new ArrayList<>(), true).isEmpty());

        List<Phoneme> toBeTested = Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("b", Phoneme.POSITION.OCME),
                new Phoneme("l", Phoneme.POSITION.OCME));

        Defaults.TARGET_PHONEMES.put("Anel", Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Batom", Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Bicicleta", Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("s", Phoneme.POSITION.OM),
                new Phoneme("kl", Phoneme.POSITION.OCME),
                new Phoneme("t", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Biblioteca", Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("bl", Phoneme.POSITION.OCME),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("k", Phoneme.POSITION.OM)));

        System.out.println("testGetNextWords - splitting consonant clusters");
        List<String> result = SimulationWordsSequence.getNextWords(toBeTested, true);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(Arrays.asList("Batom", "Bicicleta", "Biblioteca")));

        Defaults.TARGET_PHONEMES.clear();
        Defaults.TARGET_PHONEMES.put("Anel", Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Batom", Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Bicicleta", Arrays.asList(
                new Phoneme("s", Phoneme.POSITION.OM),
                new Phoneme("kl", Phoneme.POSITION.OCME)));

        Defaults.TARGET_PHONEMES.put("Biblioteca", Arrays.asList(
                new Phoneme("bl", Phoneme.POSITION.OCME),
                new Phoneme("k", Phoneme.POSITION.OM)));
        
        System.out.println("testGetNextWords - without splitting consonant clusters");
        toBeTested = Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("b", Phoneme.POSITION.OCME),
                new Phoneme("l", Phoneme.POSITION.OCME),
                new Phoneme("kl", Phoneme.POSITION.OCME));

        result = SimulationWordsSequence.getNextWords(toBeTested, false);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList("Batom", "Bicicleta")));
    }

}
