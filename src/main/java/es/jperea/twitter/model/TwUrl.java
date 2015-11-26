package es.jperea.twitter.model;

import org.json.JSONObject;

public class TwUrl {
    private final JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public TwUrl(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
