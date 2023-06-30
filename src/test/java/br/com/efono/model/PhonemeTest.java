package br.com.efono.model;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 02.
 */
public class PhonemeTest {

    /**
     * Tests for null parameters in the constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor() {
        new Phoneme(null);
    }

    /**
     * Tests {@link Phoneme#isConsonantCluster()}.
     */
    @Test
    public void testIsConsonantCluster() {
        System.out.println("testIsConsonantCluster - false");
        assertFalse(new Phoneme("k", Phoneme.POSITION.OI).isConsonantCluster());

        System.out.println("testIsConsonantCluster - true");
        assertTrue(new Phoneme("bɾ", Phoneme.POSITION.OCME).isConsonantCluster());

        System.out.println("testIsConsonantCluster - never must happen, but just in case...");
        assertTrue(new Phoneme("bɾ", Phoneme.POSITION.OI).isConsonantCluster());
    }

    /**
     * Tests for integrity.
     */
    @Test
    public void testIntegrity() {
        /**
         * The array cannot contains more than 9 strings, because we use the index treat transcriptions.
         *
         * We need to replace the labialization string to just 1 char, so we can split the phonemes correctly at
         * {@link Util#getConsonantPhonemes}.
         */
        System.out.println("testIntegrity - test for array length");
        /**
         * Needs to be less than 10, because then the index in the list with the two arrays will go until 9.
         */
        assertTrue(Phoneme.SPECIAL_CONSONANTS.length + Phoneme.LABIALIZATION.length <= 10);
    }

    /**
     * Tests {@link Phoneme#getPhoneme()}, {@link Phoneme#getPosition()} and
     * {@link Phoneme#setPosition(Phoneme.POSITION)}
     */
    @Test
    public void testGettersAndSetters() {
        System.out.println("testGettersAndSetters - valid case");
        String[] phonemes = new String[]{"bɾ", "n", "k"};
        Phoneme.POSITION[] positions = new Phoneme.POSITION[]{Phoneme.POSITION.OCI, Phoneme.POSITION.CM, Phoneme.POSITION.OM};

        for (int i = 0; i < phonemes.length; i++) {
            Phoneme phoneme = new Phoneme(phonemes[i], positions[i]);
            assertEquals(phonemes[i], phoneme.getPhoneme());
            assertEquals(positions[i], phoneme.getPosition());
        }

        System.out.println("testGettersAndSetters - phoneme without position");
        Phoneme phoneme = new Phoneme("n");
        assertEquals("n", phoneme.getPhoneme());
        assertNull(phoneme.getPosition());
    }

    /**
     * Tests {@link Phoneme#hashCode()}.
     */
    @Test
    public void testHashCode() {
        System.out.println("testHashCode - same hashCode");
        assertEquals(new Phoneme("n").hashCode(), new Phoneme("n").hashCode());
        assertEquals(new Phoneme("n", Phoneme.POSITION.CF).hashCode(), new Phoneme("n", Phoneme.POSITION.CF).hashCode());

        System.out.println("testHashCode - different hashCode");
        assertNotEquals(new Phoneme("n").hashCode(), new Phoneme("b").hashCode());
        assertNotEquals(new Phoneme("n").hashCode(), new Phoneme("n", Phoneme.POSITION.CF).hashCode());
    }

    /**
     * Tests {@link Phoneme#equals(Object)}.
     */
    @Test
    public void testEquals() {
        System.out.println("testEquals - same instance");
        Phoneme instance = new Phoneme("n", Phoneme.POSITION.CF);
        assertTrue(instance.equals(instance));

        System.out.println("testEquals - null parameter");
        Object obj = null;
        assertFalse(instance.equals(obj));

        System.out.println("testEquals - different classes");
        Object str = "n";
        assertFalse(instance.equals(str));

        System.out.println("testEquals - different phonemes, same position");
        assertFalse(instance.equals(new Phoneme("b", Phoneme.POSITION.CF)));

        System.out.println("testEquals - same phonemes, different positions");
        assertFalse(instance.equals(new Phoneme("n", Phoneme.POSITION.CM)));

        System.out.println("testEquals - all different");
        assertFalse(instance.equals(new Phoneme("b", Phoneme.POSITION.CM)));

        System.out.println("testEquals - all the same");
        assertTrue(instance.equals(new Phoneme("n", Phoneme.POSITION.CF)));

        System.out.println("special characters");
        Phoneme special = new Phoneme("pɾ");
        // TODO: precisa ver se ɾ e r são a mesma coisa na fonoaudiologia, pra ler esses fonemas da mesma forma ou manter diferentes como está agora
        System.out.println("tests for equivalent phonemes");
        assertTrue(special.equals(new Phoneme("pɾ")));
        assertFalse(special.equals(new Phoneme("pr")));
    }

}
