package com.seeapenny.client.server;


public enum SynchrAction {
    NIH(0), INS(1), UPD(2), DEL(3);
    private int id;

    private SynchrAction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SynchrAction dbValueOf(int id) {
        for (SynchrAction synchrAction : values()) {
            if (synchrAction.id == id) {
                return synchrAction;
            }
        }
        throw new IllegalArgumentException();
    }
}
