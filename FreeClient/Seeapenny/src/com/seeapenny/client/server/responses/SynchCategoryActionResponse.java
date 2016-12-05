package com.seeapenny.client.server.responses;

import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.SynchrAction;

import org.json.JSONObject;

/**
 * Created by Sony on 25.07.13.
 */
public class SynchCategoryActionResponse extends Response {

    private SynchrAction synchrAction;
    private long id;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        id = rootDto.optLong("id");
        synchrAction = SynchrAction.dbValueOf(rootDto.optInt("action"));
    }

    public SynchrAction getSynchrAction() {
        return synchrAction;
    }

    public void setSynchrAction(SynchrAction synchrAction) {
        this.synchrAction = synchrAction;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
