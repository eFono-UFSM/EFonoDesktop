package br.com.efono.experiments;

import br.com.efono.util.DatabaseUtils;
import br.com.efono.util.Defaults;
import br.com.efono.util.FileUtils;
import br.com.efono.util.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, Jun 10.
 */
public abstract class Experiment {

    protected final DatabaseUtils dbUtils;
    protected final Properties prop;

    public Experiment(final Properties prop) {
        this.prop = prop;
        dbUtils = new DatabaseUtils(prop);

        // keep here
        Defaults.TREE.init(Defaults.SORTED_WORDS);
        Defaults.TARGET_PHONEMES.putAll(dbUtils.getTargetPhonemesForEachWord(Defaults.SORTED_WORDS));

        Defaults.SIMILAR_WORDS.clear();
        Defaults.SIMILAR_WORDS.putAll(
            Util.buildSimilarWords(Arrays.asList(Defaults.SORTED_WORDS),
                Defaults.TARGET_PHONEMES,
                SequencesExperiment.MINIMUM_REPEATED_PHONEMES));
    }

    /**
     * Sorts the numbers according with their frequency in the list. The most frequent element will be at the beggining
     * of the list.
     *
     * @param numbers Number to sort.
     * @return A list with unique values containing the elements sorted.
     */
    public static List<Integer> sortByFrequency(final List<Integer> numbers) {
        // Passo 1: Conta a frequência de cada número
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (Integer number : numbers) {
            frequencyMap.put(number, frequencyMap.getOrDefault(number, 0) + 1);
        }

        // Passo 2: Cria uma lista de entradas (número, frequência)
        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(frequencyMap.entrySet());

        // Passo 3: Ordena a lista de entradas pela frequência em ordem decrescente
        entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Passo 4: Extrai os números ordenados pela frequência
        List<Integer> sortedByFrequency = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : entryList) {
            sortedByFrequency.add(entry.getKey());
        }

        return sortedByFrequency;
    }

    protected void init() {
        // output to store generated files
        File output = null;
        String outputDir = prop.getProperty(FileUtils.OUTPUT_DIR_PROP_NAME);
        if (outputDir != null) {
            output = new File(outputDir);
        }

        // just print the UML tree
//        BinaryTreePrinter.print(Defaults.TREE);
//
//        System.out.println("Target phonemes for each word: ");
//        Iterator<Map.Entry<String, List<Phoneme>>> iterator = Defaults.TARGET_PHONEMES.entrySet().iterator();
//
//        while (iterator.hasNext()) {
//            Map.Entry<String, List<Phoneme>> next = iterator.next();
//            System.out.println(next.getKey() + " -> " + next.getValue());
//        }
        runExperimentResults(output);
    }

    protected abstract void runExperimentResults(File outputDirectory);

}
