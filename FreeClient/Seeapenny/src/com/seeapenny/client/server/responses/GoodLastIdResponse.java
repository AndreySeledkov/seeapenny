package com.seeapenny.client.server.responses;

import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.server.GoodResponse;
import com.seeapenny.client.server.Response;
import com.seeapenny.client.xml.XMLable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sony on 22.07.13.
 */
public class GoodLastIdResponse extends Response {

    private List<GoodIds> goodResponseList;

    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        JSONObject priceListDto = rootDto.optJSONObject("goodLists");
        if (priceListDto != null) {
            JSONArray pricesDto = JSONUtils.array(priceListDto, "goodList");
            goodResponseList = new ArrayList<GoodIds>();
            JSONUtils.fillJSONableList(pricesDto, goodResponseList, new GoodIds());
        }
    }

    public List<GoodIds> getGoodResponseList() {
        return goodResponseList;
    }


    public class GoodIds implements JSONable, XMLable {

        private long goodId;
        private long listId;
        private long ownerId;

        public long getGoodId() {
            return goodId;
        }

        private void setGoodId(long goodId) {
            this.goodId = goodId;
        }

        public long getListId() {
            return listId;
        }

        private void setListId(long listId) {
            this.listId = listId;
        }

        public long getOwnerId() {
            return ownerId;
        }

        private void setOwnerId(long ownerId) {
            this.ownerId = ownerId;
        }

        @Override
        public void fromJson(JSONObject dto) {
            goodId = dto.optLong("@goodId");
            listId = dto.optLong("@listId");
            ownerId = dto.optLong("@ownerId");
        }

        @Override
        public GoodIds createForJsonArray() {
            return new GoodIds();
        }

        @Override
        public void fromXml(Element root) {

        }
    }
}
