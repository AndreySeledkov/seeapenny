package com.seeapenny.client.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 21.05.13
 * Time: 0:00
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseTest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 200; i++) {

//            ShopListResponse shopList = new ShopListResponse();
//            shopList.setName("name_" + i);
//            shopList.setCreateTime(new Date());
//            shopList.setLastModifiedTime(new Date());
//            shopList.setSynchAction(SynchAction.NO_CHANGES);
//            Services.getListService(this).insert(shopList);
        }
    }
}
