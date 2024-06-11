package br.com.efono.util;

import br.com.efono.experiments.SequencesExperiment;
import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 26.
 */
public class SimulationWordsSequence {

    /**
     * Default value for split consonants parameter.
     */
    public static final boolean SPLIT_CONSONANTS = true;

    // TODO: tests
    /**
     * Runs the simulation with the words sequence in the given assessment.
     *
     * @param assessment Assessment.
     * @param comp Comparator to sort KnownCases or null.
     * @param minimum Number of times that the same phoneme in the same position must be produced to be considered in
     * the phonetic inventory.
     * @return The information about the simulation.
     */
    public static SimulationInfo runSimulation(final Assessment assessment, final KnownCaseComparator comp,
            final int minimum) {
        return runSimulation(assessment, comp, minimum, SPLIT_CONSONANTS, true);
    }

    /**
     * Runs the simulation with the words sequence in the given assessment.
     *
     * @param assessment Assessment.
     * @param comp Comparator to sort KnownCases or null.
     * @param minimum Number of times that the same phoneme in the same position must be produced to be considered in
     * the phonetic inventory.
     * @param splitConsonantClusters True - this will count the consonant clusters as two phonemes:
     * <code>bɾ(OCME) -> b(OCME) + ɾ(OCME).</code>. The phoneme ɾ(OCME) can be counted more times in this way, and we
     * can evaluate more precisely the consonant clusters productions.
     * @param phoneticInventory True - will compute words required for phonetic inventory. False - it'll compute words
     * required for phonemes testing (PCC-R).
     * @return The information about the simulation.
     */
    public static SimulationInfo runSimulation(final Assessment assessment, final KnownCaseComparator comp,
            final int minimum, boolean splitConsonantClusters, final boolean phoneticInventory) {
        if (assessment != null && minimum > 0) {
            final Map<Phoneme, Integer> mapCounter = new HashMap<>();

            List<KnownCase> cases = assessment.getCases();
            ExperimentUtils.sortList(cases, comp);

            List<String> wordsRequired = getWordsRequired(cases, mapCounter, splitConsonantClusters, minimum,
                    phoneticInventory);
            /**
             * TODO: todos os fonemas produzidos ou testados (depende da flag phoneticInventory) estão em "mapCounter".
             * Os fonemas testados (alvos) deverão ser calculados a partir dos gabaritos corretos. Aí sim, podemos
             * calcular o PCC-R.
             */
            return new SimulationInfo(mapCounter, wordsRequired, assessment, comp, splitConsonantClusters);
        }
        return new SimulationInfo(assessment, comp, splitConsonantClusters);
    }

    // todo: adicionar esse mecanismo com as 7 palavras, serão mais, mas pode melhorar a precisão na hora de advinhar o PCC-R
    @Deprecated
    public static SimulationInfo runSimulation2(final Assessment assessment, final KnownCaseComparator comp,            final int minimum, boolean splitConsonantClusters) {
        if (assessment != null && minimum > 0) {
            final Map<Phoneme, Integer> mapCounter = new HashMap<>();

            List<KnownCase> cases = assessment.getCases();
            ExperimentUtils.sortList(cases, comp);

            final List<String> wordsRequired = new LinkedList<>();

            if (cases != null) {
                mapCounter.clear();
                cases.forEach(c -> {
                    List<Phoneme> phonemes = Defaults.TARGET_PHONEMES.get(c.getWord());

                    final List<Phoneme> list = new ArrayList<>();
                    phonemes.forEach(phoneme -> {
                        // bɾ(OCME) -> b(OCME) + ɾ(OCME)
                        if (phoneme.isConsonantCluster() && splitConsonantClusters) {
                            String[] split = phoneme.getPhoneme().split("");
                            for (String s : split) {
                                // repeated phonemes are allowed here, because we wanna count
                                list.add(new Phoneme(s, phoneme.getPosition()));
                            }
                        } else {
                            // repeated phonemes are allowed
                            list.add(phoneme);
                        }
                    });

                    List<Phoneme> toBeTested = new LinkedList<>();

                    for (Phoneme p : list) {
                        int count = 1;
                        if (mapCounter.containsKey(p)) {
                            count = mapCounter.get(p) + 1;
                        }
                        mapCounter.put(p, count);

                        if (count <= minimum && !wordsRequired.contains(c.getWord())) {
                            wordsRequired.add(c.getWord());
                            toBeTested.add(p);
                        }
                    }

                    List<String> nextWords = getNextWords(toBeTested, splitConsonantClusters);

                    testNextWords(nextWords, cases, minimum, splitConsonantClusters, mapCounter, wordsRequired);
                });
            }

            return new SimulationInfo(mapCounter, wordsRequired, assessment, comp, splitConsonantClusters);
        }
        return new SimulationInfo(assessment, comp, splitConsonantClusters);
    }

    @Deprecated
    private static void testNextWords(final List<String> nextWords, final List<KnownCase> cases, final int minimum,
            boolean splitConsonantClusters, final Map<Phoneme, Integer> mapCounter, final List<String> wordsRequired) {
        cases.forEach(c -> {
            if (nextWords.contains(c.getWord())) {
                List<Phoneme> phonemes = Defaults.TARGET_PHONEMES.get(c.getWord());

                final List<Phoneme> list = new ArrayList<>();
                phonemes.forEach(phoneme -> {
                    // bɾ(OCME) -> b(OCME) + ɾ(OCME)
                    if (phoneme.isConsonantCluster() && splitConsonantClusters) {
                        String[] split = phoneme.getPhoneme().split("");
                        for (String s : split) {
                            // repeated phonemes are allowed here, because we wanna count
                            list.add(new Phoneme(s, phoneme.getPosition()));
                        }
                    } else {
                        // repeated phonemes are allowed
                        list.add(phoneme);
                    }
                });

                for (Phoneme p : list) {
                    int count = 1;
                    if (mapCounter.containsKey(p)) {
                        count = mapCounter.get(p) + 1;
                    }
                    mapCounter.put(p, count);

                    if (count <= minimum && !wordsRequired.contains(c.getWord())) {
                        wordsRequired.add(c.getWord());
                    }
                }
            }
        });
    }

    /**
     * Gets all the words that contains the phonemes in the given list.
     *
     * @param toBeTested Phonemes to be tested.
     * @param splitConsonantClusters True - this will count the consonant clusters as two phonemes:
     * <code>bɾ(OCME) -> b(OCME) + ɾ(OCME).</code>. The phoneme ɾ(OCME) can be counted more times in this way, and we
     * can evaluate more precisely the consonant clusters productions.
     * @return A list with all the words that contains the phonemes in the given list.
     */
    @Deprecated
    public static List<String> getNextWords(final List<Phoneme> toBeTested, final boolean splitConsonantClusters) {
        List<String> nextWords = new LinkedList<>();
        Iterator<Map.Entry<String, List<Phoneme>>> it = Defaults.TARGET_PHONEMES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<Phoneme>> next = it.next();
            List<Phoneme> value = next.getValue();

            List<Phoneme> phon = new ArrayList<>();
            value.forEach(phoneme -> {
                // bɾ(OCME) -> b(OCME) + ɾ(OCME)
                if (phoneme.isConsonantCluster() && splitConsonantClusters) {
                    String[] split = phoneme.getPhoneme().split("");
                    for (String s : split) {
                        // repeated phonemes are allowed here, because we wanna count
                        phon.add(new Phoneme(s, phoneme.getPosition()));
                    }
                } else {
                    // repeated phonemes are allowed
                    phon.add(phoneme);
                }
            });

            for (Phoneme p : toBeTested) {
                // TODO: aqui vai pegar TODAS as palavras que contém esses fonemas, e não apenas 1 ou 2 necessárias pra testar o mínimo de vezes necessárias
                if (phon.contains(p) && !nextWords.contains(next.getKey())) {
                    nextWords.add(next.getKey());
                    break;
                }
            }
        }

        return nextWords;
    }

    /**
     * Gets the words required for phonetic inventory according with criteria: a word that contains at least one phoneme
     * which was not tested a <code>minimum</code> of times, then that word is important and will be in the required
     * list.The words are analyzed according with the order in the <code>cases</code> list, so changing the order of the
     * array can reproduce different results.The phonetic inventory contains all the phonemes that were spoken correctly
     * at minimum of times.
     *
     * In this method only will be considered phonemes spoken in the list of cases. So here, we can have incomplete
     * assessments, since probably the phonetic inventory will be incomplete as well.
     *
     * @param cases The cases to analyze.
     * @param mapCounter A map only to count how many times each phoneme was tested, according with the criteria bellow.
     * @param splitConsonantClusters True - the consonant clusters will be transformed into 2 consonants phonemes. Ex.:
     * bɾ(OCME) -> b(OCME) + ɾ(OCME). False - keep the consonant phonemes as they are. Letting this flag with false
     * possibly will return more words because it's more difficult to find the phoneme bɾ(OCME) in the cases, but
     * r(OCME) can appear in many others consonant clusters, an b(OCME) as well (br, bl).
     * @param minimum The minimum of times that each phoneme must be reproduced in order to be considered in the
     * phonetic inventory. Higher values can return more required words.
     * @param phoneticInventory True - will compute words required for phonetic inventory. False - it'll compute words
     * required for phonemes testing (PCC-R).
     * @deprecated Use {@link SequencesExperiment}.
     * @return A list with the required words, according with the criteria above.
     */
    @Deprecated
    public static List<String> getWordsRequired(final List<KnownCase> cases, final Map<Phoneme, Integer> mapCounter,
            boolean splitConsonantClusters, final int minimum, final boolean phoneticInventory) {
        final List<String> wordsRequired = new LinkedList<>();

        // TODO: vai pegar somente as palavras que estão nos casos e não considerando todas as palavras do conjunto. Como estou trabalhando apenas com avaliações completas, isso não é um problema agora
        if (cases != null && mapCounter != null) {
            mapCounter.clear();
            cases.forEach(c -> {
                /**
                 * c.getPhonemes() contains all the produced phonemes, so it's useful for us to get the phonetic
                 * inventory information. For a phoneme to be considered in phonetic inventory it must be produced a
                 * minimum of times. For PCC-R, only matters the number of times that a phoneme was tested, because with
                 * that we can calculate later the percentage of correct productions in Assessment. For that, it's good
                 * that a phoneme can be tested a minimum of times as well, to avoid false positive and negative cases.
                 *
                 * TODO: e se um fonema tiver 2 produções: correta e incorreta. Deveria ter uma palavra a mais pra
                 * desempatar...
                 */
                List<Phoneme> phonemes = c.getPhonemes();
                if (!phoneticInventory) {
                    phonemes = Defaults.TARGET_PHONEMES.get(c.getWord());
                }

                // TODO: metodo aqui
                final List<Phoneme> list = new ArrayList<>();
                phonemes.forEach(phoneme -> {
                    // bɾ(OCME) -> b(OCME) + ɾ(OCME)
                    if (phoneme.isConsonantCluster() && splitConsonantClusters) {
                        String[] split = phoneme.getPhoneme().split("");
                        for (String s : split) {
                            // repeated phonemes are allowed here, because we wanna count
                            list.add(new Phoneme(s, phoneme.getPosition()));
                        }
                    } else {
                        // repeated phonemes are allowed
                        list.add(phoneme);
                    }
                });

                for (Phoneme p : list) {
                    int count = mapCounter.getOrDefault(p, 0) + 1;
                    mapCounter.put(p, count);

                    if (count <= minimum) {
                        /**
                         * If this word contains at least one phoneme which was not tested at minimum of times, then the
                         * word is important and will be "required".
                         *
                         * If all the phonemes tested by this word were already tested at minimum 2 times, so the word
                         * doesn't would need to be here.
                         */
                        if (!wordsRequired.contains(c.getWord())) {
                            wordsRequired.add(c.getWord());
                        }
                    }
                }
            });
        }

        return wordsRequired;
    }

}
