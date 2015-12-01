import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;

public class FsnNeo4jConfig {
    public static GraphDatabaseService createEmbeddedGraphDatabaseService() {
        // GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(new File("C:/Users/Juan/Documents/Neo4j/default.graphdb"));
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(new File("./db"));
        return graphDatabaseService;
    }
}
