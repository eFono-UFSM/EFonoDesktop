package br.com.efono.model;

import br.com.efono.tree.BinaryTree;
import br.com.efono.tree.TreeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 14.
 */
public class Statistics {

    private final List<Assessment> assessments = new ArrayList<>();

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
        if (!constainsAssessment(info.getAssessment().getId())) {
            if (info.getAssessment().getId() < 1) {
                System.out.println("Warning: this assessment is not from database, I'll do statistics anyway");
            }

            assessments.add(info.getAssessment());

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

    private boolean constainsAssessment(final int id) {
        return assessments.stream().anyMatch(a -> (a.getId() == id));
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
     * Exporting PCC-R results about regions of PCC-R to CSV. First, we calculate the PCC-R following the traditional
     * method: computing all correct productions from an assessment and dividing for all the productions. In the second
     * part we try to predict what will be the disorder degree based only on the results of the first 7 words spoken in
     * the assessment with binary tree approach.
     * 
     * @see FullTree.puml
     *
     * @param tree The balanced tree with words in the assessment instrument.
     * @return A CSV with the results.
     */
    public String exportPCCR_CSV(final BinaryTree<String> tree) {
        Map<String, String[]> mapRegionsPCCR = new HashMap<>();
        mapRegionsPCCR.put("Low", new String[]{"Travesseiro", "Colher", "Estrela", "Dragão", "Igreja", "Letra", "Chifre", "Livro"});
        mapRegionsPCCR.put("Low-Moderate", new String[]{"Refri", "Fruta", "Cruz", "Zebra", "Brinco", "Jacaré", "Tesoura", "Zero"});
        mapRegionsPCCR.put("Moderate-High", new String[]{"Porta", "Nuvem", "Beijo", "Língua", "Chapéu", "Calça", "Bolsa", "Gato"});
        mapRegionsPCCR.put("High", new String[]{"Galinha", "Vaca", "Fogo", "Faca", "Dente", "Terra", "Cama", "Anel"});

        StringBuilder str = new StringBuilder("region,pcc-r Value,result,expected,same degree\n");
        assessments.forEach(a -> {
            final LinkedList<String> words = new LinkedList<>();

            tree.resetVisited(tree.getRoot());

            TreeUtils.getFirstWords(tree.getRoot(), words, a.getCases());

            double pccr = a.getPCCR();
            String resultDegree;
            if (pccr >= .85) {
                resultDegree = "Low";
            } else if (pccr >= .65 && pccr < .85) {
                resultDegree = "Low-Moderate";
            } else if (pccr >= .5 && pccr < .65) {
                resultDegree = "Moderate-High";
            } else {
                resultDegree = "High";
            }

            String expectedDegree = "NOT_FOUND";
            Iterator<Map.Entry<String, String[]>> it = mapRegionsPCCR.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String[]> next = it.next();
                if (Arrays.asList(next.getValue()).contains(words.getLast())) {
                    expectedDegree = next.getKey();
                    break;
                }
            }

            str.append(words.getLast()).append(",").append(pccr).append(",").append(resultDegree).append(",").
                    append(expectedDegree).append(",").append((resultDegree.equals(expectedDegree))).append("\n");
        });
        return str.toString();
    }

    /**
     * Export the statistics of words frequency of all the statistics in the list to CSV format.
     *
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

                    int count = 0;
                    if (mapCounter.containsKey(next.getKey())) {
                        count = mapCounter.get(next.getKey());
                    }

                    mapCounter.put(next.getKey(), count + next.getValue());
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
        if (constainsAssessment(Assessment.DEFAULT_ID)) {
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
