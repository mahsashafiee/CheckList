package com.example.checklist.model;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

public enum State {
    TODO(0), DONE(1), DOING(2);

    State(int value) {
        this.value = value;
    }

    @SuppressLint("UseSparseArrays")
    private static final Map<Integer, State> intToState = new HashMap<Integer, State>();

    private final int value;

    static {
        for (State type : State.values()) {
            intToState.put(type.value, type);
        }
    }

    public int getValue() {
        return value;
    }

    public static State fromInt(int i) {
        State type = intToState.get(i);
        if (type == null)
            return State.TODO;
        return type;
    }
}
