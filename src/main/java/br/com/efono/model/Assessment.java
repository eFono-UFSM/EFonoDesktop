package br.com.efono.model;

import br.com.efono.util.Defaults;
import br.com.efono.util.Util;
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
    private final int patientID;

    private final List<KnownCase> cases = new LinkedList<>();

    /**
     * Creates an assessment.
     */
    public Assessment() {
        id = DEFAULT_ID; // non identified
        patientID = DEFAULT_ID;
    }

    /**
     * Creates an assessment.
     *
     * @param id Assessment id in database.
     */
    public Assessment(final int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Non identified assessment is not allowed here.");
        }
        this.id = id;
        patientID = DEFAULT_ID;
    }

    /**
     * Creates an assessment.
     *
     * @param id Assessment id in database.
     * @param id_paciente Patient id.
     */
    public Assessment(final int id, final int id_paciente) {
        if (id < 0) {
            throw new IllegalArgumentException("Non identified assessment is not allowed here.");
        }
        this.id = id;
        this.patientID = id_paciente;
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
     * @return Patient id.
     */
    public int getPatientID() {
        return patientID;
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
     * value will be 0.5 indicating a High level of disorder. Reference: "Shriberg et al. (1997) The speech disorders
     * classification system (sdcs). Journal of Speech, Language and Hearing Research, 40(4):723–740."
     *
     * @param words The words to compute PCC-R.
     * @return The PCC-R value between 0 and 1 or -1 if the assessment is not valid.
     */
    public double getPCCR(final List<String> words) {
        /**
         * To calculate PCC-R, the total number of correct consonants in a speech sample is divided by the total number
         * of opportunities for consonant production in that sample and multiplied by 100. The resulting percentage
         * reflects how accurately the child produced the consonant phonemes of his or her language.
         */
        /**
         * The PCC is calculated after the division of the consonants which are correctly produced, by the total number
         * of produced consonants (correct+incorrect).
         */

        /**
         * Com essas duas referencias dá pra concluir que: total de produções (totalOpportunities) é o total de
         * produções ESPERADAS. Se a criança produziu fonemas que não eram esperados, eles não vão ser contabilizados e
         * serão irrelevantes no cálculo do PCC-R, apenas os fonemas que eram esperados serão avaliados. casinha,
         * passarinho, passinho (incorreto passarinho)
         */
        double totalOpportunities = 0d;
        double correctProductions = 0d;

        for (KnownCase c : cases) {
            if (words.contains(c.getWord())) {
                /**
                 * We can do TARGET_PHONEMES.get(c.getWord()) here because KnownCase doesn't allow words that is not in
                 * Defaults#SORTED_WORDS. Even if there is a difference in accentuation, for example, those cases will
                 * be treated and use the related word from Defaults#SORTED_WORDS.
                 */
                List<Phoneme> targetPhonemes = Defaults.TARGET_PHONEMES.get(c.getWord());
                List<Phoneme> expectedProduced = c.getPhonemes().stream().filter(p -> targetPhonemes.contains(p)).toList();

                totalOpportunities += targetPhonemes.size();
                correctProductions += expectedProduced.size();
            }
        }

        if (totalOpportunities == 0) {
            return -1;
        }

        return correctProductions / totalOpportunities;
    }

    /**
     * Gets the indicator of this assessment like doing a screening assessment with less words than original.
     *
     * @param limit The limit number of words to be used in the screening assessment. 0 to run without any limit: the
     * screening assessment will be over when it reach a leaf node in the {@link Defaults#TREE}.
     * @return The indicator get from the screening assessment.
     */
    @Deprecated
    public String getIndicatorFromScreening(final int limit) {
        IndicatorInfo info = Util.getIndicatorSDA(this, limit);
        if (info != null) {
            return info.getIndicatorAsString();
        }
        return null;
    }
}
