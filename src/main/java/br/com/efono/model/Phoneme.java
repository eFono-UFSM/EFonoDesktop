package br.com.efono.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 02.
 */
public class Phoneme {

    // TODO: deveria pegar essas constantes de algum pacote. Ver Phon.
    /**
     * Vowel phonemes. The vowel /ĩ/ needs to be before /i/ in this list, Because we replaced all vowels to spaces, and
     * if we replace /i/ first then the /̃/ lasts alone.
     */
    public static final String[] VOWELS = new String[]{"ã", "a", "ɐ", "ə", "e", "ɛ", "Ɛ", "ẽ", "ĩ", "i", "ɪ", "ĩ",
        "õ", "o", "ɔ", "õ", "ũ", "u", "ʊ", "ũ"};

    /**
     * Semi vowels or Glide are sounds that are associated with another vowel. https://pt.wikipedia.org/wiki/Semivogal
     */
    public static final String[] SEMI_VOWELS = new String[]{"w̃", "w", "j̃", "j"};

    /**
     * Consonant clusters.
     */
    public static final String[] CONSONANT_CLUSTERS = new String[]{"pɾ", "pχ", "pl", "bɾ", "bχ", "bl", "tɾ", "tχ", "tl",
        "dl", "dɾ", "dχ", "kɾ", "kχ", "kl", "gɾ", "gχ", "gl", "fɾ", "fχ", "fl", "vɾ", "vχ", "vl"};

    /**
     * Phonemes with labialization: https://pt.wikipedia.org/wiki/Labializa%C3%A7%C3%A3o.
     *
     * This array cannot contains more than 9 strings, because we use the index to treat transcriptions.
     */
    public static final String[] LABIALIZATION = new String[]{"kʷ", "gʷ", "dʷ"};

    /**
     * Consonants phonemes that are represented of more than 1 byte.
     */
    public static final String[] SPECIAL_CONSONANTS = new String[]{"s̃"};

    /**
     * List containing all the special phonemes which are represented by two or more chars.
     */
    public static final List<String> SPECIAL_PHONEMES = new ArrayList<>();

    static {
        SPECIAL_PHONEMES.addAll(Arrays.asList(LABIALIZATION));
        SPECIAL_PHONEMES.addAll(Arrays.asList(SPECIAL_CONSONANTS));
    }

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
        OCME("Medial Complex Onset"),
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
     * Default constructor.
     */
    public Phoneme() {
        this("");
    }

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
            throw new IllegalArgumentException("Phoneme cant be null.");
        }
        this.phoneme = phoneme;
        this.position = position;
    }

    /**
     * @return True - the phoneme is a consonant cluster, otherwise: false.
     */
    public boolean isConsonantCluster() {
        return Arrays.asList(CONSONANT_CLUSTERS).contains(phoneme);
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

    /**
     * Splits all the phonemes into single character phonemes.
     *
     * @return A list with phonemes.
     */
    public List<Phoneme> splitPhonemes() {
        final List<Phoneme> list = new ArrayList<>();
        // bɾ(OCME) -> b(OCME) + ɾ(OCME)
        String[] split = phoneme.split("");
        for (String s : split) {
            // repeated phonemes are allowed here, because we wanna count
            list.add(new Phoneme(s, position));
        }

        return list;
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
            StringBuilder builder = new StringBuilder();
            builder.append(phoneme).append("(").append(position.name()).append(")");
            return builder.toString();
        }
        return phoneme;
    }

}
