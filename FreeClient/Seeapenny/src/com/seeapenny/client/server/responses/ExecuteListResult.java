package com.seeapenny.client.server.responses;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.Response;

import org.json.JSONObject;

import java.util.Date;

public class ExecuteListResult extends Response {

    private long id;
    private long ownerId;
    private boolean executed;
    private Date modifiedTime;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        JSONObject elementObject = rootDto.optJSONObject("executeListResult");
        if (elementObject != null) {
            executed = elementObject.optBoolean("@executed");
            id = elementObject.optLong("@listId");
            ownerId = elementObject.optLong("@ownerId");
            modifiedTime = JSONUtils.fromJsonDate(elementObject, "@time", SeeapennyApp.DATE_TIME_FORMATTER);
        }
    }

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

