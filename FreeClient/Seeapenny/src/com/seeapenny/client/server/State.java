package com.seeapenny.client.server;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 28.02.13
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
public enum State implements Serializable {
    NORMAL(0), BOUGHT(1);

    private int id;

    private State(int id) {
        this.id = id;
    }

    public static State valueOf(int ordinal) {
        for (State state : values()) {
            if (state.getId() == ordinal) {
                return state;
            }
        }
        return null;
    }


    public int getId() {
        return id;
    }

}
