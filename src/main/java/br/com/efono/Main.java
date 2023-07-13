package br.com.efono;

import br.com.efono.db.MongoConnection;
import br.com.efono.db.MySQLConnection;
import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.SimulationInfo;
import br.com.efono.util.Defaults;
import br.com.efono.util.SimulationWordsSequence;
import br.com.efono.util.Util;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        if (args != null && args.length >= 1) {
            try {
                String configPath = args[0];

                System.out.println("Reading config file at " + configPath);
                prop.load(new FileInputStream(configPath));
            } catch (final IOException e) {
                System.out.println("Couldn't read properties file: " + e);
            }
        }

        MySQLConnection.getInstance().connect(prop);
        MongoConnection.getInstance().connect(prop);

        Defaults.TREE.init(Defaults.SORTED_WORDS);

        try {
            // continue with the application
            processSimulation();
        } catch (final SQLException ex) {
            System.out.println("Couldn't process the simulation: " + ex);
        }
    }

    private static void processSimulation() throws SQLException {
        final List<Assessment> assessments = new ArrayList<>();

        String queryAssessmentId = "SELECT DISTINCT id_avaliacao FROM avaliacaopalavra LIMIT 10;";
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

        final List<SimulationInfo> infos = new ArrayList<>();

        for (Assessment assessment : assessments) {
            // TODO: remover esses casos já na consulta, melhor desempenho
            if (assessment.getCases().size() >= Defaults.SORTED_WORDS.length / 2) {
                System.out.println(assessment);
                
                SimulationInfo hardWordsFirst = SimulationWordsSequence.runSimulation(assessment,
                        KnownCaseComparator.HardWordsFirst, 2, true);
                System.out.println(hardWordsFirst);
                infos.add(hardWordsFirst);

//            hardWordsFirst = SimulationWordsSequence.runSimulation(assessment,
//                    KnownCaseComparator.HardWordsFirst, 2, false);
//            System.out.println(hardWordsFirst);
                SimulationInfo easyWordsFirst = SimulationWordsSequence.runSimulation(assessment,
                        KnownCaseComparator.EasyWordsFirst, 2, true);
                System.out.println(easyWordsFirst);
                infos.add(easyWordsFirst);

//            easyWordsFirst = SimulationWordsSequence.runSimulation(assessment,
//                    KnownCaseComparator.EasyWordsFirst, 2, false);
//            System.out.println(easyWordsFirst);
                SimulationInfo easyHardSwitching = SimulationWordsSequence.runSimulation(assessment,
                        KnownCaseComparator.EasyHardWords, 2, true);
                System.out.println(easyHardSwitching);

                infos.add(easyHardSwitching);

                SimulationInfo binaryTreeSimulation = SimulationWordsSequence.runSimulation(assessment,
                        KnownCaseComparator.BinaryTreeComparator, 2, true);
                System.out.println(binaryTreeSimulation);
                infos.add(binaryTreeSimulation);
            }
        }
    }
}
