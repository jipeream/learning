package com.fs.fsnews.config;

import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import es.jipeream.library.JavaUtils;
import es.jipeream.library.twitter.TwitterUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FsnTwitterConfig {

    public static Properties loadProperties() throws Exception {
        Properties properties = JavaUtils.loadProperties(new File("config/fsn.twitter.properties"));
        return properties;
    }

    public static List<StreamingEndpoint> getStreamingEndpointList(Properties properties) throws Exception {
        String endpoints = properties.getProperty("fsn.twitter.endpoints");
        //
        List<StreamingEndpoint> streamingEndpointList = new ArrayList<>();
        //
        for (String endpoint : endpoints.split(",")) {
            String type = properties.getProperty(endpoint + ".type");
            if (!JavaUtils.isNullOrEmpty(type)) {
                if ("keywords".equals(type)) {
                    String[] keywords = properties.getProperty(endpoint + ".keywords").split(",");
                    streamingEndpointList.add(TwitterUtils.createStatusesFilterEndpoint(keywords));
                };
                if ("userIds".equals(type)) {
                    // TODO
                };
                if ("user".equals(type)) {
                    streamingEndpointList.add(TwitterUtils.createUserstreamEndpoint());
                };
                if ("sample".equals(type)) {
                    streamingEndpointList.add(TwitterUtils.createStatusesSampleEndpoint());
                };
            }
        }
        //
        return streamingEndpointList;
    }

    /**/

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

    public static List<StreamingEndpoint> getTestFeedUrlList() throws Exception {
        List<StreamingEndpoint> streamingEndpointList = new ArrayList<>();
        //
        streamingEndpointList.add(TwitterUtils.createStatusesFilterEndpoint(
                "@" + TWITTER_USER_SCREEN_NAME_elmundoes,
                "@" + TWITTER_USER_SCREEN_NAME_abc_es,
                "@" + TWITTER_USER_SCREEN_NAME_larazon_es,
                "@" + TWITTER_USER_SCREEN_NAME_el_pais,
                "@" + TWITTER_USER_SCREEN_NAME_EFEnoticias,
                "@" + TWITTER_USER_SCREEN_NAME_europapress
        ));
        //
//        streamingEndpointList.add(TwitterUtils.createStatusesFilterEndpoint(
//                TWITTER_USER_SCREEN_NAME_elmundoes,
//                TWITTER_USER_SCREEN_NAME_abc_es,
//                TWITTER_USER_SCREEN_NAME_larazon_es,
//                TWITTER_USER_SCREEN_NAME_el_pais,
//                TWITTER_USER_SCREEN_NAME_EFEnoticias,
//                TWITTER_USER_SCREEN_NAME_europapress
//        ));
        //
//        streamingEndpointList.add(TwitterUtils.createStatusesFilterEndpoint(
//                TWITTER_USER_ID_elmundoes,
//                TWITTER_USER_ID_abc_es,
//                TWITTER_USER_ID_larazon_es,
//                TWITTER_USER_ID_el_pais,
//                TWITTER_USER_ID_EFEnoticias,
//                TWITTER_USER_ID_europapress
//        ));
        //
//        streamingEndpointList.add(TwitterUtils.createUserstreamEndpoint());
        //
//        streamingEndpointList.add(TwitterUtils.createStatusesSampleEndpoint());
        //
        return streamingEndpointList;
    }
}
