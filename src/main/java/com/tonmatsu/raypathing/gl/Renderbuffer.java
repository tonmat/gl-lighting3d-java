package com.tonmatsu.raypathing.gl;

import org.joml.*;

import static org.lwjgl.opengl.GL45.*;

public class Renderbuffer {
    private static Renderbuffer binded;
    public final int renderbuffer;

    public Renderbuffer() {
        renderbuffer = glCreateRenderbuffers();
    }

    public void dispose() {
        glDeleteRenderbuffers(renderbuffer);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindRenderbuffer(GL_RENDERBUFFER, renderbuffer);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindRenderbuffer(GL_RENDERBUFFER, GL_NONE);
        binded = null;
    }

    public void storage(int internalFormat, Vector2i size) {
        glNamedRenderbufferStorage(renderbuffer, internalFormat, size.x, size.y);
    }
}
