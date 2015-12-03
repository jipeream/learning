package com.fs.fsnews.input.endpoints;

import com.fs.fsnews.input.config.FsnRssConfig;
import com.fs.fsnews.input.config.KafkaConfig;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import es.jipeream.library.kafka.KafkaUtils;
import es.jipeream.library.rss.RssUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerClosedException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FsnInputRssEndpointMain {
    public static void main(String[] args) throws Exception {
        Properties kafkaProperties = KafkaConfig.loadProperties("");
        Producer producer = KafkaUtils.createProducer(kafkaProperties);
        //
        List<String> entryUrlList = new ArrayList<>();
        //
        Properties fsnRssProperties = FsnRssConfig.loadProperties("");
        //
        List<URL> feedUrlList = FsnRssConfig.getFeedUrlList(fsnRssProperties);
        //
        while (true) {
            SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
            //
            for (URL feedUrl : feedUrlList) {
                try {
                    SyndFeedInput syndFeedInput = new SyndFeedInput();
                    SyndFeed inputSyndFeed = syndFeedInput.build(new XmlReader(feedUrl));
                    for (Object entry : inputSyndFeed.getEntries()) {
                        SyndEntry inputSyndEntry = (SyndEntry) entry;
                        String urlStr = inputSyndEntry.getLink();
                        // System.err.println(urlStr);
                        URL entryUrl = new URL(urlStr);
                        synchronized (entryUrlList) {
                            if (!entryUrlList.contains(entryUrl.toExternalForm())) {
                                entryUrlList.add(entryUrl.toExternalForm());
                            } else {
                                continue;
                            }
                        }
                        //
                        try {
                            SyndFeed outputSyndFeed = (SyndFeed) inputSyndFeed.clone();
                            SyndEntry outputSyndEntry = (SyndEntry) inputSyndEntry.clone();
                            outputSyndFeed.getEntries().clear();
                            outputSyndFeed.getEntries().add(outputSyndEntry);
                            //
                            System.out.println(entryUrl.toExternalForm());
//                            System.out.println(syndFeedOutput.outputString(outputSyndFeed));
                            String title = outputSyndEntry.getTitle();
                            SyndContent description = outputSyndEntry.getDescription();
                            System.out.println("Title: " + title);
                            System.out.println("Description: " + (description == null ? "-" : description.getValue().length()));
                            for (String author : RssUtils.getAuthorList(outputSyndEntry)) {
                                System.out.println("@" + author);
                            }
                            //
                            if (producer != null) {
                                String topic = fsnRssProperties.getProperty("fsn.rss.topic");
                                KeyedMessage<String, String> message = new KeyedMessage(topic, syndFeedOutput.outputString(outputSyndFeed));
                                producer.send(message);
                            }
                        } catch (Exception e) {
                            synchronized (entryUrlList) {
                                entryUrlList.remove(entryUrl.toExternalForm());
                            }
                            if (e instanceof ProducerClosedException) {
                                producer = KafkaUtils.createProducer(kafkaProperties);
                            }
                        }
                    }
                } catch (Exception e) {
                    // log
                }
            }
            //
            Thread.sleep(Long.parseLong(fsnRssProperties.getProperty("fsn.rss.loopIntervalMs", "10000")));
            //
            if (producer != null) {
                producer.close();
            }
        }
    }
}
