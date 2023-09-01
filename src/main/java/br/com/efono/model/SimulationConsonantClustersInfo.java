package br.com.efono.model;

import br.com.efono.util.Util;
import java.util.ArrayList;
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
    private final List<Phoneme> validInferred;
    private final List<Phoneme> inferredNotReproducedInTargetWords;
    private final List<Phoneme> inferredNotReproducedNotInTargetWords;

    public SimulationConsonantClustersInfo() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());
    }

    public SimulationConsonantClustersInfo(final List<Phoneme> inferredPhonemes,
            final List<Phoneme> allTargetConsonantClusters, final List<Phoneme> validInferredPhonemes,
            final List<Phoneme> allClustersInAssessment, final List<Phoneme> validInferred,
            final List<Phoneme> inferredNotReproducedInTargetWords,
            final List<Phoneme> inferredNotReproducedNotInTargetWords) {
        this.inferredPhonemes = inferredPhonemes;
        this.allTargetConsonantClusters = allTargetConsonantClusters;
        this.inferredPhonemesInTargetWords = validInferredPhonemes;
        this.allClustersInAssessment = allClustersInAssessment;
        this.validInferred = validInferred;
        this.inferredNotReproducedInTargetWords = inferredNotReproducedInTargetWords;
        this.inferredNotReproducedNotInTargetWords = inferredNotReproducedNotInTargetWords;
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

    public List<Phoneme> getValidInferred() {
        return validInferred;
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
        builder.append(Util.printClusters(validInferred));
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
    public String exportCSV(boolean header) {
        if (inferredPhonemes.isEmpty() || inferredPhonemesInTargetWords.isEmpty()) {
            return "";
        }
        // quantos fonemas foram inferidos no decorrer da avaliação
        // quantos dos fonemas inferidos a criança realmente conseguiu reproduzir
        // dos fonemas que não foram inferidos, quantos estavam nas target words
        // dos fonemas que não foram inferidos, quantos não estavam nas target words
        final StringBuilder builder = new StringBuilder();
        if (header) {
            builder.append("inferredPhonemes,inferredPhonemesInTargetWords,validInferred,inferredNotReproducedInTargetWords,inferredNotReproducedNotInTargetWords,inferencesValidation\n");
        }

        List<String> cols = new LinkedList<>();
        cols.add(Integer.toString(inferredPhonemes.size()));
        cols.add(Integer.toString(inferredPhonemesInTargetWords.size()));
        if (validInferred.isEmpty()) {
            cols.add("Nenhum");
        } else {
            cols.add(Integer.toString(validInferred.size()));
        }
        // número de fonemas inferidos que não foram reproduzidos mas que estão nas palavras alvo: inferências inválidas.
        // se não tiver nenhuma, significa que todas inferências foram válidas
        if (inferredNotReproducedInTargetWords.isEmpty()) {
            cols.add("Todas Válidas");
        } else {
            cols.add(Integer.toString(inferredNotReproducedInTargetWords.size()));
        }
        cols.add(Integer.toString(inferredNotReproducedNotInTargetWords.size()));

        int v = validInferred.size() * 100 / inferredPhonemesInTargetWords.size();
        cols.add(Integer.toString(v) + "%");

        for (int i = 0; i < cols.size(); i++) {
            builder.append(cols.get(i));
            if (i < cols.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("\n");

        return builder.toString();
    }

    public static String exportTableCSVInferredPhonemes(final List<SimulationConsonantClustersInfo> infos) {
        Map<Phoneme, Integer> mapInferredPhonemes = new HashMap<>();

        infos.forEach(i -> {
            i.getInferredPhonemes().forEach(p -> {
                if (!mapInferredPhonemes.containsKey(p)) {
                    mapInferredPhonemes.put(p, 0);
                }
                mapInferredPhonemes.put(p, mapInferredPhonemes.get(p) + 1);
            });
        });

        List<String> linesFromMapInferredPhonemes = Statistics.getLinesFromMap("inferredPhonemes", "count",
                mapInferredPhonemes);

        List<String> lines = new LinkedList<>();
        linesFromMapInferredPhonemes.forEach(l -> lines.add(l));

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            builder.append(lines.get(i)).append("\n");
        }
        return builder.toString();
    }

    public static String exportTableCSVInferredPhonemesInTargetWords(final List<SimulationConsonantClustersInfo> infos) {
        Map<Phoneme, Integer> mapInferredPhonemesInTargetWords = new HashMap<>();

        infos.forEach(i -> {
            i.getInferredPhonemesInTargetWords().forEach(p -> {
                if (!mapInferredPhonemesInTargetWords.containsKey(p)) {
                    mapInferredPhonemesInTargetWords.put(p, 0);
                }
                mapInferredPhonemesInTargetWords.put(p, mapInferredPhonemesInTargetWords.get(p) + 1);
            });
        });

        List<String> linesFromMapInferredPhonemesInTargetWords = Statistics.getLinesFromMap(
                "inferredPhonemesInTargetWords", "count", mapInferredPhonemesInTargetWords);
        List<String> lines = new LinkedList<>();
        linesFromMapInferredPhonemesInTargetWords.forEach(l -> lines.add(l));

        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < lines.size(); i++) {
            builder.append(lines.get(i)).append("\n");
        }
        return builder.toString();
    }

}
