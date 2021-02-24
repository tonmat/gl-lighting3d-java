package com.tonmatsu.raypathing.gl;

import org.lwjgl.opengl.*;

public class VertexAttribute {
    public final VertexBuffer vbo;
    public final int size;
    public final int type;
    public final boolean normalized;
    public final int bytes;

    public VertexAttribute(VertexBuffer vbo, int size, int type, boolean normalized, int bytes) {
        this.vbo = vbo;
        this.size = size;
        this.type = type;
        this.normalized = normalized;
        this.bytes = bytes;
    }

    public static VertexAttribute float1(VertexBuffer vbo) {
        return new VertexAttribute(vbo, 1, GL11.GL_FLOAT, false, 4);
    }

    public static VertexAttribute vec2(VertexBuffer vbo) {
        return new VertexAttribute(vbo, 2, GL11.GL_FLOAT, false, 8);
    }

    public static VertexAttribute vec3(VertexBuffer vbo) {
        return new VertexAttribute(vbo, 3, GL11.GL_FLOAT, false, 12);
    }

    public static VertexAttribute vec4(VertexBuffer vbo) {
        return new VertexAttribute(vbo, 4, GL11.GL_FLOAT, false, 16);
    }

    public static VertexAttribute int1(VertexBuffer vbo) {
        return new VertexAttribute(vbo, 1, GL11.GL_INT, false, 4);
    }

    public static VertexAttribute ivec2(VertexBuffer vbo) {
        return new VertexAttribute(vbo, 2, GL11.GL_INT, false, 8);
    }

    public static VertexAttribute ivec3(VertexBuffer vbo) {
        return new VertexAttribute(vbo, 3, GL11.GL_INT, false, 12);
    }

    public static VertexAttribute ivec4(VertexBuffer vbo) {
        return new VertexAttribute(vbo, 4, GL11.GL_INT, false, 16);
    }

    public static VertexAttribute uint1(VertexBuffer vbo) {
        return new VertexAttribute(vbo, 1, GL11.GL_UNSIGNED_INT, false, 4);
    }
}
