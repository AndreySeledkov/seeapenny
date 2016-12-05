package com.seeapenny.client.server.requests;

import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.service.Services;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 09.07.13
 * Time: 18:57
 * To change this template use File | Settings | File Templates.
 */
public class SyncGoodRequest {

    private List<ShopList> shopListResponses;
    private List<Good> goodResponseList;


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
                root.put("ownerId", shopListResponse.getOwnerId());

                GoodRequest goodRequest = new GoodRequest();
                goodResponseList = Services.getGoodService().getAllGood(shopListResponse.getId(), shopListResponse.getOwnerId());

                if (goodResponseList.size() == 0) {
                    root.put("elements", null);
                } else {
                    root.put("elements", goodRequest.toJson());
                }

                jsonArray.put(root);
            }

            JSONObject root = new JSONObject();
            if (shopListResponses.size() == 0) {
                root.put("list", null);
            } else {
                root.put("list", jsonArray);
            }

            return root;
        }

    }


    public class GoodRequest {


        public JSONArray toJson() throws JSONException {

            JSONArray jsonArray = new JSONArray();

            for (Good goodResponse : goodResponseList) {

                JSONObject root = new JSONObject();
                root.put("goodId", goodResponse.getId());
                jsonArray.put(root);
            }

            return jsonArray;
        }

    }

    public void setShopListResponses(List<ShopList> shopListResponses) {
        this.shopListResponses = shopListResponses;
    }
}
