import es.jipeream.library.neo4j.twitter.NeoTwDatabase;
import es.jipeream.library.twitter.model.TwStatus;
import es.jipeream.library.twitter.model.TwUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FsnNeoTwTest {

    public static final String SAMPLE_TWITTER_DIR = "./sample/twitter/";
    public static final String SAMPLE_RSS_DIR = "./sample/rss/";

    public static void aclerkTest(NeoTwDatabase neoTwDatabase) throws Exception {
        final int aclerkId = 316751683;

        System.out.println("Parsing...");

        JSONObject aclerkJsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(SAMPLE_TWITTER_DIR + "aclerk.json"))));

        System.out.println("Inserting...");

        TwUser aclerkTwUser = new TwUser(aclerkJsonObject);
        try (Transaction tx = neoTwDatabase.beginTx()) {
            Node aclerkNode = neoTwDatabase.getOrCreateTwUserNode(aclerkTwUser);

            tx.success();
        }

        System.out.println("Checking...");
        try (Transaction tx = neoTwDatabase.beginTx()) {
            Node aclerkNode = neoTwDatabase.getTwUserNodeById(aclerkTwUser.getId());
            System.out.println(aclerkNode.getProperty("id"));
            System.out.println(aclerkNode.getProperty("screenName"));
            System.out.println(aclerkNode.getProperty("jsonObject"));
        }
    }

    public static void completeTest(NeoTwDatabase neoTwDatabase) throws Exception {
        // https://api.twitter.com/1.1/search/tweets.json?q=562b47a2e2704e07768b464c

        System.out.println("Parsing...");

        JSONObject completeJsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(SAMPLE_TWITTER_DIR + "complete.json"))));

        System.out.println("Inserting...");

        try (Transaction tx = neoTwDatabase.beginTx()) {
            JSONArray statusesJsonArray = completeJsonObject.optJSONArray("statuses");
            for (int i = 0; i < statusesJsonArray.length(); ++i) {
                JSONObject jsonObject = statusesJsonArray.getJSONObject(i);
                TwStatus twStatus = new TwStatus(jsonObject);
                Node node = neoTwDatabase.getOrCreateTwStatusNode(twStatus);
            }
            tx.success();
        }

        System.out.println("Checking...");

        try (Transaction tx = neoTwDatabase.beginTx()) {
            JSONArray statusesJsonArray = completeJsonObject.optJSONArray("statuses");
            for (int i = 0; i < statusesJsonArray.length(); ++i) {
                JSONObject jsonObject = statusesJsonArray.getJSONObject(i);
                TwStatus twStatus = new TwStatus(jsonObject);
                Node node = neoTwDatabase.getTwStatusNodeById(twStatus.getId());
                System.out.println(node.getProperty("jsonObject"));
            }
        }

    }

    public static void main(String[] args) throws Exception {
        GraphDatabaseService graphDatabaseService = FsnNeo4jConfig.createEmbeddedGraphDatabaseService();

        // registerShutdownHook( graphDatabaseService );

        NeoTwDatabase neoTwDatabase = new NeoTwDatabase(graphDatabaseService);

        // FsnNeo4jTest.test(graphDatabaseService);

        neoTwDatabase.createConstraints();

        aclerkTest(neoTwDatabase);
        completeTest(neoTwDatabase);

        graphDatabaseService.shutdown();
    }
}
