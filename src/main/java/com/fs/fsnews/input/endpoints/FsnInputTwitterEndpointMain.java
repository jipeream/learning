package com.fs.fsnews.input.endpoints;

import com.fs.fsnews.config.FsnTwitterConfig;
import com.fs.fsnews.config.KafkaConfig;
import com.fs.fsnews.config.Twitter4jConfig;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.httpclient.auth.Authentication;
import es.jipeream.library.kafka.KafkaUtils;
import es.jipeream.library.twitter.ITwitterQueueListener;
import es.jipeream.library.twitter.TwitterStatusListener;
import es.jipeream.library.twitter.TwitterUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.apache.log4j.Logger;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

public class FsnInputTwitterEndpointMain {
    static Logger logger = Logger.getLogger(FsnInputTwitterEndpointMain.class);

    public static class KafkaTwitterStatusListener extends TwitterStatusListener implements ITwitterQueueListener {
        private final StreamingEndpoint streamingEndpoint;
        private final Producer producer;
        private final Properties fsnTwitterProperties;

        public KafkaTwitterStatusListener(StreamingEndpoint streamingEndpoint, Producer producer) throws Exception {
            this.streamingEndpoint = streamingEndpoint;
            this.producer = producer;
            this.fsnTwitterProperties = FsnTwitterConfig.loadProperties("");
        }

        @Override
        public void onBeginListening(BlockingQueue<String> twitterQueue) {
        }

        @Override
        public void onEndListening() {
        }

        @Override
        public void onStatusJsonStr(String statusJsonStr) throws Exception {
//            super.onStatusJsonStr(statusJsonStr);
            //
            Status status = TwitterObjectFactory.createStatus(statusJsonStr);
            sendStatus(status, statusJsonStr, streamingEndpoint, producer);
        }

        @Override
        public void onStatus(Status status) {
            sendStatus(status, null, streamingEndpoint, producer);
        }

        private void sendStatus(Status status, String statusJsonStr, StreamingEndpoint streamingEndpoint, Producer producer) {
            logger.info("-----" + streamingEndpoint.getClass().getSimpleName() + "-----" + status.getId() + "-----" + "@" + status.getUser().getScreenName() + "-" + status.getUser().getId() + "/" + "------");
            logger.info("«" + status.getText() + "»");
            for (URL url : TwitterUtils.getUrlList(status, true)) {
                logger.info(url.toExternalForm());
            }
            for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
                logger.debug("#" + hashtagEntity.getText());
            }
            if (status.getInReplyToScreenName() != null) {
                logger.debug("rep @" + status.getInReplyToScreenName());
            }
            Status retweetedStatus = status.getRetweetedStatus();
            if (retweetedStatus != null) {
                logger.debug("RT «" + retweetedStatus.getText() + "»");
                logger.debug("RT @" + retweetedStatus.getUser().getScreenName() + "-" + retweetedStatus.getUser().getId());
            }
            if (producer != null) {
                String topic = fsnTwitterProperties.getProperty("fsn.twitter.topic");
                if (statusJsonStr == null) {
                    statusJsonStr = TwitterObjectFactory.getRawJSON(status);
                }
                if (statusJsonStr == null) {
                    // statusJsonStr = TwitterObjectFactory.getRawJSON(status);
                    KeyedMessage<String, Status> message = new KeyedMessage(topic, status);
                    producer.send(message);
                } else {
                    logger.trace(statusJsonStr);
                    KeyedMessage<String, String> message = new KeyedMessage(topic, statusJsonStr);
                    producer.send(message);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Properties kafkaProperties = KafkaConfig.loadProperties("");
            Producer producer = KafkaUtils.createProducer(kafkaProperties);
            //
            Properties twitter4jProperties = Twitter4jConfig.loadProperties("");
            Authentication authentication = TwitterUtils.createAuthentication(twitter4jProperties);
            //
            Properties fsnTwitterProperties = FsnTwitterConfig.loadProperties("");
            List<StreamingEndpoint> streamingEndpointList = FsnTwitterConfig.getStreamingEndpointList(fsnTwitterProperties);
            //
            logger.info("Starting clients...");
            //
            List<TwitterUtils.TwitterClient> twitterClientList = new ArrayList<>();
            //
            String client = fsnTwitterProperties.getProperty("fsn.twitter.client", FsnTwitterConfig.TWITTER_CLIENT_BasicClient);
            //
            if (FsnTwitterConfig.TWITTER_CLIENT_Twitter4jClient.equals(client)) {
                for (StreamingEndpoint streamingEndpoint : streamingEndpointList) {
                    logger.info("Starting client - " + streamingEndpoint.toString() + "...");
                    twitterClientList.add(TwitterUtils.startTwitter4jClient(streamingEndpoint, authentication, new KafkaTwitterStatusListener(streamingEndpoint, producer)));
                }
            }
            //
            if (FsnTwitterConfig.TWITTER_CLIENT_BasicClient.equals(client)) {
                for (StreamingEndpoint streamingEndpoint : streamingEndpointList) {
                    logger.info("Starting client - " + streamingEndpoint.toString() + "...");
                    twitterClientList.add(TwitterUtils.startBasicClient(streamingEndpoint, authentication, new KafkaTwitterStatusListener(streamingEndpoint, producer)));
                }
            }
            //
            logger.info("Processing...");
            //
            Thread.sleep(Long.parseLong(fsnTwitterProperties.getProperty("fsn.twitter.durationMs", "999999999999999")));
            //
            logger.info("Stopping clients...");
            //
            for (TwitterUtils.TwitterClient twitterClient : twitterClientList) {
                twitterClient.stop();
            }
            //
            if (producer != null) {
                producer.close();
            }
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }
}