package com.fs.insights.storm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class FsiStormConfig {
    public static String getConfStringValue(Map conf, String...keys) {
        String key = getConfKey(keys);
        String value = (String) conf.get(key);
        return value;
    }

    private static String getConfKey(String[] keys) {
        String result = null;
        for (String key : keys) {
            if (result == null) {
                result = key;
            } else {
                result += "." + key;
            }
        }
        return result;
    }

    public static Map readConfig(Map conf, String fileName) {
        Properties properties = new Properties();
        try {
            // InputStream inputStream = FsiStormConfig.class.getClassLoader().getResourceAsStream(fileName);
            InputStream inputStream = new FileInputStream(fileName);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readConfig(conf, properties);
    }

    public static Map readConfig(Map conf, Properties properties) {
        for (Object i : properties.keySet()) {
            String key = i.toString();
            String value = properties.getProperty(key);
            conf.put(key, value);
        }
        return conf;
    }

    /**/

    public static String getKafkaTopicName(Map conf, String componentId) {
        String topicName = getConfStringValue(conf, componentId, "kafka", "topicName");
        return topicName;
    }

    /**/

    public static String getTwitterConsumerKey(Map conf, String componentId) {
        String consumerKey = getConfStringValue(conf, componentId, "twitter", "oauth", "consumerKey");
        return consumerKey;
    }

    public static String getTwitterConsumerSecret(Map conf, String componentId) {
        String consumerSecret = getConfStringValue(conf, componentId, "twitter", "oauth", "consumerSecret");
        return consumerSecret;
    }

    public static String getTwitterAccessToken(Map conf, String componentId) {
        String accessToken = getConfStringValue(conf, componentId, "twitter", "oauth", "accessToken");
        return accessToken;
    }

    public static String getTwitterAccessTokenSecret(Map conf, String componentId) {
        String accessTokenSecret = getConfStringValue(conf, componentId, "twitter", "oauth", "accessTokenSecret");
        return accessTokenSecret;
    }

    public static String[] getTwitterKeywords(Map conf, String componentId) {
        String keywords = getConfStringValue(conf, componentId, "twitter", "keywords");
        return keywords.split(",");
    }

    /**/

//    public static String getInputDataFieldName(Map conf, String componentId) {
//        String dataFieldName = "#fsi#data";
//        return dataFieldName;
//    }
//
//    public static String getOutputDataFieldName(Map conf, String componentId) {
//        String dataFieldName = "#fsi#data";
//        return dataFieldName;
//    }

    public static String getInputDataFieldName() {
        String dataFieldName = "#fsi#data";
        return dataFieldName;
    }

    public static String getOutputDataFieldName() {
        String dataFieldName = "#fsi#data";
        return dataFieldName;
    }

}
