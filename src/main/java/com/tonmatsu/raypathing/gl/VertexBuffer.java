package com.tonmatsu.raypathing.gl;

import java.nio.*;

import static org.lwjgl.opengl.GL45.*;

public class VertexBuffer {
    private static VertexBuffer binded;
    private final int buffer;

    public VertexBuffer(int size, int usage) {
        buffer = glCreateBuffers();
        glNamedBufferData(buffer, size, usage);
    }

    public void dispose() {
        glDeleteBuffers(buffer);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
        binded = null;
    }

    public void update(ByteBuffer data) {
        glNamedBufferSubData(buffer, 0, data);
    }
}
