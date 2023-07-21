package br.com.efono.model;

import br.com.efono.util.Defaults;
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
     * Gets the PCC-R value for this simulation according with the assessment. The PCC-R value indicates the level of
     * disorder according with the number of correct productions divided by the total of expected productions. Example:
     * if the subject was exposed to 10 target phonemes in the assessment and then spoke correctly only 5: the PCC-R
     * value will 0.5 indicating a High level of disorder. Reference: "Shriberg et al. (1997) The speech disorders
     * classification system (sdcs). Journal of Speech, Language and Hearing Research, 40(4):723–740."
     *
     * @return The PCC-R value between 0 and 1.
     */
    public double getPCCR() {
        double totalExpected = 0d;
        double correctProductions = 0d;

        for (KnownCase c : cases) {
            /**
             * We can do TARGET_PHONEMES.get(c.getWord()) here because KnownCase doesn't allow words that is not in
             * Defaults#SORTED_WORDS. Even if there is a difference in accentuation, for example, those cases will be
             * treated and use the related word from Defaults#SORTED_WORDS.
             */
            List<Phoneme> targetPhonemes = Defaults.TARGET_PHONEMES.get(c.getWord());
            // TODO: aqui seriam as produções totais e não somente os fonemas alvos.
            totalExpected += targetPhonemes.size();
            correctProductions += c.getCorrectProductions(targetPhonemes).size();
        }

        if (totalExpected == 0) {
            return 0;
        }

        return correctProductions / totalExpected;
    }

}
