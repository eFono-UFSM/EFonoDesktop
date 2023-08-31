package br.com.efono;

import br.com.efono.db.MongoConnection;
import br.com.efono.db.MySQLConnection;
import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationConsonantClustersInfo;
import br.com.efono.model.SimulationInfo;
import br.com.efono.model.Statistics;
import br.com.efono.tree.BinaryTreePrinter;
import br.com.efono.util.Defaults;
import br.com.efono.util.SimulationConsonantClusters;
import br.com.efono.util.SimulationWordsSequence;
import br.com.efono.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.bson.Document;

/**
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, May 28.
 */
public class Main {

    /**
     * Show application.
     *
     * @param args Arguments given: properties-file path.
     */
    public static void main(final String[] args) {
        System.out.println("Arguments received: " + Arrays.toString(args));

        final Properties prop = new Properties();

        String parent = "";
        if (args != null && args.length >= 1) {
            try {
                String configPath = args[0];

                parent = new File(configPath).getParent();
                System.out.println("Reading config file at " + configPath);
                prop.load(new FileInputStream(configPath));
            } catch (final IOException e) {
                System.out.println("Couldn't read properties file: " + e);
            }
        }

        MySQLConnection.getInstance().connect(prop);
        MongoConnection.getInstance().connect(prop);

        Map<String, Object> filters = new HashMap<>();
        filters.put("correct", true);

        ObjectMapper objectMapper = new ObjectMapper();
        Arrays.asList(Defaults.SORTED_WORDS).forEach(w -> {
            filters.put("word", w);
            FindIterable<Document> result = MongoConnection.getInstance().executeQuery("knowncases", filters,
                    null);

            // correct known cases for word <w>
            final List<KnownCase> correctCases = new ArrayList<>();

            if (result != null) {
                result.forEach(doc -> {
                    try {
                        KnownCase val = objectMapper.readValue(doc.toJson(), new TypeReference<KnownCase>() {
                        });
                        correctCases.add(val);
                    } catch (final JsonProcessingException ex) {
                        System.out.println("Error while parsing doc " + doc.toJson() + ":\n " + ex);
                    }
                });
            }

            // building the target phonemes for the word
            Defaults.TARGET_PHONEMES.put(w, Util.getTargetPhonemes(correctCases));
        });

        System.out.println("Target phonemes for each word: ");
        Iterator<Map.Entry<String, List<Phoneme>>> iterator = Defaults.TARGET_PHONEMES.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, List<Phoneme>> next = iterator.next();
            System.out.println(next.getKey() + " -> " + next.getValue());
        }

        Defaults.TREE.init(Defaults.SORTED_WORDS);

        System.out.println("-------------------");

        System.out.println(
                "@startuml\n"
                + "top to bottom direction");

        BinaryTreePrinter.printUML(Defaults.TREE.getRoot());

        System.out.println("@enduml");
        System.out.println("\n-------------------");

        // TODO: testar esse código
        Arrays.asList(Defaults.SORTED_WORDS).forEach(w -> {
            List<Phoneme> phonemes = Defaults.TARGET_PHONEMES.get(w);

            List<String> similarWords = new ArrayList<>();

            List<Phoneme> countPhonemes = new ArrayList<>();

            // vai pegar as mais difíceis primeiro
            for (int i = Defaults.SORTED_WORDS.length - 1; i >= 0; i--) {
                String key = Defaults.SORTED_WORDS[i];

                if (!key.equals(w)) {
                    List<Phoneme> value = Defaults.TARGET_PHONEMES.get(key);

                    for (Phoneme p : phonemes) {
                        if (value.contains(p) && !countPhonemes.contains(p)) {
                            countPhonemes.add(p);

                            if (!similarWords.contains(key)) {
                                similarWords.add(key);
                            }
                        }
                    }
                }
            }

            Defaults.SIMILAR_WORDS.put(w, similarWords);
            // TODO: não preciso de todas as palavras, só de 4 ou 5 pra testar os mesmos fonemas dela
            // mesmo assim, se cada palavra tiver 4 fonemas, vão ser 24 palavras ou 28 (6x4, 7x4). Será que melhora a precisão do PCC-R?
            System.out.println(w + "->SimilarWords[" + similarWords.size() + "]: " + similarWords);
        });

        File output = null;
        if (parent != null && !parent.isBlank()) {
            output = new File(parent);
        }

        if (1 > 0) {
            analysisConsonantClusters(output);
            return;
        }

        try {
            // continue with the application
            processSimulation(output);
        } catch (final SQLException ex) {
            System.out.println("Couldn't process the simulation: " + ex);
        }
    }

    private static List<Assessment> getAssessmentsFromDB() {
        final List<Assessment> assessments = new ArrayList<>();
        String queryAssessmentId = "SELECT DISTINCT id_avaliacao FROM avaliacaopalavra";
        ResultSet idsResult;
        try {
            idsResult = MySQLConnection.getInstance().executeQuery(queryAssessmentId);

            int discardedAssessment = 0;
            while (idsResult.next()) {
                int id = idsResult.getInt("id_avaliacao");

                // avaliacao 15 está toda correta, vou usar essa agora para fazer a simulação sem muita complexidade
                String query = "SELECT "
                        + "avaliacaopalavra.id_avaliacao, avaliacaopalavra.id_palavra, "
                        + "avaliacaopalavra.transcricao, palavra.palavra, avaliacaopalavra.correto "
                        + "FROM avaliacaopalavra, palavra WHERE palavra.id_palavra = avaliacaopalavra.id_palavra "
                        + "AND avaliacaopalavra.transcricao <> 'NULL' AND (correto = 1 OR correto = 0) "
                        + "AND id_avaliacao = " + id;
                ResultSet rs = MySQLConnection.getInstance().executeQuery(query);

                List<Integer> wordsIDs = new ArrayList<>();

                Assessment assessment = new Assessment(id);

                while (rs.next()) {
                    if (!wordsIDs.contains(rs.getInt("id_palavra"))) {
                        wordsIDs.add(rs.getInt("id_palavra"));
                        try {
                            KnownCase knownCase = new KnownCase(rs.getString("palavra"),
                                    rs.getString("transcricao"), rs.getBoolean("correto"));

                            knownCase.putPhonemes(Util.getConsonantPhonemes(knownCase.getRepresentation()));

                            assessment.addCase(knownCase);
                        } catch (final IllegalArgumentException | SQLException e) {
                            System.out.println("Exception creating known case: " + e);
                        }
                    } else {
                        System.out.println("Ignoring case with repeated word " + rs.getInt("id_palavra") + " in assessment " + id);
                    }
                }
                if (assessment.getCases().size() == Defaults.SORTED_WORDS.length) {
                    assessments.add(assessment);
                } else {
                    discardedAssessment++;
                }
            }
            System.out.println(discardedAssessment + " assessments were discarded because they have less than " + Defaults.SORTED_WORDS.length + " valid cases");
        } catch (final SQLException ex) {
            System.out.println("Exception while getting assessments from db: " + ex);
        }
        return assessments;
    }

    private static void analysisConsonantClusters(final File outputDirectory) {
        System.out.println("--------------------------------------");
        System.out.println("Analyzing Consonant Clusters");
        System.out.println("--------------------------------------");

        List<Assessment> assessments = getAssessmentsFromDB();
        System.out.println("Running simulation with " + assessments.size() + " complete assessments");
        File parent = new File(outputDirectory, "SAC-2024-results");
        parent.mkdir();

        System.out.println("Output directory with simulation statistics: " + parent);

        for (int i = 0; i <= 1; i++) {
            boolean considerOnlyClustersInTargetWords = (i == 0);
            
            System.out.println("Consider only clusters in target words: " + considerOnlyClustersInTargetWords);
            final StringBuilder builder = new StringBuilder();
            assessments.forEach(a -> {
                SimulationConsonantClustersInfo run = SimulationConsonantClusters.run(a, KnownCaseComparator.EasyWordsFirst,
                        considerOnlyClustersInTargetWords);
                builder.append(run.exportCSV(builder.toString().isBlank()));
            });
            
            System.out.println("-----------------------------------------------------------------------");

            File file = new File(parent, "results-considerOnlyClustersInTargetWords-" + considerOnlyClustersInTargetWords + ".csv");
            try (PrintWriter out = new PrintWriter(file)) {
                out.print(builder.toString());
                System.out.println("File at: " + file);
            } catch (final FileNotFoundException ex) {
                System.out.println("Couldn't write into file: " + ex);
            }
        }
    }

    private static void processSimulation(final File outputDirectory) throws SQLException {
        String queryWordsDifficult = "select palavra.palavra, count(avaliacaopalavra.id_palavra) AS errors FROM palavra, avaliacaopalavra WHERE palavra.id_palavra = avaliacaopalavra.id_palavra AND correto = 0 GROUP BY palavra";
        ResultSet result = MySQLConnection.getInstance().executeQuery(queryWordsDifficult);

        StringBuilder str = new StringBuilder("word,wordSize,errors\n");

        Map<Integer, Integer> mapSizeErrors = new HashMap<>();
        Map<Integer, Integer> mapSizes = new HashMap<>();

        while (result.next()) {
            String word = result.getString("palavra");

            if (!mapSizes.containsKey(word.length())) {
                mapSizes.put(word.length(), 1);
            } else {
                mapSizes.put(word.length(), mapSizes.get(word.length()) + 1);
            }

            if (!mapSizeErrors.containsKey(word.length())) {
                mapSizeErrors.put(word.length(), 0);
            }

            mapSizeErrors.put(word.length(), mapSizeErrors.get(word.length()) + result.getInt("errors"));

            String tamanho = String.valueOf(word.length());
            String errors = String.valueOf(result.getInt("errors"));

            str.append(word).append(",").append(tamanho).append(",").append(errors).append("\n");
        }

        File parent = new File(outputDirectory, "output-simulations");
        parent.mkdir();

        System.out.println("Output directory with simulation statistics: " + parent);

        File fileWordsDifficult = new File(parent, "wordsDifficult.csv");
        try (PrintWriter out = new PrintWriter(fileWordsDifficult)) {
            out.print(str.toString());
            System.out.println("File at: " + fileWordsDifficult);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }

        str = new StringBuilder("wordSize,errors\n");
        Iterator<Map.Entry<Integer, Integer>> iterator = mapSizeErrors.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> next = iterator.next();

            // valores normalizados: divide pela quantidade de palavras que tem o mesmo tamanho
            int errors = next.getValue() / mapSizes.get(next.getKey());

            str.append(next.getKey()).append(",").append(errors).append("\n");
        }

        File fileWordsSizeErrors = new File(parent, "wordsSizeErrors.csv");
        try (PrintWriter out = new PrintWriter(fileWordsSizeErrors)) {
            out.print(str.toString());
            System.out.println("File at: " + fileWordsSizeErrors);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }

        final List<Assessment> assessments = getAssessmentsFromDB();

        final Map<KnownCaseComparator, Statistics> mapPhoneticInventory = new HashMap<>();
        mapPhoneticInventory.put(KnownCaseComparator.HardWordsFirst, new Statistics(KnownCaseComparator.HardWordsFirst));
        mapPhoneticInventory.put(KnownCaseComparator.EasyWordsFirst, new Statistics(KnownCaseComparator.EasyWordsFirst));
        mapPhoneticInventory.put(KnownCaseComparator.EasyHardWords, new Statistics(KnownCaseComparator.EasyHardWords));
        mapPhoneticInventory.put(KnownCaseComparator.BinaryTreeComparator, new Statistics(KnownCaseComparator.BinaryTreeComparator));

        final Map<KnownCaseComparator, Statistics> mapPhoneticInventoryNoSplitClusters = new HashMap<>();
        mapPhoneticInventoryNoSplitClusters.put(KnownCaseComparator.HardWordsFirst, new Statistics(KnownCaseComparator.HardWordsFirst));
        mapPhoneticInventoryNoSplitClusters.put(KnownCaseComparator.EasyWordsFirst, new Statistics(KnownCaseComparator.EasyWordsFirst));
        mapPhoneticInventoryNoSplitClusters.put(KnownCaseComparator.EasyHardWords, new Statistics(KnownCaseComparator.EasyHardWords));
        mapPhoneticInventoryNoSplitClusters.put(KnownCaseComparator.BinaryTreeComparator, new Statistics(KnownCaseComparator.BinaryTreeComparator));

        final Map<KnownCaseComparator, Statistics> mapPCCR = new HashMap<>();
        mapPCCR.put(KnownCaseComparator.HardWordsFirst, new Statistics(KnownCaseComparator.HardWordsFirst));
        mapPCCR.put(KnownCaseComparator.EasyWordsFirst, new Statistics(KnownCaseComparator.EasyWordsFirst));
        mapPCCR.put(KnownCaseComparator.EasyHardWords, new Statistics(KnownCaseComparator.EasyHardWords));
        mapPCCR.put(KnownCaseComparator.BinaryTreeComparator, new Statistics(KnownCaseComparator.BinaryTreeComparator));

        Statistics statisticsExtended = new Statistics(KnownCaseComparator.BinaryTreeComparatorExtended);
        Statistics statisticsExtendedNoSplit = new Statistics(KnownCaseComparator.BinaryTreeComparatorExtended);

        System.out.println("Running simulation with " + assessments.size() + " complete assessments");
        for (Assessment assessment : assessments) {
            SimulationInfo hardWordsFirstPhonInv = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.HardWordsFirst, 2, true, true);
            SimulationInfo hardWordsFirstPhonInvNoSplit = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.HardWordsFirst, 2, false, true);
            SimulationInfo hardWordsFirstPCCR = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.HardWordsFirst, 2, true, false);

            mapPhoneticInventory.get(KnownCaseComparator.HardWordsFirst).extractStatistics(hardWordsFirstPhonInv);
            mapPhoneticInventoryNoSplitClusters.get(KnownCaseComparator.HardWordsFirst).extractStatistics(hardWordsFirstPhonInvNoSplit);
            mapPCCR.get(KnownCaseComparator.HardWordsFirst).extractStatistics(hardWordsFirstPCCR);

            SimulationInfo easyWordsFirstPhonInv = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.EasyWordsFirst, 2, true, true);
            SimulationInfo easyWordsFirstPhonInvNoSplit = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.EasyWordsFirst, 2, false, true);
            SimulationInfo easyWordsFirstPCCR = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.EasyWordsFirst, 2, true, false);

            mapPhoneticInventory.get(KnownCaseComparator.EasyWordsFirst).extractStatistics(easyWordsFirstPhonInv);
            mapPhoneticInventoryNoSplitClusters.get(KnownCaseComparator.EasyWordsFirst).extractStatistics(easyWordsFirstPhonInvNoSplit);
            mapPCCR.get(KnownCaseComparator.EasyWordsFirst).extractStatistics(easyWordsFirstPCCR);

            SimulationInfo easyHardSwitchingPhonInv = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.EasyHardWords, 2, true, true);
            SimulationInfo easyHardSwitchingPhonInvNoSplit = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.EasyHardWords, 2, false, true);
            SimulationInfo easyHardSwitchingPCCR = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.EasyHardWords, 2, true, false);

            mapPhoneticInventory.get(KnownCaseComparator.EasyHardWords).extractStatistics(easyHardSwitchingPhonInv);
            mapPhoneticInventoryNoSplitClusters.get(KnownCaseComparator.EasyHardWords).extractStatistics(easyHardSwitchingPhonInvNoSplit);
            mapPCCR.get(KnownCaseComparator.EasyHardWords).extractStatistics(easyHardSwitchingPCCR);

            SimulationInfo binaryTreeSimulationPhonInv = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.BinaryTreeComparator, 2, true, true);
            SimulationInfo binaryTreeSimulationPhonInvNoSplit = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.BinaryTreeComparator, 2, false, true);
            SimulationInfo binaryTreeSimulationPCCR = SimulationWordsSequence.runSimulation(assessment,
                    KnownCaseComparator.BinaryTreeComparator, 2, true, false);

            mapPhoneticInventory.get(KnownCaseComparator.BinaryTreeComparator).extractStatistics(binaryTreeSimulationPhonInv);
            mapPhoneticInventoryNoSplitClusters.get(KnownCaseComparator.BinaryTreeComparator).extractStatistics(binaryTreeSimulationPhonInvNoSplit);
            mapPCCR.get(KnownCaseComparator.BinaryTreeComparator).extractStatistics(binaryTreeSimulationPCCR);

            // blocos de palavras
            SimulationInfo binaryTreeExtended = SimulationWordsSequence.runSimulation2(assessment,
                    KnownCaseComparator.BinaryTreeComparator, 2, true);
            SimulationInfo binaryTreeExtendedNoSplit = SimulationWordsSequence.runSimulation2(assessment,
                    KnownCaseComparator.BinaryTreeComparator, 2, false);

            statisticsExtended.extractStatistics(binaryTreeExtended);
            statisticsExtendedNoSplit.extractStatistics(binaryTreeExtendedNoSplit);
        }

        Iterator<Map.Entry<KnownCaseComparator, Statistics>> it = mapPhoneticInventory.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<KnownCaseComparator, Statistics> next = it.next();

            File file = new File(parent, next.getKey().name() + "-Inv.csv");
            try (PrintWriter out = new PrintWriter(file)) {
                out.print(next.getValue().exportAllCSV());
                System.out.println("File at: " + file);
            } catch (final FileNotFoundException ex) {
                System.out.println("Couldn't write into file: " + ex);
            }
        }

        it = mapPhoneticInventoryNoSplitClusters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<KnownCaseComparator, Statistics> next = it.next();

            File file = new File(parent, next.getKey().name() + "-Inv-noSplit.csv");
            try (PrintWriter out = new PrintWriter(file)) {
                out.print(next.getValue().exportAllCSV());
                System.out.println("File at: " + file);
            } catch (final FileNotFoundException ex) {
                System.out.println("Couldn't write into file: " + ex);
            }
        }

        // TODO: acho melhor usar esses arquivos aqui: fonemas foram testados no mínimo 2x com essas palavras
        // os arquivos com PhoneticInventory significa os fonemas que foram acertados no mínimo 2x para serem considerados no inventário fonético
        it = mapPCCR.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<KnownCaseComparator, Statistics> next = it.next();

            File file = new File(parent, next.getKey().name() + "-Test.csv");
            try (PrintWriter out = new PrintWriter(file)) {
                out.print(next.getValue().exportAllCSV());
                System.out.println("File at: " + file);
            } catch (final FileNotFoundException ex) {
                System.out.println("Couldn't write into file: " + ex);
            }
        }

        // TODO: esse arquivo eu nem to usando, e o metodo runSimulation2 precisa ser melhorado
        File fileBinaryExtended = new File(parent, "BinaryTreeComparatorExtended.csv");
        try (PrintWriter out = new PrintWriter(fileBinaryExtended)) {
            out.print(statisticsExtended.exportAllCSV());
            System.out.println("File at: " + fileBinaryExtended);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }

        File fileBinaryExtendedNoSplit = new File(parent, "BinaryTreeComparatorExtended-noSplit.csv");
        try (PrintWriter out = new PrintWriter(fileBinaryExtendedNoSplit)) {
            out.print(statisticsExtendedNoSplit.exportAllCSV());
            System.out.println("File at: " + fileBinaryExtendedNoSplit);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }

        File filePCCR_Regions = new File(parent, "PCCR-BinaryTreeComparator.csv");
        try (PrintWriter out = new PrintWriter(filePCCR_Regions)) {
            out.print(mapPCCR.get(KnownCaseComparator.BinaryTreeComparator).exportPCCR_CSV(Defaults.TREE));
            System.out.println("File at: " + filePCCR_Regions);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }

        List<Statistics> listAll = new ArrayList<>(mapPhoneticInventory.values());
        File fileWordsFrequencyAll = new File(parent, "AllScenarios-wordsFrequency.csv");
        try (PrintWriter out = new PrintWriter(fileWordsFrequencyAll)) {
            out.print(Statistics.exportAllWordsFrequencyCSV(listAll));
            System.out.println("File at: " + fileWordsFrequencyAll);
        } catch (final FileNotFoundException ex) {
            System.out.println("Couldn't write into file: " + ex);
        }
    }
}
