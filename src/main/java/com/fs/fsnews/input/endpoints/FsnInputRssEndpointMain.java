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
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FsnInputRssEndpointMain {
    static Logger logger = Logger.getLogger(FsnInputRssEndpointMain.class);

    public static void main(String[] args) throws Exception {
        Properties kafkaProperties = KafkaConfig.loadProperties("");
        Producer producer = null;
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
                        logger.debug(urlStr);
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
                            logger.info(entryUrl.toExternalForm());
//                            logger.info(syndFeedOutput.outputString(outputSyndFeed));
                            String title = outputSyndEntry.getTitle();
                            SyndContent description = outputSyndEntry.getDescription();
                            logger.info("Title: " + title);
                            logger.info("Description: " + (description == null ? "-" : description.getValue().length()));
                            for (String author : RssUtils.getAuthorList(outputSyndEntry)) {
                                logger.info("@" + author);
                            }
                            //
                            if (producer == null) {
                                producer = KafkaUtils.createProducer(kafkaProperties);
                            }
                            //
                            if (producer != null) {
                                String topic = fsnRssProperties.getProperty("fsn.rss.topic");
                                KeyedMessage<String, String> message = new KeyedMessage(topic, syndFeedOutput.outputString(outputSyndFeed));
                                producer.send(message);
                            }
                        } catch (Exception e) {
                            logger.error("Error", e);
                            synchronized (entryUrlList) {
                                entryUrlList.remove(entryUrl.toExternalForm());
                            }
                            if (e instanceof ProducerClosedException) {
                                if (producer != null) {
                                    producer.close();
                                    producer = null;
                                }
                                //
                                producer = KafkaUtils.createProducer(kafkaProperties);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error", e);
                }
            }
            //
            if (producer != null) {
                producer.close();
                producer = null;
            }
            //
            Thread.sleep(Long.parseLong(fsnRssProperties.getProperty("fsn.rss.loopIntervalMs", "10000")));
        }
        //
//        if (producer != null) {
//            producer.close();
//            producer = null;
//        }
    }
}
