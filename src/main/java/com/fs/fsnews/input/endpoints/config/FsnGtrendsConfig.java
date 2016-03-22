/*
 * Decompiled with CFR 0_115.
 */
package com.fs.fsnews.input.endpoints.config;

import es.jipeream.library.JavaUtils;
import java.util.Properties;

public class FsnGtrendsConfig {
    public static Properties loadProperties(String dir) throws Exception {
        Properties properties = JavaUtils.loadProperties("config/" + dir + "/fsn.gtrends.properties");
        return properties;
    }
}

