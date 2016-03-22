/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  com.twitter.hbc.core.endpoint.StreamingEndpoint
 */
package com.fs.fsnews.input.endpoints.config;

import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import es.jipeream.library.JavaUtils;
import es.jipeream.library.twitter.TwitterUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FsnTwitterConfig {
    public static final String TWITTER_CLIENT_BasicClient = "BasicClient";
    public static final String TWITTER_CLIENT_Twitter4jClient = "Twitter4jClient";

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
}

