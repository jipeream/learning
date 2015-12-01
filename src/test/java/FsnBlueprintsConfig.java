import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.structure.Graph;

public class FsnBlueprintsConfig {
    public static Graph getNeo4LocalGraph() {
        Graph graph = Neo4jGraph.open("./db");
        return graph;
    }

}
