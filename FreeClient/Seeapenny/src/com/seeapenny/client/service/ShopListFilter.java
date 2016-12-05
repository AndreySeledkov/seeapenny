package com.seeapenny.client.service;


public class ShopListFilter {

    private final boolean includeDeleted;

    public ShopListFilter(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public boolean isIncludeDeleted() {
        return includeDeleted;
    }
}
