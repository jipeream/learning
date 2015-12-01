package com.fs.fsnews.config;

import es.jipeream.library.JavaUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.io.File;
import java.util.Properties;

public class KafkaConfig {
    public static final String TOPIC_fsinsights_twitter = "fsinsights_twitter";
    public static final String TOPIC_fsinsights_rss = "fsinsights_rss";

    public static Properties loadProperties() throws Exception {
        Properties properties = JavaUtils.loadProperties(new File("config/kafka.properties"));
        return properties;
    }

    public static Properties loadLocalProperties() throws Exception {
        Properties properties = JavaUtils.loadProperties(new File("config/kafka.local.properties"));
        return properties;
    }

    public static Properties loadPreproProperties() throws Exception {
        Properties properties = JavaUtils.loadProperties(new File("config/kafka.prepro.properties"));
        return properties;
    }

}
