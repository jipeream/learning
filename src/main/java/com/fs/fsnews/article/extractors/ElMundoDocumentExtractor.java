/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.select.Elements
 */
package com.fs.fsnews.article.extractors;

import com.fs.fsnews.article.Article;
import es.jipeream.library.jt.JtExtractor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ElMundoDocumentExtractor
extends JtExtractor<Article, Document> {
    @Override
    public void extract(Article article, Document document) {
        Element articleElement = document.select("article").first();
        Element authorElement = articleElement.select("[itemprop=author]").first();
        if (authorElement != null) {
            article.setAuthor(authorElement.text());
        }
    }
}

