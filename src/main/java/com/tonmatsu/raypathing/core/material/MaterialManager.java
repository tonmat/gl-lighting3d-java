package com.tonmatsu.raypathing.core.material;

import com.tonmatsu.raypathing.gl.*;

import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;

public class MaterialManager {
    private final ShaderStorageBuffer ssbo;
    private final ArrayList<Material> materials;
    private boolean dirty;

    public MaterialManager(int capacity) {
        ssbo = new ShaderStorageBuffer(capacity * Material.BYTES, GL_STATIC_DRAW);
        ssbo.bindBufferBase(1);
        materials = new ArrayList<>(capacity);
    }

    public void dispose() {
        ssbo.dispose();
    }

    public Material create() {
        dirty = true;
        final var material = new Material(materials.size());
        materials.add(material);
        return material;
    }

    public void update() {
        if (!dirty)
            return;
        final var size = Material.BYTES * materials.size() + Integer.BYTES;
        final var data = memAlloc(size);
        try {
            for (Material m : materials)
                m.write(data);
            data.flip();
            ssbo.update(data);
        } finally {
            memFree(data);
        }
        dirty = false;
    }
}
