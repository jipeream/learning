package com.fs.fsnews.main;

import com.fs.fsnews.config.FsnNeo4jConfig;
import org.neo4j.graphdb.*;

public class FsnNeo4jMain {
    private static enum RelTypes implements RelationshipType {
        KNOWS
    }

    public static void test(GraphDatabaseService graphDatabaseService) {
        Node firstNode;
        Node secondNode;
        Relationship relationship;

        try (Transaction tx = graphDatabaseService.beginTx()) {

            firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("message", "Hello, ");
            secondNode = graphDatabaseService.createNode();
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
        GraphDatabaseService graphDatabaseService = FsnNeo4jConfig.getEmbeddedGraphDatabaseService();

        // registerShutdownHook( graphDatabaseService );

        FsnNeo4jMain.test(graphDatabaseService);

        graphDatabaseService.shutdown();
    }

}
