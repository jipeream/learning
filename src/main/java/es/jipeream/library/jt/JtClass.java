/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package es.jipeream.library.jt;

import es.jipeream.library.jt.JtField;
import es.jipeream.library.jt.JtObject;
import org.json.JSONObject;

public class JtClass
extends JtObject {
    public JtClass(Class<? extends JtObject> javaClass) {
        this.putStringValue("simpleName", javaClass.getSimpleName());
        this.putStringValue("canonicalName", javaClass.getCanonicalName());
    }

    public Class<JtObject> getJavaClass() {
        try {
            return (Class<JtObject>) Class.forName(this.getCanonicalName());
        }
        catch (Exception e) {
            return null;
        }
    }

    public String getSimpleName() {
        return this.getStringValue("simpleName");
    }

    public String getCanonicalName() {
        return this.getStringValue("canonicalName");
    }

    public void addField(JtField jtField) {
        this.appendObjectValue("fieldList", jtField.getJsonObject());
    }

    static {
        JtClass jtClass = JtClass.registerJtClass(JtClass.class);
        jtClass.addField(JtField.createStringField("simpleName"));
        jtClass.addField(JtField.createStringField("canonicalName"));
        jtClass.addField(JtField.createCollectionField("fieldList", JtField.class));
    }
}

