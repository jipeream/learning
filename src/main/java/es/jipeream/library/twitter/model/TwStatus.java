/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package es.jipeream.library.twitter.model;

import es.jipeream.library.twitter.model.TwUser;
import org.json.JSONObject;

public class TwStatus {
    private final JSONObject jsonObject;

    public TwStatus(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return this.jsonObject;
    }

    public long getId() {
        return this.jsonObject.optLong("id", 0);
    }

    public String getText() {
        return this.jsonObject.optString("text", "");
    }

    public TwUser getUser() {
        return new TwUser(this.jsonObject.optJSONObject("user"));
    }
}

