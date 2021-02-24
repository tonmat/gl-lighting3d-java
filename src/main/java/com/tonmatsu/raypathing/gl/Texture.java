package com.tonmatsu.raypathing.gl;

import org.joml.*;

import java.nio.*;

import static org.lwjgl.opengl.EXTDirectStateAccess.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Texture {
    private static final Texture[] binded = new Texture[32];
    public final int texture;
    public final Vector2i size = new Vector2i();

    public Texture() {
        texture = glCreateTextures(GL_TEXTURE_2D);
        glTextureParameteri(texture, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTextureParameteri(texture, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTextureParameteri(texture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTextureParameteri(texture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public void dispose() {
        glDeleteTextures(texture);
    }

    public void bind(int unit) {
        if (binded[unit] == this)
            return;
        glBindTextureUnit(unit, texture);
        binded[unit] = this;
    }

    public void unbind(int unit) {
        if (binded[unit] == null)
            return;
        glBindTextureUnit(unit, GL_NONE);
        binded[unit] = null;
    }

    public void setParameteri(int name, int i) {
        glTextureParameteri(texture, name, i);
    }

    public void generateMipmap() {
        glGenerateTextureMipmap(texture);
    }

    public void bindImage(int level, int unit, int access, int format) {
        glBindImageTexture(unit, texture, level, false, 0, access, format);
    }

    public void storage(int internalFormat, Vector2i size) {
        glTextureStorage2D(texture, 1, internalFormat, size.x, size.y);
        this.size.set(size);
    }

    public void create(int internalFormat, Vector2i size, int format, int type) {
        glTextureImage2DEXT(texture, GL_TEXTURE_2D, 0, internalFormat, size.x, size.y, 0, format, type, NULL);
        this.size.set(size);
    }

    public void update(Vector2i size, int format, int type, ByteBuffer pixels) {
        glTextureSubImage2D(texture, 0, 0, 0, size.x, size.y, format, type, pixels);
    }

    public ByteBuffer getImage(int level, int format, int type, ByteBuffer pixels) {
        glGetTextureImageEXT(texture, GL_TEXTURE_2D, level, format, type, pixels);
        return pixels;
    }
}
