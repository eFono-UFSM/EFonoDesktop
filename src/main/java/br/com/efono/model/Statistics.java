package br.com.efono.model;

import br.com.efono.experiments.SequencesExperiment;
import br.com.efono.tree.BinaryTree;
import br.com.efono.tree.TreeUtils;
import br.com.efono.util.Defaults;
import br.com.efono.util.ExperimentUtils;
import br.com.efono.util.Util;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
     * @deprecated Use {@link SequencesExperiment}.
     */
    @Deprecated
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
     * Exports the statistics of words frequency with PCC-R.
     *
     * @return The CSV.
     */
    public String exportWordsFrequencyPCCR() {
        int max = 0;
        Integer mostRepeatedFrequency = 0;
        Iterator<Map.Entry<Integer, Integer>> it = mapWordsRequired.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> next = it.next();
            if (next.getValue() > max) {
                max = next.getValue();
                mostRepeatedFrequency = next.getKey();
            }
        }

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        ArrayList<Integer> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mapWordsCounter.entrySet()) {
            list.add(entry.getValue());
        }
        Collections.sort(list);
        for (int num : list) {
            for (Entry<String, Integer> entry : mapWordsCounter.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }

        List<String> sortedWords = new LinkedList<>(sortedMap.keySet());

        final List<String> words = new ArrayList<>();
        for (int i = sortedWords.size() - 1; i >= 0; i--) {
            words.add(sortedWords.get(i));
            if (words.size() == mostRepeatedFrequency) {
                break;
            }
        }

        final StringBuilder str = new StringBuilder("wordsFrequency,PCCR,degree,PCC-R 84w,degree 84w,equals degree\n");
        assessments.forEach(a -> {
            double pccrAll = a.getPCCR(Arrays.asList(Defaults.SORTED_WORDS));
            double pccrSelectedWords = a.getPCCR(words);

            String degreeAll = Util.getDegree(pccrAll);
            String degreeSelectedWords = Util.getDegree(pccrSelectedWords);

            DecimalFormat df = new DecimalFormat("#.##");
            str.append(words.size()).append(",").
                    append(df.format(pccrSelectedWords).replaceAll(",", ".")).append(",").
                    append(degreeSelectedWords).append(",").
                    append(df.format(pccrAll).replaceAll(",", ".")).append(",").
                    append(degreeAll).append(",").
                    append(degreeSelectedWords.equals(degreeAll) ? "TRUE" : "FALSE").
                    append("\n");
        });

        return str.toString();
    }

    private List<String> getLinesPCCR() {
        int max = 0;
        Integer mostRepeatedFrequency = 0;
        Iterator<Map.Entry<Integer, Integer>> it = mapWordsRequired.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> next = it.next();
            if (next.getValue() > max) {
                max = next.getValue();
                mostRepeatedFrequency = next.getKey();
            }
        }

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        ArrayList<Integer> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mapWordsCounter.entrySet()) {
            list.add(entry.getValue());
        }
        Collections.sort(list);
        for (int num : list) {
            for (Entry<String, Integer> entry : mapWordsCounter.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }

        System.out.println(comp + " mostRepeatedFrequency: " + mostRepeatedFrequency);
        System.out.println("sortedMap: " + sortedMap);

        List<String> sortedWords = new LinkedList<>(sortedMap.keySet());

        final List<String> words = new ArrayList<>();
        for (int i = sortedWords.size() - 1; i >= 0; i--) {
            words.add(sortedWords.get(i));
            if (words.size() == mostRepeatedFrequency) {
                break;
            }
        }

        System.out.println("words[" + words.size() + ": " + words);

        List<String> lines = new LinkedList<>();
        lines.add("wordsFrequency,PCCR,degree,PCC-R 84w,degree 84w,equals degree");

        assessments.forEach(a -> {
            double pccrAll = a.getPCCR(Arrays.asList(Defaults.SORTED_WORDS));
            double pccrSelectedWords = a.getPCCR(words);

            String degreeAll = Util.getDegree(pccrAll);
            String degreeSelectedWords = Util.getDegree(pccrSelectedWords);

            DecimalFormat df = new DecimalFormat("#.##");
            StringBuilder str = new StringBuilder();
            str.append(words.size()).append(",").
                    append(df.format(pccrSelectedWords).replaceAll(",", ".")).append(",").
                    append(degreeSelectedWords).append(",").
                    append(df.format(pccrAll).replaceAll(",", ".")).append(",").
                    append(degreeAll).append(",").
                    append(degreeSelectedWords.equals(degreeAll) ? "TRUE" : "FALSE");

            lines.add(str.toString());
        });

        return lines;
    }

    /**
     * Exports all info from statistics.
     *
     * @return The CSV.
     */
    public String exportAllCSV() {
        List<String> linesWordsRequired = ExperimentUtils.getLinesFromMap("wordsRequired", "frequency", mapWordsRequired);
        List<String> linesWordsFrequency = ExperimentUtils.getLinesFromMap("word", "frequency", mapWordsCounter);

        List<String> linesPCCR = getLinesPCCR();

        int maxSize = linesWordsRequired.size();
        if (linesWordsFrequency.size() > maxSize) {
            maxSize = linesWordsFrequency.size();
        }
        if (linesPCCR.size() > maxSize) {
            maxSize = linesPCCR.size();
        }

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < maxSize; i++) {
            if (i < linesWordsRequired.size()) {
                str.append(linesWordsRequired.get(i));
            } else {
                str.append(",");
            }
            str.append(",");

            if (i < linesWordsFrequency.size()) {
                str.append(linesWordsFrequency.get(i));
            } else {
                str.append(",");
            }
            str.append(",");

            if (i < linesPCCR.size()) {
                str.append(linesPCCR.get(i));
            } else {
                str.append(",,,,,"); // 6 columns
            }
            str.append("\n");
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

        // 55 words from Algorithm for Selecting Words to Compose Phonological Assessments
        String[] words55 = new String[]{"Jornal", "Tênis", "Cruz", "Mesa", "Tesoura", "Bebê", "Cachorro", "Terra", "Rabo",
            "Dragão", "Língua", "Chiclete", "Gritar", "Porta", "Refri", "Dado", "Igreja", "Relógio", "Cobra", "Zebra",
            "Brinco", "Placa", "Plástico", "Vaca", "Soprar", "Travesseiro", "Escrever", "Bruxa", "Zero", "Dedo",
            "Fralda", "Estrela", "Espelho", "Flor", "Faca", "Fogo", "Girafa", "Garfo", "Sofá", "Trem", "Vidro", "Sapo",
            "Livro", "Magro", "Pedra", "Nuvem", "Galinha", "Grama", "Chapéu", "Navio", "Caixa", "Letra", "Chifre",
            "Folha", "Cama"};

        StringBuilder str = new StringBuilder("region,PCC-R 84w,degree 84w,PCC-R 55w,degree 55w,custom words,PCC-R custom,expected custom,degree custom,words-in-blocks,PCC-R blocks,degree blocks,blocks vs 84w\n");
        assessments.forEach(a -> {
            final LinkedList<String> words = new LinkedList<>();
            tree.resetVisited(tree.getRoot());

            TreeUtils.getFirstWords(tree.getRoot(), words, a.getCases());

            String expectedDegree = "NOT_FOUND";
            Iterator<Map.Entry<String, String[]>> it = mapRegionsPCCR.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String[]> next = it.next();
                if (Arrays.asList(next.getValue()).contains(words.getLast())) {
                    expectedDegree = next.getKey();
                    break;
                }
            }

            final LinkedList<String> blocksOfWords = new LinkedList<>();
            words.forEach(w -> {
                blocksOfWords.add(w);
                blocksOfWords.addAll(Defaults.SIMILAR_WORDS.get(w));
            });

            double pccrAll = a.getPCCR(Arrays.asList(Defaults.SORTED_WORDS));
            double pccrBlocksOfWords = a.getPCCR(blocksOfWords);
            double pccrFirstWords = a.getPCCR(words);
            double pccr55Words = a.getPCCR(Arrays.asList(words55));

            String degreeAll = Util.getDegree(pccrAll);
            String degree55w = Util.getDegree(pccr55Words);
            String degreeCustom = Util.getDegree(pccrFirstWords);
            String degreeBlocks = Util.getDegree(pccrBlocksOfWords);

            DecimalFormat df = new DecimalFormat("#.##");
            str.append(words.getLast()).append(",").
                    append(df.format(pccrAll).replaceAll(",", ".")).append(",").
                    append(degreeAll).append(",").
                    append(df.format(pccr55Words).replaceAll(",", ".")).append(",").
                    append(degree55w).append(",").
                    append(String.valueOf(words.size())).append(",").
                    append(df.format(pccrFirstWords).replaceAll(",", ".")).append(",").
                    append(degreeCustom).append(",").
                    append(expectedDegree).append(",").
                    append(String.valueOf(blocksOfWords.size())).append(",").
                    append(df.format(pccrBlocksOfWords).replaceAll(",", ".")).append(",").
                    append(degreeBlocks).append(",").
                    append(degreeBlocks.equals(degreeAll) ? "TRUE" : "FALSE").
                    append("\n");
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
