package com.seeapenny.client.server.responses;

import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.SynchrAction;

import org.json.JSONObject;

/**
 * Created by Sony on 25.07.13.
 */
public class SynchShopListActionResponse extends Response {

    private SynchrAction synchrAction;
    private long listID;
    private long ownerID;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        listID = rootDto.optLong("listId");
        ownerID = rootDto.optLong("ownerId");

        synchrAction = SynchrAction.dbValueOf(rootDto.optInt("action"));
    }

    public SynchrAction getSynchrAction() {
        return synchrAction;
    }

    public void setSynchrAction(SynchrAction synchrAction) {
        this.synchrAction = synchrAction;
    }

    public long getListID() {
        return listID;
    }

    public void setListID(long listID) {
        this.listID = listID;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }
}
