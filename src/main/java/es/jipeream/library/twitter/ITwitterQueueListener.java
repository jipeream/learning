/*
 * Decompiled with CFR 0_115.
 */
package es.jipeream.library.twitter;

import java.util.concurrent.BlockingQueue;

public interface ITwitterQueueListener {
    public void onBeginListening(BlockingQueue<String> var1);

    public void onEndListening();

    public void onStatusJsonStr(String var1) throws Exception;
}

