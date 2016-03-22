/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  twitter4j.Status
 *  twitter4j.TwitterObjectFactory
 */
package es.jipeream.library.twitter;

import es.jipeream.library.twitter.ITwitterQueueListener;
import java.util.concurrent.BlockingQueue;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

public abstract class TwitterQueueListener
implements ITwitterQueueListener {
    private BlockingQueue<String> twitterQueue;

    public BlockingQueue<String> getTwitterQueue() {
        return this.twitterQueue;
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
        Status status = TwitterObjectFactory.createStatus((String)statusJsonStr);
        this.onStatus(status);
    }

    public abstract void onStatus(Status var1) throws Exception;
}

