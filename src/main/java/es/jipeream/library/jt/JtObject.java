/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package es.jipeream.library.jt;

import es.jipeream.library.JsonUtils;
import es.jipeream.library.jt.JtClass;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class JtObject {
    protected final JSONObject mJsonObject;
    private static Map<Class<? extends JtObject>, JtClass> mJtClassMap;

    public JtObject() {
        this.mJsonObject = new JSONObject();
    }

    public JtObject(JSONObject jsonObject) {
        this.mJsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return this.mJsonObject;
    }

    public String getStringValue(String key) {
        return JsonUtils.getStringValue(this.mJsonObject, key);
    }

    public JSONObject putStringValue(String key, String value) {
        return JsonUtils.putOrAppendStringValue(this.mJsonObject, key, value);
    }

    public JSONObject appendStringValue(String key, String value) {
        return JsonUtils.appendStringValue(this.mJsonObject, key, value);
    }

    public JSONObject getObjectValue(String key) {
        return JsonUtils.getObjectValue(this.mJsonObject, key);
    }

    public JSONObject putObjectValue(String key, JSONObject value) {
        return JsonUtils.putOrAppendObjectValue(this.mJsonObject, key, value);
    }

    public JSONObject appendObjectValue(String key, JSONObject value) {
        return JsonUtils.appendObjectValue(this.mJsonObject, key, value);
    }

    public static Map<Class<? extends JtObject>, JtClass> getJtClassMap() {
        if (mJtClassMap == null) {
            mJtClassMap = new HashMap<Class<? extends JtObject>, JtClass>();
        }
        return mJtClassMap;
    }

    protected static JtClass registerJtClass(Class<? extends JtObject> javaClass) {
        JtClass jtClass = new JtClass(javaClass);
        JtObject.getJtClassMap().put(javaClass, jtClass);
        return jtClass;
    }

    static {
        JtObject.registerJtClass(JtObject.class);
    }
}

