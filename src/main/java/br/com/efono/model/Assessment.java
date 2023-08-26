package br.com.efono.model;

import br.com.efono.util.Defaults;
import br.com.efono.util.NoRepeatList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 24.
 */
public class Assessment {

    /**
     * Non identified assessment. This should be used only for tests.
     */
    public static final int DEFAULT_ID = -1;

    private final int id;

    private final List<KnownCase> cases = new LinkedList<>();

    /**
     * Creates an assessment.
     */
    public Assessment() {
        id = DEFAULT_ID; // non identified
    }

    /**
     * Creates an assessment.
     *
     * @param id Assessment id in database.
     */
    public Assessment(final int id) {
        this.id = id;
        if (id < 0) {
            throw new IllegalArgumentException("Non identified assessment is not allowed here.");
        }
    }

    /**
     * Creates an assessment with the given cases.
     *
     * @param cases Cases in the assessment.
     */
    public Assessment(final List<KnownCase> cases) {
        this();
        this.cases.addAll(cases);
    }

    /**
     * @return The assessment id.
     */
    public int getId() {
        return id;
    }

    /**
     * A copy of the cases in this assessment.
     *
     * @return The list of cases.
     */
    public List<KnownCase> getCases() {
        return new LinkedList<>(cases);
    }

    /**
     * Clears all cases.
     */
    public void clear() {
        cases.clear();
    }

    /**
     * Adds all the given cases if they are not in this object.
     *
     * @param cases Cases to add.
     */
    public void addAll(final List<KnownCase> cases) {
        if (cases != null) {
            cases.forEach(c -> addCase(c));
        }
    }

    /**
     * Adds a case in this assessment.
     *
     * @param knownCase The case to be added.
     */
    public void addCase(final KnownCase knownCase) {
        if (!cases.contains(knownCase)) {
            cases.add(knownCase);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        int casesHash = 1;
        for (KnownCase c : cases) {
            casesHash *= 13 + Objects.hash(c);
        }
        hash = 89 * hash + casesHash;
        hash = 89 * hash + id;
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
        final Assessment other = (Assessment) obj;
        return Objects.equals(this.cases, other.cases) && this.id == other.id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + id + ") with [" + cases.size() + "] cases";
    }

    /**
     * Gets the PCC-R value for this simulation according with the assessment.The PCC-R value indicates the level of
     * disorder according with the number of correct productions divided by the total of expected productions. Example:
     * if the subject was exposed to 10 target phonemes in the assessment and then spoke correctly only 5: the PCC-R
     * value will 0.5 indicating a High level of disorder. Reference: "Shriberg et al. (1997) The speech disorders
     * classification system (sdcs). Journal of Speech, Language and Hearing Research, 40(4):723–740."
     *
     * @param words The words to compute PCC-R.
     * @return The PCC-R value between 0 and 1 or -1 if the assessment is not valid.
     */
    public double getPCCR(final List<String> words) {
        double totalProductions = 0d;
        double correctProductions = 0d;

        for (KnownCase c : cases) {
            if (words.contains(c.getWord())) {
                totalProductions += c.getPhonemes().size();
                if (c.isCorrect()) {
                    correctProductions += c.getPhonemes().size();
                } else {
                    /**
                     * We can do TARGET_PHONEMES.get(c.getWord()) here because KnownCase doesn't allow words that is not
                     * in Defaults#SORTED_WORDS. Even if there is a difference in accentuation, for example, those cases
                     * will be treated and use the related word from Defaults#SORTED_WORDS.
                     */
                    List<Phoneme> targetPhonemes = Defaults.TARGET_PHONEMES.get(c.getWord());
                    correctProductions += c.getCorrectProductions(targetPhonemes).size();
                }
            }
        }

        // TODO: tests
        if (totalProductions == 0) {
            return -1;
        }

        return correctProductions / totalProductions;
    }

    /**
     * Analyzes all the consonant clusters produced by the subject with the assessment.
     *
     * For example: if we have a word with <br>
     * Medial Complex Onset(bl)<br>
     * Medial Complex Onset(kl)<br>
     *
     * we can test if the subject can reproduce Medial Complex Onset(kl). If true, than we had inferred that the subject
     * can reproduce a consonant cluster based on the reproduction of two others clusters.
     */
    public void analyzeConsonantClusters() {
        /**
         * Contains a list with the parts of the already produced consonant clusters.
         */
        final List<Phoneme> splittedProductions = new NoRepeatList<>();
        final List<Phoneme> consonantClustersProduced = new NoRepeatList<>();
        final List<Phoneme> inferredPhonemes = new NoRepeatList<>();
        final List<Phoneme> notInferredPhonemes = new NoRepeatList<>();
        final List<String> possibleDiscartedWords = new NoRepeatList<>();

        cases.forEach(c -> {
            c.getPhonemes().stream().filter(p -> p.isConsonantCluster()).forEach(p -> {
                consonantClustersProduced.add(p);

                System.out.println(c.getWord() + " -> " + p);
                List<Phoneme> splitPhonemes = p.splitPhonemes();

                if (!notInferredPhonemes.contains(p) && splittedProductions.containsAll(splitPhonemes)) {
                    System.out.println(p + " inferred" + " discard: " + c.getWord());
                    inferredPhonemes.add(p);
                    possibleDiscartedWords.add(c.getWord());
                } else {
                    System.out.println(p + " not inferred");
                    notInferredPhonemes.add(p);
                }
                splitPhonemes.forEach(e -> splittedProductions.add(e));
            });
        });

        System.out.println("real produced consonant clusters: [" + consonantClustersProduced.size() + "]: " + consonantClustersProduced);
        System.out.println("infered consonant clusters: [" + inferredPhonemes.size() + "]: " + inferredPhonemes);
        System.out.println("not infered consonant clusters: [" + notInferredPhonemes.size() + "]: " + notInferredPhonemes);

        System.out.println("words to be discarted: [" + possibleDiscartedWords.size() + "]: " + possibleDiscartedWords);

    }

}
