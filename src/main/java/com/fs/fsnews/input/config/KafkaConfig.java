package com.fs.fsnews.input.config;

import es.jipeream.library.JavaUtils;

import java.util.Properties;

public class KafkaConfig {

    public static Properties loadProperties(String dir) throws Exception {
        Properties properties = JavaUtils.loadProperties("config/" + dir + "/kafka.properties");
        return properties;
    }

//    public static Properties loadLocalProperties() throws Exception {
//        Properties properties = JavaUtils.loadProperties("config/kafka.properties");
//        return properties;
//    }
//
//    public static Properties loadPreproProperties() throws Exception {
//        Properties properties = JavaUtils.loadProperties("config/kafka.properties");
//        return properties;
//    }

}
