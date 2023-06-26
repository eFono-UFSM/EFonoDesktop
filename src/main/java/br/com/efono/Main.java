package br.com.efono;

import br.com.efono.db.MongoConnection;
import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.Phoneme;
import br.com.efono.util.Util;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, May 28.
 */
public class Main {

    private static final String DEFAULT_SERVER = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_DATABASE = "demo_java";

    /**
     * Show application.
     *
     * @param args Arguments given: properties-file path.
     */
    public static void main(final String[] args) {
        System.out.println("Arguments received: " + Arrays.toString(args));

        Properties prop = new Properties();

        if (args != null && args.length >= 1) {
            try {
                String configPath = args[0];

                System.out.println("Reading config file at " + configPath);
                prop.load(new FileInputStream(configPath));
            } catch (final IOException e) {
                System.out.println("Couldn't read properties file: " + e);
            }
        }

        String server = prop.getProperty("mysql.server", DEFAULT_SERVER);
        String port = prop.getProperty("mysql.port", DEFAULT_PORT);
        String user = prop.getProperty("mysql.user", DEFAULT_USER);
        String password = prop.getProperty("mysql.password", "");
        String database = prop.getProperty("mysql.database", DEFAULT_DATABASE);

        // TODO: separar execução e processamento de queries
        StringBuilder urlBuilder = new StringBuilder("jdbc:mysql://");
        urlBuilder.append(server).append(":").append(port).append("/").
                append(database).append("?user=").append(user).append("&password=").append(password);

        Connection connection = null;
        System.out.println("Try to connect with database at " + urlBuilder.toString());
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Trying to get connection");
            connection = DriverManager.getConnection(urlBuilder.toString());
            System.out.println("Connection is Successful to the database");
        } catch (final ClassNotFoundException | SQLException e) {
            System.out.println("Couldn't connect with the database: " + e);
        }

        try {
            // continue with the application
            processSimulation(connection);
        } catch (final SQLException ex) {
            System.out.println("Couldn't process the simulation: " + ex);
        }
        
        MongoConnection.getInstance().connect(prop);
    }

    private static void processSimulation(final Connection connection) throws SQLException {
        if (connection != null) {
            String query = "SELECT "
                    + "avaliacaopalavra.id_avaliacao, "
                    + "avaliacaopalavra.transcricao, palavra.palavra, avaliacaopalavra.correto "
                    + "FROM avaliacaopalavra, palavra WHERE palavra.id_palavra = avaliacaopalavra.id_palavra "
                    + "AND avaliacaopalavra.transcricao <> 'NULL' AND (correto = 1 OR correto = 0) AND id_avaliacao = 1";

            System.out.println("query: " + query);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

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
        } else {
            System.out.println("Invalid connection given to process simulation.");
        }
    }
}
