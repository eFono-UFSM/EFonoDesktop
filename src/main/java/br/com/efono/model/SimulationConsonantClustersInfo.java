package br.com.efono.model;

import br.com.efono.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Ago 28.
 */
public class SimulationConsonantClustersInfo {

    private final List<Phoneme> inferredPhonemes;
    private final List<Phoneme> allTargetConsonantClusters;
    private final List<Phoneme> inferredPhonemesInTargetWords;
    private final List<Phoneme> allClustersInAssessment;
    private final List<Phoneme> inferredReproducedInTargetWords;
    private final List<Phoneme> invalidInferred;
    private final List<Phoneme> inferredNotReproducedInTargetWords;
    private final List<Phoneme> inferredNotReproducedNotInTargetWords;
    private final List<Phoneme> inferredReproducedNotInTargetWords;
    private final List<Phoneme> clustersParts;

    public SimulationConsonantClustersInfo() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public SimulationConsonantClustersInfo(final List<Phoneme> inferredPhonemes,
            final List<Phoneme> allTargetConsonantClusters, final List<Phoneme> inferredPhonemesInTargetWords,
            final List<Phoneme> allClustersInAssessment, final List<Phoneme> inferredReproducedInTargetWords,
            final List<Phoneme> inferredNotReproducedInTargetWords,
            final List<Phoneme> inferredNotReproducedNotInTargetWords,
            final List<Phoneme> inferredReproducedNotInTargetWords, final List<Phoneme> clustersParts) {
        this.inferredPhonemes = inferredPhonemes;
        this.allTargetConsonantClusters = allTargetConsonantClusters;
        this.inferredPhonemesInTargetWords = inferredPhonemesInTargetWords;
        this.allClustersInAssessment = allClustersInAssessment;
        this.inferredReproducedInTargetWords = inferredReproducedInTargetWords;
        this.inferredNotReproducedInTargetWords = inferredNotReproducedInTargetWords;
        this.inferredNotReproducedNotInTargetWords = inferredNotReproducedNotInTargetWords;
        this.inferredReproducedNotInTargetWords = inferredReproducedNotInTargetWords;
        this.clustersParts = clustersParts;
        this.invalidInferred = new ArrayList<>();
    }

    public SimulationConsonantClustersInfo(final List<Phoneme> inferredPhonemes,
            final List<Phoneme> allTargetConsonantClusters, final List<Phoneme> inferredPhonemesInTargetWords,
            final List<Phoneme> allClustersInAssessment, final List<Phoneme> inferredReproducedInTargetWords,
            final List<Phoneme> invalidInferred, final List<Phoneme> clustersParts) {
        this.inferredPhonemes = inferredPhonemes;
        this.allTargetConsonantClusters = allTargetConsonantClusters;
        this.inferredPhonemesInTargetWords = inferredPhonemesInTargetWords;
        this.allClustersInAssessment = allClustersInAssessment;
        this.inferredReproducedInTargetWords = inferredReproducedInTargetWords;
        this.inferredNotReproducedInTargetWords = new ArrayList<>();
        this.inferredNotReproducedNotInTargetWords = new ArrayList<>();
        this.inferredReproducedNotInTargetWords = new ArrayList<>();
        this.invalidInferred = invalidInferred;
        this.clustersParts = clustersParts;
    }

    public List<Phoneme> getInferredReproducedNotInTargetWords() {
        return inferredReproducedNotInTargetWords;
    }

    public List<Phoneme> getInvalidInferred() {
        return invalidInferred;
    }

    public List<Phoneme> getClustersParts() {
        return clustersParts;
    }

    public List<Phoneme> getInferredPhonemes() {
        return inferredPhonemes;
    }

    public List<Phoneme> getAllTargetConsonantClusters() {
        return allTargetConsonantClusters;
    }

    public List<Phoneme> getInferredPhonemesInTargetWords() {
        return inferredPhonemesInTargetWords;
    }

    public List<Phoneme> getAllClustersInAssessment() {
        return allClustersInAssessment;
    }

    public List<Phoneme> getInferredReproducedInTargetWords() {
        return inferredReproducedInTargetWords;
    }

    public List<Phoneme> getInferredNotReproducedInTargetWords() {
        return inferredNotReproducedInTargetWords;
    }

    public List<Phoneme> getInferredNotReproducedNotInTargetWords() {
        return inferredNotReproducedNotInTargetWords;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("\n==========================================\n");

        builder.append("infered phonemes:\n");
        builder.append(Util.printClusters(inferredPhonemes));
        builder.append("-------------------\n");

        builder.append("inferredPhonemesInTargetWords:\n");
        builder.append(Util.printClusters(inferredPhonemesInTargetWords));
        builder.append("-------------------\n");

        builder.append("the logic was valid in:\n");
        builder.append(Util.printClusters(inferredReproducedInTargetWords));
        builder.append("-------------------\n");

        builder.append("allClustersInAssessment\n");
        builder.append(Util.printClusters(allClustersInAssessment));
        builder.append("-------------------\n");

        builder.append("inferredNotReproducedInTargetWords\n");
        builder.append(Util.printClusters(inferredNotReproducedInTargetWords));
        builder.append("-------------------\n");

        builder.append("inferredNotReproducedNotInTargetWords:\n");
        builder.append(Util.printClusters(inferredNotReproducedNotInTargetWords));
        builder.append("-------------------\n");

        return builder.toString();
    }

    /**
     * Exports all the wanted information to CSV format.
     *
     * @param header With header or not.
     * @return The information in CSV format.
     */
    public String exportCSVAbleToReproduce(boolean header) {
        if (inferredPhonemes.isEmpty() || inferredPhonemesInTargetWords.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        if (header) {
            /*
            inferredPhonemes = todos os fonemas inferidos (A)
            allConsonantClustersInTargetWords = todos os fonemas (alvo) nas palavras alvo (B)
            inferredPhonemesInTargetWords = inferidos que estão nas palavras alvo (A) x (B)
            allClustersInAssessment = fonemas produzidos na avaliação (C)
            inferredReproducedInTargetWords = fonemas inferidos que estão nas palavras alvo e que foram produzidos na avaliação (A) x (B) x (C)
            inferredReproducedNotInTargetWords = fonemas inferidos que NÃO estão nas palavras alvo e que foram produzidos na avaliação A x C - B
            inferredNotReproducedInTargetWords = inferidos que estão nas palavras alvo e que não foram produzidos (A) x (B) - (C) azul escuro (inválidas)
            inferredNotReproducedNotInTargetWords = inferidos que não estão nas palavras alvo e que não foram produzidos (A) - ((B) U (C)) impossível de validar
             */
            builder.append("inferredPhonemes,allConsonantClustersInTargetWords,inferredPhonemesInTargetWords,allClustersInAssessment,inferredReproducedInTargetWords,inferredReproducedNotInTargetWords,inferredNotReproducedInTargetWords,inferredNotReproducedNotInTargetWords\n");
        }

        List<String> cols = new LinkedList<>();
        cols.add(Integer.toString(inferredPhonemes.size()));
        cols.add(Integer.toString(allTargetConsonantClusters.size()));
        cols.add(Integer.toString(inferredPhonemesInTargetWords.size()));
        cols.add(Integer.toString(allClustersInAssessment.size()));
        cols.add(Integer.toString(inferredReproducedInTargetWords.size()));
        cols.add(Integer.toString(inferredReproducedNotInTargetWords.size()));
        cols.add(Integer.toString(inferredNotReproducedInTargetWords.size()));
        cols.add(Integer.toString(inferredNotReproducedNotInTargetWords.size()));

        for (int i = 0; i < cols.size(); i++) {
            builder.append(cols.get(i));
            if (i < cols.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("\n");

        return builder.toString();
    }

    /**
     * Exports all the wanted information to CSV format.
     *
     * @param header With header or not.
     * @return The information in CSV format.
     */
    public String exportCSVNotAbleToReproduce(boolean header) {
        if (inferredPhonemes.isEmpty() || inferredPhonemesInTargetWords.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        if (header) {
            /*
            inferredPhonemes = inferidos que a criança NÃO consegue produzir (D)
            allConsonantClustersInTargetWords = encontros consonantais (alvos) nas palavras alvo (B)
            inferredPhonemesInTargetWords = inferidos que estão nas palavras alvo D x B
            allClustersInAssessment = fonemas produzidos na avaliação (C)
            validInferred = fonemas inferidos que ela NÃO conseguiria produzir que estão nas palavras alvo e que ela realmente não produziu (D x B - C)
            invalidInferred = fonemas inferidos  que ela NÃO conseguiria produzir que estão nas palavras alvo mas que ela produziu (D x B x C)
             */
            builder.append("inferredPhonemes,allConsonantClustersInTargetWords,inferredPhonemesInTargetWords,allClustersInAssessment,validInferred,invalidInferred\n");
        }

        List<String> cols = new LinkedList<>();
        cols.add(Integer.toString(inferredPhonemes.size()));
        cols.add(Integer.toString(allTargetConsonantClusters.size()));
        cols.add(Integer.toString(inferredPhonemesInTargetWords.size()));
        cols.add(Integer.toString(allClustersInAssessment.size()));
        cols.add(Integer.toString(inferredReproducedInTargetWords.size()));
        cols.add(Integer.toString(invalidInferred.size()));

        for (int i = 0; i < cols.size(); i++) {
            builder.append(cols.get(i));
            if (i < cols.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("\n");

        return builder.toString();
    }

    private static void putCountingInMap(final List list, final Map<Object, Integer> map) {
        list.forEach(p -> {
            if (!map.containsKey(p)) {
                map.put(p, 0);
            }
            map.put(p, map.get(p) + 1);
        });
    }

    /**
     * Export lists counting from all the SimulationConsonantClustersInfo.
     *
     * @param infos Information to do the counting.
     * @return The lists counting in CSV format.
     */
    public static String exportCountingInfosToCSV(final List<SimulationConsonantClustersInfo> infos) {
        // pega a lista e faz uma contagem dos elementos dela
        Map<Object, Integer> mapInferredPhonemesInTargetWords = new HashMap<>();
        Map<Object, Integer> mapInferredPhonemes = new HashMap<>();
        infos.forEach(i -> {
            putCountingInMap(i.getInferredPhonemes(), mapInferredPhonemes);
            putCountingInMap(i.getInferredPhonemesInTargetWords(), mapInferredPhonemesInTargetWords);
        });

        List<String> linesFromMapInferredPhonemes = Statistics.getLinesFromMap("inferredPhonemes", "count",
                mapInferredPhonemes);
        List<String> linesFromMapInferredPhonemesInTargetWords = Statistics.getLinesFromMap("inferredPhonemesInTargetWords", "count",
                mapInferredPhonemesInTargetWords);

        List<List<String>> data = Arrays.asList(linesFromMapInferredPhonemes, linesFromMapInferredPhonemesInTargetWords);
        int maxSize = 0;
        for (List l : data) {
            if (l.size() > maxSize) {
                maxSize = l.size();
            }
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < maxSize; i++) {
            for (int j = 0; j < data.size(); j++) {
                List<String> lines = data.get(j);

                if (i < lines.size()) {
                    builder.append(lines.get(i));
                } else {
                    builder.append(",");
                }
                if (j < data.size() - 1) {
                    builder.append(",,"); // add a separator between infos
                } else {
                    builder.append("\n");
                }
            }
        }

        return builder.toString();
    }

}
