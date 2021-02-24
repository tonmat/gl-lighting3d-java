package com.tonmatsu.raypathing.core.shaders.blend;

import com.tonmatsu.raypathing.gl.*;

import static com.tonmatsu.raypathing.gl.VertexAttribute.*;
import static org.lwjgl.opengl.ARBComputeVariableGroupSize.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.*;

public class BlendShader {
    private static final int GROUP_SIZE = 16;
    private final ShaderProgram shaderProgram;
    private final VertexArray vao;
    private final IndexBuffer ibo;
    private final VertexBuffer vbo;
    private Texture inTextureA;
    private Texture inTextureB;
    private Texture inDepthA;
    private Texture inDepthB;
    private final Texture outTexture;
    private final Framebuffer framebuffer;

    public BlendShader() {
        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(GL_COMPUTE_SHADER, "shaders/04-blend.cs.glsl");
        shaderProgram.link();

        vao = new VertexArray();
        ibo = new IndexBuffer(6 * Integer.BYTES, GL_STATIC_DRAW);
        vao.bind();
        ibo.bind();
        vao.unbind();
        ibo.unbind();
        fillIndices();
        vbo = new VertexBuffer(4 * 4 * Float.BYTES, GL_STATIC_DRAW);
        vao.setAttributes(vec3(vbo), uint1(vbo), uint1(vbo));
        fillVertices();

        outTexture = new Texture();

        framebuffer = new Framebuffer();
        framebuffer.texture(GL_COLOR_ATTACHMENT0, outTexture, 0);
        framebuffer.drawBuffers(GL_COLOR_ATTACHMENT0);
    }

    public void dispose() {
        shaderProgram.dispose();
        vao.dispose();
        ibo.dispose();
        vbo.dispose();
        outTexture.dispose();
        framebuffer.dispose();
    }

    public void setInTextureA(Texture inTextureA) {
        this.inTextureA = inTextureA;
    }

    public void setInTextureB(Texture inTextureB) {
        this.inTextureB = inTextureB;
    }

    public void setInDepthA(Texture inDepthA) {
        this.inDepthA = inDepthA;
    }

    public void setInDepthB(Texture inDepthB) {
        this.inDepthB = inDepthB;
    }

    public Texture getOutTexture() {
        return outTexture;
    }

    public Framebuffer getFramebuffer() {
        return framebuffer;
    }

    public void dispatch() {
        if (!inTextureA.size.equals(outTexture.size)) {
            outTexture.create(GL_RGBA32F, inTextureA.size, GL_RGBA, GL_FLOAT);
        }
        inTextureA.bindImage(0, 0, GL_READ_ONLY, GL_RGBA32F);
        inTextureB.bindImage(0, 1, GL_READ_ONLY, GL_RGBA32F);
        inDepthA.bind(2);
        inDepthB.bind(3);
        outTexture.bindImage(0, 4, GL_WRITE_ONLY, GL_RGBA32F);
        framebuffer.bind();
        shaderProgram.bind();
        glDispatchComputeGroupSizeARB(
                inTextureA.size.x / GROUP_SIZE, inTextureA.size.y / GROUP_SIZE, 1,
                GROUP_SIZE, GROUP_SIZE, 1);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        shaderProgram.unbind();
        framebuffer.unbind();
        inDepthA.unbind(2);
        inDepthB.unbind(3);
    }

    private void fillIndices() {
        final var data = memAlloc(6 * Integer.BYTES);
        try {
            data.putInt(0);
            data.putInt(1);
            data.putInt(2);
            data.putInt(2);
            data.putInt(3);
            data.putInt(0);
            data.flip();
            ibo.update(data);
        } finally {
            memFree(data);
        }
    }

    private void fillVertices() {
        final var data = memAlloc(4 * 4 * Float.BYTES);
        try {
            data.putFloat(-1.0f).putFloat(-1.0f).putFloat(0.0f).putFloat(1.0f);
            data.putFloat(+1.0f).putFloat(-1.0f).putFloat(1.0f).putFloat(1.0f);
            data.putFloat(+1.0f).putFloat(+1.0f).putFloat(1.0f).putFloat(0.0f);
            data.putFloat(-1.0f).putFloat(+1.0f).putFloat(0.0f).putFloat(0.0f);
            data.flip();
            vbo.update(data);
        } finally {
            memFree(data);
        }
    }
}
