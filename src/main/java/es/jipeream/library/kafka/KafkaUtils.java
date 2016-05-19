package es.jipeream.library.kafka;

import es.jipeream.library.JavaUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class KafkaUtils {

    public static Producer createProducer(Properties properties) throws Exception {
        if (JavaUtils.isNullOrEmpty(properties.getProperty("metadata.broker.list"))) {
            return null;
        } else {
            ProducerConfig producerConfig = new ProducerConfig(properties);
            Producer<String, String> producer = new Producer(producerConfig);
            return producer;
        }
    }

}
