package com.fs.fsnews.main;

import com.fs.fsnews.config.FsnKafkaConfig;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import twitter4j.Status;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FsnRssKafkaMain {
    public static void main(String[] args) throws Exception {

        List<String> entryUrlList = new ArrayList<>();

        List<URL> feedUrlList = new ArrayList<>();
        feedUrlList.add(new URL("http://estaticos.elmundo.es/elmundo/rss/portada.xml"));
        feedUrlList.add(new URL("http://estaticos.elmundo.es/elmundodeporte/rss/portada.xml"));

//        Producer producer = FsnKafkaConfig.createProducer();
        Producer producer = null;

        while (true) {
            SyndFeedOutput syndFeedOutput = new SyndFeedOutput();

            for (URL feedUrl : feedUrlList) {
                SyndFeedInput syndFeedInput = new SyndFeedInput();
                SyndFeed inputSyndFeed = syndFeedInput.build(new XmlReader(feedUrl));
                for (Object entry : inputSyndFeed.getEntries()) {
                    SyndEntry inputSyndEntry = (SyndEntry) entry;
                    String entryUrl = inputSyndEntry.getUri();
                    synchronized (entryUrlList) {
                        if (!entryUrlList.contains(entryUrl)) {
                            entryUrlList.add(entryUrl);
                            //
                            SyndFeed outputSyndFeed = (SyndFeed) inputSyndFeed.clone();
                            SyndEntry outputSyndEntry = (SyndEntry) inputSyndEntry.clone();
                            outputSyndFeed.getEntries().clear();
                            outputSyndFeed.getEntries().add(outputSyndEntry);
                            //
                            // System.out.println(entryUrl);
                            System.out.println(syndFeedOutput.outputString(outputSyndFeed));
                            //
                            if (producer != null) {
                                KeyedMessage<String, String> message = new KeyedMessage("fsinsights", syndFeedOutput.outputString(outputSyndFeed));
                                producer.send(message);
                            }
                        }
                    }
                }
            }

            Thread.sleep(10000);
        }
    }
}
