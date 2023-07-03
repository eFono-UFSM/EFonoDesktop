package br.com.efono.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 24.
 */
public class AssessmentTest {

    /**
     * Tests {@link Assessment#getCases()} and {@link Assessment#addCase(KnownCase)}.
     */
    @Test
    public void testGettersAndSetters() {
        System.out.println("testGettersAndSetters");
        Assessment instance = new Assessment();
        assertTrue(instance.getCases().isEmpty());

        KnownCase knownCase = new KnownCase("Relógio", "[χe’lɔʒu]", true);
        KnownCase other = new KnownCase("Sapato", "[sa’patu]", true);

        instance.addCase(knownCase);
        assertEquals(1, instance.getCases().size());

        System.out.println("testGettersAndSetters - adding the same case again: nothing happens");
        instance.addCase(knownCase);
        assertEquals(1, instance.getCases().size());

        System.out.println("testGettersAndSetters - adding the same case again, but incorrect: nothing happens");
        instance.addCase(new KnownCase("Relógio", "[χe’lɔʒu]", false));
        assertEquals(1, instance.getCases().size());

        System.out.println("testGettersAndSetters - different case");
        instance.addCase(other);
        assertEquals(2, instance.getCases().size());
    }

    /**
     * Tests {@link Assessment#equals(Object)}.
     */
    @Test
    public void testEquals() {
        System.out.println("testEquals - empty case");
        Assessment instance = new Assessment();
        assertTrue(instance.equals(new Assessment()));

        System.out.println("testEquals - same instance");
        KnownCase knownCase = new KnownCase("Relógio", "[χe’lɔʒu]", true);
        KnownCase other = new KnownCase("Sapato", "[sa’patu]", true);

        instance.addAll(Arrays.asList(knownCase, other));
        assertTrue(instance.equals(instance));

        System.out.println("testEquals - different classes");
        Object obj = "";
        assertFalse(instance.equals(obj));

        System.out.println("testEquals - null object");
        obj = null;
        assertFalse(instance.equals(obj));

        System.out.println("testEquals - different instances but the same object");
        Assessment otherInst = new Assessment(Arrays.asList(knownCase, other));
        assertTrue(instance.equals(otherInst));

        System.out.println("testEquals - different set of known cases");
        assertFalse(instance.equals(new Assessment(Arrays.asList(other))));
    }

    /**
     * Tests {@link Assessment#hashCode()}.
     */
    @Test
    public void testHashCode() {
        System.out.println("testHashCode - empty case");
        assertEquals(new Assessment().hashCode(), new Assessment().hashCode());

        KnownCase knownCase = new KnownCase("Relógio", "[χe’lɔʒu]", true);
        KnownCase otherCase = new KnownCase("Sapato", "[sa’patu]", true);

        System.out.println("testHashCode - different instances but the same object: different order");
        Assessment instance = new Assessment(Arrays.asList(knownCase, otherCase));
        Assessment other = new Assessment(Arrays.asList(otherCase, knownCase));
        assertEquals(instance.hashCode(), other.hashCode());

        System.out.println("testHashCode - different instances but the same object: same order");
        other = new Assessment(Arrays.asList(knownCase, otherCase));
        assertEquals(instance.hashCode(), other.hashCode());

        System.out.println("testHashCode - different hashes");
        assertNotEquals(instance.hashCode(), new Assessment(Arrays.asList(otherCase)).hashCode());
    }

    /**
     * Tests {@link Assessment#addAll(java.util.List)} and {@link Assessment#clear()}.
     */
    @Test
    public void testAddAndClear() {
        KnownCase knownCase = new KnownCase("Relógio", "[χe’lɔʒu]", true);
        KnownCase otherCase = new KnownCase("Sapato", "[sa’patu]", true);

        System.out.println("testAddAndClear - addAll");
        Assessment instance = new Assessment();
        assertEquals(0, instance.getCases().size());

        List<KnownCase> list = new ArrayList<>(Arrays.asList(knownCase, otherCase));
        instance.addAll(list);
        assertEquals(list.size(), instance.getCases().size());

        System.out.println("testAddAndClear - clear");
        instance.clear();
        assertEquals(2, list.size());
        assertEquals(0, instance.getCases().size());
    }

}
