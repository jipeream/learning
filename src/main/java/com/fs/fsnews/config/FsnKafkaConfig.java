package com.fs.fsnews.config;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class FsnKafkaConfig {
    public static Producer createProducer() {
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
