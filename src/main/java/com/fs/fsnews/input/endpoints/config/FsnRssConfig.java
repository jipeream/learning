/*
 * Decompiled with CFR 0_115.
 */
package com.fs.fsnews.input.endpoints.config;

import es.jipeream.library.JavaUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FsnRssConfig {
    public static Properties loadProperties(String dir) throws Exception {
        Properties properties = JavaUtils.loadProperties("config/" + dir + "/fsn.rss.properties");
        return properties;
    }

    public static List<URL> getFeedUrlList(Properties properties) throws Exception {
        String feeds = properties.getProperty("fsn.rss.feeds");
        ArrayList<URL> feedUrlList = new ArrayList<URL>();
        for (String feed : feeds.split(",")) {
            String feedUrl = properties.getProperty(feed + ".url");
            if (JavaUtils.isNullOrEmpty(feedUrl)) continue;
            feedUrlList.add(new URL(feedUrl));
        }
        return feedUrlList;
    }
}

