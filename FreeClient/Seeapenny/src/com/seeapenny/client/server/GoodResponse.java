package com.seeapenny.client.server;

import com.seeapenny.client.util.FieldGood;
import com.seeapenny.client.util.SUtil;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.bean.GoodCategory;
import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.xml.XMLable;

import org.json.JSONObject;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 11.02.13
 * Time: 22:37
 * To change this template use File | Settings | File Templates.
 */
public class GoodResponse implements JSONable, XMLable, Serializable {

    private long id;
    private long oldId;
    private long listId;
    private int measure;

    private Date modifiedTime;
    private Date createTime;

    private String smallUrl = "";
    private String largeUrl = "";

    private double price;

    private long ownerId; // владелец товара единтичны с со списком (создатель)

    private long lastEditorId;

    private User userOwner;
    private User userAuthor;
    private User userLastEditor;

    private String note = "";
    private boolean priority;


    private int state;
    private double quantity;
    private String name;

    private int categoryId;

    private long imageId;


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

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public int getMeasure() {
        return measure;
    }

    public void setMeasure(int measure) {
        this.measure = measure;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getLastEditorId() {
        return lastEditorId;
    }

    public void setLastEditorId(long lastEditorId) {
        this.lastEditorId = lastEditorId;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public User getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(User userOwner) {
        this.userOwner = userOwner;
    }

    public User getUserAuthor() {
        return userAuthor;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setUserAuthor(User userAuthor) {
        this.userAuthor = userAuthor;
    }

    public User getUserLastEditor() {
        return userLastEditor;
    }

    public void setUserLastEditor(User userLastEditor) {
        this.userLastEditor = userLastEditor;
    }

    @Override
    public void fromJson(JSONObject dto) {
        id = dto.optLong("@id");
        oldId = dto.optLong("@oldId");
        listId = dto.optLong("@listId");


//        localGoodId = dto.optLong("@keyId");
        measure = dto.optInt("@measure");


        createTime = JSONUtils.fromJsonDate(dto, "@createTime", SeeapennyApp.DATE_TIME_FORMATTER);
        modifiedTime = JSONUtils.fromJsonDate(dto, "@modifiedTime", SeeapennyApp.DATE_TIME_FORMATTER);

        smallUrl = dto.optString("@smallUrl");
        largeUrl = dto.optString("@largeUrl");

        note = dto.optString("@note");
        priority = dto.optBoolean("@priority");


        price = dto.optDouble("@price");

        ownerId = dto.optLong("@ownerId");

        state = dto.optInt("@state");
        quantity = dto.optDouble("@quantity");
        name = dto.optString("@name");


        JSONObject owner = dto.optJSONObject("owner");
        if (owner != null) {
            userOwner = new User();
            JSONUtils.fillJSONable(dto, "owner", userOwner);

            ownerId = userOwner.getId();
        }


        JSONObject lastEditor = dto.optJSONObject("lastEditor");
        if (lastEditor != null) {
            userLastEditor = new User();
            JSONUtils.fillJSONable(dto, "lastEditor", userLastEditor);
            lastEditorId = userLastEditor.getId();
        } else {
            lastEditorId = SeeapennyApp.getInstance().getOwnerID();//todo not good
        }


        JSONObject author = dto.optJSONObject("author");
        if (author != null) {
            userAuthor = new User();
            JSONUtils.fillJSONable(dto, "author", userAuthor);
        }

        categoryId = dto.optInt("@categoryId");
    }

    @Override
    public GoodResponse createForJsonArray() {
        return new GoodResponse();
    }

    @Override
    public void fromXml(Element root) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
