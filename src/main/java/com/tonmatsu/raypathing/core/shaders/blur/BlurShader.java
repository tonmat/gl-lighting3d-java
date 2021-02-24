package com.tonmatsu.raypathing.core.shaders.blur;

import com.tonmatsu.raypathing.gl.*;

import static org.lwjgl.opengl.ARBComputeVariableGroupSize.*;
import static org.lwjgl.opengl.GL43.*;

public class BlurShader {
    private static final int GROUP_SIZE = 16;
    private final ShaderProgram shaderProgram;
    private Texture inTexture;
    private Texture outTexture;
    private final Texture[] outTextures = new Texture[2];

    public BlurShader() {
        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(GL_COMPUTE_SHADER, "shaders/06-blur.cs.glsl");
        shaderProgram.link();

        for (int i = 0; i < outTextures.length; i++)
            outTextures[i] = new Texture();
        outTexture = outTextures[0];
    }

    public void dispose() {
        shaderProgram.dispose();
        for (Texture outTexture : outTextures)
            outTexture.dispose();
    }

    public void setInTexture(Texture inTexture) {
        this.inTexture = inTexture;
    }

    public Texture getOutTexture() {
        return outTexture;
    }

    public void dispatch() {
        if (!inTexture.size.equals(outTexture.size)) {
            for (final var texture : outTextures)
                texture.create(GL_RGBA32F, inTexture.size, GL_RGBA, GL_FLOAT);
        }
        shaderProgram.bind();
        for (int i = 0; i < 16; i++) {
            final var j = i % 2;
            final var inTexture = i == 0 ? this.inTexture : outTextures[1 - j];
            outTexture = outTextures[j];
            inTexture.bindImage(0, j, GL_READ_ONLY, GL_RGBA32F);
            outTexture.bindImage(0, 1, GL_WRITE_ONLY, GL_RGBA32F);
            shaderProgram.setUniform1b("u_horizontal", j == 0);
            glDispatchComputeGroupSizeARB(
                    inTexture.size.x / GROUP_SIZE, inTexture.size.y / GROUP_SIZE, 1,
                    GROUP_SIZE, GROUP_SIZE, 1);
            glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        }
        shaderProgram.unbind();
    }
}
