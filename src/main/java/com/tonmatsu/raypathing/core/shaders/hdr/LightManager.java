package com.tonmatsu.raypathing.core.shaders.hdr;

import com.tonmatsu.raypathing.core.camera.*;
import com.tonmatsu.raypathing.gl.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LightManager {
    public final int capacity;
    private final ShaderStorageBuffer ssbo;
    private final ByteBuffer buffer;
    private final ArrayList<Light> lights;

    public LightManager(int capacity) {
        this.capacity = capacity;
        ssbo = new ShaderStorageBuffer(capacity * Light.BYTES + Integer.BYTES, GL_DYNAMIC_DRAW);
        ssbo.bindBufferBase(0);
        buffer = memAlloc(capacity * Light.BYTES + Integer.BYTES);
        lights = new ArrayList<>(capacity);
    }

    public void dispose() {
        ssbo.dispose();
        memFree(buffer);
    }

    public Light create() {
        final var light = new Light(lights.size());
        lights.add(light);
        return light;
    }

    public void remove(Light light) {
        lights.remove(light);
    }

    public void update(Camera camera) {
        buffer.clear();
        buffer.putInt(lights.size());
        for (final var light : lights)
            light.write(buffer, camera);
        buffer.flip();
        ssbo.update(buffer);
    }

    public List<Light> getLights() {
        return lights;
    }
}
