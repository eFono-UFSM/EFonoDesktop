package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationInfo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import static br.com.efono.model.KnownCaseComparator.Defaults.SORTED_WORDS;

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

        SimulationInfo expected = new SimulationInfo(mapCounterExpected, wordsRequiredExpected);
        SimulationInfo result = SimulationWordsSequence.runSimulation(assessment, KnownCaseComparator.EasyWordsFirst,
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

        SimulationInfo expected = new SimulationInfo(mapCounterExpected, wordsRequiredExpected);
        SimulationInfo result = SimulationWordsSequence.runSimulation(assessment, KnownCaseComparator.HardWordsFirst,
                minimum);

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

        SimulationInfo expected = new SimulationInfo(mapCounterExpected, wordsRequiredExpected);
        SimulationInfo result = SimulationWordsSequence.runSimulation(assessment, null, minimum);

        assertEquals(expected, result);
    }

}
