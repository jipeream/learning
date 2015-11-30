package com.fs.fsnews.main;

import com.fs.fsnews.config.FsnBlueprintsConfig;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class FsnBlueprintsMain {
    public static void main(String[] args) throws Exception {
        Graph graph = FsnBlueprintsConfig.getNeo4LocalGraph();
        //
        try (Transaction tx = graph.tx()) {
            Vertex marko = graph.addVertex(T.label, "person", T.key, "marko", "name", "marko", "age", 29);
            Vertex vadas = graph.addVertex(T.label, "person", T.key, "vadas", "name", "vadas", "age", 27);
            Vertex lop = graph.addVertex(T.label, "software", T.key, "lop", "name", "lop", "lang", "java");
            Vertex josh = graph.addVertex(T.label, "person", T.key, "josh", "name", "josh", "age", 32);
            Vertex ripple = graph.addVertex(T.label, "software", T.key, "ripple", "name", "ripple", "lang", "java");
            Vertex peter = graph.addVertex(T.label, "person", T.key, "peter", "name", "peter", "age", 35);
            marko.addEdge("knows", vadas, "weight", 0.5f);
            marko.addEdge("knows", josh, "weight", 1.0f);
            marko.addEdge("created", lop, "weight", 0.4f);
            josh.addEdge("created", ripple, "weight", 1.0f);
            josh.addEdge("created", lop, "weight", 0.4f);
            peter.addEdge("created", lop, "weight", 0.2f);
            //
            tx.commit();
        }
        //
        graph.close();
    }
}
