package br.com.efono.util;

import br.com.efono.db.MongoConnection;
import br.com.efono.db.MySQLConnection;
import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.Phoneme;
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
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, May 11.
 */
public class DatabaseUtils {

    /**
     * Default constructor.
     *
     * @param prop Properties object with values to connect with database.
     */
    public DatabaseUtils(final Properties prop) {
        MySQLConnection.getInstance().connect(prop);
        MongoConnection.getInstance().connect(prop);
    }

    /**
     * Gets all the correct cases from the database for each given word.
     *
     * @param words Given words.
     * @return A map containing the key,value: word -> correctCases.
     */
    public Map<String, List<KnownCase>> getCorrectCasesForEachWord(final String[] words) {
        Map<String, List<KnownCase>> map = new HashMap<>();
        try {
            String query = "SELECT palavra.palavra, avaliacaopalavra.transcricao, avaliacaopalavra.correto FROM palavra, avaliacaopalavra "
                + "WHERE palavra.id_palavra = avaliacaopalavra.id_palavra AND transcricao != \"\" AND correto=1;";

            ResultSet rs = MySQLConnection.getInstance().executeQuery(query);
            while (rs.next()) {
                try {
                    KnownCase knownCase = new KnownCase(rs.getString("palavra"), rs.getString("transcricao"), rs.getBoolean("correto"));
                    if (Arrays.asList(words).contains(knownCase.getWord())) {
                        List<KnownCase> list = map.getOrDefault(knownCase.getWord(), new ArrayList<>());
                        list.add(knownCase);

                        map.put(knownCase.getWord(), list);
                    }
                } catch (final Exception e) {
                    System.out.println("Can't compute this case: " + e);
                }
            }
        } catch (final SQLException ex) {
            System.out.println("Exception " + ex);
        }
        return map;
    }

    /**
     * Gets the expected phonemes for each word that should be reproduced by a child in a phonological assessment.
     *
     * @param words The words composing the assessment.
     * @return A map with key,value: word -> expected phonemes.
     */
    public Map<String, List<Phoneme>> getTargetPhonemesForEachWord(final String[] words) {
        Map<String, List<KnownCase>> correctCases = getCorrectCasesForEachWord(words);

        Iterator<Map.Entry<String, List<KnownCase>>> it = correctCases.entrySet().iterator();

        Map<String, List<Phoneme>> map = new HashMap<>();

        while (it.hasNext()) {
            Map.Entry<String, List<KnownCase>> next = it.next();
            List<Phoneme> targetPhonemes = Util.getTargetPhonemes(next.getValue());
            map.put(next.getKey(), targetPhonemes);
        }
        return map;
    }

    /**
     * Gets a list with the completed assessments from database, which means all the assessments that contains the
     * {@link Defaults#SORTED_WORDS} transcriptions.
     *
     * @return A list with the complete assessments from DB.
     */
    public List<Assessment> getCompleteAssessmentsFromDB() {
        final List<Assessment> assessments = new ArrayList<>();
        String queryAssessmentId = "SELECT DISTINCT id_avaliacao, id_paciente FROM avaliacao";
        ResultSet idsResult;
        try {
            idsResult = MySQLConnection.getInstance().executeQuery(queryAssessmentId);

            int discardedAssessment = 0;
            while (idsResult.next()) {
                int id = idsResult.getInt("id_avaliacao");
                int patientID = idsResult.getInt("id_paciente");

                // avaliacao 15 está toda correta, vou usar essa agora para fazer a simulação sem muita complexidade
                String query = "SELECT "
                    + "avaliacaopalavra.id_avaliacao, avaliacaopalavra.id_palavra, "
                    + "avaliacaopalavra.transcricao, palavra.palavra, avaliacaopalavra.correto "
                    + "FROM avaliacaopalavra, palavra WHERE palavra.id_palavra = avaliacaopalavra.id_palavra "
                    + "AND avaliacaopalavra.transcricao <> 'NULL' AND (correto = 1 OR correto = 0) "
                    + "AND id_avaliacao = " + id;
                ResultSet rs = MySQLConnection.getInstance().executeQuery(query);

                List<Integer> wordsIDs = new ArrayList<>();

                Assessment assessment = new Assessment(id, patientID);

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

}
