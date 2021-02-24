package com.tonmatsu.raypathing.gl;

import org.joml.*;

import static org.lwjgl.opengl.EXTDirectStateAccess.*;
import static org.lwjgl.opengl.GL45.*;

public class Framebuffer {
    private static Framebuffer binded;
    private final int framebuffer;

    public Framebuffer() {
        framebuffer = glCreateFramebuffers();
    }

    public void dispose() {
        glDeleteFramebuffers(framebuffer);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);
        binded = null;
    }

    public void texture(int attachment, Texture texture, int level) {
        glNamedFramebufferTexture2DEXT(framebuffer, attachment, GL_TEXTURE_2D, texture.texture, level);
    }

    public void renderbuffer(int attachment, Renderbuffer renderbuffer) {
        glNamedFramebufferRenderbuffer(framebuffer, attachment, GL_RENDERBUFFER, renderbuffer.renderbuffer);
    }

    public void drawBuffers(int... attachments) {
        glNamedFramebufferDrawBuffers(framebuffer, attachments);
    }

    public void blit(Framebuffer target, Vector2i size, int mask, int filter) {
        if (target == null)
            glBlitNamedFramebuffer(framebuffer, GL_NONE, 0, 0, size.x, size.y, 0, 0, size.x, size.y, mask, filter);
        else
            glBlitNamedFramebuffer(framebuffer, target.framebuffer, 0, 0, size.x, size.y, 0, 0, size.x, size.y, mask, filter);
    }
}
