/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 *  org.neo4j.graphdb.GraphDatabaseService
 *  org.neo4j.graphdb.Label
 *  org.neo4j.graphdb.Node
 *  org.neo4j.graphdb.Relationship
 *  org.neo4j.graphdb.RelationshipType
 *  org.neo4j.graphdb.Transaction
 *  org.neo4j.graphdb.schema.ConstraintCreator
 *  org.neo4j.graphdb.schema.ConstraintDefinition
 *  org.neo4j.graphdb.schema.Schema
 */
package es.jipeream.library.neo4j.twitter;

import es.jipeream.library.neo4j.twitter.NeoTwLabels;
import es.jipeream.library.neo4j.twitter.NeoTwRelationshipTypes;
import es.jipeream.library.twitter.model.TwStatus;
import es.jipeream.library.twitter.model.TwUser;
import java.io.PrintStream;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.ConstraintCreator;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.Schema;

public class NeoTwDatabase {
    static Logger logger = Logger.getLogger((Class)NeoTwDatabase.class);
    protected final GraphDatabaseService graphDb;

    public NeoTwDatabase(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    public GraphDatabaseService getGraphDb() {
        return this.graphDb;
    }

    public Transaction beginTx() {
        return this.graphDb.beginTx();
    }

    public void createConstraints() {
        Transaction tx = this.graphDb.beginTx();
        Throwable throwable = null;
        try {
            this.graphDb.schema().constraintFor((Label)NeoTwLabels.TwStatus).assertPropertyIsUnique("id").create();
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (tx != null) {
                if (throwable != null) {
                    try {
                        tx.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    tx.close();
                }
            }
        }
        tx = this.graphDb.beginTx();
        throwable = null;
        try {
            this.graphDb.schema().constraintFor((Label)NeoTwLabels.TwUser).assertPropertyIsUnique("id").create();
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (tx != null) {
                if (throwable != null) {
                    try {
                        tx.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    tx.close();
                }
            }
        }
    }

    public Node getTwUserNodeById(long twUserId) {
        return this.graphDb.findNode((Label)NeoTwLabels.TwUser, "id", (Object)twUserId);
    }

    public Node getTwUserNodeByScreenName(String twUserScreenName) {
        return this.graphDb.findNode((Label)NeoTwLabels.TwUser, "screenName", (Object)twUserScreenName);
    }

    public Node createTwUserNode(TwUser twUser) {
        Node node = this.graphDb.createNode(new Label[]{NeoTwLabels.TwUser});
        node.setProperty("id", (Object)twUser.getId());
        node.setProperty("screenName", (Object)twUser.getScreenName());
        node.setProperty("jsonObject", (Object)twUser.getJsonObject().toString());
        return node;
    }

    public Node getOrCreateTwUserNode(TwUser twUser) {
        Node node = this.getTwUserNodeById(twUser.getId());
        if (node == null) {
            node = this.createTwUserNode(twUser);
            System.out.println("Created node " + ((Label)node.getLabels().iterator().next()).name() + " " + node.getProperty("screenName"));
        } else {
            System.out.println("Found node " + ((Label)node.getLabels().iterator().next()).name() + " " + node.getProperty("screenName"));
        }
        return node;
    }

    public Node getTwStatusNodeById(long twStatusId) {
        return this.graphDb.findNode((Label)NeoTwLabels.TwStatus, "id", (Object)twStatusId);
    }

    public Node createTwStatusNode(TwStatus twStatus) {
        Node node = this.graphDb.createNode(new Label[]{NeoTwLabels.TwStatus});
        node.setProperty("id", (Object)twStatus.getId());
        node.setProperty("text", (Object)twStatus.getText());
        node.setProperty("jsonObject", (Object)twStatus.getJsonObject().toString());
        TwUser twUser = twStatus.getUser();
        Node twUserNode = this.getOrCreateTwUserNode(twUser);
        Relationship twUserRelationship = twUserNode.createRelationshipTo(node, (RelationshipType)NeoTwRelationshipTypes.IsAuthorOf);
        twUserRelationship.setProperty("location", (Object)twUser.getJsonObject().optString("location"));
        return node;
    }

    public Node getOrCreateTwStatusNode(TwStatus twStatus) {
        Node node = this.getTwStatusNodeById(twStatus.getId());
        if (node == null) {
            node = this.createTwStatusNode(twStatus);
            logger.debug((Object)("Created node " + ((Label)node.getLabels().iterator().next()).name() + " " + node.getProperty("text")));
        } else {
            logger.debug((Object)("Found node " + ((Label)node.getLabels().iterator().next()).name() + " " + node.getProperty("text")));
        }
        return node;
    }
}

