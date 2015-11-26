package com.fs.fsnews.main;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;

public class FsnNeo4jMain {
    private static enum RelTypes implements RelationshipType {
        KNOWS
    }

    public static void test(GraphDatabaseService graphDb) {
        Node firstNode;
        Node secondNode;
        Relationship relationship;

        try (Transaction tx = graphDb.beginTx()) {

            firstNode = graphDb.createNode();
            firstNode.setProperty("message", "Hello, ");
            secondNode = graphDb.createNode();
            secondNode.setProperty("message", "World!");

            relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
            relationship.setProperty("message", "brave Neo4j ");

            System.out.print(firstNode.getProperty("message"));
            System.out.print(relationship.getProperty("message"));
            System.out.print(secondNode.getProperty("message"));

            tx.success();
        }
    }


    public static void main(String[] args) throws Exception {
        // GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File("C:/Users/Juan/Documents/Neo4j/default.graphdb"));
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File("./db"));

        // registerShutdownHook( graphDb );

        FsnNeo4jMain.test(graphDb);

        graphDb.shutdown();
    }

}
