package com.seeapenny.client.adapter;

/**
 * Created by Sony on 06.08.13.
 */
public class DrawerMenu {

    private int id;
    private int resId;
    private String title;

    public DrawerMenu(int id, String title, int resId) {
        this.id = id;
        this.resId = resId;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
