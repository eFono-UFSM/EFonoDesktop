/*
 * @copyright Copyright (c) 2014 Animati Sistemas de Informática Ltda. (http://www.animati.com.br)
 */
package br.com.efono;

import br.com.efono.experiments.SequencesExperiment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.tree.BinaryTreePrinter;
import br.com.efono.util.DatabaseUtils;
import br.com.efono.util.Defaults;
import br.com.efono.util.ExperimentUtils;
import br.com.efono.util.FileUtils;
import br.com.efono.util.Util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author João Bolsson (joaobolsson@animati.com.br)
 * @version 2024, jun. 10.
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("Arguments received: " + Arrays.toString(args));

        File output = null;
        final Properties prop = FileUtils.readProperties(args);

        if (1 > 0) {
            testBinaryTreeComparator(prop);
            return;
        }

        String outputDir = prop.getProperty(FileUtils.OUTPUT_DIR_PROP_NAME);
        if (outputDir != null) {
            output = new File(outputDir);
        }

        File parent = new File(output, "testing");
        parent.mkdir();

        String id = "test";

        Map<Integer, Integer> mapWordsRequired = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            mapWordsRequired.put(i, i * 2);
        }

        List<String> bestWords = new ArrayList<>();
        Map<String, Integer> mapWordsCounter = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            String w = "Cavalo" + i;
            mapWordsCounter.put(w, i * 3);
            bestWords.add(w);
        }

        List<String> linesWordsRequired = ExperimentUtils.getLinesFromMap("wordsRequired-" + id, "frequency-" + id, mapWordsRequired);
        List<String> linesWordsFrequency = ExperimentUtils.getLinesFromMap("word-" + id, "frequency-" + id, mapWordsCounter);
        List<String> linesPCCR = new ArrayList<>();
        linesPCCR.add("PCC-R");
        for (int i = 0; i < 10; i++) {
            linesPCCR.add(String.valueOf(0.1 * i));
        }

        List<String> report = new LinkedList<>();
        report.add("bestSize-" + id + ",bestWords-" + id);

        StringBuilder builder = new StringBuilder();
        for (String w : bestWords) {
            builder.append(w).append("-");
        }

        report.add(Integer.toString(10) + "," + builder.toString());

        List<List<String>> data = Arrays.asList(linesWordsRequired, linesWordsFrequency, report, linesPCCR);

        String csv = ExperimentUtils.concatListsToCSV(data);
        File file = new File(parent, "TestFile.csv");
        try (PrintWriter out = new PrintWriter(file)) {
            out.print(csv);
            System.out.println("File at: " + file);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }
    }

    public static void testBinaryTreeComparator(final Properties prop) {
        DatabaseUtils dbUtils = new DatabaseUtils(prop);

        // keep here
        Defaults.TREE.init(Defaults.SORTED_WORDS);
        Defaults.TARGET_PHONEMES.putAll(dbUtils.getTargetPhonemesForEachWord(Defaults.SORTED_WORDS));

        Defaults.SIMILAR_WORDS.clear();
        Defaults.SIMILAR_WORDS.putAll(
            Util.buildSimilarWords(Arrays.asList(Defaults.SORTED_WORDS),
                Defaults.TARGET_PHONEMES,
                SequencesExperiment.MINIMUM_REPEATED_PHONEMES));

        List<KnownCase> list = new LinkedList<>();
        for (String w : Defaults.SORTED_WORDS) {
            list.add(new KnownCase(w, w, true));
        }

        BinaryTreePrinter.print(Defaults.TREE);

        System.out.println("list before: " + list);
        list = ExperimentUtils.sortList(list, KnownCaseComparator.BinaryTreeComparator);
        System.out.println("list after: " + list);
    }

}
