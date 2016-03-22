/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  com.sun.syndication.feed.synd.SyndContent
 *  com.sun.syndication.feed.synd.SyndEntry
 *  com.sun.syndication.feed.synd.SyndFeed
 *  com.sun.syndication.io.SyndFeedInput
 *  com.sun.syndication.io.SyndFeedOutput
 *  com.sun.syndication.io.XmlReader
 *  kafka.javaapi.producer.Producer
 *  kafka.producer.KeyedMessage
 *  kafka.producer.ProducerClosedException
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.fs.fsnews.input.endpoints;

import com.fs.fsnews.article.Article;
import com.fs.fsnews.article.ArticleUtils;
import com.fs.fsnews.config.KafkaConfig;
import com.fs.fsnews.input.endpoints.config.FsnRssConfig;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import es.jipeream.library.JavaUtils;
import es.jipeream.library.kafka.KafkaUtils;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerClosedException;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class FsnInputArticleEndpointMain {
    static Logger logger = Logger.getLogger((Class)FsnInputArticleEndpointMain.class);

    public static void main(String[] args) throws Exception {
        String configDir = JavaUtils.getConfigDir(args);
        Properties kafkaProperties = KafkaConfig.loadProperties(configDir);
        Producer producer = null;
        ArrayList<String> entryUrlList = new ArrayList<String>();
        Properties fsnRssProperties = FsnRssConfig.loadProperties(configDir);
        List<URL> feedUrlList = FsnRssConfig.getFeedUrlList(fsnRssProperties);
        do {
            SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
            for (URL feedUrl : feedUrlList)
                try {
                    SyndFeedInput syndFeedInput = new SyndFeedInput();
                    SyndFeed inputSyndFeed = syndFeedInput.build((Reader) new XmlReader(feedUrl));
                    String inputSyndRssString = syndFeedOutput.outputString(inputSyndFeed);
                    for (Object entry : inputSyndFeed.getEntries()) {
                        SyndEntry outputSyndEntry;
                        SyndEntry inputSyndEntry = (SyndEntry) entry;
                        String urlStr = inputSyndEntry.getLink();
                        logger.trace(urlStr);
                        URL entryUrl = new URL(urlStr);
                        ArrayList<String> arrayList = entryUrlList;
                        synchronized (arrayList) {
                            if (entryUrlList.contains(entryUrl.toExternalForm())) {
                                continue;
                            }
                            entryUrlList.add(entryUrl.toExternalForm());
                        }
                        try {
                            SyndFeed outputSyndFeed = (SyndFeed) inputSyndFeed.clone();
                            outputSyndEntry = (SyndEntry) inputSyndEntry.clone();
                            outputSyndFeed.getEntries().clear();
                            outputSyndFeed.getEntries().add(outputSyndEntry);
                            String outputSyndRssString = syndFeedOutput.outputString(outputSyndFeed);
                            String title = outputSyndEntry.getTitle();
                            SyndContent description = outputSyndEntry.getDescription();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("rss", outputSyndRssString);
                            Article article = ArticleUtils.extractArticleData(entryUrl);
                            if (article != null) {
                                logger.info("[" + article.getJsonObject().toString(2) + "]");
                                jsonObject.put("article", article.getJsonObject());
                            }
                            if (producer == null) {
                                producer = KafkaUtils.createProducer(kafkaProperties);
                            }
                            if (producer == null) continue;
                            String topic = fsnRssProperties.getProperty("fsn.article.topic");
                            KeyedMessage message = new KeyedMessage(topic, jsonObject.toString());
                            producer.send(message);
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
                                producer = KafkaUtils.createProducer(kafkaProperties);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error", e);
                }
            if (producer != null) {
                producer.close();
                producer = null;
            }
            Thread.sleep(Long.parseLong(fsnRssProperties.getProperty("fsn.rss.loopIntervalMs", "10000")));
        } while (true);
    }
}

