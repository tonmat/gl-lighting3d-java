package com.tonmatsu.raypathing.core.material;

import org.joml.*;

import java.nio.*;

public class Material {
    public static final int BYTES = 16;
    public final int id;
    public final Vector3f albedo = new Vector3f();
    public float shininess;

    public Material(int id) {
        this.id = id;
    }

    public void write(ByteBuffer buffer) {
        buffer.putFloat(albedo.x);
        buffer.putFloat(albedo.y);
        buffer.putFloat(albedo.z);
        buffer.putFloat(shininess);
    }
}
