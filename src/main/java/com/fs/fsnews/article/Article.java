/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.fs.fsnews.article;

import es.jipeream.library.jt.JtObject;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

public class Article
extends JtObject {
    public Article() {
    }

    public Article(JSONObject jsonObject) {
        super(jsonObject);
    }

    public URL getUrl() {
        try {
            return new URL(this.getStringValue("url"));
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    public void setUrl(URL url) throws JSONException {
        this.putStringValue("url", url.toString());
    }

    public String getSectionName() {
        return this.getStringValue("sectionName");
    }

    public void setSectionName(String sectionName) throws JSONException {
        this.putStringValue("sectionName", sectionName);
    }

    public String getTitle() {
        return this.getStringValue("title");
    }

    public void setTitle(String title) {
        this.putStringValue("title", title);
    }

    public String getContent() {
        return this.getStringValue("content");
    }

    public void setContent(String content) {
        this.putStringValue("content", content);
    }

    public String getAuthor() {
        return this.getStringValue("author");
    }

    public void setAuthor(String author) {
        this.putStringValue("author", author);
    }
}

