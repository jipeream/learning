/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 */
package com.fs.fsnews.article;

import com.fs.fsnews.article.Article;
import com.fs.fsnews.article.extractors.DublinCoreDocumentExtractor;
import com.fs.fsnews.article.extractors.ElMundoDocumentExtractor;
import com.fs.fsnews.article.extractors.MetaDocumentExtractor;
import com.fs.fsnews.article.extractors.MicroDataDocumentExtractor;
import es.jipeream.library.jt.JtExtractor;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ArticleUtils {
    static List<JtExtractor> mArticleExtractorList = new ArrayList<JtExtractor>();

    public static Article extractArticleData(URL url) throws IOException, JSONException {
        Article article = null;
        for (JtExtractor articleExtractor : mArticleExtractorList) {
            if (article == null) {
                article = new Article();
                article.setUrl(url);
            }
            int timeout = 5000;
            Document document = Jsoup.parse((URL)url, (int)timeout);
            articleExtractor.extract(article, document);
        }
        return article;
    }

    static {
        mArticleExtractorList.add(new MicroDataDocumentExtractor());
        mArticleExtractorList.add(new DublinCoreDocumentExtractor());
        mArticleExtractorList.add(new MetaDocumentExtractor("article:"));
        mArticleExtractorList.add(new MetaDocumentExtractor("og:"));
        mArticleExtractorList.add(new MetaDocumentExtractor("twitter:"));
        mArticleExtractorList.add(new ElMundoDocumentExtractor());
    }
}

