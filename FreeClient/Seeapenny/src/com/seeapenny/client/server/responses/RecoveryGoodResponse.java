package com.seeapenny.client.server.responses;

import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.GoodResponse;
import com.seeapenny.client.server.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 09.07.13
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class RecoveryGoodResponse extends Response {

    private List<GoodResponse> goodResponseList;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        JSONObject priceListDto = rootDto.optJSONObject("goodLists");
        if (priceListDto != null) {
            JSONArray pricesDto = JSONUtils.array(priceListDto, "goodList");
            goodResponseList = new ArrayList<GoodResponse>();
            JSONUtils.fillJSONableList(pricesDto, goodResponseList, new GoodResponse());
        }
    }

    public List<GoodResponse> getGoodResponseList() {
        return goodResponseList;
    }

    public void setGoodResponseList(List<GoodResponse> goodResponseList) {
        this.goodResponseList = goodResponseList;
    }
}
