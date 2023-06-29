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
import org.junit.Ignore;

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
     * Tests {@link SimulationWordsSequence#runSimulation(Assessment, KnownCaseComparator, int)}.
     */
    @Test
    public void testRunSimulationEasyFirst() {
        // TODO: test with null comp
        // TODO: precisa ver se o "r" é a mesma coisa que o "ɾ" na fonoaudiologia
        /**
         * The transcription given doesn't matter. Only the phonemes.
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
        // TODO: test with null comp
        // TODO: precisa ver se o "r" é a mesma coisa que o "ɾ" na fonoaudiologia
        /**
         * The transcription given doesn't matter. Only the phonemes.
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

    @Test
    @Ignore
    public void testRunSimulationConsonantClusters() {
        // TODO
        KnownCase cobra = new KnownCase(SORTED_WORDS[52], "kɔbɾə", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("bɾ", Phoneme.POSITION.OCME))); // 52

        KnownCase chifre = new KnownCase(SORTED_WORDS[65], "ʃifɾis", true, Arrays.asList(
                new Phoneme("ʃ", Phoneme.POSITION.OI),
                new Phoneme("fɾ", Phoneme.POSITION.OCME),
                new Phoneme("s", Phoneme.POSITION.CF))); // 65

        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        // TODO: encontros consonantais: bɾ(OCME) -> b(OCME) + ɾ(OCME)
        mapCounterExpected.put(new Phoneme("b", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        // fɾ(OCME) -> f(OCME) + ɾ(OCME)
        mapCounterExpected.put(new Phoneme("f", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("s", Phoneme.POSITION.CF), 1);
        fail();
    }

    @Test
    public void test() {
        assertTrue(true);
    }

}
