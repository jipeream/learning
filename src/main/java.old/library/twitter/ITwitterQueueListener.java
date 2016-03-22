package es.jipeream.library.twitter;

import java.util.concurrent.BlockingQueue;

public interface ITwitterQueueListener {
    void onBeginListening(BlockingQueue<String> twitterQueue);

    void onEndListening();

    void onStatusJsonStr(String statusJsonStr) throws Exception;
}
