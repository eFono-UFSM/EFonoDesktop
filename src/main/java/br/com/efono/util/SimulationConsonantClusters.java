package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationConsonantClustersInfo;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Ago 28.
 */
public class SimulationConsonantClusters {

    public static SimulationConsonantClustersInfo run(final Assessment assessment, final KnownCaseComparator comp) {
        return run(assessment, comp, false);
    }

    public static SimulationConsonantClustersInfo run(final Assessment assessment, final KnownCaseComparator comp,
            boolean considerOnlyClustersInTargetWords) {
        if (assessment != null && comp != null) {
            List<KnownCase> cases = assessment.getCases();
            SimulationWordsSequence.sortList(cases, comp);

            List<String> words = new LinkedList<>(); // words in the same sequence as the assessment            
            Map<String, List<Phoneme>> mapWordsPhonemes = new HashMap<>();

            cases.forEach(c -> {
                words.add(c.getWord());
                mapWordsPhonemes.put(c.getWord(), c.getPhonemes());
            });

            List<Phoneme> inferredPhonemes = Util.getInferredPhonemes(mapWordsPhonemes, words, new NoRepeatList<>());

            // contains all the target consonant clusters, so we can compare if the child was really capable of reproduce some of the inferred phonemes.
            List<Phoneme> allTargetConsonantClusters = new NoRepeatList<>();
            Defaults.TARGET_PHONEMES.forEach((key, value) -> {
                value.stream().filter(p -> p.isConsonantCluster()).forEach(p -> allTargetConsonantClusters.add(p));
            });

            /**
             * inferredPhonemes that are in target words. So, if some of the inferred phonemes are in child assessment
             * than our logic is valid. Otherwise, means that some of the inferred phonemes that are in target words the
             * child wasn't capable of reproduce.
             */
            List<Phoneme> inferredPhonemesInTargetWords = new NoRepeatList<>();
            inferredPhonemes.stream().filter(p -> allTargetConsonantClusters.contains(p)).forEach(p -> inferredPhonemesInTargetWords.add(p));

            List<Phoneme> allClustersInAssessment = new NoRepeatList<>();
            List<Phoneme> validInferred = new NoRepeatList<>();

            // precisa validar também os inferidos que ela conseguiu reproduzir mas que não estão nas target wordas
            assessment.getCases().forEach(c -> {
                c.getPhonemes().stream().filter(p -> p.isConsonantCluster()).forEach(p -> {
                    allClustersInAssessment.add(p);
                    if (considerOnlyClustersInTargetWords) {
                        if (inferredPhonemesInTargetWords.contains(p)) {
                            validInferred.add(p);
                        }
                    } else if (inferredPhonemes.contains(p)) {
                        validInferred.add(p);
                    }
                });
            });

            List<Phoneme> inferred = inferredPhonemes;
            if (considerOnlyClustersInTargetWords) {
                inferred = inferredPhonemesInTargetWords;
            }
            
            List<Phoneme> inferredNotReproducedInTargetWords = new NoRepeatList<>();
            List<Phoneme> inferredNotReproducedNotInTargetWords = new NoRepeatList<>();
            inferred.forEach(c -> {
                if (!validInferred.contains(c)) {
                    System.out.println(c);
                    if (allTargetConsonantClusters.contains(c)) {
                        inferredNotReproducedInTargetWords.add(c);
                    } else {
                        inferredNotReproducedNotInTargetWords.add(c);
                    }
                }
            });

            return new SimulationConsonantClustersInfo(inferredPhonemes, allTargetConsonantClusters,
                    inferredPhonemesInTargetWords, allClustersInAssessment, validInferred,
                    inferredNotReproducedInTargetWords, inferredNotReproducedNotInTargetWords);
        }
        return new SimulationConsonantClustersInfo();

    }

}
