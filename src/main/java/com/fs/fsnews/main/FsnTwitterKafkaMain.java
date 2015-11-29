package com.fs.fsnews.main;

import com.fs.fsnews.config.FsnKafkaConfig;
import com.fs.fsnews.config.FsnTwitterConfig;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import es.jipeream.library.twitter.TwStatusListener;
import es.jipeream.library.twitter.TwitterUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import twitter4j.Status;
import twitter4j.StatusListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class FsnTwitterKafkaMain {
    public static void testTwitter4jClient(StreamingEndpoint streamingEndpoint, Producer producer) throws Exception {
        Authentication authentication = FsnTwitterConfig.createAuthentication();
        BlockingQueue<String> twitterQueue = TwitterUtils.createStatusQueue(100);
        List<StatusListener> userStreamListenerList = TwitterUtils.createStatusListenerList(new TwStatusListener() {
            @Override
            public void onStatus(Status status) {
                sendStatus(status, streamingEndpoint, producer);
            }
        });
        int numProcessingThreads = 2;
        ExecutorService executorService = TwitterUtils.createExecutorService(numProcessingThreads);
        Twitter4jStatusClient twitter4jStatusClient = TwitterUtils.createTwitter4jStatusClient(streamingEndpoint, authentication, twitterQueue, userStreamListenerList, executorService);
        twitter4jStatusClient.connect();
        TwitterUtils.prepareTwitter4jStatusClient(twitter4jStatusClient, numProcessingThreads);
        //
        Thread.sleep(60 * 1000);
        twitter4jStatusClient.stop();
    }

    public static Thread testBasicClient(StreamingEndpoint streamingEndpoint, Producer producer) throws Exception {
        Authentication authentication = FsnTwitterConfig.createAuthentication();
        BlockingQueue<String> twitterQueue = TwitterUtils.createStatusQueue(100);
        BasicClient basicClient = TwitterUtils.createBasicClient(streamingEndpoint, authentication, twitterQueue);
        basicClient.connect();
        Thread thread = TwitterUtils.createStatusQueueListeningThread(twitterQueue, new TwitterUtils.TwitterQueueListener() {
            @Override
            public void onStatus(Status status) {
                sendStatus(status, streamingEndpoint, producer);
            }
        });
        thread.start();
        return thread;
    }

    private static void sendStatus(Status status, StreamingEndpoint streamingEndpoint, Producer producer) {
//        String statusJsonStr = TwitterObjectFactory.getRawJSON(status);
//        System.out.println(statusJsonStr);
        System.out.println("-----" + streamingEndpoint.getClass().getSimpleName() + "-----" + status.getId() + "-----" + "@" + status.getUser().getScreenName() + "-" + status.getUser().getId() + "/" + "------");
        System.out.println("«" + status.getText() + "»");
        for (URL url : TwitterUtils.getUrlList(status, true)) {
            System.out.println(url.toExternalForm());
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
            KeyedMessage<String, Status> message = new KeyedMessage(FsnKafkaConfig.TOPIC_fsinsights_twitter, status);
//            KeyedMessage<String, String> message = new KeyedMessage(FsnKafkaConfig.TOPIC_fsinsights_twitter, statusJsonStr);
            producer.send(message);
        }
    }

    public static final long TWITTER_USER_ID_el_pais = 7996082;
    public static final String TWITTER_USER_SCREEN_NAME_el_pais = "el_pais";

    public static final long TWITTER_USER_ID_elmundoes = 14436030;
    public static final String TWITTER_USER_SCREEN_NAME_elmundoes = "elmundoes";

    public static final long TWITTER_USER_ID_abc_es = 19923515;
    public static final String TWITTER_USER_SCREEN_NAME_abc_es = "abc_es";

    public static final long TWITTER_USER_ID_larazon_es = 112694236;
    public static final String TWITTER_USER_SCREEN_NAME_larazon_es = "larazon_es";

    public static final long TWITTER_USER_ID_EFEnoticias = 0;
    public static final String TWITTER_USER_SCREEN_NAME_EFEnoticias = "EFEnoticias";

    public static final long TWITTER_USER_ID_europapress = 121385551;
    public static final String TWITTER_USER_SCREEN_NAME_europapress = "europapress";

    public static void main(String[] args) {
        try {
            List<StreamingEndpoint> streamingEndpointList = new ArrayList<>();
            streamingEndpointList.add(TwitterUtils.createStatusesFilterEndpoint(
                    TWITTER_USER_SCREEN_NAME_elmundoes,
                    TWITTER_USER_SCREEN_NAME_abc_es,
                    TWITTER_USER_SCREEN_NAME_larazon_es,
                    TWITTER_USER_SCREEN_NAME_el_pais
            ));
            streamingEndpointList.add(TwitterUtils.createStatusesFilterEndpoint(
                    TWITTER_USER_SCREEN_NAME_EFEnoticias,
                    TWITTER_USER_SCREEN_NAME_europapress
            ));
//            streamingEndpointList.add(TwitterUtils.createStatusesFilterEndpoint(
//                    TWITTER_USER_ID_elmundoes,
//                    TWITTER_USER_ID_abc_es,
//                    TWITTER_USER_ID_larazon_es,
//                    TWITTER_USER_ID_el_pais
//            ));
//            streamingEndpointList.add(TwitterUtils.createStatusesFilterEndpoint(
//                    TWITTER_USER_ID_EFEnoticias,
//                    TWITTER_USER_ID_europapress
//            ));
//            streamingEndpointList.add(TwitterUtils.createStatusesFilterEndpoint(
//                    TWITTER_USER_ID_elmundoes
//            ));
            streamingEndpointList.add(TwitterUtils.createUserstreamEndpoint());
//            streamingEndpointList.add(TwitterUtils.createStatusesSampleEndpoint());
            //
            Producer producer = null;
//            Producer producer = FsnKafkaConfig.createLocalhostProducer();
//            Producer producer = FsnKafkaConfig.createPreproProducer();
            //
            for (StreamingEndpoint streamingEndpoint : streamingEndpointList) {
                testTwitter4jClient(streamingEndpoint, producer);
//                testBasicClient(streamingEndpoint, producer);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}