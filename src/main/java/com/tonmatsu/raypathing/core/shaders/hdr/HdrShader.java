package com.tonmatsu.raypathing.core.shaders.hdr;

import com.tonmatsu.raypathing.gl.*;

import static org.lwjgl.opengl.ARBComputeVariableGroupSize.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;

public class HdrShader {
    private static final int GROUP_SIZE = 16;
    private final ShaderProgram shaderProgram;
    private Texture inPositionTexture;
    private Texture inNormalTexture;
    private Texture inAlbedoShininessTexture;
    private final Texture outColorTexture;

    public HdrShader() {
        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(GL_COMPUTE_SHADER, "shaders/02-hdr.cs.glsl");
        shaderProgram.link();

        outColorTexture = new Texture();
    }

    public void dispose() {
        shaderProgram.dispose();
        inPositionTexture.dispose();
        inNormalTexture.dispose();
        inAlbedoShininessTexture.dispose();
        outColorTexture.dispose();
    }

    public void setInPositionTexture(Texture inPositionTexture) {
        this.inPositionTexture = inPositionTexture;
    }

    public void setInNormalTexture(Texture inNormalTexture) {
        this.inNormalTexture = inNormalTexture;
    }

    public void setInAlbedoShininessTexture(Texture inAlbedoShininessTexture) {
        this.inAlbedoShininessTexture = inAlbedoShininessTexture;
    }

    public Texture getOutColorTexture() {
        return outColorTexture;
    }

    public void dispatch() {
        if (!inPositionTexture.size.equals(outColorTexture.size))
            outColorTexture.create(GL_RGBA32F, inPositionTexture.size, GL_RGBA, GL_FLOAT);
        inPositionTexture.bindImage(0, 0, GL_READ_ONLY, GL_RGBA32F);
        inNormalTexture.bindImage(0, 1, GL_READ_ONLY, GL_RGBA32F);
        inAlbedoShininessTexture.bindImage(0, 2, GL_READ_ONLY, GL_RGBA32F);
        outColorTexture.bindImage(0, 3, GL_WRITE_ONLY, GL_RGBA32F);
        shaderProgram.bind();
        glDispatchComputeGroupSizeARB(
                inPositionTexture.size.x / GROUP_SIZE, inPositionTexture.size.y / GROUP_SIZE, 1,
                GROUP_SIZE, GROUP_SIZE, 1);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT | GL_BUFFER_UPDATE_BARRIER_BIT);
        shaderProgram.unbind();
    }
}
