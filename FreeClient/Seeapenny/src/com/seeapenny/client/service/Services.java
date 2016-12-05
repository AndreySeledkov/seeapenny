package com.seeapenny.client.service;

import android.content.Context;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 04.03.13
 * Time: 1:01
 * To change this template use File | Settings | File Templates.
 */
public class Services {

    public static ShopListService listService;
    public static GoodService goodService;
    public static UserService userService;
    public static CategoryService categoryService;
    public static AccessService accessService;

    public static HistoryService historyService;


    public static HistoryService getHistoryService() {
        if (historyService == null) {
            historyService = new HistoryService();
        }
        return historyService;
    }


    public static ShopListService getListService() {
        if (listService == null) {
            listService = new ShopListService();
        }
        return listService;
    }


    public static AccessService getAccessService() {
        if (accessService == null) {
            accessService = new AccessService();
        }
        return accessService;
    }

    public static GoodService getGoodService() {
        if (goodService == null) {
            goodService = new GoodService();
        }
        return goodService;
    }

    public static void setUserService(UserService userService) {
        Services.userService = userService;
    }

    public static UserService getUserService() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }


    public static CategoryService getCategoryService() {
        if (categoryService == null) {
            categoryService = new CategoryService();
        }
        return categoryService;
    }

    public static void setCategoryService(CategoryService categoryService) {
        Services.categoryService = categoryService;
    }

    public static void clearAll() {
        listService.removeAll();
        goodService.removeAll();
        accessService.removeAll();
        historyService.removeAll();
        userService.removeAll();
    }
}
