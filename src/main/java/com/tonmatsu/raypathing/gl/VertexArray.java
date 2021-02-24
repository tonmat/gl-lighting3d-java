package com.tonmatsu.raypathing.gl;

import static org.lwjgl.opengl.GL45.*;

public class VertexArray {
    private static VertexArray binded;
    private final int array;

    public VertexArray() {
        array = glCreateVertexArrays();
    }

    public void dispose() {
        glDeleteVertexArrays(array);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindVertexArray(array);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindVertexArray(GL_NONE);
        binded = null;
    }

    public void setAttributes(VertexAttribute... attributes) {
        if (attributes.length == 0)
            return;
        bind();
        var stride = 0;
        for (final var attribute : attributes)
            stride += attribute.bytes;
        var pointer = 0;
        VertexAttribute attribute = null;
        for (int i = 0; i < attributes.length; i++) {
            attribute = attributes[i];
            attribute.vbo.bind();
            switch (attribute.type) {
                case GL_INT:
                case GL_UNSIGNED_INT:
                    glVertexAttribIPointer(i, attribute.size, attribute.type, stride, pointer);
                    break;
                default:
                    glVertexAttribPointer(i, attribute.size, attribute.type, attribute.normalized, stride, pointer);
                    break;
            }
            glEnableVertexAttribArray(i);
            pointer += attribute.bytes;
        }
        attribute.vbo.unbind();
        unbind();
    }
}
