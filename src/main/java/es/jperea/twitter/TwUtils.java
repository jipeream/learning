package es.jperea.twitter;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class TwUtils {

    public static BasicClient createBasicClient(StreamingEndpoint streamingEndpoint, Authentication authentication, BlockingQueue<String> statusQueue) {
        BasicClient basicClient = new ClientBuilder().hosts(Constants.STREAM_HOST)
                .endpoint(streamingEndpoint).authentication(authentication)
                .processor(new StringDelimitedProcessor(statusQueue)).build();
        return basicClient;
    }

    public static Twitter4jStatusClient createTwitter4jStatusClient(StreamingEndpoint streamingEndpoint, Authentication authentication, BlockingQueue<String> statusQueue, List<? extends StatusListener> streamListenerList, ExecutorService executorService) {
        BasicClient basicClient = new ClientBuilder().hosts(Constants.STREAM_HOST)
                .endpoint(streamingEndpoint).authentication(authentication)
                .processor(new StringDelimitedProcessor(statusQueue)).build();
        Twitter4jStatusClient twitter4jStatusClient = new Twitter4jStatusClient(basicClient, statusQueue, streamListenerList, executorService);

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

    public interface IStatusJsonStrQueueListener {
        void onBeginListening();
        void onEndListening();
        void onStatusJsonStr(String statusJsonStr) throws Exception;
    }

    public static abstract class StatusQueueListener implements IStatusJsonStrQueueListener {
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

    public static Thread createStatusQueueListeningThread(BlockingQueue<String> statusQueue, IStatusJsonStrQueueListener statusQueueListenerCallback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                statusQueueListenerCallback.onBeginListening();
                while (true) {
                    try {
                        String statusJsonStr = statusQueue.take();
                        statusQueueListenerCallback.onStatusJsonStr(statusJsonStr);
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
}
