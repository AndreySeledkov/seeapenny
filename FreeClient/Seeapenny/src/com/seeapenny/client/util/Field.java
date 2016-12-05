package com.seeapenny.client.util;

import java.util.EnumSet;

public enum Field {
    NAME(1), CATEGORY(2), UNIT_MEASURE(3), QUANTITY(4), STATE(5), PRICE(6), IMAGE(7), PRIORITY(9), NOTE(10), LAST_EDITOR_ID(11), MODIFIED_TIME(12),SYNCH_ACTION(13);
    private int id;

    private Field(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Field getDbValue(int id) {
        for (Field field : values()) {
            if (field.id == id) {
                return field;
            }
        }
        throw new IllegalArgumentException();
    }


    public static EnumSet<Field> createEmptySaveTypes() {
        return EnumSet.noneOf(Field.class);
    }
}
