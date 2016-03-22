/*
 * Decompiled with CFR 0_115.
 */
package com.fs.fsnews.config;

import es.jipeream.library.JavaUtils;
import java.util.Properties;

public class KafkaConfig {
    public static Properties loadProperties(String dir) throws Exception {
        Properties properties = JavaUtils.loadProperties("config/" + dir + "/kafka.properties");
        return properties;
    }
}

