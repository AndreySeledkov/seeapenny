package com.seeapenny.client.server.requests;

import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.server.ShopListResponse;
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
public class SyncShopListRequest {

    private List<ShopList> shopLists;

    public JSONObject toJson() throws JSONException {
        ShopListRequest shopListRequest = new ShopListRequest();
        return shopListRequest.toJson();
    }

    public class ShopListRequest {


        public JSONObject toJson() throws JSONException {

            JSONArray jsonArray = new JSONArray();

            for (ShopList shopList : shopLists) {

                JSONObject root = new JSONObject();
                root.put("ownerId", shopList.getOwnerId());
                root.put("listId", shopList.getId());
                jsonArray.put(root);
            }

            JSONObject root = new JSONObject();
            if (shopLists.size() == 0) {
                root.put("list", null);
            } else {
                root.put("list", jsonArray);
            }

            return root;
        }

    }

    public void setShopLists(List<ShopList> shopLists) {
        this.shopLists = shopLists;
    }
}
