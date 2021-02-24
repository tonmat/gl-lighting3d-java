package com.tonmatsu.raypathing.core.shaders.light;

import com.tonmatsu.raypathing.core.camera.*;
import com.tonmatsu.raypathing.gl.*;
import org.joml.*;

import java.nio.*;

import static com.tonmatsu.raypathing.gl.VertexAttribute.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LightShader {
    private final ByteBuffer vertices;
    private final ShaderProgram shaderProgram;
    private final VertexArray vao;
    private final IndexBuffer ibo;
    private final VertexBuffer vbo;
    private final Texture colorTexture;
    private final Texture depthTexture;
    private final Framebuffer framebuffer;
    private int indexCount;
    private Camera camera;
    private final Vector3f temp = new Vector3f();

    public LightShader(int capacity) {
        final var faces = capacity * 6;
        final var maxIndices = faces * 6;
        final var maxVertices = faces * 4;

        vertices = memAlloc(maxVertices * 6 * Float.BYTES);

        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(GL_VERTEX_SHADER, "shaders/03-light.vs.glsl");
        shaderProgram.attachShader(GL_FRAGMENT_SHADER, "shaders/03-light.fs.glsl");
        shaderProgram.link();

        vao = new VertexArray();
        ibo = new IndexBuffer(maxIndices * Integer.BYTES, GL_STATIC_DRAW);
        vao.bind();
        ibo.bind();
        vao.unbind();
        ibo.unbind();
        fillIndices(faces, maxIndices);
        vbo = new VertexBuffer(vertices.capacity(), GL_DYNAMIC_DRAW);
        vao.setAttributes(vec3(vbo), vec3(vbo));

        colorTexture = new Texture();
        depthTexture = new Texture();

        framebuffer = new Framebuffer();
        framebuffer.texture(GL_COLOR_ATTACHMENT0, colorTexture, 0);
        framebuffer.texture(GL_DEPTH_ATTACHMENT, depthTexture, 0);
        framebuffer.drawBuffers(GL_COLOR_ATTACHMENT0);
    }

    public void dispose() {
        memFree(vertices);
        shaderProgram.dispose();
        vao.dispose();
        ibo.dispose();
        vbo.dispose();
        colorTexture.dispose();
        depthTexture.dispose();
        framebuffer.dispose();
    }

    public Texture getColorTexture() {
        return colorTexture;
    }

    public Texture getDepthTexture() {
        return depthTexture;
    }

    public Framebuffer getFramebuffer() {
        return framebuffer;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setFramebufferSize(Vector2i size) {
        colorTexture.create(GL_RGBA32F, size, GL_RGBA, GL_FLOAT);
        depthTexture.create(GL_DEPTH_COMPONENT32F, size, GL_DEPTH_COMPONENT, GL_FLOAT);
    }

    public void begin() {
        vertices.clear();
        indexCount = 0;
    }

    public void draw(Vector3f center, Vector3f color) {
        final var p = 0.05f;
        final var n = -0.05f;
        write(p, n, 0, n, n, 0, n, p, 0, p, p, 0, temp.set(center).add(0, 0, n), color);
        write(n, n, 0, p, n, 0, p, p, 0, n, p, 0, temp.set(center).add(0, 0, p), color);
        write(0, n, p, 0, n, n, 0, p, n, 0, p, p, temp.set(center).add(p, 0, 0), color);
        write(0, n, n, 0, n, p, 0, p, p, 0, p, n, temp.set(center).add(n, 0, 0), color);
        write(n, 0, p, p, 0, p, p, 0, n, n, 0, n, temp.set(center).add(0, p, 0), color);
        write(n, 0, n, p, 0, n, p, 0, p, n, 0, p, temp.set(center).add(0, n, 0), color);
    }

    public void end() {
        vertices.flip();
        vbo.update(vertices);

        framebuffer.bind();
        shaderProgram.bind();
        shaderProgram.setUniformMatrix4f("u_vp", camera.combined);
        vao.bind();
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, NULL);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        vao.unbind();
        shaderProgram.unbind();
        framebuffer.unbind();
    }

    private void fillIndices(int faces, int maxIndices) {
        final var data = memAlloc(maxIndices * Integer.BYTES);
        try {
            for (int i = 0; i < faces; i++) {
                final var v = i * 4;
                data.putInt(v);
                data.putInt(v + 1);
                data.putInt(v + 2);
                data.putInt(v + 2);
                data.putInt(v + 3);
                data.putInt(v);
            }
            data.flip();
            ibo.update(data);
        } finally {
            memFree(data);
        }
    }

    private void write(
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            Vector3f p, Vector3f c) {

        indexCount += 6;
        vertices.putFloat(p.x + x1).putFloat(p.y + y1).putFloat(p.z + z1).putFloat(c.x).putFloat(c.y).putFloat(c.z);
        vertices.putFloat(p.x + x2).putFloat(p.y + y2).putFloat(p.z + z2).putFloat(c.x).putFloat(c.y).putFloat(c.z);
        vertices.putFloat(p.x + x3).putFloat(p.y + y3).putFloat(p.z + z3).putFloat(c.x).putFloat(c.y).putFloat(c.z);
        vertices.putFloat(p.x + x4).putFloat(p.y + y4).putFloat(p.z + z4).putFloat(c.x).putFloat(c.y).putFloat(c.z);
    }
}
