package br.com.efono.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 24.
 */
public class Assessment {

    private final List<KnownCase> cases = new LinkedList<>();

    /**
     * Creates an assessment.
     */
    public Assessment() {
        // empty
    }

    /**
     * Creates an assessment with the given cases.
     *
     * @param cases Cases in the assessment.
     */
    public Assessment(final List<KnownCase> cases) {
        this.cases.addAll(cases);
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
        hash = 89 * hash + Objects.hashCode(this.cases);
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
        return Objects.equals(this.cases, other.cases);
    }

}
