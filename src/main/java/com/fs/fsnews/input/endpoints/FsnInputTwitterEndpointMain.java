/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  com.twitter.hbc.core.endpoint.StreamingEndpoint
 *  com.twitter.hbc.httpclient.auth.Authentication
 *  kafka.javaapi.producer.Producer
 *  kafka.producer.KeyedMessage
 *  org.apache.log4j.Logger
 *  twitter4j.HashtagEntity
 *  twitter4j.Status
 *  twitter4j.TwitterObjectFactory
 *  twitter4j.User
 */
package com.fs.fsnews.input.endpoints;

import com.fs.fsnews.config.KafkaConfig;
import com.fs.fsnews.config.Twitter4jConfig;
import com.fs.fsnews.input.endpoints.config.FsnTwitterConfig;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.httpclient.auth.Authentication;
import es.jipeream.library.JavaUtils;
import es.jipeream.library.kafka.KafkaUtils;
import es.jipeream.library.twitter.ITwitterQueueListener;
import es.jipeream.library.twitter.TwitterStatusListener;
import es.jipeream.library.twitter.TwitterUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.apache.log4j.Logger;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;

public class FsnInputTwitterEndpointMain {
    static Logger logger = Logger.getLogger((Class) FsnInputTwitterEndpointMain.class);

    public static void main(String[] args) throws Exception {
        String configDir = JavaUtils.getConfigDir(args);
        try {
            Properties kafkaProperties = KafkaConfig.loadProperties(configDir);
            Producer producer = KafkaUtils.createProducer(kafkaProperties);
            Properties twitter4jProperties = Twitter4jConfig.loadProperties(configDir);
            Authentication authentication = TwitterUtils.createAuthentication(twitter4jProperties);
            Properties fsnTwitterProperties = FsnTwitterConfig.loadProperties(configDir);
            List<StreamingEndpoint> streamingEndpointList = FsnTwitterConfig.getStreamingEndpointList(fsnTwitterProperties);
            logger.info("Starting clients...");
            ArrayList<TwitterUtils.TwitterClient> twitterClientList = new ArrayList();
            String client = fsnTwitterProperties.getProperty("fsn.twitter.client", "BasicClient");
            if ("Twitter4jClient".equals(client)) {
                for (StreamingEndpoint streamingEndpoint : streamingEndpointList) {
                    logger.info(("Starting client - " + streamingEndpoint.toString() + "..."));
                    twitterClientList.add(TwitterUtils.startTwitter4jClient(streamingEndpoint, authentication, new KafkaTwitterStatusListener(configDir, streamingEndpoint, producer)));
                }
            }
            if ("BasicClient".equals(client)) {
                for (StreamingEndpoint streamingEndpoint : streamingEndpointList) {
                    logger.info(("Starting client - " + streamingEndpoint.toString() + "..."));
                    twitterClientList.add(TwitterUtils.startBasicClient(streamingEndpoint, authentication, new KafkaTwitterStatusListener(configDir, streamingEndpoint, producer)));
                }
            }
            logger.info("Processing...");
            Thread.sleep(Long.parseLong(fsnTwitterProperties.getProperty("fsn.twitter.durationMs", "999999999999999")));
            logger.info("Stopping clients...");
            for (TwitterUtils.TwitterClient twitterClient : twitterClientList) {
                twitterClient.stop();
            }
            if (producer != null) {
                producer.close();
            }
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }

    public static class KafkaTwitterStatusListener
            extends TwitterStatusListener
            implements ITwitterQueueListener {
        private final StreamingEndpoint streamingEndpoint;
        private final Producer producer;
        private final Properties fsnTwitterProperties;

        public KafkaTwitterStatusListener(String configDir, StreamingEndpoint streamingEndpoint, Producer producer) throws Exception {
            this.streamingEndpoint = streamingEndpoint;
            this.producer = producer;
            this.fsnTwitterProperties = FsnTwitterConfig.loadProperties(configDir);
        }

        @Override
        public void onBeginListening(BlockingQueue<String> twitterQueue) {
        }

        @Override
        public void onEndListening() {
        }

        @Override
        public void onStatusJsonStr(String statusJsonStr) throws Exception {
            Status status = TwitterObjectFactory.createStatus((String) statusJsonStr);
            this.sendStatus(status, statusJsonStr, this.streamingEndpoint, this.producer);
        }

        @Override
        public void onStatus(Status status) {
            this.sendStatus(status, null, this.streamingEndpoint, this.producer);
        }

        private void sendStatus(Status status, String statusJsonStr, StreamingEndpoint streamingEndpoint, Producer producer) {
            Status retweetedStatus;
            FsnInputTwitterEndpointMain.logger.info(("-----" + streamingEndpoint.getClass().getSimpleName() + "-----" + status.getId() + "-----" + "@" + status.getUser().getScreenName() + "-" + status.getUser().getId() + "/" + "------"));
            FsnInputTwitterEndpointMain.logger.info(("\u00ab" + status.getText() + "\u00bb"));
            for (URL url : TwitterUtils.getUrlList(status, true)) {
                FsnInputTwitterEndpointMain.logger.info(url.toExternalForm());
            }
            for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
                FsnInputTwitterEndpointMain.logger.debug(("#" + hashtagEntity.getText()));
            }
            if (status.getInReplyToScreenName() != null) {
                FsnInputTwitterEndpointMain.logger.debug(("rep @" + status.getInReplyToScreenName()));
            }
            if ((retweetedStatus = status.getRetweetedStatus()) != null) {
                FsnInputTwitterEndpointMain.logger.debug(("RT \u00ab" + retweetedStatus.getText() + "\u00bb"));
                FsnInputTwitterEndpointMain.logger.debug(("RT @" + retweetedStatus.getUser().getScreenName() + "-" + retweetedStatus.getUser().getId()));
            }
            if (producer != null) {
                String topic = this.fsnTwitterProperties.getProperty("fsn.twitter.topic");
                if (statusJsonStr == null) {
                    statusJsonStr = TwitterObjectFactory.getRawJSON(status);
                }
                if (statusJsonStr == null) {
                    KeyedMessage message = new KeyedMessage(topic, status);
                    producer.send(message);
                } else {
                    FsnInputTwitterEndpointMain.logger.trace(statusJsonStr);
                    KeyedMessage message = new KeyedMessage(topic, statusJsonStr);
                    producer.send(message);
                }
            } else {
                File outDir = new File("samples/out");
                if (outDir.exists()) {
                    File mediaDir = new File(outDir, "twitter");
                    mediaDir.mkdirs();
                    String fileName = Long.toString(status.getId()) + ".json";
                    fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                    File file = new File(mediaDir, fileName);
                    try {
                        PrintWriter printWriter = new PrintWriter(file);
                        printWriter.write(statusJsonStr);
                        printWriter.close();
                    } catch (FileNotFoundException e) {
                    }
                }
            }
        }
    }

}

