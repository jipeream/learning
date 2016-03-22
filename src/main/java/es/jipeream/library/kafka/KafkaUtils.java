/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  kafka.javaapi.producer.Producer
 *  kafka.producer.ProducerConfig
 */
package es.jipeream.library.kafka;

import es.jipeream.library.JavaUtils;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

public class KafkaUtils {
    public static Producer createProducer(Properties properties) throws Exception {
        if (JavaUtils.isNullOrEmpty(properties.getProperty("metadata.broker.list"))) {
            return null;
        }
        ProducerConfig producerConfig = new ProducerConfig(properties);
        Producer producer = new Producer(producerConfig);
        return producer;
    }
}

