package br.com.efono.model;

import br.com.efono.util.Util;
import java.util.ArrayList;
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

    public SimulationConsonantClustersInfo() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public SimulationConsonantClustersInfo(final List<Phoneme> inferredPhonemes,
            final List<Phoneme> allTargetConsonantClusters, final List<Phoneme> validInferredPhonemes,
            final List<Phoneme> allClustersInAssessment, final List<Phoneme> validInferred) {
        this.inferredPhonemes = inferredPhonemes;
        this.allTargetConsonantClusters = allTargetConsonantClusters;
        this.inferredPhonemesInTargetWords = validInferredPhonemes;
        this.allClustersInAssessment = allClustersInAssessment;
        this.validInferred = validInferred;
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

}
