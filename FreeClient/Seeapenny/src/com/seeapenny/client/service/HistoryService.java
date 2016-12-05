package com.seeapenny.client.service;

import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.dao.HistoryDAO;

import java.util.ArrayList;

public class HistoryService {
    private HistoryDAO historyDAO;

    public HistoryService() {
        historyDAO = new HistoryDAO();
    }

    public void addShopList(ShopList shopList) {

        ArrayList<Good> goods = Services.getGoodService().getAllGood(shopList.getId(), shopList.getOwnerId());
        for (Good good : goods) {
            historyDAO.addGood(good.getId(), good.getListId(), good.getName(), good.getPrice(), good.getQuantity(), good.getMeasure(), good.isPriority(),
                    good.getNote(), good.getCategoryId(), good.getImageId(), good.getState(), good.getOwnerId(), good.getLastEditorId(), good.getFormatCreateTime(), good.getFormatLastModifiedTime());
        }
        historyDAO.addShopList(shopList.getId(), shopList.getOwnerId(), shopList.getName(), shopList.getFormatCreateTime(), shopList.getFormatLastModifiedTime());
    }

    public boolean removeAll(){
       return historyDAO.removeAll();
    }
}
