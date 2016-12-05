package com.seeapenny.client.adapter;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.server.User;
import com.seeapenny.client.service.AccessType;
import com.seeapenny.client.service.Services;
import com.seeapenny.client.util.SUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Sony on 25.07.13.
 */
public class ShopList {

    public ShopList() {
    }

    private long id;
    private User ownerUser;
    private long ownerId;

    private List<User> userShareList;

    private String name;
    private Date createTime;
    private Date lastModifiedTime;
    private boolean isSelected;

    private double totalPrice;


    private SynchAction synchAction = SynchAction.NO_CHANGES;

    private int groupPosition;

    private Boolean isShared;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    //если не было синхронизация с сервером
    public void changeSynchAction() {
        if (getSynchAction() != SynchAction.INSERT) {
            setSynchAction(SynchAction.UPDATE);
        }
    }

    public SynchAction getSynchAction() {
        return synchAction;
    }

    public void setSynchAction(SynchAction synchAction) {
        this.synchAction = synchAction;
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }

    public int getCountLeftGoods() {
        return Services.getGoodService().getLeftGoods(id, ownerId);
    }

    public void setShared(Boolean shared) {
        isShared = shared;
    }

    public boolean isShared() {
        if (isShared == null) {
            return Services.getAccessService().isShared(AccessType.LIST, id);
        }
        return isShared;
    }

    public boolean changeName(String name) {
        if (!SUtil.stringEquals(getName(), name)) {
            setName(name);
            return true;
        }
        return false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public String getFormatCreateTime() {
        return SeeapennyApp.getInstance().formatDatetime(createTime);
    }

    public String getFormatLastModifiedTime() {
        return SeeapennyApp.getInstance().formatDatetime(lastModifiedTime);
    }


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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
