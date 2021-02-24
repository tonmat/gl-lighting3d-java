package com.tonmatsu.raypathing.core;

import com.tonmatsu.raypathing.commons.*;
import com.tonmatsu.raypathing.core.camera.*;
import com.tonmatsu.raypathing.core.material.*;
import com.tonmatsu.raypathing.core.shaders.blend.*;
import com.tonmatsu.raypathing.core.shaders.bloom.*;
import com.tonmatsu.raypathing.core.shaders.blur.*;
import com.tonmatsu.raypathing.core.shaders.deferred.*;
import com.tonmatsu.raypathing.core.shaders.hdr.*;
import com.tonmatsu.raypathing.core.shaders.light.*;
import com.tonmatsu.raypathing.core.shaders.postfx.*;
import com.tonmatsu.raypathing.gl.*;
import org.joml.Math;
import org.joml.*;

import static org.joml.Math.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private long window;
    private Vector2i viewport;
    private double[] cursorX, cursorY;
    private FirstPersonCamera camera;
    private MaterialManager materialManager;
    private LightManager lightManager;
    private DeferredShader deferredShader;
    private HdrShader hdrShader;
    private LightShader lightShader;
    private BlendShader blendShader1;
    private BloomShader bloomShader;
    private BlurShader blurShader;
    private BlendShader blendShader2;
    private PostFXShader postFXShader;
    private float exposure;
    private Light lightR;
    private Light lightG;
    private Light lightB;
    private Vector3f temp = new Vector3f();

    public void onStart(long window) {
        this.window = window;
        viewport = new Vector2i();
        cursorX = new double[1];
        cursorY = new double[1];

        camera = new FirstPersonCamera();
        camera.position.set(0.0f, 0.0f, 2.0f);

        materialManager = new MaterialManager(1);
        final var material = materialManager.create();
        material.albedo.set(1.0f, 1.0f, 1.0f);
        material.shininess = 16.0f;

        lightManager = new LightManager(3);
        lightR = lightManager.create();
        lightR.ambient.set(0.01f, 0.0f, 0.0f);
        lightR.diffuse.set(0.9f, 0.2f, 0.2f);
        lightR.specular.set(0.9f, 0.2f, 0.2f);
        lightR.attenuation.set(1.0f, 0.7f, 0.8f);
        lightG = lightManager.create();
        lightG.ambient.set(0.0f, 0.01f, 0.0f);
        lightG.diffuse.set(0.2f, 0.9f, 0.2f);
        lightG.specular.set(0.2f, 0.9f, 0.2f);
        lightG.attenuation.set(1.0f, 0.7f, 0.8f);
        lightB = lightManager.create();
        lightB.ambient.set(0.0f, 0.0f, 0.01f);
        lightB.diffuse.set(0.2f, 0.2f, 0.9f);
        lightB.specular.set(0.2f, 0.2f, 0.9f);
        lightB.attenuation.set(1.0f, 0.7f, 0.8f);

        deferredShader = new DeferredShader(16);
        hdrShader = new HdrShader();
        lightShader = new LightShader(4);
        blendShader1 = new BlendShader();
        bloomShader = new BloomShader();
        blurShader = new BlurShader();
        blendShader2 = new BlendShader();
        postFXShader = new PostFXShader();
    }

    public void onStop() {
        materialManager.dispose();
        lightManager.dispose();
        deferredShader.dispose();
        hdrShader.dispose();
        lightShader.dispose();
        blendShader1.dispose();
        bloomShader.dispose();
        blurShader.dispose();
        blendShader2.dispose();
        postFXShader.dispose();
    }

    public void onViewportResized(Vector2i viewport) {
        glViewport(0, 0, viewport.x, viewport.y);
        this.viewport.set(viewport);
        camera.projection.setPerspective(
                1.2f,
                (float) viewport.x / viewport.y,
                0.01f, 100.0f);
        deferredShader.setFramebufferSize(viewport);
        lightShader.setFramebufferSize(viewport);
    }

    public void onUpdate(Ticker ticker) {
        var speed = 4.0f;
        var forward = 0;
        var right = 0;
        var up = 0;
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) speed = 1.0f;
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) forward++;
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) forward--;
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) right++;
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) right--;
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) up++;
        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) up--;
        glfwGetCursorPos(window, cursorX, cursorY);

        camera.moveForward(forward * ticker.delta * speed);
        camera.moveRight(right * ticker.delta * speed);
        camera.moveUp(up * ticker.delta * speed);
        camera.rotation.x = 0.001f * (float) cursorY[0];
        camera.rotation.y = 0.001f * (float) cursorX[0];
        camera.update();

        //

        lightR.position.x = sin(ticker.elapsedTime * 0.8f) * 2.0f;
        lightR.position.y = cos(ticker.elapsedTime * 0.8f) * 2.0f;
        lightG.position.x = sin(ticker.elapsedTime * 0.4f) * 2.0f;
        lightG.position.z = cos(ticker.elapsedTime * 0.4f) * 2.0f;
        lightB.position.y = sin(ticker.elapsedTime * 0.8f) * 2.0f;
        lightB.position.z = cos(ticker.elapsedTime * 0.8f) * 2.0f;

        materialManager.update();

        lightManager.update(camera);

        //

        deferredShader.setCamera(camera);
        deferredShader.begin();
        deferredShader.draw(new Vector3f(0.0f, 0.0f, 0.0f), 0);
        deferredShader.end();

        hdrShader.setInPositionTexture(deferredShader.getPositionTexture());
        hdrShader.setInNormalTexture(deferredShader.getNormalTexture());
        hdrShader.setInAlbedoShininessTexture(deferredShader.getAlbedoShininessTexture());
        hdrShader.dispatch();

        lightShader.setCamera(camera);
        lightShader.begin();
        for (final var light : lightManager.getLights())
            lightShader.draw(light.position, temp.set(light.ambient).add(light.diffuse).add(light.specular));
        lightShader.end();

        blendShader1.setInTextureA(hdrShader.getOutColorTexture());
        blendShader1.setInTextureB(lightShader.getColorTexture());
        blendShader1.setInDepthA(deferredShader.getDepthTexture());
        blendShader1.setInDepthB(lightShader.getDepthTexture());
        blendShader1.dispatch();

        bloomShader.setInTexture(blendShader1.getOutTexture());
        bloomShader.dispatch();

        blurShader.setInTexture(bloomShader.getOutBloom());
        blurShader.dispatch();

        blendShader2.setInTextureA(blendShader1.getOutTexture());
        blendShader2.setInTextureB(blurShader.getOutTexture());
        blendShader2.setInDepthA(deferredShader.getDepthTexture());
        blendShader2.setInDepthB(deferredShader.getDepthTexture());
        blendShader2.dispatch();

        final var exposureTarget = Math.clamp(0.0f, 4.0f, 0.5f / bloomShader.getBrightness());
        exposure = lerp(exposure, exposureTarget, 2.0f * ticker.delta);

        postFXShader.setInTexture(blendShader2.getOutTexture());
        postFXShader.setExposure(exposure);
        postFXShader.setGamma(2.2f);
        postFXShader.dispatch();

        glClear(GL_COLOR_BUFFER_BIT);

        drawTexture(postFXShader.getOutTexture(), 0, 0, 1);

        drawTexture(deferredShader.getPositionTexture(), -4, 4, 0.2f);
        drawTexture(deferredShader.getNormalTexture(), -4, 2, 0.2f);
        drawTexture(deferredShader.getAlbedoShininessTexture(), -4, 0, 0.2f);
        drawTexture(hdrShader.getOutColorTexture(), -4, -2, 0.2f);
        drawTexture(lightShader.getColorTexture(), -4, -4, 0.2f);

        drawTexture(blendShader1.getOutTexture(), 4, 4, 0.2f);
        drawTexture(bloomShader.getOutBrightness(), 4, 2, 0.2f);
        drawTexture(bloomShader.getOutBloom(), 4, 0, 0.2f);
        drawTexture(blurShader.getOutTexture(), 4, -2, 0.2f);
        drawTexture(blendShader2.getOutTexture(), 4, -4, 0.2f);
    }

    private void drawTexture(Texture texture, float x, float y, float scale) {
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        texture.bind(0);
        glLoadIdentity();
        glScalef(scale, scale, 1.0f);
        glTranslatef(x, y, 0);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(-1, -1);
        glTexCoord2f(1, 0);
        glVertex2f(+1, -1);
        glTexCoord2f(1, 1);
        glVertex2f(+1, +1);
        glTexCoord2f(0, 1);
        glVertex2f(-1, +1);
        glEnd();
        texture.unbind(0);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }
}
