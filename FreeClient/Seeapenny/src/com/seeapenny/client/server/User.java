package com.seeapenny.client.server;

import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.xml.XMLUtils;
import com.seeapenny.client.xml.XMLable;
import org.json.JSONObject;
import org.w3c.dom.Element;

import java.io.Serializable;

public class User implements JSONable, XMLable, Serializable {

    private long id;
    private String name;
    private String smallUrl;
    private String largeUrl;

    private String onlineStatus;
    private String lastVisit;
    private boolean userBlocked;
    private boolean userDeleted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void fromJson(JSONObject dto) {
        id = dto.optLong("@userId");
        name = dto.optString("@name");
        onlineStatus = dto.optString("@onlineStatus");
        lastVisit = dto.optString("@lastVisit");
        userBlocked = dto.optBoolean("@userBlocked");
        userDeleted = dto.optBoolean("@userDeleted");

        smallUrl = dto.optString("@smallUrl");
        largeUrl = dto.optString("@largeUrl");
    }

    @Override
    public User createForJsonArray() {
        return new User();
    }

    @Override
    public void fromXml(Element dto) {
        id = XMLUtils.attributeLong(dto, "userId");
        name = dto.getAttribute("@name");
        onlineStatus = dto.getAttribute("@onlineStatus");
        lastVisit = dto.getAttribute("@lastVisit");
        userBlocked = XMLUtils.attributeBoolean(dto, "@userBlocked");
        userDeleted = XMLUtils.attributeBoolean(dto, "@userDeleted");

        smallUrl = dto.getAttribute("@smallUrl");
        largeUrl = dto.getAttribute("@largeUrl");
    }

    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(String lastVisit) {
        this.lastVisit = lastVisit;
    }

    public boolean isUserBlocked() {
        return userBlocked;
    }

    public void setUserBlocked(boolean userBlocked) {
        this.userBlocked = userBlocked;
    }

    public boolean isUserDeleted() {
        return userDeleted;
    }

    public void setUserDeleted(boolean userDeleted) {
        this.userDeleted = userDeleted;
    }

//    private long listId;
//
//    public long getListId() {
//        return listId;
//    }
//
//    public void setListId(long listId) {
//        this.listId = listId;
//    }
}
