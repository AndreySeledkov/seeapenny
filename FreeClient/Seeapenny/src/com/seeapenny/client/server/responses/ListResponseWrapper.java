package com.seeapenny.client.server.responses;

import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.ShopListResponse;
import com.seeapenny.client.server.Response;

import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 17.02.13
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
public class ListResponseWrapper extends Response {

    private ShopListResponse shopListResponse;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);


        shopListResponse = new ShopListResponse();
        JSONUtils.fillJSONable(rootDto, "shopList", shopListResponse);
    }

    public ShopListResponse getShopListResponse() {
        return shopListResponse;
    }

    public void setShopListResponse(ShopListResponse shopListResponse) {
        this.shopListResponse = shopListResponse;
    }

}
