package es.jipeream.library.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import es.jipeream.library.http.HttpUtils;
import twitter4j.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterUtils {

    public static BasicClient createBasicClient(StreamingEndpoint streamingEndpoint, Authentication authentication, BlockingQueue<String> twQueue) {
        BasicClient basicClient = new ClientBuilder().hosts(Constants.STREAM_HOST)
                .endpoint(streamingEndpoint).authentication(authentication)
                .processor(new StringDelimitedProcessor(twQueue)).build();
        return basicClient;
    }

    public static Twitter4jStatusClient createTwitter4jStatusClient(StreamingEndpoint streamingEndpoint, Authentication authentication, BlockingQueue<String> twQueue, List<? extends StatusListener> streamListenerList, ExecutorService executorService) {
        BasicClient basicClient = new ClientBuilder().hosts(Constants.STREAM_HOST)
                .endpoint(streamingEndpoint).authentication(authentication)
                .processor(new StringDelimitedProcessor(twQueue)).build();
        Twitter4jStatusClient twitter4jStatusClient = new Twitter4jStatusClient(basicClient, twQueue, streamListenerList, executorService);
        return twitter4jStatusClient;
    }

    public static void prepareTwitter4jStatusClient(Twitter4jStatusClient twitter4jStatusClient, int numProcessingThreads) {
        for (int threads = 0; threads < numProcessingThreads; threads++) {
            // This must be called once per processing thread
            twitter4jStatusClient.process();
        }
    }

    public static BlockingQueue<String> createStatusQueue(int queueSize) {
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
                urlList.add(url);
            } catch (MalformedURLException e) {
            } catch (Exception e) {
            }
        }
        return urlList;
    }

    public interface ITwitterQueueListener {
        void onBeginListening();
        void onEndListening();
        void onStatusJsonStr(String statusJsonStr) throws Exception;
    }

    public static abstract class TwitterQueueListener implements ITwitterQueueListener {
        @Override
        public void onBeginListening() {
        }

        @Override
        public void onEndListening() {
        }

        @Override
        public void onStatusJsonStr(String statusJsonStr) throws Exception {
            Status status = TwitterObjectFactory.createStatus(statusJsonStr);
            onStatus(status);
        }

        public abstract void onStatus(Status status) throws Exception;

    }

    public static Thread createStatusQueueListeningThread(BlockingQueue<String> twitterQueue, ITwitterQueueListener statusQueueListenerCallback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                statusQueueListenerCallback.onBeginListening();
                while (true) {
                    try {
                        String twitterJsonStr = twitterQueue.take();
                        if (twitterJsonStr.startsWith("{\"created_at\":")) {
                            statusQueueListenerCallback.onStatusJsonStr(twitterJsonStr);
                        } else {
                            System.err.println(twitterJsonStr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
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
//        statusesFilterEndpoint.delimited(false);
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
//        userstreamEndpoint.delimited(false);
        userstreamEndpoint.allReplies(true);
//        userstreamEndpoint.withUser(true);
        userstreamEndpoint.withFollowings(true);
        return userstreamEndpoint;
    }

    public static StatusesSampleEndpoint createStatusesSampleEndpoint() {
        StatusesSampleEndpoint statusesSampleEndpoint = new StatusesSampleEndpoint();
//        statusesSampleEndpoint.delimited(false);
        return statusesSampleEndpoint;
    }
}
