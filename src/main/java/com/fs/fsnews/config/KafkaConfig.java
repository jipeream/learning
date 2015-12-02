package com.fs.fsnews.config;

import es.jipeream.library.JavaUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.io.File;
import java.util.Properties;

public class KafkaConfig {

    public static Properties loadProperties(String dir) throws Exception {
        Properties properties = JavaUtils.loadProperties("config/"+ dir + "/kafka.properties");
        return properties;
    }

//    public static Properties loadLocalProperties() throws Exception {
//        Properties properties = JavaUtils.loadProperties("config/kafka.local.properties");
//        return properties;
//    }
//
//    public static Properties loadPreproProperties() throws Exception {
//        Properties properties = JavaUtils.loadProperties("config/kafka.prepro.properties");
//        return properties;
//    }

}
