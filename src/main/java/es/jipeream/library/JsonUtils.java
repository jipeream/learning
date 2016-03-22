/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package es.jipeream.library;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    public static String getStringValue(JSONObject jsonObject, String key) {
        return jsonObject.optString(key);
    }

    public static JSONObject putStringValue(JSONObject jsonObject, String key, String value) {
        try {
            jsonObject.put(key, (Object)value);
        }
        catch (JSONException e) {
            // empty catch block
        }
        return jsonObject;
    }

    public static JSONObject putOrAppendStringValue(JSONObject jsonObject, String key, String value) {
        try {
            if (jsonObject.has(key)) {
                JSONArray jsonArray = JsonUtils.getOrCreateArray(jsonObject, key);
                jsonArray.put((Object)value);
            } else {
                jsonObject.put(key, (Object)value);
            }
        }
        catch (JSONException e) {
            // empty catch block
        }
        return jsonObject;
    }

    public static JSONArray appendStringValue(JSONArray jsonArray, String value) {
        jsonArray.put((Object)value);
        return jsonArray;
    }

    public static JSONObject appendStringValue(JSONObject jsonObject, String key, String value) {
        JSONArray jsonArray = JsonUtils.getOrCreateArray(jsonObject, key);
        JsonUtils.appendStringValue(jsonArray, value);
        return jsonObject;
    }

    public static JSONObject getObjectValue(JSONObject jsonObject, String key) {
        return jsonObject.optJSONObject(key);
    }

    public static JSONObject putObjectValue(JSONObject jsonObject, String key, JSONObject value) {
        try {
            jsonObject.put(key, (Object)value);
        }
        catch (JSONException e) {
            // empty catch block
        }
        return jsonObject;
    }

    public static JSONObject putOrAppendObjectValue(JSONObject jsonObject, String key, JSONObject value) {
        try {
            if (jsonObject.has(key)) {
                JSONArray jsonArray = JsonUtils.getOrCreateArray(jsonObject, key);
                jsonArray.put((Object)value);
            } else {
                jsonObject.put(key, (Object)value);
            }
        }
        catch (JSONException e) {
            // empty catch block
        }
        return jsonObject;
    }

    public static JSONArray appendObjectValue(JSONArray jsonArray, JSONObject value) {
        jsonArray.put((Object)value);
        return jsonArray;
    }

    public static JSONObject appendObjectValue(JSONObject jsonObject, String key, JSONObject value) {
        JSONArray jsonArray = JsonUtils.getOrCreateArray(jsonObject, key);
        JsonUtils.appendObjectValue(jsonArray, value);
        return jsonObject;
    }

    public static JSONArray getOrCreateArray(JSONObject jsonObject, String key) {
        try {
            JSONArray jsonArray = jsonObject.optJSONArray(key);
            if (jsonArray == null) {
                jsonArray = new JSONArray();
                if (jsonObject.has(key)) {
                    jsonArray.put(jsonObject.get(key));
                    jsonObject.remove(key);
                }
                jsonObject.put(key, (Object)jsonArray);
            }
            return jsonArray;
        }
        catch (JSONException e) {
            return null;
        }
    }
}

