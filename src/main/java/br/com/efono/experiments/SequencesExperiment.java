package br.com.efono.experiments;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.util.Defaults;
import br.com.efono.util.ExperimentUtils;
import br.com.efono.util.FileUtils;
import br.com.efono.util.Util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

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
    public static final byte MINIMUM_REPEATED_PHONEMES = 2;

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
            KnownCaseComparator.HardWordsFirst, KnownCaseComparator.EasyWordsFirst, KnownCaseComparator.EasyHardWords,
            KnownCaseComparator.BinaryTreeComparator, KnownCaseComparator.BinaryTreeSDA,
            KnownCaseComparator.BlocksOfWords, KnownCaseComparator.BlocksOfWordsSDA};
        // TODO: comparator para usar somente as 55 palavras de marques:2023 e ver como fica
        //            // 55 words from Algorithm for Selecting Words to Compose Phonological Assessments
//            String[] words55 = new String[]{"Jornal", "Tênis", "Cruz", "Mesa", "Tesoura", "Bebê", "Cachorro", "Terra", "Rabo",
//                "Dragão", "Língua", "Chiclete", "Gritar", "Porta", "Refri", "Dado", "Igreja", "Relógio", "Cobra", "Zebra",
//                "Brinco", "Placa", "Plástico", "Vaca", "Soprar", "Travesseiro", "Escrever", "Bruxa", "Zero", "Dedo",
//                "Fralda", "Estrela", "Espelho", "Flor", "Faca", "Fogo", "Girafa", "Garfo", "Sofá", "Trem", "Vidro", "Sapo",
//                "Livro", "Magro", "Pedra", "Nuvem", "Galinha", "Grama", "Chapéu", "Navio", "Caixa", "Letra", "Chifre",
//                "Folha", "Cama"};

        // initializing the map with aggregators
        final Map<KnownCaseComparator, ResultAggregator> map = new HashMap<>();

        // for the first three comparators doesn't matter what is it in the assessment, only the words. We create a fake cases here to pass to getWordsRequired (it will only use the word).
        List<KnownCase> fakeCases = new ArrayList<>();
        for (String w : Defaults.SORTED_WORDS) {
            fakeCases.add(new KnownCase(w, w, true));
        }

        List<Assessment> fakeAssessments = Arrays.asList(new Assessment(fakeCases));

        for (KnownCaseComparator comp : comparators) {
            List<Assessment> list;

            if (!isKindOfBinaryTreeComparator(comp)) {
                list = fakeAssessments;
            } else {
                list = assessments;
            }

            System.out.println("Running " + comp + " for " + list.size() + " assessments");
            for (int i = 0; i < list.size(); i++) {
                Assessment assessment = list.get(i);

                List<String> wordsRequiredSplit = getWordsRequired(assessment, comp, true);
                List<String> wordsRequiredNoSplit = getWordsRequired(assessment, comp, false);

                System.out.println(comp + ": Updating result " + (i + 1) + "/" + list.size());
                ResultAggregator aggregator = map.getOrDefault(comp, new ResultAggregator());
                aggregator.updateResults(assessment, wordsRequiredSplit, wordsRequiredNoSplit);

                map.put(comp, aggregator);
            }
        }

        // exporting results
        for (Map.Entry<KnownCaseComparator, ResultAggregator> next : map.entrySet()) {
            System.out.println("Exporting results of " + next.getKey() + " to csv...");
            File file = new File(parent, next.getKey().name() + ".csv");
            try (PrintWriter out = new PrintWriter(file)) {
                out.print(next.getValue().exportCSV(assessments));
                System.out.println("File at: " + file);
            } catch (final FileNotFoundException ex) {
                System.out.println("Couldn't write into file: " + ex);
            }
        }

        // exporting report results
        System.out.println("Exporting report results to csv...");
        File file = new File(parent, "Report.csv");
        try (PrintWriter out = new PrintWriter(file)) {
            out.print(exportReportResults(map));
            System.out.println("File at: " + file);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }

        // exporting report results
        System.out.println("Exporting words report to csv...");
        File fileWordsReport = new File(parent, "Words-Report.csv");
        try (PrintWriter out = new PrintWriter(fileWordsReport)) {
            out.print(exportWordsEntriesReport());
            System.out.println("File at: " + fileWordsReport);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }
    }

    private String exportWordsEntriesReport() {
        Map<String, List<String>> allSimilarWords = new HashMap<>();
        allSimilarWords.putAll(
            Util.buildSimilarWords(Arrays.asList(Defaults.SORTED_WORDS),
                Defaults.TARGET_PHONEMES,
                Defaults.SORTED_WORDS.length + 1));

        StringBuilder builder = new StringBuilder();
        builder.append("Palavra\tFonemas-Alvo\tBloco de Palavras Similares\n");
        for (String w : Defaults.SORTED_WORDS) {
            builder.append(w).append("\t").append(Defaults.TARGET_PHONEMES.getOrDefault(w, new ArrayList<>())).append("\t").append(allSimilarWords.getOrDefault(w, new ArrayList<>())).append("\n");
        }

        return builder.toString();
    }

    private String exportReportResults(final Map<KnownCaseComparator, ResultAggregator> map) {
        // Ordenar as entradas do mapa pelos valores em ordem decrescente
        List<Map.Entry<KnownCaseComparator, ResultAggregator>> sortedEntries = map.entrySet().stream().sorted(
            (final Map.Entry<KnownCaseComparator, ResultAggregator> o1, final Map.Entry<KnownCaseComparator, ResultAggregator> o2)
            -> o2.getValue().getAllResultSplit().bestSize - o1.getValue().getAllResultSplit().bestSize).collect(Collectors.toList());

        List<String> columnsCounters = new ArrayList<>();
        columnsCounters.add("");
        columnsCounters.add("Qt Palavras Mais Comum [split]");
        columnsCounters.add("Concordância [split]");
        columnsCounters.add("Qt Palavras Mais Comum [no split]");
        columnsCounters.add("Concordância [no split]");

        List<List<String>> columns = new ArrayList<>();
        columns.add(columnsCounters);

        DecimalFormat df = new DecimalFormat("#");
        sortedEntries.forEach(entry -> {
            ResultAggregator.Result resultSplit = entry.getValue().getAllResultSplit();
            ResultAggregator.Result resultNoSplit = entry.getValue().getAllResultNoSplit();

            List<String> column = new ArrayList<>();
            column.add(entry.getKey().name());
            column.add(Integer.toString(resultSplit.bestSize));
            column.add(df.format(entry.getValue().concordanceSplit).replaceAll(",", "."));
            column.add(Integer.toString(resultNoSplit.bestSize));
            column.add(df.format(entry.getValue().concordanceNoSplit).replaceAll(",", "."));

            columns.add(column);
        });

        StringBuilder lines = new StringBuilder();
        // all the columns have the same number of lines
        for (int i = 0; i < columnsCounters.size(); i++) {
            StringBuilder line = new StringBuilder();
            for (List<String> c : columns) {
                line.append(c.get(i)).append(",");
            }
            lines.append(line.substring(0, line.lastIndexOf(","))).append("\n");
        }
        return lines.toString();
    }

    private static boolean isKindOfBinaryTreeComparator(final KnownCaseComparator comp) {
        return comp.equals(KnownCaseComparator.BinaryTreeComparator)
            || comp.equals(KnownCaseComparator.BinaryTreeSDA)
            || comp.equals(KnownCaseComparator.BlocksOfWords)
            || comp.equals(KnownCaseComparator.BlocksOfWordsSDA);
    }

    private class ResultAggregator {

        protected final Map<Assessment, Result> resultSplitMap = new HashMap<>();
        protected final Map<Assessment, Result> resultNoSplitMap = new HashMap<>();

        void updateResults(final Assessment assessment, final List<String> wordsRequiredSplit,
            final List<String> wordsRequiredNoSplit) {
            Result resultSplit = resultSplitMap.getOrDefault(assessment, new Result());
            resultSplit.update(wordsRequiredSplit);
            resultSplitMap.put(assessment, resultSplit);

            Result resultNoSplit = resultNoSplitMap.getOrDefault(assessment, new Result());
            resultNoSplit.update(wordsRequiredNoSplit);
            resultNoSplitMap.put(assessment, resultNoSplit);
        }

        Result getAllResultSplit() {
            Result resultSplit = new Result();
            System.out.print("Updating global results...");
            resultSplitMap.values().forEach(r -> resultSplit.update(r));

            return resultSplit;
        }

        Result getAllResultNoSplit() {
            Result resultNoSplit = new Result();
            System.out.print("Updating global results...");
            resultNoSplitMap.values().forEach(r -> resultNoSplit.update(r));

            return resultNoSplit;
        }

        String exportCSV(final List<Assessment> assessments) {
            // words required section
            List<String> wordsRequired = new ArrayList<>();
            wordsRequired.add("Qt Palavras,sem split,split");

            List<Integer> keys = new ArrayList<>();
            Result resultSplit = getAllResultSplit();
            Result resultNoSplit = getAllResultNoSplit();

            for (Integer k : resultSplit.mapWordsRequired.keySet()) {
                if (!keys.contains(k)) {
                    keys.add(k);
                }
            }
            for (Integer k : resultNoSplit.mapWordsRequired.keySet()) {
                if (!keys.contains(k)) {
                    keys.add(k);
                }
            }

            Collections.sort(keys);
            for (Integer k : keys) {
                wordsRequired.add(k + "," + resultNoSplit.mapWordsRequired.getOrDefault(k, 0) + "," + resultSplit.mapWordsRequired.getOrDefault(k, 0));
            }

            // words counter section
            List<String> wordsCounter = new ArrayList<>();
            wordsCounter.add("word,frequency-split,frequency-no-split");
            for (String w : Defaults.SORTED_WORDS) {
                wordsCounter.add(w + "," + resultSplit.mapWordsCounter.getOrDefault(w, 0) + "," + resultNoSplit.mapWordsCounter.getOrDefault(w, 0));
            }

            // report of best size in the set of words and what words should be
            List<String> report = new ArrayList<>();
            report.add(",split,no-split");
            report.add("bestSize," + resultSplit.bestSize + "," + resultNoSplit.bestSize);
            report.add("words," + resultSplit.getBestWordsFormated() + "," + resultNoSplit.getBestWordsFormated());

            // PCC-R
            List<String> indicators = getIndicatorsResult(assessments);

            // Arrays.asList("-"): this is a empty column
            return ExperimentUtils.concatListsToCSV(Arrays.asList(wordsRequired, Arrays.asList("-"),
                wordsCounter, Arrays.asList("-"), Arrays.asList("-"), report, Arrays.asList("-"), indicators));
        }

        Result getResultSplit(final Assessment a) {
            if (resultSplitMap.size() == 1) {
                return new ArrayList<>(resultSplitMap.values()).get(0);
            }
            return resultSplitMap.getOrDefault(a, new Result());
        }

        Result getResultNoSplit(final Assessment a) {
            if (resultNoSplitMap.size() == 1) {
                return new ArrayList<>(resultNoSplitMap.values()).get(0);
            }
            return resultNoSplitMap.getOrDefault(a, new Result());
        }

        double concordanceSplit, concordanceNoSplit;

        List<String> getIndicatorsResult(final List<Assessment> assessments) {
            System.out.println("Getting indicators results");

            double countTrueSplit = 0, countTrueNoSplit = 0;
            List<String> indicators = new ArrayList<>();
            indicators.add("Degree 84w,Custom words [split],Degree [split],Custom words [no-split],Degree [no-split],Concordance [split],Concordance [no-split]");
            for (Assessment a : assessments) {
                String degree84w = Util.getDegree(a.getPCCR(Arrays.asList(Defaults.SORTED_WORDS)));

                List<String> customWordsSplit = getResultSplit(a).getBestWords();
                List<String> customWordsNoSplit = getResultNoSplit(a).getBestWords();

                String degreeSplit = Util.getDegree(a.getPCCR(customWordsSplit));
                String degreeNoSplit = Util.getDegree(a.getPCCR(customWordsNoSplit));

                String matchSplit = degree84w.equals(degreeSplit) ? "TRUE" : "FALSE";
                String matchNoSplit = degree84w.equals(degreeNoSplit) ? "TRUE" : "FALSE";

                if (matchSplit.equals("TRUE")) {
                    countTrueSplit++;
                }
                if (matchNoSplit.equals("TRUE")) {
                    countTrueNoSplit++;
                }

                indicators.add(buildLine(degree84w, customWordsSplit.size(), degreeSplit, customWordsNoSplit.size(), degreeNoSplit, matchSplit, matchNoSplit));
            }
            System.out.println("Done!");

            concordanceSplit = (countTrueSplit / assessments.size()) * 100;
            concordanceNoSplit = (countTrueNoSplit / assessments.size()) * 100;

            return indicators;
        }

        String buildLine(final Object... columns) {
            StringBuilder builder = new StringBuilder();
            for (Object col : columns) {
                builder.append(col).append(",");
            }
            return builder.substring(0, builder.toString().lastIndexOf(","));
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

            // the most repeated size of words.
            private int mostRepeatedFrequency = 0;

            // size of the most repeated "size of the set of words" observed in the result
            private int bestSize = 0;

            void update(final List<String> wordsRequired) {
                for (String w : wordsRequired) {
                    Integer currentVal = mapWordsCounter.getOrDefault(w, 0);
                    mapWordsCounter.put(w, currentVal + 1);
                }

                int countWordsRequired = wordsRequired.size();
                Integer newVal = mapWordsRequired.getOrDefault(countWordsRequired, 0) + 1;
                mapWordsRequired.put(countWordsRequired, newVal);

                if (newVal > mostRepeatedFrequency) {
                    mostRepeatedFrequency = newVal;
                    bestSize = countWordsRequired;
                }
            }

            void update(final Result result) {
                result.mapWordsCounter.entrySet().forEach(entry -> {
                    mapWordsCounter.put(entry.getKey(), mapWordsCounter.getOrDefault(entry.getKey(), 0) + entry.getValue());
                });

                result.mapWordsRequired.entrySet().forEach(entry -> {
                    Integer newVal = mapWordsRequired.getOrDefault(entry.getKey(), 0) + entry.getValue();
                    mapWordsRequired.put(entry.getKey(), newVal);

                    if (newVal > mostRepeatedFrequency) {
                        mostRepeatedFrequency = newVal;
                        bestSize = entry.getKey();
                    }
                });
            }

            List<String> getBestWords() {
                // Ordenar as entradas do mapa pelos valores em ordem decrescente
                List<Map.Entry<String, Integer>> sortedEntries = mapWordsCounter.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .collect(Collectors.toList());

                List<String> bestWords = new LinkedList<>();

                // Seleciona as top N chaves com maiores valores
                for (int i = 0; i < Math.min(bestSize, sortedEntries.size()); i++) {
                    Map.Entry<String, Integer> entry = sortedEntries.get(i);
                    bestWords.add(entry.getKey());
                }
                return bestWords;
            }

            String getBestWordsFormated() {
                List<String> bestWords = getBestWords();
                StringBuilder builderBestWords = new StringBuilder();
                for (String w : bestWords) {
                    builderBestWords.append(w).append("-");
                }
                return builderBestWords.toString();
            }
        }
    }

    /**
     * Gets the words required for phonetic inventory according with criteria: a word that contains at least one phoneme
     * which was not tested a {@link SequencesExperiment#MINIMUM_REPEATED_PHONEMES} of times, then that word is
     * important and will be required. The words are analyzed respecting the given order in the list, so changing the
     * order of the array can reproduce different results. The phonetic inventory contains all the phonemes that were
     * spoken correctly at minimum of times.
     *
     * In this method will be only considered phonemes spoken in the list of cases. So here, we can have incomplete
     * assessments, since probably the phonetic inventory will be incomplete as well.
     *
     * @param assessment The assessment with cases to analyze.
     * @param comp Comparator to build words sequence.
     * @param splitConsonantClusters True - the consonant clusters will be transformed into 2 consonants phonemes. Ex.:
     * bɾ(OCME) -> b(OCME) + ɾ(OCME). False - keep the consonant phonemes as they are. Letting this flag with false
     * possibly will return more words because it's more difficult to find the phoneme bɾ(OCME) in the cases, but
     * r(OCME) can appear in many others consonant clusters, an b(OCME) as well (br, bl). Reference DOI:
     * 10.5220/0012555600003690
     * @return A list with the required words, according with the criteria above.
     */
    public static List<String> getWordsRequired(final Assessment assessment, final KnownCaseComparator comp,
        boolean splitConsonantClusters) {
        List<KnownCase> cases = assessment.getCases();
        // sortList will return always the same order for the first three comparators, because they don't use assessment information
        if (isKindOfBinaryTreeComparator(comp)) {
            cases = ExperimentUtils.sortList(cases, KnownCaseComparator.BinaryTreeComparator);
        } else {
            cases = ExperimentUtils.sortList(cases, comp);
        }
        /**
         * A map only to count how many times each phoneme was tested.
         */
        final Map<Phoneme, Integer> mapCounter = new HashMap<>();
        final List<String> wordsRequired = new ArrayList<>();
        /**
         * It'll only consider word that are in the assessment and not all the words in the set for a complete
         * assessment. This is not a problem right now because we are working only with complete assessment that have
         * all the words in the target set.
         */
        if (cases != null) {
            List<String> wordsToVisit = new LinkedList<>();
            List<String> wordsInCases = new LinkedList<>();

            if (comp.equals(KnownCaseComparator.BinaryTreeSDA) || comp.equals(KnownCaseComparator.BlocksOfWordsSDA)) {
                // the only words to coonsider here is the ones of SDA (until there is no more words to go in the tree).
                wordsInCases.addAll(Util.getIndicatorSDA(assessment, 0).getWordsSequence());
            } else {
                cases.forEach(c -> wordsInCases.add(c.getWord()));
            }

            wordsInCases.forEach(w -> {
                if (!wordsToVisit.contains(w)) {
                    wordsToVisit.add(w);

                    if (comp.equals(KnownCaseComparator.BlocksOfWords) || comp.equals(KnownCaseComparator.BlocksOfWordsSDA)) {
                        // adds all the similar words in the sequence
                        Defaults.SIMILAR_WORDS.get(w).forEach(similar -> {
                            if (!wordsToVisit.contains(similar)) {
                                wordsToVisit.add(similar);
                            }
                        });
                    }
                }
            });

            wordsToVisit.forEach(w -> {
                /**
                 * We must always consider the target phonemes for a word. If we consider the phonemes produced here,
                 * for example "tavalo" for the word "cavalo" we will be considering that the word cavalo is necessary
                 * for the child produce the phoneme `t`, and this is wrong.
                 */
                List<Phoneme> phonemes = Defaults.TARGET_PHONEMES.get(w);
                // TODO: e se um fonema tiver 2 produções: correta e incorreta. Deveria ter uma palavra a mais pra desempatar...

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

                    /**
                     * If this word contains at least one phoneme which was not tested at minimum of times, then the
                     * word is important and will be "required".
                     *
                     * If all the phonemes tested by this word were already tested at minimum 2 times, so the word
                     * doesn't would need to be here.
                     */
                    if (count <= MINIMUM_REPEATED_PHONEMES && !wordsRequired.contains(w)) {
                        wordsRequired.add(w);
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
