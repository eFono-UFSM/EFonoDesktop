package br.com.efono.model;

import br.com.efono.util.Defaults;
import java.text.DecimalFormat;
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

    /**
     * Tests {@link Assessment#getPCCR(List)}.
     */
    @Test
    public void testGetPCCR() {
        System.out.println("testGetPCCR - valid assessment");
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM))); // correto

        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", false, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("k", Phoneme.POSITION.OM))); // substituiu 't' por 'k'

        KnownCase bicicleta = new KnownCase("Bicicleta", "[bisi’klɛtə]", false, Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("kl", Phoneme.POSITION.OCME), // omitiu 's'
                new Phoneme("t", Phoneme.POSITION.OM)));

        KnownCase biblioteca = new KnownCase("Biblioteca", "[biblio’tɛkə]", false, Arrays.asList(
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

        // 11 produções totais
        // corretas: 9
        double resultPCCR = assessment.getPCCR(Arrays.asList("Anel", "Batom", "Bicicleta", "Biblioteca"));
        assertEquals(0.818, resultPCCR, 0.001);

        DecimalFormat df = new DecimalFormat("#.##");
        assertEquals("0.82", df.format(resultPCCR).replaceAll(",", "."));

//        TODO:
//        System.out.println("incomplete assessment");
//        assertEquals(-1, assessment.getPCCR(Arrays.asList(Defaults.SORTED_WORDS)), 0.01);
    }

    /**
     * Tests {@link Assessment#getIndicatorFromScreening(int)}.
     */
    @Test
    public void testGetIndicatorFromScreening() {
        Defaults.TREE.init(Defaults.SORTED_WORDS);

        List<KnownCase> cases = new ArrayList<>();

        cases.add(new KnownCase("Ventilador", "", true));
        cases.add(new KnownCase("Mesa", "", true));
        cases.add(new KnownCase("Presente", "", false));
        cases.add(new KnownCase("Escrever", "", true));
        cases.add(new KnownCase("Vidro", "", true));
        cases.add(new KnownCase("Floresta", "", true));
        cases.add(new KnownCase("Biblioteca", "", true));
        cases.add(new KnownCase("Travesseiro", "", false));
        cases.add(new KnownCase("Rabo", "", true));
        cases.add(new KnownCase("Dado", "", true));
        cases.add(new KnownCase("Coelho", "", true));
        cases.add(new KnownCase("Prato", "", true));
        cases.add(new KnownCase("Soprar", "", false));
        cases.add(new KnownCase("Grama", "", false));
        cases.add(new KnownCase("Cobra", "", true));
        cases.add(new KnownCase("Zebra", "", false));

        Assessment instance = new Assessment(cases);
        assertEquals(null, instance.getIndicatorFromScreening(-1));
        assertEquals("Low", instance.getIndicatorFromScreening(1));
        assertEquals("Moderate-Low", instance.getIndicatorFromScreening(2));
        assertEquals("Moderate-Low", instance.getIndicatorFromScreening(0)); // parou em Cobra

    }

}
