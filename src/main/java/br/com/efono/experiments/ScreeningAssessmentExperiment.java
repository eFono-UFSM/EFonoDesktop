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
        System.out.println("Starting ScreeningAssessmentExperiment");
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
                String indicatorPCCR = Util.getDegree(pccr);
                StringBuilder sb = new StringBuilder();

                sb.append(a.getId()).append(",");
                sb.append(df.format(pccr).replaceAll(",", ".")).append(",");
                sb.append(indicatorPCCR).append(",");

                Range rangeIndicatorPCCR = getTreeIndicatorFromPCCR(indicatorPCCR);

                for (int i = 1; i <= maxWordsScreening; i++) {
                    Assessment.IndicatorInfo indicatorInfo = a.getIndicatorInfoFromScreening(i);

                    int deltaFromSDA = getDeltaFromSDA(rangeIndicatorPCCR, indicatorInfo.getIndicator());
                    sb.append(deltaFromSDA).append(",");
                }
                // SDA
                Assessment.IndicatorInfo sdaInfo = a.getIndicatorInfoFromScreening(0);
                int deltaSDA = getDeltaFromSDA(rangeIndicatorPCCR, sdaInfo.getIndicator());
                sb.append(deltaSDA).append("\n");
                writer.write(sb.toString());
            }
        } catch (final IOException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }
    }

    private Range getTreeIndicatorFromPCCR(final String indicatorPCCR) {
        if ("High".equals(indicatorPCCR)) {
            return new Range(0, 20);
        } else if ("Moderate-High".equals(indicatorPCCR)) {
            return new Range(21, 40);
        } else if ("Moderate-Low".equals(indicatorPCCR)) {
            return new Range(42, 61);
        }
        return new Range(62, Defaults.SORTED_WORDS.length - 1);
    }

    /**
     * Calculates the delta between the indicator from SDA and the target range. The PCC-R indicator is in the target
     * range. With this method we want to know how far our SDA indicator is from the PCC-R range. If the SDA indicator
     * was the same of PCC-R, then the delta returned will be 0.
     *
     * @param targetRange The referenced range of values.
     * @param indicatorSDA The indicator from SDA.
     * @return A value indicating how far the SDA indicator is from the target range.
     */
    private int getDeltaFromSDA(final Range targetRange, final int indicatorSDA) {
        if (indicatorSDA >= targetRange.min && indicatorSDA <= targetRange.max) {
            // the indicator from SDA is the same from target (PCC-R).
            return 0;
        } else if (indicatorSDA < targetRange.min) {
            // SDA indicator is at the left side of range
            return targetRange.min - indicatorSDA;
        }
        // SDA indicator is at the right side of range
        return indicatorSDA - targetRange.max;
    }

    private class Range {

        private final int min, max;

        /**
         * Creates an object that represents a range.
         *
         * @param min Min value in the range (inclusive).
         * @param max Max value in the range (inclusive).
         */
        public Range(int min, int max) {
            // min <= value <= max
            this.min = min;
            this.max = max;
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

        ScreeningAssessmentExperiment experiment = new ScreeningAssessmentExperiment(prop);
        experiment.init();
    }

}
