package com.seeapenny.client.server.responses;

import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.GoodResponse;

import org.json.JSONObject;


public class SynchronizedGoodResponse extends SynchGoodActionResponse {

    private GoodResponse goodResponse;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        goodResponse = new GoodResponse();
        JSONUtils.fillJSONable(rootDto, "good", goodResponse);
    }

    public GoodResponse getGoodResponse() {
        return goodResponse;
    }
}
