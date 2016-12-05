package com.seeapenny.client.server.responses;

import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.ShopListResponse;

import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 15.03.13
 * Time: 0:35
 * To change this template use File | Settings | File Templates.
 */
public class SynchronizedShopListResponse extends SynchShopListActionResponse {

    private ShopListResponse shopListResponse;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        JSONObject shopListObj = rootDto.optJSONObject("shopList");
        if (shopListObj != null) {
            shopListResponse = new ShopListResponse();
            JSONUtils.fillJSONable(rootDto, "shopList", shopListResponse);
        }
    }

    public ShopListResponse getShopListResponse() {
        return shopListResponse;
    }

    public void setShopListResponse(ShopListResponse shopListResponse) {
        this.shopListResponse = shopListResponse;
    }
}
