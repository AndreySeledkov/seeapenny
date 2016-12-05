package com.seeapenny.client;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 01.04.13
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
public enum SynchAction {
    NO_CHANGES(0), INSERT(1), UPDATE(2), DELETE(3);

    private int id;

    private SynchAction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SynchAction valueOf(int ordinal) {
        return values()[ordinal];
    }
}
