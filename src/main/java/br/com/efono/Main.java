package br.com.efono;

import br.com.efono.db.MongoConnection;
import br.com.efono.db.MySQLConnection;
import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.SimulationInfo;
import br.com.efono.model.Statistics;
import br.com.efono.util.Defaults;
import br.com.efono.util.SimulationWordsSequence;
import br.com.efono.util.Util;
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

        Defaults.TREE.init(Defaults.SORTED_WORDS);

        File output = null;
        if (parent != null && !parent.isBlank()) {
            output = new File(parent);
        }

        try {
            // continue with the application
            processSimulation(output);
        } catch (final SQLException ex) {
            System.out.println("Couldn't process the simulation: " + ex);
        }
    }

    private static void processSimulation(final File outputDirectory) throws SQLException {
        final List<Assessment> assessments = new ArrayList<>();

        String queryAssessmentId = "SELECT DISTINCT id_avaliacao FROM avaliacaopalavra";
        ResultSet idsResult = MySQLConnection.getInstance().executeQuery(queryAssessmentId);
        while (idsResult.next()) {
            int id = idsResult.getInt("id_avaliacao");

            // avaliacao 15 está toda correta, vou usar essa agora para fazer a simulação sem muita complexidade
            String query = "SELECT "
                    + "avaliacaopalavra.id_avaliacao, "
                    + "avaliacaopalavra.transcricao, palavra.palavra, avaliacaopalavra.correto "
                    + "FROM avaliacaopalavra, palavra WHERE palavra.id_palavra = avaliacaopalavra.id_palavra "
                    + "AND avaliacaopalavra.transcricao <> 'NULL' AND (correto = 1 OR correto = 0) "
                    + "AND id_avaliacao = " + id;
            ResultSet rs = MySQLConnection.getInstance().executeQuery(query);
            Assessment assessment = new Assessment(id);

            while (rs.next()) {
                try {
                    // TODO: teste Exception creating known case: java.lang.IllegalArgumentException: Non identified phonemes are now allowed in a Known Case.
                    // nao pode adicionar esse caso na lista da avaliacao: ignora oq vem do banco
                    KnownCase knownCase = new KnownCase(rs.getString("palavra"),
                            rs.getString("transcricao"), rs.getBoolean("correto"));

                    knownCase.putPhonemes(Util.getConsonantPhonemes(knownCase.getRepresentation()));

                    assessment.addCase(knownCase);
                } catch (final IllegalArgumentException | SQLException e) {
                    System.out.println("Exception creating known case: " + e);
                }
            }
            assessments.add(assessment);
        }

        final Map<KnownCaseComparator, Statistics> m = new HashMap<>();
        m.put(KnownCaseComparator.HardWordsFirst, new Statistics(KnownCaseComparator.HardWordsFirst));
        m.put(KnownCaseComparator.EasyWordsFirst, new Statistics(KnownCaseComparator.EasyWordsFirst));
        m.put(KnownCaseComparator.EasyHardWords, new Statistics(KnownCaseComparator.EasyHardWords));
        m.put(KnownCaseComparator.BinaryTreeComparator, new Statistics(KnownCaseComparator.BinaryTreeComparator));

        for (Assessment assessment : assessments) {
            // TODO: remover esses casos já na consulta, melhor desempenho
            if (assessment.getCases().size() >= Defaults.SORTED_WORDS.length / 2) {
//                System.out.println(assessment);

                SimulationInfo hardWordsFirst = SimulationWordsSequence.runSimulation(assessment,
                        KnownCaseComparator.HardWordsFirst, 2, true);
//                System.out.println(hardWordsFirst);

                m.get(KnownCaseComparator.HardWordsFirst).extractStatistics(hardWordsFirst);

//            hardWordsFirst = SimulationWordsSequence.runSimulation(assessment,
//                    KnownCaseComparator.HardWordsFirst, 2, false);
//            System.out.println(hardWordsFirst);
                SimulationInfo easyWordsFirst = SimulationWordsSequence.runSimulation(assessment,
                        KnownCaseComparator.EasyWordsFirst, 2, true);
//                System.out.println(easyWordsFirst);

                m.get(KnownCaseComparator.EasyWordsFirst).extractStatistics(easyWordsFirst);

//            easyWordsFirst = SimulationWordsSequence.runSimulation(assessment,
//                    KnownCaseComparator.EasyWordsFirst, 2, false);
//            System.out.println(easyWordsFirst);
                SimulationInfo easyHardSwitching = SimulationWordsSequence.runSimulation(assessment,
                        KnownCaseComparator.EasyHardWords, 2, true);
//                System.out.println(easyHardSwitching);

                m.get(KnownCaseComparator.EasyHardWords).extractStatistics(easyHardSwitching);

                SimulationInfo binaryTreeSimulation = SimulationWordsSequence.runSimulation(assessment,
                        KnownCaseComparator.BinaryTreeComparator, 2, true);
//                System.out.println(binaryTreeSimulation);

                m.get(KnownCaseComparator.BinaryTreeComparator).extractStatistics(binaryTreeSimulation);
            }
        }

        File parent = new File(outputDirectory, "output-simulations");
        parent.mkdir();

        System.out.println("Output directory with simulation statistics: " + parent);
        Iterator<Map.Entry<KnownCaseComparator, Statistics>> it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<KnownCaseComparator, Statistics> next = it.next();

            File file = new File(parent, next.getKey().name() + "-counter.csv");
            try (PrintWriter out = new PrintWriter(file)) {
                out.print(next.getValue().exportCSV());
                System.out.println("File at: " + file);
            } catch (final FileNotFoundException ex) {
                System.out.println("Couldn't write into file: " + ex);
            }

//            System.out.println(next.getValue());
        }
    }
}
