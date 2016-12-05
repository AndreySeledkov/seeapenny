package com.seeapenny.client.server.requests;

import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.service.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sony on 03.08.13.
 */
public class ListIdsRequest {

    private List<ShopList> shopListResponses;


    public JSONObject toJson() throws JSONException {
        ShopListRequest shopListRequest = new ShopListRequest();
        return shopListRequest.toJson();
    }

    public class ShopListRequest {


        public JSONObject toJson() throws JSONException {

            JSONArray jsonArray = new JSONArray();

            for (ShopList shopListResponse : shopListResponses) {

                JSONObject root = new JSONObject();
                root.put("listId", shopListResponse.getId());

                jsonArray.put(root);
            }

            JSONObject root = new JSONObject();
            if (shopListResponses.size() == 0) {
                root.put("lists", null);
            } else {
                root.put("lists", jsonArray);
            }

            return root;
        }

    }

    public void setShopListResponses(List<ShopList> shopListResponses) {
        this.shopListResponses = shopListResponses;
    }
}
