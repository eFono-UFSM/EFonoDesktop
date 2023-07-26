package br.com.efono.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 26.
 */
public class MySQLConnection {

    private static final String DEFAULT_SERVER = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_DATABASE = "demo_java";

    private Connection connection;

    private MySQLConnection() {
        // empty
    }

    /**
     * Connects with the MySQL client.
     *
     * @param prop Properties with MySQL credentials.
     */
    public void connect(final Properties prop) {
        // TODO: close previous connection
        String server = prop.getProperty("mysql.server", DEFAULT_SERVER);
        String port = prop.getProperty("mysql.port", DEFAULT_PORT);
        String user = prop.getProperty("mysql.user", DEFAULT_USER);
        String password = prop.getProperty("mysql.password", "");
        String database = prop.getProperty("mysql.database", DEFAULT_DATABASE);

        StringBuilder urlBuilder = new StringBuilder("jdbc:mysql://");
        urlBuilder.append(server).append(":").append(port).append("/").
                append(database).append("?user=").append(user).append("&password=").append(password);

        System.out.println("Try to connect with database at " + urlBuilder.toString());

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Trying to get connection");
            connection = DriverManager.getConnection(urlBuilder.toString());
            System.out.println("Connection is Successful to the database");
        } catch (final ClassNotFoundException | SQLException e) {
            System.out.println("Couldn't connect with the database: " + e);
            connection = null;
        }
    }

    /**
     * Executes the given query and return the result set.
     *
     * @param query Query to execute.
     * @return The result set.
     * @throws SQLException
     */
    public ResultSet executeQuery(final String query) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            System.out.println("Executing query " + query);
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        }
        System.out.println("Couldn't execute the query. Connection is null.");
        return null;
    }

    /**
     * Close connection.
     *
     * @throws java.sql.SQLException
     */
    public void close() throws SQLException {
        connection.close();
        connection = null;
    }

    /**
     * @return The instance of this class.
     */
    public static final MySQLConnection getInstance() {
        return MySQLConnectionHolder.INSTANCE;
    }

    private static class MySQLConnectionHolder {

        private static final MySQLConnection INSTANCE = new MySQLConnection();
    }

}
