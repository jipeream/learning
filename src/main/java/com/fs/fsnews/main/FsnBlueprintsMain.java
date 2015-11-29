package com.fs.fsnews.main;

import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class FsnBlueprintsMain {
    public static void main(String[] args) {
        Graph graph = Neo4jGraph.open("./db");
        Vertex marko = graph.addVertex(T.label, "person", T.id, 1, "name", "marko", "age", 29);
        Vertex vadas = graph.addVertex(T.label, "person", T.id, 2, "name", "vadas", "age", 27);
        Vertex lop = graph.addVertex(T.label, "software", T.id, 3, "name", "lop", "lang", "java");
        Vertex josh = graph.addVertex(T.label, "person", T.id, 4, "name", "josh", "age", 32);
        Vertex ripple = graph.addVertex(T.label, "software", T.id, 5, "name", "ripple", "lang", "java");
        Vertex peter = graph.addVertex(T.label, "person", T.id, 6, "name", "peter", "age", 35);
        marko.addEdge("knows", vadas, T.id, 7, "weight", 0.5f);
        marko.addEdge("knows", josh, T.id, 8, "weight", 1.0f);
        marko.addEdge("created", lop, T.id, 9, "weight", 0.4f);
        josh.addEdge("created", ripple, T.id, 10, "weight", 1.0f);
        josh.addEdge("created", lop, T.id, 11, "weight", 0.4f);
        peter.addEdge("created", lop, T.id, 12, "weight", 0.2f);
    }
}
