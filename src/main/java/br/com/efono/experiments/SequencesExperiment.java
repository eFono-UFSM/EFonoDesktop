package br.com.efono.experiments;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.util.Defaults;
import br.com.efono.util.ExperimentUtils;
import br.com.efono.util.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, Jun 10.
 */
public class SequencesExperiment extends Experiment {

    /**
     * Minimum number of times that a phoneme in a certain position must be tested to be considered in the phonetic
     * inventory.
     */
    private static final byte MINIMUM_REPEATED_PHONEMES = 2;

    public SequencesExperiment(final Properties prop) {
        super(prop);
    }

    @Override
    protected void runExperimentResults(final File outputDirectory) {
        File parent = new File(outputDirectory, "sequences-experiment-results");
        parent.mkdir();

        final List<Assessment> assessments = dbUtils.getCompleteAssessmentsFromDB();
        System.out.println("Running experiment with " + assessments.size() + " complete assessments");

        KnownCaseComparator[] comparators = new KnownCaseComparator[]{
            KnownCaseComparator.HardWordsFirst, KnownCaseComparator.EasyWordsFirst,
            KnownCaseComparator.EasyHardWords, KnownCaseComparator.BinaryTreeComparator};

        final Map<KnownCaseComparator, ResultAggregator> map = new HashMap<>();

        assessments.forEach(assessment -> {
            final Map<Phoneme, Integer> mapCounterSplit = new HashMap<>();
            final Map<Phoneme, Integer> mapCounterNoSplit = new HashMap<>();

            List<KnownCase> cases = assessment.getCases();

            for (KnownCaseComparator comp : comparators) {
                ExperimentUtils.sortList(cases, comp);

                List<String> wordsRequiredSplit = getWordsRequired(cases, mapCounterSplit, true);
                List<String> wordsRequiredNoSplit = getWordsRequired(cases, mapCounterNoSplit, false);

                map.getOrDefault(comp, new ResultAggregator()).updateResults(wordsRequiredSplit, wordsRequiredNoSplit);
            }
        });

        // TODO: csv from map
    }

    private class ResultAggregator {

        private final Result resultSplit, resultNoSplit;

        ResultAggregator() {
            this.resultSplit = new Result();
            this.resultNoSplit = new Result();
        }

        void updateResults(final List<String> wordsRequiredSplit, final List<String> wordsRequiredNoSplit) {
            resultSplit.update(wordsRequiredSplit);
            resultNoSplit.update(wordsRequiredNoSplit);
        }

        private class Result {

            /**
             * The key is the number of words required and the value is how many times this number was required in the
             * experiment. This helps us to understand the best number of words that should be available in an
             * instrument of phonological assessment following a specific sequence of words.
             */
            private final Map<Integer, Integer> mapWordsRequired = new HashMap<>();

            /**
             * The key is the word required and the value is the number of times this words was required. This will
             * helps us to understand what words should be in an instrument of phonological assessment.
             */
            private final Map<String, Integer> mapWordsCounter = new HashMap<>();

            void update(final List<String> wordsRequired) {
                for (String w : wordsRequired) {
                    Integer currentVal = mapWordsCounter.getOrDefault(w, 0);
                    mapWordsCounter.put(w, currentVal + 1);
                }

                int countWordsRequired = wordsRequired.size();
                Integer val = mapWordsRequired.getOrDefault(countWordsRequired, 0);
                mapWordsRequired.put(countWordsRequired, val + 1);
            }
        }
    }

    /**
     * Gets the words required for phonetic inventory according with criteria: a word that contains at least one phoneme
     * which was not tested a {@link SequencesExperiment#MINIMUM_REPEATED_PHONEMES} of times, then that word is
     * important and will be in the required list.The words are analyzed according with the order in the
     * <code>cases</code> list, so changing the order of the array can reproduce different results.The phonetic
     * inventory contains all the phonemes that were spoken correctly at minimum of times.
     *
     * In this method only will be considered phonemes spoken in the list of cases. So here, we can have incomplete
     * assessments, since probably the phonetic inventory will be incomplete as well.
     *
     * @param cases The cases to analyze.
     * @param mapCounter A map only to count how many times each phoneme was tested, according with the criteria bellow.
     * @param splitConsonantClusters True - the consonant clusters will be transformed into 2 consonants phonemes. Ex.:
     * bɾ(OCME) -> b(OCME) + ɾ(OCME). False - keep the consonant phonemes as they are. Letting this flag with false
     * possibly will return more words because it's more difficult to find the phoneme bɾ(OCME) in the cases, but
     * r(OCME) can appear in many others consonant clusters, an b(OCME) as well (br, bl). Reference DOI:
     * 10.5220/0012555600003690
     * @return A list with the required words, according with the criteria above.
     */
    public static List<String> getWordsRequired(final List<KnownCase> cases, final Map<Phoneme, Integer> mapCounter,
        boolean splitConsonantClusters) {
        final List<String> wordsRequired = new LinkedList<>();
        /**
         * It'll only consider word that are in the assessment and not all the words in the set for a complete
         * assessment. This is not a problem right now because we are working only with complete assessment that have
         * all the words in the target set.
         */
        if (cases != null && mapCounter != null) {
            mapCounter.clear();
            cases.forEach(c -> {
                /**
                 * We must always consider the target phonemes for a word. If we consider the phonemes produced here,
                 * for example "tavalo" for the word "cavalo" we will be considering that the word cavalo is necessary
                 * for the child produce the phoneme `t`, and this is wrong.
                 */
                List<Phoneme> phonemes = Defaults.TARGET_PHONEMES.get(c.getWord());
                // TODO: e se um fonema tiver 2 produções: correta e incorreta. Deveria ter uma palavra a mais pra desempatar...

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
                    int count = 1;
                    if (mapCounter.containsKey(p)) {
                        count = mapCounter.get(p) + 1;
                    }
                    mapCounter.put(p, count);

                    if (count <= MINIMUM_REPEATED_PHONEMES) {
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

    public static void main(final String[] args) {
        System.out.println("Arguments received: " + Arrays.toString(args));
        Properties prop = FileUtils.readProperties(args);

        new SequencesExperiment(prop).init();
    }

}
