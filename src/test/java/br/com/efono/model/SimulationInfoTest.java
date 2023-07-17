package br.com.efono.model;

import br.com.efono.util.Defaults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 28.
 */
public class SimulationInfoTest {

    /**
     * Tests {@link SimulationInfo#SimulationInfo(java.util.Map, java.util.List)}.
     */
    @Test(expected = NullPointerException.class)
    public void testConstruct() {
        SimulationInfo instance = new SimulationInfo(null, Arrays.asList("Casa"));
    }

    /**
     * Tests {@link SimulationInfo#SimulationInfo(java.util.Map, java.util.List)}.
     */
    @Test(expected = NullPointerException.class)
    public void testConstruct2() {
        SimulationInfo instance = new SimulationInfo(new HashMap<>(), null);
    }

    /**
     * Tests {@link SimulationInfo#equals(Object)}.
     */
    @Test
    public void testEquals() {
        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        mapCounterExpected.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 2);
        mapCounterExpected.put(new Phoneme("l", Phoneme.POSITION.OM), 1);
        mapCounterExpected.put(new Phoneme("k", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("b", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("f", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("s", Phoneme.POSITION.CF), 1);

        final List<String> wordsRequiredExpected = new LinkedList<>();
        wordsRequiredExpected.addAll(Arrays.asList("Anel", "Casa", "Porta"));

        SimulationInfo instance = new SimulationInfo(mapCounterExpected, wordsRequiredExpected);

        System.out.println("testEquals - same instance");
        assertTrue(instance.equals(instance));

        System.out.println("testEquals - null object");
        Object obj = null;
        assertFalse(instance.equals(obj));

        System.out.println("testEquals - object from different class");
        obj = "";
        assertFalse(instance.equals(obj));

        System.out.println("testEquals - counter maps with different sizes");
        final Map<Phoneme, Integer> otherMap = new HashMap();
        otherMap.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        otherMap.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 2);

        assertFalse(instance.equals(new SimulationInfo(otherMap, wordsRequiredExpected)));

        System.out.println("testEquals - counter maps with same size but different values");
        otherMap.clear();
        otherMap.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        otherMap.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 2);
        otherMap.put(new Phoneme("l", Phoneme.POSITION.OM), 1);
        otherMap.put(new Phoneme("k", Phoneme.POSITION.OI), 1);
        otherMap.put(new Phoneme("b", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("f", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("ɾ", Phoneme.POSITION.CF), 1); // here is the difference
        assertFalse(instance.equals(new SimulationInfo(otherMap, wordsRequiredExpected)));

        System.out.println("testEquals - counter maps equals but not the same words required");
        otherMap.clear();
        otherMap.putAll(mapCounterExpected);

        assertFalse(instance.equals(new SimulationInfo(otherMap, Arrays.asList("Anel", "Casa", "Teste"))));

        System.out.println("testEquals - repeated words");
        assertFalse(new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Casa", "Porta")).
                equals(new SimulationInfo(otherMap, Arrays.asList("Anel", "Casa", "Casa"))));

        assertFalse(instance.equals(new SimulationInfo(otherMap, Arrays.asList("Anel", "Casa", "Casa"))));

        System.out.println("testEquals - all equals");
        assertTrue(instance.equals(new SimulationInfo(otherMap, Arrays.asList("Anel", "Porta", "Casa"))));
    }

    /**
     * Tests {@link SimulationInfo#equals(java.lang.Object)} when {@link SimulationInfo#SimulationInfo(Map, List,
     * KnownCaseComparator, boolean)} is used.
     */
    @Test
    public void testEquals2() {
        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        mapCounterExpected.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 2);

        SimulationInfo instance = new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Cobra"), null, null, true);

        System.out.println("testEquals2 - all the same but different comparators");
        assertFalse(instance.equals(new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Cobra"), null,
                KnownCaseComparator.EasyWordsFirst, true)));

        System.out.println("testEquals2 - all the same but different flags to split consonant clusters");
        assertFalse(instance.equals(new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Cobra"), null,
                null, false)));

        System.out.println("testEquals2 - all the same but different non null comparators");
        instance = new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Cobra"), null,
                KnownCaseComparator.EasyWordsFirst, true);

        assertFalse(instance.equals(new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Cobra"), null,
                KnownCaseComparator.HardWordsFirst, true)));

        System.out.println("testEquals2 - all the same but different assessments");
        Assessment assessment = new Assessment();
        instance = new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Cobra"), assessment,
                KnownCaseComparator.EasyWordsFirst, true);

        assertFalse(instance.equals(new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Cobra"), null,
                KnownCaseComparator.EasyWordsFirst, true)));

        System.out.println("testEquals2 - all the same");
        assertTrue(instance.equals(new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Cobra"), assessment,
                KnownCaseComparator.EasyWordsFirst, true)));
    }

    /**
     * Tests {@link SimulationInfo#toString()}.
     */
    @Test
    public void testToString() {
        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        mapCounterExpected.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 2);

        System.out.println("testToString - null comparator");
        SimulationInfo instance = new SimulationInfo(mapCounterExpected, Arrays.asList("Anel", "Cobra"), null, null,
                true);

        assertFalse(instance.toString().isBlank());
    }

    /**
     * Tests {@link SimulationInfo#hashCode()}.
     */
    @Test
    public void testHashCode() {
        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        mapCounterExpected.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 2);
        mapCounterExpected.put(new Phoneme("l", Phoneme.POSITION.OM), 1);
        mapCounterExpected.put(new Phoneme("k", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("b", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("f", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("s", Phoneme.POSITION.CF), 1);

        final List<String> wordsRequiredExpected = new LinkedList<>();
        wordsRequiredExpected.addAll(Arrays.asList("Anel", "Casa", "Porta"));

        SimulationInfo instance = new SimulationInfo(mapCounterExpected, wordsRequiredExpected);

        // not in the same order
        final Map<Phoneme, Integer> otherMap = new HashMap();
        otherMap.put(new Phoneme("f", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 2);
        otherMap.put(new Phoneme("l", Phoneme.POSITION.OM), 1);
        otherMap.put(new Phoneme("k", Phoneme.POSITION.OI), 1);
        otherMap.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        otherMap.put(new Phoneme("b", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("s", Phoneme.POSITION.CF), 1);

        System.out.println("testHashCode - different objects with same hashCode");
        assertEquals(instance.hashCode(),
                new SimulationInfo(otherMap, Arrays.asList("Anel", "Porta", "Casa")).hashCode());

        otherMap.clear();
        otherMap.put(new Phoneme("f", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 2);
        otherMap.put(new Phoneme("l", Phoneme.POSITION.OM), 1);

        System.out.println("testHashCode - different hashes");
        assertNotEquals(instance.hashCode(),
                new SimulationInfo(otherMap, Arrays.asList("Anel", "Porta", "Casa")).hashCode());

        System.out.println("testHashCode - different objects with same assessment");
        otherMap.clear();
        otherMap.put(new Phoneme("f", Phoneme.POSITION.OCME), 1);
        otherMap.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 2);
        otherMap.put(new Phoneme("l", Phoneme.POSITION.OM), 1);
        final Assessment assessment = new Assessment();
        instance = new SimulationInfo(otherMap, Arrays.asList("Carro", "Porta"), assessment,
                KnownCaseComparator.HardWordsFirst, true);

        assertEquals(instance.hashCode(), new SimulationInfo(new HashMap<>(otherMap), Arrays.asList("Porta", "Carro"),
                assessment, KnownCaseComparator.HardWordsFirst, true).hashCode());

        System.out.println("testHashCode - same objects with different flag to split consonants");
        assertNotEquals(instance.hashCode(), new SimulationInfo(new HashMap<>(otherMap), Arrays.asList("Porta", "Carro"),
                assessment, KnownCaseComparator.HardWordsFirst, false).hashCode());
    }

    /**
     * Tests {@link SimulationInfo#getPCCR()}.
     */
    @Test
    public void testGetPCCR() {
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM))); // correto

        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("k", Phoneme.POSITION.OM))); // substituiu 't' por 'k'

        KnownCase bicicleta = new KnownCase("Bicicleta", "[bisi’klɛtə]", true, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("kl", Phoneme.POSITION.OCME), // omitiu 's'
                new Phoneme("t", Phoneme.POSITION.OM)));

        KnownCase biblioteca = new KnownCase("Biblioteca", "[biblio’tɛkə]", true, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("bl", Phoneme.POSITION.OCME),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("k", Phoneme.POSITION.OM),
                new Phoneme("r", Phoneme.POSITION.CF))); // acrescentou 'r'

        final Assessment assessment = new Assessment(Arrays.asList(anel, batom, bicicleta, biblioteca));

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

        SimulationInfo instance = new SimulationInfo(new HashMap<>(), new ArrayList<>(), assessment, null, true);

        // 11 produções esperadas
        // corretas: 9
        assertEquals(0.81, instance.getPCCR(), 0.01);
    }
}
