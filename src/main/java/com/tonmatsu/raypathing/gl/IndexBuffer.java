package com.tonmatsu.raypathing.gl;

import java.nio.*;

import static org.lwjgl.opengl.GL45.*;

public class IndexBuffer {
    private static IndexBuffer binded;
    private final int buffer;

    public IndexBuffer(int size, int usage) {
        buffer = glCreateBuffers();
        glNamedBufferData(buffer, size, usage);
    }

    public void dispose() {
        glDeleteBuffers(buffer);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffer);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE);
        binded = null;
    }

    public void update(ByteBuffer data) {
        glNamedBufferSubData(buffer, 0, data);
    }
}
