package com.seeapenny.client.server.responses;

import com.seeapenny.client.server.Response;

import org.json.JSONObject;

/**
 * Created by Sony on 22.07.13.
 */
public class LastIdResponse extends Response {

    private long id;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        JSONObject object = rootDto.optJSONObject("lastId");
        if (object != null) {
            id = object.optLong("@id");
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
