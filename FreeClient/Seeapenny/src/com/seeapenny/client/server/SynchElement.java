package com.seeapenny.client.server;

import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.util.Field;
import com.seeapenny.client.xml.XMLable;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 05.04.13
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public class SynchElement implements JSONable, XMLable {

    private Field field;
    private String value;
    private String modifiedTime;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public JSONObject toJson() throws JSONException {

        JSONObject root = new JSONObject();
        root.put("field", field.ordinal());
        root.put("value", value);
        root.put("lastModifiedTime", modifiedTime);

        return root;
    }

    @Override
    public void fromJson(JSONObject dto) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <J extends JSONable> J createForJsonArray() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void fromXml(Element root) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
