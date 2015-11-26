package com.fs.fsnews.main;

import com.fs.fsnews.config.FsnTwitterConfig;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import es.jperea.twitter.TwStatusListener;
import es.jperea.twitter.TwUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.TwitterObjectFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class FsnTwitterKafkaMain {
    public static void testTwitter4jClient(StreamingEndpoint streamingEndpoint, Producer producer) throws Exception {
        Authentication authentication = FsnTwitterConfig.createAuthentication();
        BlockingQueue<String> statusQueue = TwUtils.createStatusQueue(100);
        List<StatusListener> userStreamListenerList = TwUtils.createStatusListenerList(new TwStatusListener() {
            @Override
            public void onStatus(Status status) {
                String statusJsonStr = TwitterObjectFactory.getRawJSON(status);
                System.out.println(statusJsonStr);
                if (producer != null) {
//                     KeyedMessage<String, Status> message = new KeyedMessage("fsinsights", status);
                    KeyedMessage<String, String> message = new KeyedMessage("fsinsights", statusJsonStr);
                    producer.send(message);
                }
            }
        });
        int numProcessingThreads = 2;
        ExecutorService executorService = TwUtils.createExecutorService(numProcessingThreads);
        Twitter4jStatusClient twitter4jStatusClient = TwUtils.createTwitter4jStatusClient(streamingEndpoint, authentication, statusQueue, userStreamListenerList, executorService);
        twitter4jStatusClient.connect();
        TwUtils.prepareTwitter4jStatusClient(twitter4jStatusClient, numProcessingThreads);
        //
        Thread.sleep(60 * 1000);
        twitter4jStatusClient.stop();
    }

    public static Thread testBasicClient(StreamingEndpoint streamingEndpoint, Producer producer) throws Exception {
        Authentication authentication = FsnTwitterConfig.createAuthentication();
        BlockingQueue<String> statusQueue = TwUtils.createStatusQueue(100);
        BasicClient basicClient = TwUtils.createBasicClient(streamingEndpoint, authentication, statusQueue);
        basicClient.connect();
        Thread thread = TwUtils.createStatusQueueListeningThread(statusQueue, new TwUtils.StatusQueueListener() {
            @Override
            public void onStatus(Status status) {
                String statusJsonStr = TwitterObjectFactory.getRawJSON(status);
                System.out.println(statusJsonStr);
                if (producer != null) {
//                     KeyedMessage<String, Status> message = new KeyedMessage("fsinsights", status);
                    KeyedMessage<String, String> message = new KeyedMessage("fsinsights", statusJsonStr);
                    producer.send(message);
                }
            }
        });
        thread.start();
        return thread;
    }

    public static void main(String[] args) {
        try {
            StreamingEndpoint streamingEndpoint;
            streamingEndpoint = FsnTwitterConfig.createStatusesFilterEndpoint("elmundoes", "abc_es", "larazon_es", "el_pais");
//             streamingEndpoint = FsnTwitterConfig.createStatusesSampleEndpoint();
            //
            Producer producer = null;
//            Producer producer = FsnKafkaConfig.createProducer();
            //
            testTwitter4jClient(streamingEndpoint, producer);
//             testBasicClient(streamingEndpoint, producer);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}