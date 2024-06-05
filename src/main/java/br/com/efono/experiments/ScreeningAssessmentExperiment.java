package br.com.efono.experiments;

import br.com.efono.model.Assessment;
import br.com.efono.model.IndicatorInfo;
import br.com.efono.util.DatabaseUtils;
import br.com.efono.util.Defaults;
import br.com.efono.util.FileUtils;
import br.com.efono.util.Range;
import br.com.efono.util.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // 0 -> negativo (não tem desvio - baixo,mod-baixo)
        // 1 -> positivo (tem desvio - alto,mod-alto)
        String[] realValues = new String[assessments.size()];

        int maxWordsScreening = 5;
        // Indicator Name -> predictedValues
        Map<String, String[]> mapConfusionMatrix = new HashMap<>();

        for (int i = 0; i <= maxWordsScreening; i++) {
            // 0-> SDA
            mapConfusionMatrix.put("Indicator " + i + "w", new String[assessments.size()]);
        }

        String negativo = "Negativo";
        String positivo = "Positivo";

        System.out.println("Output directory with experiments results: " + parent);

        DecimalFormat df = new DecimalFormat("#.##");
        File file = new File(parent, "results.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            List<String> allWords = Arrays.asList(Defaults.SORTED_WORDS);

            // SDA (Screening Dynamic Assessment): no limits, continue until reaches a leaf node in the screening.
            StringBuilder headerBuilder = new StringBuilder("ID_ASSESSMENT,PATIENT,PCC-R Complete Assessment,Indicator 84w,Indicator SDA,");
            for (int i = 1; i <= maxWordsScreening; i++) {
                headerBuilder.append("Indicator ").append(i).append("w");
                if (i < maxWordsScreening) {
                    headerBuilder.append(",");
                } else {
                    headerBuilder.append("\n");
                }
            }

            writer.write(headerBuilder.toString());
            for (int index = 0; index < assessments.size(); index++) {
                Assessment a = assessments.get(index);
                double pccr = a.getPCCR(allWords);
                String indicatorPCCR = Util.getDegree(pccr);
                realValues[index] = indicatorPCCR.contains("Low") ? negativo : positivo;

                StringBuilder sb = new StringBuilder();

                sb.append(a.getId()).append(",");
                sb.append(a.getPatientID()).append(",");
                sb.append(df.format(pccr).replaceAll(",", ".")).append(",");
                sb.append(indicatorPCCR).append(",");

                Range referenceRangeIndicatorPCCR = Util.getTreeIndicatorFromPCCR(indicatorPCCR);

                // 0 -> SDA
                for (int i = 0; i <= maxWordsScreening; i++) {
                    IndicatorInfo info = Util.getIndicatorSDA(a, i);
                    int indicatorSDA = info.getIndicator();
                    int deltaFromSDA = getDelta(referenceRangeIndicatorPCCR, indicatorSDA);
                    sb.append(deltaFromSDA);

                    if (i < maxWordsScreening) {
                        sb.append(",");
                    } else {
                        sb.append("\n");
                    }

                    // predicted values to build confusion matrix
                    String[] predictedValues = mapConfusionMatrix.get("Indicator " + i + "w");
                    predictedValues[index] = info.getIndicatorAsString().contains("Low") ? negativo : positivo;
                    mapConfusionMatrix.put("Indicator " + i + "w", predictedValues);
                }
                writer.write(sb.toString());
            }
        } catch (final IOException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }

        File confusionMatrixFile = new File(parent, "confusion-matrix.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(confusionMatrixFile))) {
            for (int index = 0; index <= maxWordsScreening; index++) {
                String key = "Indicator " + index + "w";
                writer.write("Confusion Matrix for " + key + "\n\n");

                String[] predictedValues = mapConfusionMatrix.get(key);
                /*
                Verdadeiros positivos (TP): o número de previsões corretas para a classe "Positivo".
                Verdadeiros negativos (TN): o número de previsões corretas para a classe "Negativo".
                Falsos positivos (FP): o número de previsões "Positivo" que são na verdade "Negativo".
                Falsos negativos (FN): o número de previsões "Negativo" que são na verdade "Positivo".
                 */
                double tp = 0, tn = 0, fp = 0, fn = 0;
                if (realValues.length == predictedValues.length) {
                    for (int i = 0; i < realValues.length; i++) {
                        if (realValues[i].equals(positivo) && predictedValues[i].equals(positivo)) {
                            tp++;
                        } else if (realValues[i].equals(negativo) && predictedValues[i].equals(negativo)) {
                            tn++;
                        } else if (realValues[i].equals(negativo) && predictedValues[i].equals(positivo)) {
                            fp++;
                        } else if (realValues[i].equals(positivo) && predictedValues[i].equals(negativo)) {
                            fn++;
                        }
                    }
                }

                writer.write(",Previsão Positivo,Previsão Negativo\n");
                writer.write("Real Positivo," + tp + "," + fn + "\n");
                writer.write("Real Negativo," + fp + "," + tn + "\n\n");

                double sensibilidade = tp / (tp + fn);
                double especificidade = tn / (tn + fp);
                double acuracia = (tp + tn) / (tp + tn + fp + fn);

                writer.write("Sensibilidade," + df.format(sensibilidade).replaceAll(",", ".") + "\n");
                writer.write("Especificidade," + df.format(especificidade).replaceAll(",", ".") + "\n");
                writer.write("Acurácia," + df.format(acuracia).replaceAll(",", ".") + "\n\n\n");
            }
        } catch (final IOException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }
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
    private int getDelta(final Range targetRange, final int indicatorSDA) {
        if (indicatorSDA >= targetRange.getMin() && indicatorSDA <= targetRange.getMax()) {
            // the indicator from SDA is the same from target (PCC-R).
            return 0;
        } else if (indicatorSDA < targetRange.getMin()) {
            // SDA indicator is at the left side of range
            return targetRange.getMin() - indicatorSDA;
        }
        // SDA indicator is at the right side of range
        return indicatorSDA - targetRange.getMax();
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
