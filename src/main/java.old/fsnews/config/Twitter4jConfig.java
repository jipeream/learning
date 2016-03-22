package com.fs.fsnews.config;

import es.jipeream.library.JavaUtils;

import java.util.Properties;

public class Twitter4jConfig {

    public static Properties loadProperties(String dir) throws Exception {
        Properties properties = JavaUtils.loadProperties("config/" + dir + "/twitter4j.properties");
        return properties;
    }

//    public static Properties loadJipereamProperties() throws Exception {
//        Properties properties = JavaUtils.loadProperties("config/twitter4j.properties");
//        return properties;
//    }

}