package br.com.efono.model;

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
    }
}
