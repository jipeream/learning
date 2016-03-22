/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  com.sun.syndication.feed.synd.SyndEntry
 *  com.sun.syndication.feed.synd.SyndFeed
 *  com.sun.syndication.io.SyndFeedOutput
 */
package es.jipeream.library.rss;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedOutput;
import es.jipeream.library.JavaUtils;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class RssUtils {
    public static List<String> getAuthorList(SyndEntry syndEntry) {
        HashMap<String, Object> authorMap = new HashMap<String, Object>();
        for (String author : syndEntry.getAuthor().split("/")) {
            if (JavaUtils.isNullOrEmpty(author = author.trim())) continue;
            authorMap.put(author, null);
        }
        for (Object author2 : syndEntry.getAuthors()) {
            authorMap.put(author2.toString(), null);
        }
        for (Object contributor : syndEntry.getContributors()) {
            authorMap.put(contributor.toString(), null);
        }
        return new ArrayList<String>(authorMap.keySet());
    }

    public static Document getSyndFeedDocument(SyndFeed syndFeed) throws Exception {
        SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
        String syndFeedRssString = syndFeedOutput.outputString(syndFeed);
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(syndFeedRssString)));
        return document;
    }

    public static List<Element> getItemElementList(SyndFeed syndFeed) throws Exception {
        ArrayList<Element> itemElementList = new ArrayList<Element>();
        Document document = RssUtils.getSyndFeedDocument(syndFeed);
        NodeList itemNodeList = document.getElementsByTagName("item");
        for (int i = 0; i < itemNodeList.getLength(); ++i) {
            Element itemElement = (Element)itemNodeList.item(i);
            itemElementList.add(itemElement);
        }
        return itemElementList;
    }

    public static List<URL> getImageUrlList(SyndFeed syndFeed) throws Exception {
        ArrayList<URL> imageUrlList = new ArrayList<URL>();
        List<Element> itemElementList = RssUtils.getItemElementList(syndFeed);
        for (Element itemElement : itemElementList) {
            NodeList contentNodeList = itemElement.getElementsByTagName("media:content");
            for (int i = 0; i < contentNodeList.getLength(); ++i) {
                Element contentElement = (Element)contentNodeList.item(i);
                String url = contentElement.getAttribute("url");
                imageUrlList.add(new URL(url));
            }
            NodeList enclosureNodeList = itemElement.getElementsByTagName("enclosure");
            for (int i2 = 0; i2 < enclosureNodeList.getLength(); ++i2) {
                Element enclosureElement = (Element)enclosureNodeList.item(i2);
                String type = enclosureElement.getAttribute("type");
                if (!type.startsWith("image/")) continue;
                String url = enclosureElement.getAttribute("url");
                imageUrlList.add(new URL(url));
            }
        }
        return imageUrlList;
    }

    public static URL getBestImageUrl(SyndFeed syndFeed) throws Exception {
        List<URL> imageUrlList = RssUtils.getImageUrlList(syndFeed);
        if (imageUrlList.isEmpty()) {
            return null;
        }
        for (URL imageUrl : imageUrlList) {
            if (!imageUrl.toString().contains("noticia_normal")) continue;
            return imageUrl;
        }
        return imageUrlList.get(0);
    }
}

