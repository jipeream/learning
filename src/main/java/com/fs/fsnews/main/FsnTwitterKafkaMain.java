package com.fs.fsnews.main;

import com.fs.fsnews.config.FsnTwitterConfig;
import com.fs.fsnews.config.KafkaConfig;
import com.fs.fsnews.config.Twitter4jConfig;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import es.jipeream.library.kafka.KafkaUtils;
import es.jipeream.library.twitter.TwStatusListener;
import es.jipeream.library.twitter.TwitterUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import twitter4j.HashtagEntity;
import twitter4j.Status;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FsnTwitterKafkaMain {
    private static class KafkaTwStatusListener extends TwStatusListener {
        private final StreamingEndpoint streamingEndpoint;
        private final Producer producer;
        private final Properties fsnTwitterProperties;

        public KafkaTwStatusListener(StreamingEndpoint streamingEndpoint, Producer producer) throws Exception {
            this.streamingEndpoint = streamingEndpoint;
            this.producer = producer;
            this.fsnTwitterProperties = FsnTwitterConfig.loadProperties("");
        }

        @Override
        public void onStatus(Status status) {
            sendStatus(status, streamingEndpoint, producer);
        }

        private void sendStatus(Status status, StreamingEndpoint streamingEndpoint, Producer producer) {
//        String statusJsonStr = TwitterObjectFactory.getRawJSON(status);
//        System.out.println(statusJsonStr);
            System.out.println("-----" + streamingEndpoint.getClass().getSimpleName() + "-----" + status.getId() + "-----" + "@" + status.getUser().getScreenName() + "-" + status.getUser().getId() + "/" + "------");
            System.out.println("«" + status.getText() + "»");
            for (URL url : TwitterUtils.getUrlList(status, true)) {
                System.out.println(url.toExternalForm());
            }
            for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
                System.out.println("#" + hashtagEntity.getText());
            }
            if (status.getInReplyToScreenName() != null) {
                System.out.println("rep @" + status.getInReplyToScreenName());
            }
            Status retweetedStatus = status.getRetweetedStatus();
            if (retweetedStatus != null) {
                System.out.println("RT «" + retweetedStatus.getText() + "»");
                System.out.println("RT @" + retweetedStatus.getUser().getScreenName() + "-" + retweetedStatus.getUser().getId());
            }
            if (producer != null) {
                String topic = fsnTwitterProperties.getProperty("fsn.twitter.topic");
                KeyedMessage<String, Status> message = new KeyedMessage(topic, status);
//                KeyedMessage<String, String> message = new KeyedMessage(topic, statusJsonStr);
                producer.send(message);
            }
        }
    }

    public static void main(String[] args) {
        try {
            //
            Properties kafkaProperties = KafkaConfig.loadProperties("");
            Producer producer = KafkaUtils.createProducer(kafkaProperties);
            //
            Properties twitter4jProperties = Twitter4jConfig.loadProperties("");
            Authentication authentication = TwitterUtils.createAuthentication(twitter4jProperties);
            //
            Properties fsnTwitterProperties = FsnTwitterConfig.loadProperties("");
            List<StreamingEndpoint> streamingEndpointList = FsnTwitterConfig.getStreamingEndpointList(fsnTwitterProperties);
            //
            System.err.println("Starting clients...");
            //
            List<Twitter4jStatusClient> twitter4jStatusClientList = new ArrayList<>();
            List<Thread> threadList = new ArrayList<>();
            //
            for (StreamingEndpoint streamingEndpoint : streamingEndpointList) {
                System.err.println("Starting client - " + streamingEndpoint.toString() + "...");
                twitter4jStatusClientList.add(TwitterUtils.startTwitter4jClient(streamingEndpoint, authentication, new KafkaTwStatusListener(streamingEndpoint, producer)));
            }
            //
//            for (StreamingEndpoint streamingEndpoint : streamingEndpointList) {
//                threadList.add(TwitterUtils.startBasicClient(streamingEndpoint, authentication, new KafkaTwStatusListener(streamingEndpoint, producer)));
//            }
            //
            System.err.println("Processing...");
            //
            Thread.sleep(Long.parseLong(fsnTwitterProperties.getProperty("fsn.twitter.durationMs", "1000000000000")));
            //
            System.err.println("Stopping clients...");
            //
            for (Twitter4jStatusClient twitter4jStatusClient : twitter4jStatusClientList) {
                twitter4jStatusClient.stop();
            }
            //
            for (Thread thread: threadList) {
                thread.stop(); // TODO !!
            }
            //
            if (producer != null) {
                producer.close();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}