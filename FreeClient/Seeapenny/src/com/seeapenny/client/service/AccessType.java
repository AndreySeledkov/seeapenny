package com.seeapenny.client.service;

import java.util.EnumSet;

/**
 * Created by Sony on 15.07.13.
 */
public enum AccessType {
    LIST(1), CATEGORY(2);

    private int id;

    private AccessType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public static AccessType getDbValue(int type) {
        for (AccessType accessType : values()) {
            if (type == accessType.getId()) {
                return accessType;
            }
        }

        return null;
    }
}
