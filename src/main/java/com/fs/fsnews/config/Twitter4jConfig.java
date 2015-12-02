package com.fs.fsnews.config;

import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import es.jipeream.library.JavaUtils;
import es.jipeream.library.twitter.TwitterUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Twitter4jConfig {

    public static Properties loadProperties(String dir) throws Exception {
        Properties properties = JavaUtils.loadProperties("config/"+ dir + "/twitter4j.properties");
        return properties;
    }

//    public static Properties loadJipereamProperties() throws Exception {
//        Properties properties = JavaUtils.loadProperties("config/twitter4j.jipeream.properties");
//        return properties;
//    }

}