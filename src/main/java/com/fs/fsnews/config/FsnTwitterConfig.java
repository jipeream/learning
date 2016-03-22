/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  com.twitter.hbc.core.endpoint.StreamingEndpoint
 */
package com.fs.fsnews.config;

import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import es.jipeream.library.JavaUtils;
import es.jipeream.library.twitter.TwitterUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FsnTwitterConfig {
    public static final String TWITTER_CLIENT_BasicClient = "BasicClient";
    public static final String TWITTER_CLIENT_Twitter4jClient = "Twitter4jClient";
    public static final long TWITTER_USER_ID_el_pais = 7996082;
    public static final String TWITTER_USER_SCREEN_NAME_el_pais = "el_pais";
    public static final long TWITTER_USER_ID_elmundoes = 14436030;
    public static final String TWITTER_USER_SCREEN_NAME_elmundoes = "elmundoes";
    public static final long TWITTER_USER_ID_abc_es = 19923515;
    public static final String TWITTER_USER_SCREEN_NAME_abc_es = "abc_es";
    public static final long TWITTER_USER_ID_larazon_es = 112694236;
    public static final String TWITTER_USER_SCREEN_NAME_larazon_es = "larazon_es";
    public static final long TWITTER_USER_ID_EFEnoticias = 0;
    public static final String TWITTER_USER_SCREEN_NAME_EFEnoticias = "EFEnoticias";
    public static final long TWITTER_USER_ID_europapress = 121385551;
    public static final String TWITTER_USER_SCREEN_NAME_europapress = "europapress";

    public static Properties loadProperties(String dir) throws Exception {
        Properties properties = JavaUtils.loadProperties("config/" + dir + "/fsn.twitter.properties");
        return properties;
    }

    public static List<StreamingEndpoint> getStreamingEndpointList(Properties properties) throws Exception {
        String endpoints = properties.getProperty("fsn.twitter.endpoints");
        ArrayList<StreamingEndpoint> streamingEndpointList = new ArrayList<StreamingEndpoint>();
        for (String endpoint : endpoints.split(",")) {
            String type = properties.getProperty(endpoint + ".type");
            if (JavaUtils.isNullOrEmpty(type)) continue;
            if ("keywords".equals(type)) {
                String[] keywords = properties.getProperty(endpoint + ".keywords").split(",");
                streamingEndpointList.add((StreamingEndpoint)TwitterUtils.createStatusesFilterEndpoint(keywords));
            }
            if ("userIds".equals(type)) {
                // empty if block
            }
            if ("user".equals(type)) {
                streamingEndpointList.add((StreamingEndpoint)TwitterUtils.createUserstreamEndpoint());
            }
            if (!"sample".equals(type)) continue;
            streamingEndpointList.add((StreamingEndpoint)TwitterUtils.createStatusesSampleEndpoint());
        }
        return streamingEndpointList;
    }

    public static List<StreamingEndpoint> getTestFeedUrlList() throws Exception {
        ArrayList<StreamingEndpoint> streamingEndpointList = new ArrayList<StreamingEndpoint>();
        streamingEndpointList.add((StreamingEndpoint)TwitterUtils.createStatusesFilterEndpoint("@elmundoes", "@abc_es", "@larazon_es", "@el_pais", "@EFEnoticias", "@europapress"));
        return streamingEndpointList;
    }
}

