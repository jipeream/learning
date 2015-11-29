package com.fs.fsnews.config;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class FsnKafkaConfig {
    public static final String TOPIC_fsinsights_twitter = "fsinsights_twitter";
    public static final String TOPIC_fsinsights_rss = "fsinsights_rss";

    public static Producer createLocalhostProducer() {
        Properties properties = new Properties();
        properties.put("metadata.broker.list", "localhost:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        // properties.put("serializer.class", "org.apache.kafka.common.serialization.ByteArraySerializer");
        // properties.put("client.id", "camus");

        ProducerConfig producerConfig = new ProducerConfig(properties);
        Producer<String, String> producer = new Producer(producerConfig);

        return producer;
    }

    public static Producer createPreproProducer() {
        Properties properties = new Properties();
        properties.put("metadata.broker.list", "192.168.3.228:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        // properties.put("serializer.class", "org.apache.kafka.common.serialization.ByteArraySerializer");
        // properties.put("client.id", "camus");

        ProducerConfig producerConfig = new ProducerConfig(properties);
        Producer<String, String> producer = new Producer(producerConfig);

        return producer;
    }

}
