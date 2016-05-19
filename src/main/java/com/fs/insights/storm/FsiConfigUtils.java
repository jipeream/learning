package com.fs.insights.storm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class FsiConfigUtils {
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
            // InputStream inputStream = FsiConfigUtils.class.getClassLoader().getResourceAsStream(fileName);
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

    public static String getKafkaTopicName(Map conf, String id) {
        String topicName = getConfStringValue(conf, id, "kafka", "topicName");
        return topicName;
    }
}
