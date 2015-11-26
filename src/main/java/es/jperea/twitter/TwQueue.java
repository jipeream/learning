package es.jperea.twitter;

import kafka.producer.KeyedMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwQueue {
    public TwQueue(int queueSize) {
        this.blockingQueue = new LinkedBlockingQueue<String>(queueSize);
    }

    private final BlockingQueue<String> blockingQueue;

    public BlockingQueue<String> getBlockingQueue() {
        return blockingQueue;
    }
}
