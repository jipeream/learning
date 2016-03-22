/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 */
package es.jipeream.library.jt;

import es.jipeream.library.jt.JtObject;
import org.json.JSONArray;

public class JtObjectCollection<T extends JtObject> {
    private final JSONArray mJsonArray = new JSONArray();

    public JSONArray getJsonArray() {
        return this.mJsonArray;
    }
}

