package br.com.efono.model;

import java.util.Objects;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 02.
 */
public class Phoneme {

    /**
     * All the possible phoneme positions at the word.
     */
    public enum POSITION {

        /**
         * Beginning of syllable, word beginning. Example "s" at [ca.sa].
         */
        OI("Initial Onset"),
        /**
         * Beginning of syllable, middle of the word. Example "v" at [ca.va.lo].
         */
        OM("Medial Onset"),
        /**
         * Beginning of syllable, beginning of word. Example "Br" at [Bra.sil].
         */
        OCI("Initial Complex Onset"),
        /**
         * Beginning of syllable, middle of the word. Example "bl" at [bi.blio.te.ca].
         */
        OCME("Initial Medial Complex Onset"),
        /**
         * End of syllable, middle of the word. Example "r" at [ca.dar.ço].
         */
        CM("Medial Coda"),
        /**
         * End of syllable, end of the word. Example "r" at [a.mor].
         */
        CF("Final Coda");

        private final String name;

        POSITION(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    private final String phoneme;
    private POSITION position;

    /**
     * Creates a phoneme representation.
     *
     * @param phoneme Phoneme character.
     */
    public Phoneme(final String phoneme) {
        this(phoneme, null);
    }

    /**
     * Creates a phoneme representation.
     *
     * @param phoneme Phoneme character.
     * @param position Phoneme position in the word.
     */
    public Phoneme(final String phoneme, final POSITION position) {
        if (phoneme == null) {
            throw new IllegalArgumentException("Phoneme can't be null.");
        }
        this.phoneme = phoneme;
        this.position = position;
    }

    /**
     * Gets the phoneme.
     *
     * @return The phoneme representation.
     */
    public String getPhoneme() {
        return phoneme;
    }

    /**
     * Gets the position.
     *
     * @return The phoneme position at the word.
     */
    public POSITION getPosition() {
        return position;
    }

    /**
     * Sets the phoneme position at the word.
     *
     * @param position Phoneme position.
     */
    public void setPosition(final POSITION position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.phoneme);
        hash = 89 * hash + Objects.hashCode(this.position);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Phoneme other = (Phoneme) obj;

        return Objects.equals(this.phoneme, other.phoneme) && Objects.equals(this.position, other.position);
    }

    @Override
    public String toString() {
        if (position != null) {
            StringBuilder builder = new StringBuilder(position.toString());
            builder.append("(").append(phoneme).append(")");
            return builder.toString();
        }
        return phoneme;
    }

}
