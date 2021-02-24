package com.tonmatsu.raypathing.core.shaders.postfx;

import com.tonmatsu.raypathing.gl.*;

import static org.lwjgl.opengl.ARBComputeVariableGroupSize.*;
import static org.lwjgl.opengl.GL43.*;

public class PostFXShader {
    private static final int GROUP_SIZE = 16;
    private final ShaderProgram shaderProgram;
    private Texture inTexture;
    private final Texture outTexture;
    private float exposure = 1.0f;
    private float gamma = 2.2f;

    public PostFXShader() {
        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(GL_COMPUTE_SHADER, "shaders/07-postfx.cs.glsl");
        shaderProgram.link();

        outTexture = new Texture();
    }

    public void dispose() {
        shaderProgram.dispose();
        outTexture.dispose();
    }

    public void setInTexture(Texture inTexture) {
        this.inTexture = inTexture;
    }

    public Texture getOutTexture() {
        return outTexture;
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public void dispatch() {
        if (!inTexture.size.equals(outTexture.size))
            outTexture.create(GL_RGBA32F, inTexture.size, GL_RGBA, GL_FLOAT);
        inTexture.bindImage(0, 0, GL_READ_ONLY, GL_RGBA32F);
        outTexture.bindImage(0, 1, GL_WRITE_ONLY, GL_RGBA32F);
        shaderProgram.setUniform1f("u_exposure", exposure);
        shaderProgram.setUniform1f("u_gamma", gamma);
        shaderProgram.bind();
        glDispatchComputeGroupSizeARB(
                inTexture.size.x / GROUP_SIZE, inTexture.size.y / GROUP_SIZE, 1,
                GROUP_SIZE, GROUP_SIZE, 1);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        shaderProgram.unbind();
    }
}
