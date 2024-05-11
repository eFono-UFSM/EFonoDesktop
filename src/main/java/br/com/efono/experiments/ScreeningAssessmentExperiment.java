package br.com.efono.experiments;

import br.com.efono.model.Assessment;
import br.com.efono.util.DatabaseUtils;
import br.com.efono.util.Defaults;
import br.com.efono.util.FileUtils;
import br.com.efono.util.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, May 11.
 */
public class ScreeningAssessmentExperiment {

    private final DatabaseUtils dbUtils;
    private final Properties prop;

    public ScreeningAssessmentExperiment(final Properties prop) {
        this.prop = prop;
        dbUtils = new DatabaseUtils(prop);

        // keep here
        Defaults.TREE.init(Defaults.SORTED_WORDS);
        Defaults.TARGET_PHONEMES.putAll(dbUtils.getTargetPhonemesForEachWord(Defaults.SORTED_WORDS));
    }

    private void init() {
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

    private void runExperimentResults(final File outputDirectory) {
        final List<Assessment> assessments = dbUtils.getCompleteAssessmentsFromDB();

        System.out.println("Running experiment with " + assessments.size() + " complete assessments");
        File parent = new File(outputDirectory, "Computer-Speech-2024-experiments-results");
        parent.mkdir();

        System.out.println("Output directory with experiments results: " + parent);

        DecimalFormat df = new DecimalFormat("#.##");
        File file = new File(parent, "results.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            List<String> allWords = Arrays.asList(Defaults.SORTED_WORDS);

            int maxWordsScreening = 5;

            StringBuilder headerBuilder = new StringBuilder("ID_ASSESSMENT,PCC-R Complete Assessment,Indication 84w,");
            for (int i = 1; i <= maxWordsScreening; i++) {
                headerBuilder.append("Indication ").append(i).append("w,");
            }
            // SDA (Screening Dynamic Assessment): no limits, continue until reaches a leaf node in the screening.
            headerBuilder.append("Indication SDA\n");

            writer.write(headerBuilder.toString());
            for (Assessment a : assessments) {
                double pccr = a.getPCCR(allWords);
                StringBuilder sb = new StringBuilder();

                sb.append(a.getId()).append(",");
                sb.append(df.format(pccr).replaceAll(",", ".")).append(",");
                sb.append(Util.getDegree(pccr)).append(",");

                for (int i = 1; i <= maxWordsScreening; i++) {
                    sb.append(a.getIndicatorFromScreening(i)).append(",");
                }
                // SDA
                sb.append(a.getIndicatorFromScreening(0)).append("\n");
                writer.write(sb.toString());
            }
        } catch (final IOException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }
    }

    /**
     * Main method.
     *
     * @param args Command line arguments.
     */
    public static void main(final String[] args) {
        System.out.println("Arguments received: " + Arrays.toString(args));
        Properties prop = FileUtils.readProperties(args);

        new ScreeningAssessmentExperiment(prop).init();
    }


}
