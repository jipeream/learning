package es.jipeream.library.neo4j.twitter;

import es.jipeream.library.twitter.model.TwStatus;
import es.jipeream.library.twitter.model.TwUser;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;

public class NeoTwDatabase {
    static Logger logger = Logger.getLogger(NeoTwDatabase.class);

    /**/

    public NeoTwDatabase(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    /**/

    protected final GraphDatabaseService graphDb;

    public GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    /**/

    public Transaction beginTx() {
        return graphDb.beginTx();
    }

    /**/

    public void createConstraints() {
        try (Transaction tx = graphDb.beginTx()) {
            graphDb.schema()
                    .constraintFor(NeoTwLabels.TwStatus)
                    .assertPropertyIsUnique("id")
                    .create();
        }

        try (Transaction tx = graphDb.beginTx()) {
            graphDb.schema()
                    .constraintFor(NeoTwLabels.TwUser)
                    .assertPropertyIsUnique("id")
                    .create();
        }
    }

    /**/

    public Node getTwUserNodeById(long twUserId) {
        return graphDb.findNode(NeoTwLabels.TwUser, "id", twUserId);
    }

    public Node getTwUserNodeByScreenName(String twUserScreenName) {
        return graphDb.findNode(NeoTwLabels.TwUser, "screenName", twUserScreenName);
    }

    public Node createTwUserNode(TwUser twUser) {
        Node node = graphDb.createNode(NeoTwLabels.TwUser);
        //
        node.setProperty("id", twUser.getId());
        node.setProperty("screenName", twUser.getScreenName());
        node.setProperty("jsonObject", twUser.getJsonObject().toString());
        //
        return node;
    }

    public Node getOrCreateTwUserNode(TwUser twUser) {
        Node node = getTwUserNodeById(twUser.getId());
        if (node == null) {
            node = createTwUserNode(twUser);
            System.out.println("Created node " + node.getLabels().iterator().next().name() + " " + node.getProperty("screenName"));
        } else {
            System.out.println("Found node " + node.getLabels().iterator().next().name() + " " + node.getProperty("screenName"));
        }
        return node;
    }

    /**/

    public Node getTwStatusNodeById(long twStatusId) {
        return graphDb.findNode(NeoTwLabels.TwStatus, "id", twStatusId);
    }

    public Node createTwStatusNode(TwStatus twStatus) {
        Node node = graphDb.createNode(NeoTwLabels.TwStatus);
        node.setProperty("id", twStatus.getId());
        node.setProperty("text", twStatus.getText());
        node.setProperty("jsonObject", twStatus.getJsonObject().toString());
        //
        TwUser twUser = twStatus.getUser();
        Node twUserNode = getOrCreateTwUserNode(twUser);
        Relationship twUserRelationship = twUserNode.createRelationshipTo(node, NeoTwRelationshipTypes.IsAuthorOf);
        twUserRelationship.setProperty("location", twUser.getJsonObject().optString("location"));
        //
        return node;
    }

    public Node getOrCreateTwStatusNode(TwStatus twStatus) {
        Node node = getTwStatusNodeById(twStatus.getId());
        if (node == null) {
            node = createTwStatusNode(twStatus);
            logger.debug("Created node " + node.getLabels().iterator().next().name() + " " + node.getProperty("text"));
        } else {
            logger.debug("Found node " + node.getLabels().iterator().next().name() + " " + node.getProperty("text"));
        }
        return node;
    }
}
