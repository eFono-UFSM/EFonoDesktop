package br.com.efono.model;

import br.com.efono.util.Util;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

        builder.append("allClustersInAssessment\n");
        builder.append(Util.printClusters(allClustersInAssessment));
        builder.append("-------------------\n");

        builder.append("valid infered consonant clusters:\n");
        builder.append(Util.printClusters(inferredPhonemesInTargetWords));
        builder.append("-------------------\n");

        builder.append("the logic was valid in:\n");
        builder.append(Util.printClusters(validInferred));
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
        // quantos fonemas foram inferidos no decorrer da avaliação
        // quantos dos fonemas inferidos a criança realmente conseguiu reproduzir
        // dos fonemas que não foram inferidos, quantos estavam nas target words
        // dos fonemas que não foram inferidos, quantos não estavam nas target words
        final StringBuilder builder = new StringBuilder("inferredPhonemes,inferredProduced,invalidInferences,cantInvalidateInferences");

        List<String> cols = new LinkedList<>();
        cols.add(Integer.toString(inferredPhonemes.size()));
        cols.add(Integer.toString(validInferred.size()));
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

}
