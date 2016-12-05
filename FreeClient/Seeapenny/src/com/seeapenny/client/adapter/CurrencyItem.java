package com.seeapenny.client.adapter;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 20.06.13
 * Time: 2:07
 * To change this template use File | Settings | File Templates.
 */
public class CurrencyItem {
    private int position;
    private String name;
    private boolean isSelected;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
