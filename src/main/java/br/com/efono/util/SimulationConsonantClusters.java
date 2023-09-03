package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationConsonantClustersInfo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Ago 28.
 */
public class SimulationConsonantClusters {

    /**
     * Runs an analysis in the assessment to infer the phonemes that the child would reproduce, based on the phonemes in
     * the assessment that she could reproduce.For example: If the child could reproduce "bl" and "pr" in this
     * assessment, we infer that she would be able to reproduce "br" and "pl" as well.
     *
     * @param assessment Assessment to analyze.
     * @param considerOnlyClustersInTargetWords Only will validate inferred phonemes that are in target words.
     * @return An object that keeps all the information from the analysis.
     */
    public static SimulationConsonantClustersInfo runInferencesAnalysisCorrect(final Assessment assessment,
            boolean considerOnlyClustersInTargetWords) {
        if (assessment != null) {
            List<Phoneme> allClustersInAssessment = new NoRepeatList<>();
            List<KnownCase> cases = assessment.getCases();

            Map<String, List<Phoneme>> mapWordsPhonemes = new LinkedHashMap<>();
            cases.forEach(c -> mapWordsPhonemes.put(c.getWord(), c.getPhonemes()));

            List<Phoneme> clustersParts = new NoRepeatList<>();
            cases.forEach(c -> {
                // all the clusters parts: target and produced phonemes parts
                List<Phoneme> targetSplit = new NoRepeatList<>();
                List<Phoneme> producedSplit = new NoRepeatList<>();

                Defaults.TARGET_PHONEMES.get(c.getWord()).stream().filter(p -> p.isConsonantCluster()).forEach(
                        p -> targetSplit.addAll(p.splitPhonemes()));

                c.getPhonemes().stream().filter(p -> p.isConsonantCluster()).forEach(p -> {
                    producedSplit.addAll(p.splitPhonemes());
                    allClustersInAssessment.add(p);
                });

                if (considerOnlyClustersInTargetWords) {
                    targetSplit.stream().filter(p -> producedSplit.contains(p)).forEach(p -> clustersParts.add(p));
                } else {
                    clustersParts.addAll(producedSplit);
                }
            });

            List<Phoneme> inferredPhonemes = Util.getPossibleClusters(clustersParts);

            /**
             * Contains all the consonant clusters that are in target words.
             */
            List<Phoneme> allConsonantClustersInTargetWords = new NoRepeatList<>();
            Defaults.TARGET_PHONEMES.values().forEach(value -> value.stream().filter(p -> p.isConsonantCluster()).forEach(p -> allConsonantClustersInTargetWords.add(p)));

            /**
             * Contains all the inferred phonemes that are in target words.
             */
            List<Phoneme> inferredPhonemesInTargetWords = new NoRepeatList<>();
            inferredPhonemes.stream().filter(p -> allConsonantClustersInTargetWords.contains(p)).forEach(p -> inferredPhonemesInTargetWords.add(p));

            // base list of inferred phonemes to do the validation
            final List<Phoneme> inferred = new ArrayList<>(inferredPhonemes);
            if (considerOnlyClustersInTargetWords) {
                inferred.clear();
                inferred.addAll(inferredPhonemesInTargetWords);
            }

            /**
             * Contains all the valid inferences.
             */
            List<Phoneme> validInferred = new NoRepeatList<>();

            cases.forEach(c -> {
                c.getPhonemes().stream().filter(p -> inferred.contains(p)).forEach(p -> validInferred.add(p));
            });

            // precisa validar também os inferidos que ela conseguiu reproduzir mas que não estão nas target wordas
            assessment.getCases().forEach(c -> {
                c.getPhonemes().stream().filter(p -> p.isConsonantCluster()).forEach(p -> {
                    if (considerOnlyClustersInTargetWords) {
                        if (inferredPhonemesInTargetWords.contains(p)) {
                            validInferred.add(p);
                        }
                    } else if (inferredPhonemes.contains(p)) {
                        validInferred.add(p);
                    }
                });
            });

            List<Phoneme> inferredNotReproducedInTargetWords = new NoRepeatList<>();
            List<Phoneme> inferredNotReproducedNotInTargetWords = new NoRepeatList<>();
            inferred.forEach(c -> {
                if (!validInferred.contains(c)) {
                    System.out.println(c);
                    if (allConsonantClustersInTargetWords.contains(c)) {
                        inferredNotReproducedInTargetWords.add(c);
                    } else {
                        inferredNotReproducedNotInTargetWords.add(c);
                    }
                }
            });

            /*
            inferredPhonemes = todo o conjunto azul claro (A)
            allConsonantClustersInTargetWords = todo o conjunto verde (B)
            inferredPhonemesInTargetWords = (A) x (B) azul escuro + cinza
            allClustersInAssessment = todo o conjunto amarelo (C)
            validInferred = (A) x (B) x (C) cinza
            inferredNotReproducedInTargetWords = (A) x (B) - (C) azul escuro
            inferredNotReproducedNotInTargetWords = (A) - (B) - (C) azul claro
             */
            /**
             * For SAC-2024 I'm considering the valid inferences only the inferred phonemes that are in target words and
             * were reproduced in the assessment.
             */
            return new SimulationConsonantClustersInfo(inferredPhonemes, allConsonantClustersInTargetWords,
                    inferredPhonemesInTargetWords, allClustersInAssessment, validInferred,
                    inferredNotReproducedInTargetWords, inferredNotReproducedNotInTargetWords, clustersParts);
        }
        return new SimulationConsonantClustersInfo();

    }

    /**
     * Runs an analysis in the assessment to infer the phonemes that the child can't reproduce, based on the phonemes in
     * the assessment that she couldn't reproduce as well.
     *
     * For example: if we have 2 targets words that have "bl" and "pr" as target phonemes, that means that the child
     * must reproduce them in the assessment. If she couldn't reproduce "bl" and "pr" in this assessment, we infer that
     * she wouldn't be able to reproduce "br" and "pl" as well.
     *
     * @param assessment Assessment to analyze.
     * @return An object that keeps all the information from the analysis.
     */
    public static SimulationConsonantClustersInfo runInferencesAnalysisIncorrect(final Assessment assessment) {
        if (assessment != null) {
            List<Phoneme> allClustersInAssessment = new NoRepeatList<>();
            List<KnownCase> cases = assessment.getCases();

            List<Phoneme> clustersParts = new NoRepeatList<>();

            cases.forEach(c -> {
                // all the clusters parts: target and produced phonemes parts
                List<Phoneme> targetSplit = new NoRepeatList<>();
                List<Phoneme> producedSplit = new NoRepeatList<>();

                Defaults.TARGET_PHONEMES.get(c.getWord()).stream().filter(p -> p.isConsonantCluster()).forEach(
                        p -> targetSplit.addAll(p.splitPhonemes()));

                c.getPhonemes().stream().filter(p -> p.isConsonantCluster()).forEach(p -> {
                    producedSplit.addAll(p.splitPhonemes());
                    allClustersInAssessment.add(p);
                });

                targetSplit.forEach(p -> {
                    if (!producedSplit.contains(p)) {
                        clustersParts.add(p);
                    } else {
                        clustersParts.remove(p);
                    }
                });
            });
            List<Phoneme> inferredPhonemes = Util.getPossibleClusters(clustersParts);

            /**
             * Contains all the consonant clusters that are in target words.
             */
            List<Phoneme> allConsonantClustersInTargetWords = new NoRepeatList<>();
            Defaults.TARGET_PHONEMES.values().forEach(value -> {
                value.stream().filter(p -> p.isConsonantCluster()).forEach(
                        p -> allConsonantClustersInTargetWords.add(p));
            });

            /**
             * inferredPhonemes that are in target words.
             */
            List<Phoneme> inferredPhonemesInTargetWords = new NoRepeatList<>();
            inferredPhonemes.stream().filter(p -> allConsonantClustersInTargetWords.contains(p)).forEach(
                    p -> inferredPhonemesInTargetWords.add(p));

            // os que eu inferi que ela não conseguiria produzir que estão nas palavras alvo e que ela realmente não produziu
            List<Phoneme> validInferred = new NoRepeatList<>();
            // os que eu inferi que ela não conseguiria produzir que estão nas palavras alvo mas que ela produziu
            List<Phoneme> invalidInferred = new NoRepeatList<>();
            inferredPhonemesInTargetWords.forEach(p -> {
                if (!allClustersInAssessment.contains(p)) { // realmente não produziu
                    validInferred.add(p);
                } else {
                    invalidInferred.add(p);
                }
            });

            return new SimulationConsonantClustersInfo(inferredPhonemes, allConsonantClustersInTargetWords,
                    inferredPhonemesInTargetWords, allClustersInAssessment, validInferred,
                    invalidInferred, clustersParts);
        }
        return new SimulationConsonantClustersInfo();

    }

}
