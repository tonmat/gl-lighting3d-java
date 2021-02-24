package com.tonmatsu.raypathing.gl;

import java.nio.*;

import static org.lwjgl.opengl.GL45.*;

public class ShaderStorageBuffer {
    private static ShaderStorageBuffer binded;
    private final int buffer;

    public ShaderStorageBuffer(int size, int usage) {
        buffer = glCreateBuffers();
        glNamedBufferData(buffer, size, usage);
    }

    public void dispose() {
        glDeleteBuffers(buffer);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, GL_NONE);
        binded = null;
    }

    public void bindBufferBase(int index) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, buffer);
    }

    public void update(ByteBuffer data) {
        glNamedBufferSubData(buffer, 0, data);
    }
}
