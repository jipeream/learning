package com.fs.fsnews.input.endpoints;

import com.fs.fsnews.article.Article;
import com.fs.fsnews.article.extractors.MicroDataDocumentExtractor;
import com.fs.fsnews.config.KafkaConfig;
import com.fs.fsnews.input.endpoints.config.FsnRssConfig;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import es.jipeream.library.JavaUtils;
import es.jipeream.library.kafka.KafkaUtils;
import es.jipeream.library.rss.RssUtils;

import java.io.File;
import java.io.PrintWriter;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FsnInputRssEndpointMain {
    static Logger logger = Logger.getLogger((Class) FsnInputRssEndpointMain.class);

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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws Exception {
        String configDir = JavaUtils.getConfigDir(args);
        Properties kafkaProperties = KafkaConfig.loadProperties(configDir);
        Producer producer = null;
        ArrayList<String> entryUrlList = new ArrayList();
        Properties fsnRssProperties = FsnRssConfig.loadProperties(configDir);
        List<URL> feedUrlList = FsnInputRssEndpointMain.getFeedUrlList(fsnRssProperties);
        do {
            SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
            for (URL feedUrl : feedUrlList) {
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
                            Element articleBodyElement;
                            int timeout;
                            Element newsArticleElement;
                            Document document;
                            SyndFeed outputSyndFeed = (SyndFeed) inputSyndFeed.clone();
                            outputSyndEntry = (SyndEntry) inputSyndEntry.clone();
                            outputSyndFeed.getEntries().clear();
                            outputSyndFeed.getEntries().add(outputSyndEntry);
                            logger.info((Object) entryUrl.toExternalForm());
                            String title = outputSyndEntry.getTitle();
                            SyndContent description = outputSyndEntry.getDescription();
                            logger.info((Object) ("Title: " + title));
                            logger.info((Object) ("Description: " + (description == null ? "-" : Integer.valueOf(description.getValue().length()))));
                            for (String author : RssUtils.getAuthorList((SyndEntry) outputSyndEntry)) {
                                logger.info((Object) ("@" + author));
                            }
                            URL imageUrl = RssUtils.getBestImageUrl(outputSyndFeed);
                            if (imageUrl != null) {
                                logger.info((Object) ("[" + imageUrl.toString() + "]"));
                            }
                            document = Jsoup.parse((URL) entryUrl, (int) (timeout = 5000));
                            newsArticleElement = document.select("[itemtype=http://schema.org/NewsArticle]").first();
                            articleBodyElement = document.select("[itemprop=articleBody]").first();
                            if (newsArticleElement != null && articleBodyElement != null) {
                                String articleBody = articleBodyElement.text();
                                SyndContentImpl syndContent = new SyndContentImpl();
                                syndContent.setType("text/plain");
                                syndContent.setValue(articleBody);
                                ArrayList<SyndContentImpl> contents = new ArrayList();
                                contents.add(syndContent);
                                outputSyndEntry.setContents(contents);
                            }
                            String outputSyndRssString = syndFeedOutput.outputString(outputSyndFeed);
                            logger.info((Object) outputSyndRssString);
                            String rssString = outputSyndRssString;
                            if (producer == null) {
                                producer = KafkaUtils.createProducer(kafkaProperties);
                            }
                            if (producer != null) {
                                String topic = fsnRssProperties.getProperty("fsn.rss.topic");
                                KeyedMessage message = new KeyedMessage(topic, rssString);
                                producer.send(message);
                                continue;
                            }
                            File outDir = new File("samples/out");
                            if (!outDir.exists()) continue;
                            File mediaDir = new File(outDir, entryUrl.getHost());
                            mediaDir.mkdirs();
                            String fileName = outputSyndEntry.getTitle() + ".xml";
                            fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                            File file = new File(mediaDir, fileName);
                            PrintWriter printWriter = new PrintWriter(file);
                            printWriter.write(outputSyndRssString);
                            printWriter.close();
                            fileName = outputSyndEntry.getTitle() + ".json";
                            Article article = new Article();
                            new MicroDataDocumentExtractor().extract(article.getJsonObject(), document);
                            fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                            File file2 = new File(mediaDir, fileName);
                            PrintWriter printWriter2 = new PrintWriter(file2);
                            printWriter2.write(article.getJsonObject().toString(2));
                            printWriter2.close();
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
            }
            if (producer != null) {
                producer.close();
                producer = null;
            }
            Thread.sleep(Long.parseLong(fsnRssProperties.getProperty("fsn.rss.loopIntervalMs", "10000")));
        } while (true);
    }
}

