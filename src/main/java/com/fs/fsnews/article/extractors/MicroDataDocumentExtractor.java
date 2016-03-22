/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.select.Elements
 */
package com.fs.fsnews.article.extractors;

import es.jipeream.library.JsonUtils;
import es.jipeream.library.jt.JtExtractor;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MicroDataDocumentExtractor
extends JtExtractor<JSONObject, Document> {
    @Override
    public void extract(JSONObject jsonObject, Document document) {
        Element rootElement = document.body();
        try {
            this.extractRec(rootElement, jsonObject);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void extractRec(Element element, JSONObject jsonObject) throws JSONException {
        if (element.hasAttr("itemscope")) {
            JSONObject microdataJsonObject = new JSONObject();
            if (element.hasAttr("itemprop")) {
                String itemprop = element.attr("itemprop");
                JsonUtils.putOrAppendObjectValue(jsonObject, itemprop, microdataJsonObject);
            } else {
                JsonUtils.putOrAppendObjectValue(jsonObject, "@microdata", microdataJsonObject);
            }
            jsonObject = microdataJsonObject;
            if (element.hasAttr("itemtype")) {
                String itemtype = element.attr("itemtype");
                JsonUtils.putOrAppendStringValue(microdataJsonObject, "@itemtype", itemtype);
            }
            if (element.hasAttr("itemid")) {
                String itemid = element.attr("itemid");
                JsonUtils.putOrAppendStringValue(microdataJsonObject, "@itemid", itemid);
            }
            if (element.hasAttr("itemref")) {
                String itemref = element.attr("itemref");
                JsonUtils.putOrAppendStringValue(microdataJsonObject, "@itemref", itemref);
            }
        } else if (element.hasAttr("itemprop")) {
            String itemprop = element.attr("itemprop");
            String value = element.text();
            if ("|meta|".contains("|" + element.tagName() + "|") && element.hasAttr("content")) {
                value = element.attr("content");
            }
            if ("|a|area|link|".contains("|" + element.tagName() + "|") && element.hasAttr("href")) {
                value = element.attr("href");
            }
            if ("|audio|embed|iframe|img|source|track|video|".contains("|" + element.tagName() + "|") && element.hasAttr("src")) {
                value = element.attr("src");
            }
            if ("|object|".contains("|" + element.tagName() + "|") && element.hasAttr("data")) {
                value = element.attr("data");
            }
            if ("|time|".contains("|" + element.tagName() + "|") && element.hasAttr("datetime")) {
                value = element.attr("datetime");
            }
            JsonUtils.putOrAppendStringValue(jsonObject, itemprop, value);
        }
        for (Element elementIt : element.children()) {
            this.extractRec(elementIt, jsonObject);
        }
    }
}

