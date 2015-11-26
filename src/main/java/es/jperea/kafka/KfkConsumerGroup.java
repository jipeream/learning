package es.jperea.kafka;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class KfkConsumerGroup {
    private final ConsumerConnector consumerConnector;
    private final String topic;
    private ExecutorService executor;

    public KfkConsumerGroup(Properties properties, String topic) {
        this.consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
        this.topic = topic;
    }

    public void shutdown() {
        if (consumerConnector != null) consumerConnector.shutdown();
        if (executor != null) executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
    }

    public void run(int numThreads) {
        Map<String, Integer> numThreadsByTopicMap = new HashMap<>();
        numThreadsByTopicMap.put(topic, new Integer(numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> kafkaStreamListByTopicMap = consumerConnector.createMessageStreams(numThreadsByTopicMap);
        List<KafkaStream<byte[], byte[]>> kafkaStreamList = kafkaStreamListByTopicMap.get(topic);

        executor = Executors.newFixedThreadPool(numThreads);

        int threadNumber = 0;
        for (final KafkaStream kafkaStream : kafkaStreamList) {
            executor.submit(new KfkConsumerThread(kafkaStream, threadNumber));
            threadNumber++;
        }
    }

    protected abstract void onMessageConsumed(byte[] message);

    private class KfkConsumerThread implements Runnable {
        private final KafkaStream kafkaStream;
        private final int threadNumber;

        public KfkConsumerThread(KafkaStream kafkaStream, int threadNumber) {
            this.kafkaStream = kafkaStream;
            this.threadNumber = threadNumber;
        }

        @Override
        public void run() {
            System.out.println("Starting thread: " + threadNumber);
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                byte[] message = it.next().message();
                // System.out.println("Consumed in thread " + threadNumber + ": " + new String(message));
                onMessageConsumed(message);
            }
            System.out.println("Shutting down thread: " + threadNumber);
        }
    }

}
