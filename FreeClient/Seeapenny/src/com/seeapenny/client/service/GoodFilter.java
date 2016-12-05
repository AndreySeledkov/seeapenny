package com.seeapenny.client.service;

/**
 * Created by Sony on 05.08.13.
 */
public class GoodFilter {

    private final boolean showInserted;
    private final boolean showDeleted;

    public GoodFilter(boolean showInserted, boolean showDeleted) {
        this.showInserted = showInserted;
        this.showDeleted = showDeleted;
    }

    public boolean isShowInserted() {
        return showInserted;
    }

    public boolean isShowDeleted() {
        return showDeleted;
    }
}
