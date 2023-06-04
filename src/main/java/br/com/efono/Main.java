package br.com.efono;

import br.com.efono.util.Util;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import org.apache.commons.lang3.StringEscapeUtils;

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

        if (1 > 0) {
            // TODO: ainda esta salvando com unicode: {"key1":"[\u2019lu.vẽj̃]","key2":"olaMundinho"}
            Util.createJSON(StringEscapeUtils.unescapeJava("[’lu.vẽj̃]"), "olaMundinho");
            return;
        }

        Properties prop = new Properties();

        String server = DEFAULT_SERVER;
        String port = DEFAULT_PORT;
        String database = DEFAULT_DATABASE;
        String user = DEFAULT_USER;
        String password = "";

        if (args != null && args.length >= 1) {
            try {
                String configPath = args[0];

                System.out.println("Reading config file at " + configPath);
                prop.load(new FileInputStream(configPath));
                server = prop.getProperty("mysql.server", "");
                port = prop.getProperty("mysql.port", "");
                user = prop.getProperty("mysql.user", "");
                password = prop.getProperty("mysql.password", "");
                database = prop.getProperty("mysql.database", "");
            } catch (final IOException e) {
                System.out.println("Couldn't read properties file: " + e);
            }
        }

        StringBuilder urlBuilder = new StringBuilder("jdbc:mysql://");
        urlBuilder.append(server).append(":").append(port).append("/").append(database);

        Connection connection = null;
        System.out.println("Try to connect with database at " + urlBuilder.toString());
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(urlBuilder.toString(), user, password);
            System.out.println("Connection is Successful to the database");
            //            String query = "Insert into student(id,name) values(NULL,'ram')";
            //            Statement statement = connection.createStatement();
            //            statement.execute(query);
        } catch (final ClassNotFoundException | SQLException e) {
            System.out.println("Couldn't connect with the database: " + e);
        }

        // continue with the application
        if (connection != null) {
            // TODO
        }

    }
}
