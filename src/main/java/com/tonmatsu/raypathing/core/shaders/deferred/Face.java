package com.tonmatsu.raypathing.core.shaders.deferred;

public enum Face {
    NORTH(0),
    SOUTH(1),
    EAST(2),
    WEST(3),
    TOP(4),
    BOTTOM(5);

    public final int id;

    Face(int id) {
        this.id = id;
    }
}
