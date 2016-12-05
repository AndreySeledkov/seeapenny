package com.seeapenny.client.server.responses;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.Response;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Sony on 25.07.13.
 */
public class ExecuteCategoryResult extends Response {

    private long catId;
    private long ownerId;
    private boolean executed;
    private Date modifiedTime;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        JSONObject elementObject = rootDto.optJSONObject("executeCategoryResult");
        if (elementObject != null) {
            executed = elementObject.optBoolean("@executed");
            catId = elementObject.optLong("@catId");
            ownerId = elementObject.optLong("@ownerId");
            modifiedTime = JSONUtils.fromJsonDate(elementObject, "@time", SeeapennyApp.DATE_TIME_FORMATTER);
        }
    }

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
