/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package es.jipeream.library.twitter.model;

import org.json.JSONObject;

public class TwUser {
    private final JSONObject jsonObject;

    public TwUser(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return this.jsonObject;
    }

    public long getId() {
        return this.jsonObject.optLong("id", 0);
    }

    public String getName() {
        return this.jsonObject.optString("name", "");
    }

    public String getScreenName() {
        return this.jsonObject.optString("screen_name", "");
    }

    public String getAt() {
        return "@" + this.getScreenName();
    }
}

