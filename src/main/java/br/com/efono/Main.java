package br.com.efono;

import br.com.efono.db.MongoConnection;
import br.com.efono.db.MySQLConnection;
import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.Phoneme;
import br.com.efono.util.Util;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
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

        try {
            // continue with the application
            processSimulation();
        } catch (final SQLException ex) {
            System.out.println("Couldn't process the simulation: " + ex);
        }
    }

    private static void processSimulation() throws SQLException {

        /**
         * The first words in the array are the most difficult, according with the statistics in our database. The
         * number of wrong transcriptions of a word indicates the level of difficult.
         */
        final String[] words = new String[]{"Travesseiro", "Biblioteca", "Floresta", "Colher", "Microfone", "Vidro", "Pedra",
            "Estrela", "Magro", "Dragão", "Escrever", "Flor", "Igreja", "Plástico", "Letra", "Bruxa", "Gritar",
            "Bicicleta", "Chifre", "Chiclete", "Livro", "Presente", "Refri", "Fralda", "Fruta", "Placa", "Soprar",
            "Trem", "Cruz", "Grama", "Zebra", "Cobra", "Prato", "Brinco", "Girafa", "Jacaré", "Garfo", "Jornal",
            "Nariz", "Tesoura", "Passarinho", "Zero", "Ventilador", "Porta", "Relógio", "Nuvem", "Espelho", "Cachorro",
            "Caixa", "Beijo", "Chinelo", "Língua", "Coelho", "Pastel", "Chapéu", "Folha", "Calça", "Barriga", "Sofá",
            "Casa", "Bolsa", "Cavalo", "Gato", "Mesa", "Galinha", "Lápis", "Vaca", "Cabelo", "Sapato", "Sapo", "Fogo",
            "Caminhão", "Faca", "Rabo", "Dente", "Tênis", "Terra", "Navio", "Dado", "Batom", "Cama", "Dedo", "Bebê",
            "Anel"};

        if (1 > 0) {
            return;
        }

        // avaliacao 15 está toda correta, vou usar essa agora para fazer a simulação sem muita complexidade
        String query = "SELECT "
                + "avaliacaopalavra.id_avaliacao, "
                + "avaliacaopalavra.transcricao, palavra.palavra, avaliacaopalavra.correto "
                + "FROM avaliacaopalavra, palavra WHERE palavra.id_palavra = avaliacaopalavra.id_palavra "
                + "AND avaliacaopalavra.transcricao <> 'NULL' AND (correto = 1 OR correto = 0) AND id_avaliacao = 15";
        ResultSet rs = MySQLConnection.getInstance().executeQuery(query);

        Assessment assessment = new Assessment();

        int lines = 0;
        while (rs.next()) {
            //Display values
            System.out.print("id_avaliacao: " + rs.getInt("id_avaliacao"));
            System.out.print(", transcricao: " + rs.getString("transcricao"));
            System.out.print(", palavra: " + rs.getString("palavra"));
            System.out.println(", correto: " + rs.getBoolean("correto"));

            try {
                KnownCase knownCase = new KnownCase(rs.getString("palavra"),
                        rs.getString("transcricao"), rs.getBoolean("correto"));

                knownCase.putPhonemes(Util.getConsonantPhonemes(knownCase.getRepresentation()));

                assessment.addCase(knownCase);

                System.out.println(knownCase);
            } catch (final IllegalArgumentException | SQLException e) {
                System.out.println("Exception creating known case: " + e);
            }

            lines++;
        }
        System.out.println("lines read: " + lines + " cases in the assessment: " + assessment.getCases().size());
        if (lines != 84 && lines != assessment.getCases().size()) {
            System.out.println("Invalid assessment to do the simulation. All words are required.");
        } else {
            Map<Phoneme, Integer> mapCounter = new HashMap<>();

            assessment.getCases().forEach(c -> {
                System.out.println("\tcase of " + c.getWord());
                c.getPhonemes().forEach(p -> {
                    int count = 1;
                    if (mapCounter.containsKey(p)) {
                        count = mapCounter.get(p) + 1;
                    }

                    System.out.println(p + " -> " + count);
                    mapCounter.put(p, count);
                });
            });
        }
    }
}
