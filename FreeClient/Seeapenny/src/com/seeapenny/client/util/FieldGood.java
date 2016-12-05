package com.seeapenny.client.util;

import java.util.EnumSet;

/**
 * Created by Sony on 19.07.13.
 */
public enum FieldGood {

    NAME(1), MEASURE(2), MODIFIED_TIME(3), PRICE(6), LAST_EDITOR_ID(7), STATE(8), CREATE_TIME(9), QUANTITY(10), CATEGORY(11), NOTE(12), PRIORITY(13), IMAGE(14), SYNCH_ACTION(15), OWNER_ID(16);
    private int id;

    private FieldGood(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static FieldGood getDbValue(int id) {
        for (FieldGood field : values()) {
            if (field.id == id) {
                return field;
            }
        }
        throw new IllegalArgumentException();
    }


    public static EnumSet<FieldGood> createEmptySaveTypes() {
        return EnumSet.noneOf(FieldGood.class);
    }
}
