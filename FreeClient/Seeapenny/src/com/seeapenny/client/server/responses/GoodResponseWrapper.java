package com.seeapenny.client.server.responses;

import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.GoodResponse;
import com.seeapenny.client.server.Response;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 26.02.13
 * Time: 20:13
 * To change this template use File | Settings | File Templates.
 */
public class GoodResponseWrapper extends Response {

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

    public void setGoodResponse(GoodResponse goodResponse) {
        this.goodResponse = goodResponse;
    }

//    public int getTotalCount() {
//        return totalCount;
//    }
//
//    public void setTotalCount(int totalCount) {
//        this.totalCount = totalCount;
//    }
}
