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
     * KnownCaseComparator. This helps helps us to understand the best number of words that should be available in an
     * instrument of phonological assessment.
     */
    private final Map<Integer, Integer> mapWordsRequired = new HashMap<>();

    /**
     * The key is the word required and the value is the number of times this words was required. This will helps us to
     * understand what words should be in an instrument of phonological assessment.
     */
    private final Map<String, Integer> mapWordsCounter = new HashMap<>();
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

            List<String> wordsRequired = info.getWordsRequired();
            for (String w : wordsRequired) {
                if (!mapWordsCounter.containsKey(w)) {
                    mapWordsCounter.put(w, 0);
                }
                Integer currentVal = mapWordsCounter.get(w);
                mapWordsCounter.put(w, currentVal + 1);
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
    
    /**
     * Export the statistics of words frequency to CSV format.
     *
     * @return The CSV.
     */
    public String exportWordsFrequencyCSV() {
        StringBuilder str = new StringBuilder("word,frequency\n");

        Iterator<Map.Entry<String, Integer>> it = mapWordsCounter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> next = it.next();
            str.append(next.getKey()).append(",").append(next.getValue()).append("\n");
        }
        return str.toString();
    }
    
    /**
     * Export the statistics of words frequency of all the statistics in the list to CSV format.
     * @param list List with statistics.
     * @return The CSV.
     */
    public static String exportAllWordsFrequencyCSV(final List<Statistics> list) {
        Map<String, Integer> mapCounter = new HashMap<>();
        for (Statistics s : list) {
            if (mapCounter.isEmpty()) {
                mapCounter.putAll(s.mapWordsCounter);
            } else {
                Iterator<Map.Entry<String, Integer>> it = s.mapWordsCounter.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Integer> next = it.next();
                    mapCounter.put(next.getKey(), mapCounter.get(next.getKey()) + next.getValue());
                }
            }
        }
        
        StringBuilder str = new StringBuilder("word,frequency\n");

        Iterator<Map.Entry<String, Integer>> it = mapCounter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> next = it.next();
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
