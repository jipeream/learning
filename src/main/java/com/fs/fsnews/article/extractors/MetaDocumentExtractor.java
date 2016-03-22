/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.select.Elements
 */
package com.fs.fsnews.article.extractors;

import es.jipeream.library.JsonUtils;
import es.jipeream.library.jt.JtExtractor;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MetaDocumentExtractor
extends JtExtractor<JSONObject, Document> {
    private final String mPrefix;

    public MetaDocumentExtractor(String prefix) {
        this.mPrefix = prefix;
    }

    @Override
    public void extract(JSONObject jsonObject, Document document) {
        Elements metaElements = document.select("meta");
        for (Element metaElement : metaElements) {
            String property;
            String content = metaElement.attr("content");
            String name = metaElement.attr("name");
            if (name != null && (this.mPrefix == null || name.startsWith(this.mPrefix))) {
                this.appendMetaName(jsonObject, name, content);
            }
            if ((property = metaElement.attr("property")) == null || this.mPrefix != null && !property.startsWith(this.mPrefix)) continue;
            this.appendMetaProperty(jsonObject, property, content);
        }
    }

    public void appendMetaName(JSONObject jsonObject, String name, String content) {
        JsonUtils.putOrAppendStringValue(jsonObject, name, content);
    }

    public void appendMetaProperty(JSONObject jsonObject, String property, String content) {
        JsonUtils.putOrAppendStringValue(jsonObject, property, content);
    }
}

