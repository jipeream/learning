package es.jperea.learning;

import com.google.common.collect.Lists;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import es.jperea.twitter.TwStatusListener;
import es.jperea.twitter.TwUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import twitter4j.Status;
import twitter4j.StatusListener;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class LearningTwKafka {

    public static Authentication createAuthentication() {
        // Datos jipeream
        String consumerKey = "WUbe7rRkEZxLclBUxgVEK4tzI";
        String consumerSecret = "QaUeWy0NZVqPdy7FB12DgIubHhZAdkfWnSrQXbd8vDqrokM8NV";
        String accessToken = "197066312-QQLCZ5qbLu2t4IFizULAuYqX64SduT2puTMvtbYn";
        String accessTokenSecret = "u9ATwZmpp4DwCQflrsYqq42qd4gXTQFU5vFclYcyKWSPt";

        Authentication authentication = new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        // Authentication authentication = new BasicAuth(username, password);

        return authentication;
    }

    public static Producer createProducer() {
        Properties properties = new Properties();
        properties.put("metadata.broker.list", "192.168.3.228:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        // properties.put("serializer.class", "org.apache.kafka.common.serialization.ByteArraySerializer");
        // properties.put("client.id", "camus");

        ProducerConfig producerConfig = new ProducerConfig(properties);
        Producer<String, String> producer = new Producer(producerConfig);

        return producer;
    }

    public static StatusesFilterEndpoint createStatusesFilterEndpoint(String... keywords) {
        StatusesFilterEndpoint statusesFilterEndpoint = new StatusesFilterEndpoint();
        statusesFilterEndpoint.trackTerms(Lists.newArrayList(keywords));
        return statusesFilterEndpoint;
    }

    public static StatusesSampleEndpoint createStatusesSampleEndpoint() {
        StatusesSampleEndpoint statusesSampleEndpoint = new StatusesSampleEndpoint();
        return statusesSampleEndpoint;
    }

    public static void testTwitter4jClient(StreamingEndpoint streamingEndpoint) throws Exception {
        Authentication authentication = createAuthentication();
        BlockingQueue<String> statusQueue = TwUtils.createStatusQueue(100);
        Producer producer = createProducer();
        List<StatusListener> userStreamListenerList = TwUtils.createStatusListenerList(new TwStatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println(status.toString());
                KeyedMessage<String, Status> message = new KeyedMessage("fsinsights", status);
                producer.send(message);
            }
        });
        int numProcessingThreads = 2;
        ExecutorService executorService = TwUtils.createExecutorService(numProcessingThreads);
        Twitter4jStatusClient twitter4jStatusClient = TwUtils.createTwitter4jStatusClient(streamingEndpoint, authentication, statusQueue, userStreamListenerList, executorService);
        twitter4jStatusClient.connect();
        TwUtils.prepareTwitter4jStatusClient(twitter4jStatusClient, numProcessingThreads);
        Thread.sleep(10000);
        twitter4jStatusClient.stop();
        producer.close();
    }

    public static Thread testBasicClient(StreamingEndpoint streamingEndpoint) throws Exception {
        Authentication authentication = createAuthentication();
        BlockingQueue<String> statusQueue = TwUtils.createStatusQueue(100);
        Producer producer = createProducer();
        BasicClient basicClient = TwUtils.createBasicClient(streamingEndpoint, authentication, statusQueue);
        basicClient.connect();
        Thread thread = TwUtils.createStatusQueueListeningThread(statusQueue, new TwUtils.StatusQueueListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println(status);
                // KeyedMessage<String, String> message = new KeyedMessage("fsinsights", statusJsonStr);
                // KeyedMessage<String, byte[]> message = new KeyedMessage("fsinsights", statusJsonStr.getBytes());
                KeyedMessage<String, Status> message = new KeyedMessage("fsinsights", status);
                producer.send(message);
            }
        });
        thread.start();
        return thread;
    }

    public static void main(String[] args) {
        try {
            StreamingEndpoint streamingEndpoint;
            streamingEndpoint = createStatusesFilterEndpoint("elmundoes", "abc_es", "larazon_es", "el_pais");
            // streamingEndpoint = createStatusesSampleEndpoint();
            //
            testTwitter4jClient(streamingEndpoint);
            // testBasicClient(streamingEndpoint);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}