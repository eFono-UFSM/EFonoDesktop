package br.com.efono.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 14.
 */
public class Statistics {

    private final List<Integer> assessments = new ArrayList<>();

    /**
     * The key is the number of words required and the value is how many times this number was required for this
     * KnownCaseComparator.
     */
    private final Map<Integer, Integer> mapWordsRequired = new HashMap<>();
    private final KnownCaseComparator comp;

    /**
     * Creates a statistics object for the given comparator.
     *
     * @param comp Comparator to do statistics.
     */
    public Statistics(final KnownCaseComparator comp) {
        this.comp = comp;
    }

    /**
     * Extracts statistics from simulation info.
     *
     * @param info Simulation info to extract statistics.
     */
    public void extractStatistics(final SimulationInfo info) {
        if (!assessments.contains(info.getAssessment().getId())) {
            if (info.getAssessment().getId() < 1) {
                System.out.println("Warning: this assessment is not from database, I'll do statistics anyway");
            }

            assessments.add(info.getAssessment().getId());

            int countWordsRequired = info.getWordsRequired().size();
            if (!mapWordsRequired.containsKey(countWordsRequired)) {
                mapWordsRequired.put(countWordsRequired, 0);
            }

            Integer val = mapWordsRequired.get(countWordsRequired);
            mapWordsRequired.put(countWordsRequired, val + 1);
        }
    }

    /**
     * Export this statistics to CSV format.
     *
     * @return The CSV.
     */
    public String exportCSV() {
        StringBuilder str = new StringBuilder("wordsRequired,frequency\n");

        Iterator<Map.Entry<Integer, Integer>> it = mapWordsRequired.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> next = it.next();
            str.append(next.getKey()).append(",").append(next.getValue()).append("\n");
        }
        return str.toString();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Statistics[\n");
        if (assessments.contains(Assessment.DEFAULT_ID)) {
            str.append("[WARN] This statistics is not reliable: assessments outside from dabatase are being used\n");
        }
        str.append("\tcomparator: ").append(comp.name()).append("\n");
        str.append("\tassessments analyzed: ").append(assessments.size()).append("\n");

        Iterator<Map.Entry<Integer, Integer>> it = mapWordsRequired.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> next = it.next();
            str.append("\twordsRequired: ").append(next.getKey()).append(" | frequency: ").append(next.getValue()).append("\n");
        }
        str.append("]");

        return str.toString();
    }

}
