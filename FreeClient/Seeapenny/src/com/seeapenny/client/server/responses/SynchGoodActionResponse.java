package com.seeapenny.client.server.responses;

import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.SynchrAction;

import org.json.JSONObject;

/**
 * Created by Sony on 25.07.13.
 */
public class SynchGoodActionResponse extends Response {

    private SynchrAction synchrAction;
    private long listId;
    private long goodId;
    private long ownerId;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        listId = rootDto.optLong("listId");
        ownerId = rootDto.optLong("ownerId");
        goodId = rootDto.optLong("goodId");
        synchrAction = SynchrAction.dbValueOf(rootDto.optInt("action"));
    }

    public SynchrAction getSynchrAction() {
        return synchrAction;
    }

    public void setSynchrAction(SynchrAction synchrAction) {
        this.synchrAction = synchrAction;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public long getGoodId() {
        return goodId;
    }

    public void setGoodId(long goodId) {
        this.goodId = goodId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
