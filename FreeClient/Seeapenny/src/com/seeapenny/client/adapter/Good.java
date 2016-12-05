package com.seeapenny.client.adapter;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.server.User;
import com.seeapenny.client.util.FieldGood;
import com.seeapenny.client.util.SUtil;

import java.util.Date;
import java.util.EnumSet;

/**
 * Created by Sony on 25.07.13.
 */
public class Good {

    private long id;
    private long listId;
    private int measure;

    private Date modifiedTime;
    private Date createTime;

    private SynchAction synchAction = SynchAction.NO_CHANGES;

    private int goodCategoryImageId;

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

    private boolean isSelected;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getGoodCategoryImageId() {
        return goodCategoryImageId;
    }

    public void setGoodCategoryImageId(int goodCategoryImageId) {
        this.goodCategoryImageId = goodCategoryImageId;
    }

    //если не было синхронизация с сервером
    public void changeSynchAction(EnumSet<FieldGood> fieldGoods) {
        if (getSynchAction() != SynchAction.INSERT) {
            setSynchAction(SynchAction.UPDATE);
            fieldGoods.add(FieldGood.SYNCH_ACTION);
        }
    }

    public SynchAction getSynchAction() {
        return synchAction;
    }

    public void setSynchAction(SynchAction synchAction) {
        this.synchAction = synchAction;
    }


    public boolean changeName(String name, EnumSet<FieldGood> set) {
        if (!SUtil.stringEquals(getName(), name)) {
            setName(name);
            set.add(FieldGood.NAME);
            return true;
        }
        return false;
    }

    public boolean changeCategory(int categoryId, EnumSet<FieldGood> set) {
        if (getCategoryId() != categoryId) {
            setCategoryId(categoryId);
            set.add(FieldGood.CATEGORY);
            return true;
        }
        return false;
    }

    public boolean changeQuantity(double quantity, EnumSet<FieldGood> set) {
        if (SUtil.compareDouble(getQuantity(), quantity) != 0) {
            setQuantity(quantity);
            set.add(FieldGood.QUANTITY);
            return true;
        }
        return false;
    }


    public boolean changePrice(double price, EnumSet<FieldGood> set) {
        if (SUtil.compareDouble(price, getPrice()) != 0) {
            setPrice(price);
            set.add(FieldGood.PRICE);
            return true;
        }
        return false;
    }

    public boolean changeMeasure(int measure, EnumSet<FieldGood> set) {
        if (getMeasure() != measure) {
            setMeasure(measure);
            set.add(FieldGood.MEASURE);
            return true;
        }
        return false;
    }

    public boolean changeState(int state, EnumSet<FieldGood> set) {
        if (getState() != state) {
            setState(state);
            set.add(FieldGood.STATE);
            return true;
        }
        return false;
    }

    public boolean changeNote(String note, EnumSet<FieldGood> set) {
        if (!SUtil.objectEquals(getNote(), note)) {
            setNote(note);
            set.add(FieldGood.NOTE);
            return true;
        }
        return false;
    }

    public boolean changeImageID(long imageID, EnumSet<FieldGood> set) {
        if (getImageId() != imageId) {
            setImageId(imageId);
            set.add(FieldGood.IMAGE);
            return true;
        }
        return false;
    }

    public boolean changeLastEditor(long lastEditorId, EnumSet<FieldGood> set) {
        if (SUtil.compareLong(getLastEditorId(), lastEditorId) != 0) {
            setLastEditorId(lastEditorId);
            set.add(FieldGood.LAST_EDITOR_ID);
            return true;
        }
        return false;
    }

    public boolean changeOwnerId(long ownerId, EnumSet<FieldGood> set) {
        if (SUtil.compareLong(getLastEditorId(), ownerId) != 0) {
            setOwnerId(ownerId);
            set.add(FieldGood.OWNER_ID);
            return true;
        }
        return false;
    }

    public boolean changePriority(boolean priority, EnumSet<FieldGood> set) {
        if (isPriority() != priority) {
            setPriority(priority);
            set.add(FieldGood.PRIORITY);
            return true;
        }
        return false;
    }

    public String getFormatCreateTime() {
        return SeeapennyApp.getInstance().formatDatetime(createTime);
    }

    public String getFormatLastModifiedTime() {
        return SeeapennyApp.getInstance().formatDatetime(modifiedTime);
    }

}
