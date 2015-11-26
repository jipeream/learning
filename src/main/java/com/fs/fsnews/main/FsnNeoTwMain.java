package com.fs.fsnews.main;

import es.jperea.neo4j.twitter.NeoTwDb;
import es.jperea.twitter.model.TwStatus;
import es.jperea.twitter.model.TwUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FsnNeoTwMain {

    public static final String NEO4J_DATABASE_DIR = "A:/fsinsights-learning/db/";
    public static final String SAMPLE_TWITTER_DIR = "./sample/twitter/";
    public static final String SAMPLE_RSS_DIR = "./sample/rss/";

    public static void aclerkTest(NeoTwDb neoTwDb) throws Exception {
        final int aclerkId = 316751683;

        System.out.println("Parsing...");

        JSONObject aclerkJsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(SAMPLE_TWITTER_DIR + "aclerk.json"))));

        System.out.println("Inserting...");

        TwUser aclerkTwUser = new TwUser(aclerkJsonObject);
        try (Transaction tx = neoTwDb.beginTx()) {
            Node aclerkNode = neoTwDb.getOrCreateTwUserNode(aclerkTwUser);

            tx.success();
        }

        System.out.println("Checking...");
        try (Transaction tx = neoTwDb.beginTx()) {
            Node aclerkNode = neoTwDb.getTwUserNodeById(aclerkTwUser.getId());
            System.out.println(aclerkNode.getProperty("id"));
            System.out.println(aclerkNode.getProperty("screenName"));
            System.out.println(aclerkNode.getProperty("jsonObject"));
        }
    }

    public static void completeTest(NeoTwDb neoTwDb) throws Exception {
        // https://api.twitter.com/1.1/search/tweets.json?q=562b47a2e2704e07768b464c

        System.out.println("Parsing...");

        JSONObject completeJsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(SAMPLE_TWITTER_DIR + "complete.json"))));

        System.out.println("Inserting...");

        try (Transaction tx = neoTwDb.beginTx()) {
            JSONArray statusesJsonArray = completeJsonObject.optJSONArray("statuses");
            for (int i = 0; i < statusesJsonArray.length(); ++i) {
                JSONObject jsonObject = statusesJsonArray.getJSONObject(i);
                TwStatus twStatus = new TwStatus(jsonObject);
                Node node = neoTwDb.getOrCreateTwStatusNode(twStatus);
            }
            tx.success();
        }

        System.out.println("Checking...");

        try (Transaction tx = neoTwDb.beginTx()) {
            JSONArray statusesJsonArray = completeJsonObject.optJSONArray("statuses");
            for (int i = 0; i < statusesJsonArray.length(); ++i) {
                JSONObject jsonObject = statusesJsonArray.getJSONObject(i);
                TwStatus twStatus = new TwStatus(jsonObject);
                Node node = neoTwDb.getTwStatusNodeById(twStatus.getId());
                System.out.println(node.getProperty("jsonObject"));
            }
        }

    }

    public static void main(String[] args) throws Exception {
        // GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File("C:/Users/Juan/Documents/Neo4j/default.graphdb"));
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(NEO4J_DATABASE_DIR));
        // registerShutdownHook( graphDb );

        NeoTwDb neoTwDb = new NeoTwDb(graphDb);

        // FsnNeo4jMain.test(graphDb);

        neoTwDb.createConstraints();

        aclerkTest(neoTwDb);
        completeTest(neoTwDb);

        graphDb.shutdown();
    }
}
