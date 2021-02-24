package com.tonmatsu.raypathing.core.shaders.bloom;

import com.tonmatsu.raypathing.gl.*;

import static java.lang.Math.*;
import static org.lwjgl.opengl.ARBComputeVariableGroupSize.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryStack.*;

public class BloomShader {
    private static final double LOG2 = log(2.0);
    private static final int GROUP_SIZE = 16;
    private final ShaderProgram shaderProgram;
    private Texture inTexture;
    private Texture outBrightness;
    private Texture outBloom;
    private int outBrightnessMipmapLevel;
    private float brightness;

    public BloomShader() {
        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(GL_COMPUTE_SHADER, "shaders/05-bloom.cs.glsl");
        shaderProgram.link();

        inTexture = new Texture();
        outBrightness = new Texture();
        outBrightness.setParameteri(GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        outBloom = new Texture();
    }

    public void dispose() {
        shaderProgram.dispose();
        outBrightness.dispose();
        outBrightness.dispose();
        outBloom.dispose();
    }

    public void setInTexture(Texture inTexture) {
        this.inTexture = inTexture;
    }

    public Texture getOutBrightness() {
        return outBrightness;
    }

    public Texture getOutBloom() {
        return outBloom;
    }

    public float getBrightness() {
        return brightness;
    }

    public void dispatch() {
        if (!inTexture.size.equals(outBrightness.size)) {
            outBrightness.create(GL_RGBA32F, inTexture.size, GL_RGBA, GL_FLOAT);
            outBloom.create(GL_RGBA32F, inTexture.size, GL_RGBA, GL_FLOAT);
            outBrightnessMipmapLevel = (int) floor(log(max(inTexture.size.x, inTexture.size.y)) / LOG2);
        }
        inTexture.bindImage(0, 0, GL_READ_ONLY, GL_RGBA32F);
        outBrightness.bindImage(0, 1, GL_WRITE_ONLY, GL_RGBA32F);
        outBloom.bindImage(0, 2, GL_WRITE_ONLY, GL_RGBA32F);
        shaderProgram.setUniform1f("u_brightness", brightness);
        shaderProgram.bind();
        glDispatchComputeGroupSizeARB(
                inTexture.size.x / GROUP_SIZE, inTexture.size.y / GROUP_SIZE, 1,
                GROUP_SIZE, GROUP_SIZE, 1);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        shaderProgram.unbind();
        outBrightness.generateMipmap();
        try (final var stack = stackPush()) {
            brightness = outBrightness.getImage(outBrightnessMipmapLevel, GL_RED, GL_FLOAT, stack.malloc(Float.BYTES)).getFloat();
        }
    }
}
