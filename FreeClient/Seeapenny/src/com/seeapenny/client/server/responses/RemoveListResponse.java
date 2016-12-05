package com.seeapenny.client.server.responses;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.Response;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 26.02.13
 * Time: 19:14
 * To change this template use File | Settings | File Templates.
 */
public class RemoveListResponse extends Response {

    private long listId;
    private boolean executed;
    private Date modifiedTime;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);


        JSONObject elementObject = rootDto.optJSONObject("removeResult");
        if (elementObject != null) {
            executed = elementObject.optBoolean("@executed");
            listId = elementObject.optLong("@contentId");
            modifiedTime = JSONUtils.fromJsonDate(elementObject, "@time", SeeapennyApp.DATE_TIME_FORMATTER);
        }
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }
}
