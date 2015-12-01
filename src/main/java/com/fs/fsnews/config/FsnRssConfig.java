package com.fs.fsnews.config;

import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import es.jipeream.library.JavaUtils;
import es.jipeream.library.twitter.TwitterUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FsnRssConfig {

    public static Properties loadProperties() throws Exception {
        Properties properties = JavaUtils.loadProperties(new File("config/fsn.rss.properties"));
        return properties;
    }

    public static List<URL> getFeedUrlList(Properties properties) throws Exception {
        String feeds = properties.getProperty("fsn.rss.feeds");
        //
        List<URL> feedUrlList = new ArrayList<>();
        //
        for (String feed : feeds.split(",")) {
            String feedUrl = properties.getProperty(feed + ".url");
            if (!JavaUtils.isNullOrEmpty(feedUrl)) {
                feedUrlList.add(new URL(feedUrl));
            }
        }
        //
        return feedUrlList;
    }

    public static List<URL> getTestFeedUrlList() throws Exception {
        List<URL> feedUrlList = new ArrayList<>();
        //
        // http://www.elmundo.es/rss/
        feedUrlList.add(new URL("http://estaticos.elmundo.es/elmundo/rss/portada.xml"));
        feedUrlList.add(new URL("http://estaticos.elmundo.es/elmundodeporte/rss/portada.xml"));
        //
        // http://servicios.elpais.com/rss/
        feedUrlList.add(new URL("http://ep00.epimg.net/rss/elpais/portada.xml"));
        feedUrlList.add(new URL("http://ep00.epimg.net/rss/tags/noticias_mas_vistas.xml"));
        feedUrlList.add(new URL("http://ep01.epimg.net/rss/elpais/blogs.xml"));
        feedUrlList.add(new URL("http://ep00.epimg.net/rss/deportes/portada.xml"));
        //
        // http://www.abc.es/rss/
        feedUrlList.add(new URL("http://www.abc.es/rss/feeds/abcPortada.xml"));
        feedUrlList.add(new URL("http://www.abc.es/rss/feeds/abc_ultima.xml"));
        feedUrlList.add(new URL("http://www.abc.es/rss/feeds/blogs-actualidad.xml"));
        feedUrlList.add(new URL("http://www.abc.es/rss/feeds/abc_Deportes.xml"));
        //
        // http://www.la-razon.com/rss.html
        feedUrlList.add(new URL("http://www.larazon.es/rss/portada.xml"));
        // feedUrlList.add(new URL("http://www.la-razon.com/rss/latest/?contentType=NWS"));
        //
        // http://www.efe.com
        feedUrlList.add(new URL("http://www.efe.com/efe/espana/1/rss"));
        //
        return feedUrlList;
    }
}
