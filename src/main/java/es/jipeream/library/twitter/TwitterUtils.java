package es.jipeream.library.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import es.jipeream.library.JavaUtils;
import es.jipeream.library.http.HttpUtils;
import org.apache.log4j.Logger;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.URLEntity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TwitterUtils {
    static Logger logger = Logger.getLogger(TwitterUtils.class);

    public static BasicClient createBasicClient(StreamingEndpoint streamingEndpoint, Authentication authentication, BlockingQueue<String> blockingQueue) {
        BasicClient basicClient = new ClientBuilder().hosts(Constants.STREAM_HOST)
                .endpoint(streamingEndpoint).authentication(authentication)
                .processor(new StringDelimitedProcessor(blockingQueue)).build();
        return basicClient;
    }

    public static Twitter4jStatusClient createTwitter4jStatusClient(StreamingEndpoint streamingEndpoint, Authentication authentication, BlockingQueue<String> blockingQueue, List<? extends StatusListener> streamListenerList, ExecutorService executorService) {
        BasicClient basicClient = new ClientBuilder().hosts(Constants.STREAM_HOST)
                .endpoint(streamingEndpoint).authentication(authentication)
                .processor(new StringDelimitedProcessor(blockingQueue)).build();
        Twitter4jStatusClient twitter4jStatusClient = new Twitter4jStatusClient(basicClient, blockingQueue, streamListenerList, executorService);
        return twitter4jStatusClient;
    }

    public static void startTwitter4jClientThreads(Twitter4jStatusClient twitter4jStatusClient, int numProcessingThreads) {
        for (int threads = 0; threads < numProcessingThreads; threads++) {
            // This must be called once per processing thread
            twitter4jStatusClient.process();
        }
    }

    public static BlockingQueue<String> createBlockingQueue(int queueSize) {
        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>(queueSize);
        return blockingQueue;
    }

    public static ExecutorService createExecutorService(int numProcessingThreads) {
        ExecutorService executorService = Executors.newFixedThreadPool(numProcessingThreads);
        return executorService;
    }

    public static List<StatusListener> createStatusListenerList(StatusListener... statusListeners) {
        List<StatusListener> streamListenerList = new ArrayList<>();
        for (StatusListener statusListener : statusListeners) {
            streamListenerList.add(statusListener);
        }
        return streamListenerList;
    }

    public static List<URL> getUrlList(Status status, boolean unshorten) {
        List<URL> urlList = new ArrayList<>();
        for (URLEntity urlEntity : status.getURLEntities()) {
            try {
                URL url = new URL(urlEntity.getExpandedURL());
                if (unshorten) {
                    url = HttpUtils.getUnshortenedUrl(url);
                }
                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath());
                urlList.add(url);
            } catch (MalformedURLException e) {
            } catch (Exception e) {
            }
        }
        return urlList;
    }

    public static Thread createStatusQueueListeningThread(final BlockingQueue<String> twitterQueue, final ITwitterQueueListener statusQueueListenerCallback, final AtomicBoolean stopping) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                statusQueueListenerCallback.onBeginListening(twitterQueue);
                while (!stopping.get()) {
                    try {
                        // String twitterJsonStr = twitterQueue.take();
                        String twitterJsonStr = twitterQueue.poll(1000, TimeUnit.MILLISECONDS);
                        if (twitterJsonStr != null) {
                            if (twitterJsonStr.startsWith("{\"created_at\":")) {
                                logger.debug(twitterJsonStr);
                                statusQueueListenerCallback.onStatusJsonStr(twitterJsonStr);
                            } else {
                                logger.trace(twitterJsonStr);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error", e);
                        break;
                    }
                }
                statusQueueListenerCallback.onEndListening();
            }
        });
        return thread;
    }

    public static StatusesFilterEndpoint createStatusesFilterEndpoint() {
        StatusesFilterEndpoint statusesFilterEndpoint = new StatusesFilterEndpoint();
        statusesFilterEndpoint.delimited(true);
        return statusesFilterEndpoint;
    }

    public static StatusesFilterEndpoint createStatusesFilterEndpoint(Long... followedUserIds) {
        StatusesFilterEndpoint statusesFilterEndpoint = createStatusesFilterEndpoint();
        setStatusesFilterEndpointFollowedUserIds(statusesFilterEndpoint, followedUserIds);
        return statusesFilterEndpoint;
    }

    public static StatusesFilterEndpoint createStatusesFilterEndpoint(String... keywords) {
        StatusesFilterEndpoint statusesFilterEndpoint = createStatusesFilterEndpoint();
        setStatusesFilterEndpointTrackedTerms(statusesFilterEndpoint, keywords);
        return statusesFilterEndpoint;
    }

    public static void setStatusesFilterEndpointFollowedUserIds(StatusesFilterEndpoint statusesFilterEndpoint, Long... followedUserIds) {
        statusesFilterEndpoint.followings(Lists.newArrayList(followedUserIds));
    }

    public static void setStatusesFilterEndpointTrackedTerms(StatusesFilterEndpoint statusesFilterEndpoint, String... trackedTerms) {
        statusesFilterEndpoint.trackTerms(Lists.newArrayList(trackedTerms));
    }

    public static UserstreamEndpoint createUserstreamEndpoint() {
        UserstreamEndpoint userstreamEndpoint = new UserstreamEndpoint();
        userstreamEndpoint.delimited(true);
        userstreamEndpoint.allReplies(true);
//        userstreamEndpoint.withUser(true);
        userstreamEndpoint.withFollowings(true);
        return userstreamEndpoint;
    }

    public static StatusesSampleEndpoint createStatusesSampleEndpoint() {
        StatusesSampleEndpoint statusesSampleEndpoint = new StatusesSampleEndpoint();
        statusesSampleEndpoint.delimited(true);
        return statusesSampleEndpoint;
    }

    /**/

    public static Authentication createAuthentication(File propertiesFile) throws Exception {
        Properties properties = JavaUtils.loadProperties(propertiesFile);
        Authentication authentication = createAuthentication(properties);
        return authentication;
    }

    public static Authentication createAuthentication(Properties properties) throws Exception {
        String consumerKey = properties.getProperty("oauth.consumerKey");
        String consumerSecret = properties.getProperty("oauth.consumerSecret");
        String accessToken = properties.getProperty("oauth.accessToken");
        String accessTokenSecret = properties.getProperty("oauth.accessTokenSecret");
        Authentication authentication = new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        return authentication;
    }

    /**/

    public static class TwitterClient {
        public final Client client;
        public final Thread thread;
        public final AtomicBoolean stopping;

        public TwitterClient(Client client, Thread thread, AtomicBoolean stopping) {
            this.client = client;
            this.thread = thread;
            this.stopping = stopping;
        }

        public void stop() throws InterruptedException {
            client.stop();
            if (stopping != null) {
                stopping.set(true);
            }
            if (thread != null) {
                thread.join(10000);
            }
        }
    }

    public static TwitterClient startTwitter4jClient(StreamingEndpoint streamingEndpoint, Authentication authentication, TwitterStatusListener twitterStatusListener) throws Exception {
        int numProcessingThreads = 2;
        int queueSize = 100;
        if (streamingEndpoint instanceof StatusesSampleEndpoint) {
            numProcessingThreads = 50;
            queueSize = 10000;
        }
        BlockingQueue<String> blockingQueue = TwitterUtils.createBlockingQueue(queueSize);
        List<StatusListener> statusListenerList = TwitterUtils.createStatusListenerList(twitterStatusListener);
        ExecutorService executorService = TwitterUtils.createExecutorService(numProcessingThreads);
        Twitter4jStatusClient twitter4jStatusClient = TwitterUtils.createTwitter4jStatusClient(streamingEndpoint, authentication, blockingQueue, statusListenerList, executorService);
        twitter4jStatusClient.connect();
        TwitterUtils.startTwitter4jClientThreads(twitter4jStatusClient, numProcessingThreads);
        return new TwitterClient(twitter4jStatusClient, null, null);
    }

    public static TwitterClient startBasicClient(StreamingEndpoint streamingEndpoint, Authentication authentication, ITwitterQueueListener twitterQueueListener) throws Exception {
        BlockingQueue<String> blockingQueue = TwitterUtils.createBlockingQueue(100);
        BasicClient basicClient = TwitterUtils.createBasicClient(streamingEndpoint, authentication, blockingQueue);
        basicClient.connect();
        AtomicBoolean stopping = new AtomicBoolean();
        Thread thread = TwitterUtils.createStatusQueueListeningThread(blockingQueue, twitterQueueListener, stopping);
        thread.start();
        return new TwitterClient(basicClient, thread, stopping);
    }

}
