package com.fs.fsnews.main;

import es.jipeream.library.kafka.KfkConsumerGroup;

import java.util.Properties;

public class FsnKafkaConsumerMain {
    public static void main(String[] args) throws Exception {
        testConsumer();
    }

    /**/

    private final static String zookeeperConnect = "127.0.0.1:2181";
    private final static String topic = "testTopic";
    private final static int numThreads = 1;
    private final static String groupId = "testGroupId";

    private static Properties createProperties() {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", zookeeperConnect);
        properties.put("group.id", groupId);
        properties.put("zookeeper.session.timeout.ms", "5000");
        properties.put("zookeeper.sync.time.ms", "250");
        properties.put("auto.commit.interval.ms", "1000");
        // properties.put("serializer.class", "kafka.serializer.StringEncoder");
        return properties;
    }

    /**/

    private static void testConsumer() throws Exception {
        KfkConsumerGroup kfkConsumerGroup = new KfkConsumerGroup(createProperties(), topic) {
            @Override
            protected void onMessageConsumed(byte[] message) {
                System.out.println(new String(message));
            }
        };
        kfkConsumerGroup.run(numThreads);

        Thread.sleep(10000);

        kfkConsumerGroup.shutdown();
    }

    /**/

//    private static void testProducer() throws Exception {
//        ProducerData<String, String> data = new ProducerData<String, String>("test-topic", "test-message");
//        producer.send(data);
//    }
}
