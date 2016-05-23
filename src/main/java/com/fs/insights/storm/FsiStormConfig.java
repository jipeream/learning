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


    public static String getKafkaTopicName(Map conf, String componentId) {
        String topicName = getConfStringValue(conf, componentId, "kafka", "topicName");
        return topicName;
    }

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

//    public static String getKafkaTopicName(IFsiStormComponent fsiComponent) {
//        return getKafkaTopicName(fsiComponent.getConf(), fsiComponent.getComponentId());
//    }
//
//    public static String getInputDataFieldName(IFsiStormComponent fsiComponent) {
//        return getInputDataFieldName(fsiComponent.getConf(), fsiComponent.getComponentId());
//    }
//
//    public static String getOutputDataFieldName(IFsiStormComponent fsiComponent) {
//        return getOutputDataFieldName(fsiComponent.getConf(), fsiComponent.getComponentId());
//    }

//    public static String getKafkaZkRoot() {
//        String zkRoot = "/" + "kafka-storm";
//        return zkRoot;
//    }

}
