package es.jipeream.library.twitter;

import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

import java.util.concurrent.BlockingQueue;

public abstract class TwitterQueueListener implements ITwitterQueueListener {
    private BlockingQueue<String> twitterQueue;

    public BlockingQueue<String> getTwitterQueue() {
        return twitterQueue;
    }

    @Override
    public void onBeginListening(BlockingQueue<String> twitterQueue) {
        this.twitterQueue = twitterQueue;
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
