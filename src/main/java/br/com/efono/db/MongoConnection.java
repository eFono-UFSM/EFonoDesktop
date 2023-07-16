package br.com.efono.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 26.
 */
public class MongoConnection {

    private static final String DEFAULT_SERVER = "localhost";
    private static final String DEFAULT_PORT = "27017";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_DATABASE = "demo_java";

    private MongoClient mongoClient;
    private MongoDatabase database;

    private MongoConnection() {
        // empty
    }

    /**
     * Connects with the mongo client.
     *
     * @param prop Properties with MongoDB credentials.
     */
    public void connect(final Properties prop) {
        // TODO: close previous connection
        String server = prop.getProperty("mongodb.server", DEFAULT_SERVER);
        String port = prop.getProperty("mongodb.port", DEFAULT_PORT);
        String user = prop.getProperty("mongodb.user", DEFAULT_USER);
        String password = prop.getProperty("mongodb.password", "");
        String db = prop.getProperty("mongodb.database", DEFAULT_DATABASE);

        final StringBuilder builder = new StringBuilder("mongodb://");
        builder.append(user).append(":").append(password).append("@")
                .append(server).append(":").append(port);

        String uri = builder.toString();

        System.out.println("Connecting with MongoDB server...");
        ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
        System.out.println("Building settings");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi)
                .build();

        System.out.println("Creating connection...");
        mongoClient = MongoClients.create(settings);
        System.out.println("Getting database...");
        database = mongoClient.getDatabase(db);

        try {
            System.out.println("Testing MongoDB connection...");
            // Send a ping to confirm a successful connection
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);
            System.out.println("Successfully connected to MongoDB.");
        } catch (final MongoException me) {
            System.out.println(me);
            mongoClient = null;
            database = null;
        }
    }

    /**
     * Executes a query in mongo db.
     *
     * @param collectionName The collection name.
     * @param filters The filters (combined with AND only: filter1 AND filter2 AND ...).
     * @param fieldsToReturn Fields to return in the query result.
     */
    public void executeQuery(final String collectionName, final Map<String, Object> filters, final List<String> fieldsToReturn) {
        if (database != null && collectionName != null) {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            List<Bson> listFilters = new ArrayList<>();
            List<Bson> listProjections = new ArrayList<>();

            if (filters != null) {
                filters.forEach((k, v) -> {
                    listFilters.add(Filters.eq(k, v));
                });
            }
            if (fieldsToReturn != null && !fieldsToReturn.isEmpty()) {
                fieldsToReturn.forEach((p) -> {
                    listProjections.add(Projections.include(p));
                });
            }

            // exclude _id from the result: we don't need it here
            listProjections.add(Projections.excludeId());

            Bson filter = Filters.and(listFilters);
            Bson projection = Projections.fields(listProjections);

            collection.find(filter).projection(projection).forEach(doc -> System.out.println(doc.toJson()));
        }
    }

    /**
     * Close connection.
     */
    public void close() {
        mongoClient.close();
        database = null;
        mongoClient = null;
    }

    /**
     * @return The instance of this class.
     */
    public static final MongoConnection getInstance() {
        return MongoConnectionHolder.INSTANCE;
    }

    private static class MongoConnectionHolder {

        private static final MongoConnection INSTANCE = new MongoConnection();
    }

}
