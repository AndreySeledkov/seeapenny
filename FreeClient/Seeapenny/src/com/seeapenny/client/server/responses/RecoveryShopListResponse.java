package com.seeapenny.client.server.responses;

import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.ShopListResponse;

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
public class RecoveryShopListResponse extends Response {

    private List<ShopListResponse> shopListResponses;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        JSONObject priceListDto = rootDto.optJSONObject("shopLists");
        if (priceListDto != null) {
            JSONArray pricesDto = JSONUtils.array(priceListDto, "shopList");
            shopListResponses = new ArrayList<ShopListResponse>();
            JSONUtils.fillJSONableList(pricesDto, shopListResponses, new ShopListResponse());
        }
    }

    public List<ShopListResponse> getShopListResponses() {
        return shopListResponses;
    }

    public void setShopListResponses(List<ShopListResponse> shopListResponses) {
        this.shopListResponses = shopListResponses;
    }
}
