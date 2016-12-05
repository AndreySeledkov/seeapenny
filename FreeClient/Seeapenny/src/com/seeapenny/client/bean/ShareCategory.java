package com.seeapenny.client.bean;

import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.xml.XMLable;

import org.json.JSONObject;
import org.w3c.dom.Element;

import java.io.Serializable;

/**
 * Created by Sony on 15.07.13.
 */
public class ShareCategory implements JSONable, XMLable, Serializable {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void fromJson(JSONObject dto) {
        id = dto.optLong("@id");
    }

    @Override
    public <J extends JSONable> J createForJsonArray() {
        return null;
    }

    @Override
    public void fromXml(Element root) {

    }
}
