package br.com.efono.experiments;

import br.com.efono.model.Assessment;
import br.com.efono.model.IndicatorInfo;
import br.com.efono.util.Defaults;
import br.com.efono.util.ExperimentUtils;
import br.com.efono.util.FileUtils;
import br.com.efono.util.Range;
import br.com.efono.util.Util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
public class ScreeningAssessmentExperiment extends Experiment {

    private final static DecimalFormat DF = new DecimalFormat("#.##");
    private final static int MAX_WORDS_SCREENING = 5;
    private final static String NEGATIVO = "Negativo";
    private final static String POSITIVO = "Positivo";

    /**
     * Prefix key to be used in mapConfusionMatrix.
     */
    private static final String INDICATOR_PREFIX_KEY = "Indication ";

    public ScreeningAssessmentExperiment(final Properties prop) {
        super(prop);
    }

    private String exportCSV(final List<Assessment> assessments) {
        // 0 -> NEGATIVO (não tem desvio - baixo,mod-baixo)
        // 1 -> POSITIVO (tem desvio - alto,mod-alto)
        String[] realValues = new String[assessments.size()];

        int maxWordsScreening = 5;
        // Indicator Name -> predictedValues
        Map<String, String[]> mapConfusionMatrix = new HashMap<>();

        // key: palavras na triagem, 0 -> Altura BST. Value: erros
        Map<Integer, List<Integer>> mapErrors = new HashMap<>();

        String negativo = "Negativo";
        String positivo = "Positivo";
        List<String> allWords = Arrays.asList(Defaults.SORTED_WORDS);

        // SDA (Screening Dynamic Assessment): no limits, continue until reaches a leaf node in the screening.
        StringBuilder headerBuilder = new StringBuilder("ID_ASSESSMENT,PATIENT,PCC-R Complete Assessment," + INDICATOR_PREFIX_KEY + "84w," + INDICATOR_PREFIX_KEY + "SDA,");
        for (int i = 1; i <= maxWordsScreening; i++) {
            headerBuilder.append(INDICATOR_PREFIX_KEY).append(i).append("w");
            if (i < maxWordsScreening) {
                headerBuilder.append(",");
            }
        }

        List<String> linesAssessments = new ArrayList<>();
        linesAssessments.add(headerBuilder.toString());

        for (int index = 0; index < assessments.size(); index++) {
            Assessment a = assessments.get(index);
            double pccr = a.getPCCR(allWords);
            String indicatorPCCR = Util.getDegree(pccr);
            realValues[index] = indicatorPCCR.contains("Low") ? negativo : positivo;

            StringBuilder sb = new StringBuilder();

            sb.append(a.getId()).append(",");
            sb.append(a.getPatientID()).append(",");
            sb.append(DF.format(pccr).replaceAll(",", ".")).append(",");
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
                }

                if (deltaFromSDA > 0) {
                    // used to get the most frequent deltas
                    List<Integer> deltas = mapErrors.getOrDefault(i, new ArrayList<>());
                    deltas.add(deltaFromSDA);
                    mapErrors.put(i, deltas);
                }

                String indicationKey = INDICATOR_PREFIX_KEY + i + "w";

                // predicted values to build confusion matrix
                String[] predictedValues = mapConfusionMatrix.getOrDefault(indicationKey, new String[assessments.size()]);
                predictedValues[index] = info.getIndicatorAsString().contains("Low") ? negativo : positivo;
                mapConfusionMatrix.put(indicationKey, predictedValues);
            }
            linesAssessments.add(sb.toString());
        }

        Map<Integer, List<Integer>> mapFrequencies = new HashMap<>();
        mapErrors.keySet().forEach(k -> {
            mapFrequencies.put(k, sortByFrequency(mapErrors.get(k)));
        });

        return ExperimentUtils.concatListsToCSV(Arrays.asList(linesAssessments,
            getLinesDeltasFrequencies(mapErrors),
            getLinesDeltasFrequenciesPercentErrors(mapErrors),
            getLinesUniqueDeltas(mapFrequencies),
            getLinesConfusionMatrix(mapConfusionMatrix, realValues)));
    }

    private List<String> getLinesDeltasFrequencies(final Map<Integer, List<Integer>> mapErrors) {
        List<String> lines = new ArrayList<>();

        // sorts the lists
        StringBuilder headerBuilder = new StringBuilder(",");
        mapErrors.keySet().forEach(k -> {
            if (k != 0) {
                // skip Altura da BST: adiciona depois
                headerBuilder.append(k).append(" Palavra");
                if (k > 1) {
                    headerBuilder.append("s");
                }
                headerBuilder.append(",");

                List<Integer> list = mapErrors.get(k);
                list.sort((a, b) -> b - a); // ordem decrescente
                mapErrors.put(k, list);
            }
        });
        if (mapErrors.containsKey(0)) {
            headerBuilder.append("Altura BST");
        } else {
            headerBuilder.delete(headerBuilder.lastIndexOf(","), headerBuilder.lastIndexOf(",") + 1);
        }
        lines.add(headerBuilder.toString());

        Map<Integer, List<Integer>> mapFrequencies = new HashMap<>();
        mapErrors.entrySet().forEach(e -> {
            mapFrequencies.put(e.getKey(), sortByFrequency(e.getValue()));
        });

        // the max most frequent deltas
        int max = 3; // 1º, 2º, maxº...
        for (int i = 0; i < max; i++) {
            StringBuilder line = new StringBuilder((i + 1) + "º Delta Mais Frequente,");

            int th = i;
            mapFrequencies.entrySet().forEach(e -> {
                if (e.getKey() != 0) {
                    // skip Altura da BST: adiciona depois
                    line.append(e.getValue().get(th)).append(",");
                }
            });

            // Altura BST
            if (mapFrequencies.containsKey(0)) {
                line.append(mapFrequencies.get(0).get(th));
            } else {
                line.delete(line.lastIndexOf(","), line.lastIndexOf(",") + 1);
            }

            lines.add(line.toString());
        }

        return lines;
    }

    private List<String> getLinesUniqueDeltas(final Map<Integer, List<Integer>> mapFrequencies) {
        List<String> lines = new ArrayList<>();

        lines.add("Limite da Triagem,Deltas Únicos");
        mapFrequencies.entrySet().forEach(e -> {
            if (e.getKey() > 0) {
                StringBuilder word = new StringBuilder(" Palavra");
                if (e.getKey() > 1) {
                    word.append("s");
                }
                lines.add(e.getKey() + word.toString() + "," + e.getValue().size());
            }
        });
        if (mapFrequencies.containsKey(0)) {
            List<Integer> get = mapFrequencies.get(0);
            lines.add("Altura BST," + get.size());
        }
        return lines;
    }

    private List<String> getLinesDeltasFrequenciesPercentErrors(final Map<Integer, List<Integer>> mapErrors) {
        List<String> lines = new ArrayList<>();

        Map<Integer, List<Integer>> mapFrequencies = new HashMap<>();

        // sorts the lists
        StringBuilder headerBuilder = new StringBuilder(",");
        mapErrors.keySet().forEach(k -> {
            if (k != 0) {
                // skip Altura da BST: adiciona depois
                headerBuilder.append(k).append(" Palavra");
                if (k > 1) {
                    headerBuilder.append("s");
                }
                headerBuilder.append(",");
            }

            mapFrequencies.put(k, sortByFrequency(mapErrors.get(k)));
        });
        if (mapErrors.containsKey(0)) {
            headerBuilder.append("Altura BST");
        } else {
            headerBuilder.delete(headerBuilder.lastIndexOf(","), headerBuilder.lastIndexOf(",") + 1);
        }
        lines.add(headerBuilder.toString());

        // the max most frequent deltas
        int max = 3; // 1º, 2º, maxº...

        DecimalFormat dfErrors = new DecimalFormat("#.####");
        for (int i = 0; i < max; i++) {
            StringBuilder line = new StringBuilder((i + 1) + "º Delta Mais Frequente,");

            int th = i;
            mapFrequencies.entrySet().forEach(e -> {
                if (e.getKey() != 0) {
                    double percentErrors = getPercentErrors(mapErrors, mapFrequencies, e.getKey(), th);
                    line.append("\"").append(dfErrors.format(percentErrors)).append("\"").append(",");
                }
            });

            // Altura BST
            if (mapFrequencies.containsKey(0)) {
                double percentErrors = getPercentErrors(mapErrors, mapFrequencies, 0, th);
                line.append("\"").append(dfErrors.format(percentErrors)).append("\"");
            } else {
                line.delete(line.lastIndexOf(","), line.lastIndexOf(",") + 1);
            }

            lines.add(line.toString());
        }

        return lines;
    }

    private double getPercentErrors(final Map<Integer, List<Integer>> mapErrors,
        final Map<Integer, List<Integer>> mapFrequencies, final int key, final int th) {
        // the most frequent delta
        int deltaTarget = mapFrequencies.get(key).get(th);

        // how many times it appears?
        double countDelta = 0;
        List<Integer> deltas = mapErrors.getOrDefault(key, new ArrayList<>());

        for (Integer d : deltas) {
            if (d == deltaTarget) {
                countDelta++;
            }
        }

//        System.out.println("key: " + key + " deltas: " + deltas.size() + " delta target: " + deltaTarget + " th: " + th + " count errors: " + countDelta);
        return countDelta / deltas.size();
    }

    @Override
    protected void runExperimentResults(final File outputDirectory) {
        final List<Assessment> assessments = dbUtils.getCompleteAssessmentsFromDB();

        System.out.println("Running experiment with " + assessments.size() + " complete assessments");
        File parent = new File(outputDirectory, "screening-experiments-results");
        parent.mkdir();

        System.out.println("Output directory with experiments results: " + parent);

        File file = new File(parent, "results.csv");
        try (PrintWriter out = new PrintWriter(file)) {
            out.print(exportCSV(assessments));
            System.out.println("File at: " + file);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }
    }

    private List<String> getLinesConfusionMatrix(final Map<String, String[]> mapConfusionMatrix, final String[] realValues) {
        List<String> lines = new ArrayList<>();

        for (int index = 0; index <= MAX_WORDS_SCREENING; index++) {
            String key = INDICATOR_PREFIX_KEY + index + "w";

            lines.add("Confusion Matrix for " + (index > 0 ? index + "w" : "Altura BST"));

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
                    if (realValues[i].equals(POSITIVO) && predictedValues[i].equals(POSITIVO)) {
                        tp++;
                    } else if (realValues[i].equals(NEGATIVO) && predictedValues[i].equals(NEGATIVO)) {
                        tn++;
                    } else if (realValues[i].equals(NEGATIVO) && predictedValues[i].equals(POSITIVO)) {
                        fp++;
                    } else if (realValues[i].equals(POSITIVO) && predictedValues[i].equals(NEGATIVO)) {
                        fn++;
                    }
                }
            }

            lines.add(",Previsão Positivo,Previsão Negativo");
            lines.add("Real Positivo," + tp + "," + fn);
            lines.add("Real Negativo," + fp + "," + tn);

            double sensibilidade = tp / (tp + fn);
            double especificidade = tn / (tn + fp);
            double acuracia = (tp + tn) / (tp + tn + fp + fn);

            lines.add("Sensibilidade," + DF.format(sensibilidade).replaceAll(",", "."));
            lines.add("Especificidade," + DF.format(especificidade).replaceAll(",", "."));
            lines.add("Acurácia," + DF.format(acuracia).replaceAll(",", "."));
        }

        return lines;
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
