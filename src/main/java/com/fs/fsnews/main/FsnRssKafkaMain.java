package com.fs.fsnews.main;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import es.jperea.rss.RssUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FsnRssKafkaMain {
    public static void main(String[] args) throws Exception {

        List<String> entryUrlList = new ArrayList<>();

        List<URL> feedUrlList = new ArrayList<>();

        // http://www.elmundo.es/rss/
        feedUrlList.add(new URL("http://estaticos.elmundo.es/elmundo/rss/portada.xml"));
        feedUrlList.add(new URL("http://estaticos.elmundo.es/elmundodeporte/rss/portada.xml"));

        // http://servicios.elpais.com/rss/
        feedUrlList.add(new URL("http://ep00.epimg.net/rss/elpais/portada.xml"));
        feedUrlList.add(new URL("http://ep00.epimg.net/rss/tags/noticias_mas_vistas.xml"));
        feedUrlList.add(new URL("http://ep01.epimg.net/rss/elpais/blogs.xml"));
        feedUrlList.add(new URL("http://ep00.epimg.net/rss/deportes/portada.xml"));

        // http://www.abc.es/rss/
        feedUrlList.add(new URL("http://www.abc.es/rss/feeds/abcPortada.xml"));
        feedUrlList.add(new URL("http://www.abc.es/rss/feeds/abc_ultima.xml"));
        feedUrlList.add(new URL("http://www.abc.es/rss/feeds/blogs-actualidad.xml"));
        feedUrlList.add(new URL("http://www.abc.es/rss/feeds/abc_Deportes.xml"));

        // http://www.la-razon.com/rss.html
        feedUrlList.add(new URL("http://www.la-razon.com/rss/latest/?contentType=NWS"));

//        Producer producer = FsnKafkaConfig.createProducer();
        Producer producer = null;

        while (true) {
            SyndFeedOutput syndFeedOutput = new SyndFeedOutput();

            for (URL feedUrl : feedUrlList) {
                SyndFeedInput syndFeedInput = new SyndFeedInput();
                SyndFeed inputSyndFeed = syndFeedInput.build(new XmlReader(feedUrl));
                for (Object entry : inputSyndFeed.getEntries()) {
                    SyndEntry inputSyndEntry = (SyndEntry) entry;
                    URL entryUrl = new URL(inputSyndEntry.getUri());
                    synchronized (entryUrlList) {
                        if (!entryUrlList.contains(entryUrl.toExternalForm())) {
                            entryUrlList.add(entryUrl.toExternalForm());
                        } else {
                            continue;
                        }
                    }
                    //
                    SyndFeed outputSyndFeed = (SyndFeed) inputSyndFeed.clone();
                    SyndEntry outputSyndEntry = (SyndEntry) inputSyndEntry.clone();
                    outputSyndFeed.getEntries().clear();
                    outputSyndFeed.getEntries().add(outputSyndEntry);
                    //
                    System.out.println(entryUrl.toExternalForm());
//                    System.out.println(syndFeedOutput.outputString(outputSyndFeed));
                    for (String author : RssUtils.getAuthorList(outputSyndEntry)) {
                        System.out.println("@" + author);
                    }
                    //
                    if (producer != null) {
                        KeyedMessage<String, String> message = new KeyedMessage("fsinsights", syndFeedOutput.outputString(outputSyndFeed));
                        producer.send(message);
                    }
                }
            }

            Thread.sleep(10000);
        }
    }
}
