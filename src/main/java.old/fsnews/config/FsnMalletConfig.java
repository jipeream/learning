package com.fs.fsnews.config;

import es.jipeream.library.JavaUtils;

import java.util.Properties;

public class FsnMalletConfig {
    public static Properties loadProperties(String dir) throws Exception {
        Properties properties = JavaUtils.loadProperties("config/" + dir + "/fsn.mallet.properties");
        return properties;
    }

}
