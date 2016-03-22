/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package es.jipeream.library.jt;

import es.jipeream.library.jt.EJtFieldType;
import es.jipeream.library.jt.JtClass;
import es.jipeream.library.jt.JtObject;
import org.json.JSONObject;

public class JtField
extends JtObject {
    public JtField(String fieldName, EJtFieldType fieldType) {
        this.putStringValue("fieldName", fieldName);
        this.putStringValue("fieldType", fieldType.name());
    }

    public String getFieldName() {
        return this.getStringValue("fieldName");
    }

    public EJtFieldType getFieldType() {
        return EJtFieldType.valueOf(this.getStringValue("fieldType"));
    }

    protected static JtField createStringField(String fieldName) {
        JtField jtField = new JtField(fieldName, EJtFieldType.STRING);
        return jtField;
    }

    protected static JtField createEnumField(String fieldName, Class<? extends Enum> enumClass) {
        JtField jtField = new JtField(fieldName, EJtFieldType.ENUM);
        jtField.putStringValue("className", enumClass.getCanonicalName());
        return jtField;
    }

    public static JtField createCollectionField(String fieldName, Class<? extends JtObject> collectionClass) {
        JtField jtField = new JtField(fieldName, EJtFieldType.COLLECTION);
        jtField.putStringValue("className", collectionClass.getCanonicalName());
        return jtField;
    }

    static {
        JtClass jtClass = JtField.registerJtClass(JtField.class);
        jtClass.addField(JtField.createStringField("fieldName"));
        jtClass.addField(JtField.createEnumField("fieldType", EJtFieldType.class));
    }
}

