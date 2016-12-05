package com.seeapenny.client.service;

import android.content.Context;

import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.bean.GoodCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 18.04.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
public class CategoryService {

    public static int LAST_CATEGORY_ID = 23;

//    List<GoodCategory> goodCategoryList = new ArrayList<GoodCategory>();


    private long lastCategoryID;

    public CategoryService() {
//        categoriesDAO = new CategoriesDAO();
//        initCategory();

//        lastCategoryID = getLastId();
    }

//    private void initCategory() {
////        goodCategoryList.clear();
//        String[] innerCategories = SeeapennyApp.getInstance().getResources().getStringArray(R.array.categories);
//
//        GoodCategory goodCategoryDefault = new GoodCategory();
//
//        goodCategoryDefault.setId(0);
//        goodCategoryDefault.setPosition(0);
//        goodCategoryDefault.setCategory(SeeapennyApp.getInstance().getResources().getString(R.string.chooseCategoryPrompt));
//        goodCategoryList.add(goodCategoryDefault);
//
//        for (int i = 0; i < innerCategories.length; i++) {
//            GoodCategory goodCategory = new GoodCategory();
//
//            goodCategory.setId(i + 1);
//            goodCategory.setPosition(i + 1);
//            goodCategory.setCategory(innerCategories[i]);
//            goodCategoryList.add(goodCategory);
//        }
//
//        List<GoodCategory> customCategories = categoriesDAO.getListCategory();
//
//        int index = innerCategories.length;
//        for (GoodCategory goodCategory : customCategories) {
//            goodCategory.setEmbedded(false);
//            goodCategory.setPosition(index++);
//        }
//
//        goodCategoryList.addAll(customCategories);
//    }

    public List<GoodCategory> getListCategory() {
        List<GoodCategory> goodCategoryList = new ArrayList<GoodCategory>();
        String[] innerCategories = SeeapennyApp.getInstance().getResources().getStringArray(R.array.categories);

        GoodCategory goodCategoryDefault = new GoodCategory();

        goodCategoryDefault.setId(0);
        goodCategoryDefault.setCategory(SeeapennyApp.getInstance().getResources().getString(R.string.chooseCategoryPrompt));
        goodCategoryList.add(goodCategoryDefault);

        for (int i = 0; i < innerCategories.length; i++) {
            GoodCategory goodCategory = new GoodCategory();

            goodCategory.setId(i + 1);
            goodCategory.setCategory(innerCategories[i]);
            goodCategoryList.add(goodCategory);
        }


        return goodCategoryList;
    }

    public long getLastCategoryID() {
        return lastCategoryID;
    }

    public void resetLastId(long newValue) {
        lastCategoryID = newValue;
    }
}
