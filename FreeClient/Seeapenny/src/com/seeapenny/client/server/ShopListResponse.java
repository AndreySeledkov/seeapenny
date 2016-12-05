package com.seeapenny.client.server;

import com.seeapenny.client.util.SUtil;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.service.AccessType;
import com.seeapenny.client.service.Services;
import com.seeapenny.client.xml.XMLUtils;
import com.seeapenny.client.xml.XMLable;

import org.json.JSONObject;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShopListResponse implements JSONable, XMLable, Serializable {

    private long id;
    private long oldId;
    private User ownerUser;
    private long ownerId;

    private List<User> userShareList;

    private String name;
    private Date createTime;
    private Date lastModifiedTime;

    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }


    @Override
    public void fromJson(JSONObject dto) {
        id = dto.optLong("@id");
        oldId = dto.optLong("@oldId");
        name = dto.optString("@name");

        ownerUser = new User();
        JSONUtils.fillJSONable(dto, "user", ownerUser);


        ownerId = ownerUser.getId();

        JSONObject userShareListObj = dto.optJSONObject("shares");

        if (userShareListObj != null) {
            userShareList = new ArrayList<User>();
            JSONUtils.arrayToList(userShareListObj, "user", userShareList, new User());
        }

        createTime = JSONUtils.fromJsonDate(dto, "@createTime", SeeapennyApp.DATE_TIME_FORMATTER);
        lastModifiedTime = JSONUtils.fromJsonDate(dto, "@lastModifiedTime", SeeapennyApp.DATE_TIME_FORMATTER);

    }

    @Override
    public void fromXml(org.w3c.dom.Element root) {
        id = XMLUtils.attributeLong(root, "id");
        name = root.getAttribute("name");

        Element contactDto = XMLUtils.element(root, "user");
        if (contactDto != null) {
            ownerUser = new User();
            ownerUser.fromXml(contactDto);
            ownerId = ownerUser.getId();
        }


//        JSONObject userShareListObj = dto.optJSONObject("shares");
//
//        if (userShareListObj != null) {
//            userShareList = new ArrayList<User>();
//            JSONUtils.arrayToList(userShareListObj, "user", userShareList, new User());
//        }

        createTime = XMLUtils.attributeDate(root, "createTime", SeeapennyApp.DATE_TIME_FORMATTER);
        lastModifiedTime = XMLUtils.attributeDate(root, "lastModifiedTime", SeeapennyApp.DATE_TIME_FORMATTER);

    }

    @Override
    public ShopListResponse createForJsonArray() {
        return new ShopListResponse();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOldId() {
        return oldId;
    }

    public void setOldId(long oldId) {
        this.oldId = oldId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public List<User> getUserShareList() {
        return userShareList;
    }

    public void setUserShareList(List<User> userShareList) {
        this.userShareList = userShareList;
    }

}
