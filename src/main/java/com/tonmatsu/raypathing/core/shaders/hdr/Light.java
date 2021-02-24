package com.tonmatsu.raypathing.core.shaders.hdr;

import com.tonmatsu.raypathing.core.camera.*;
import org.joml.*;

import java.nio.*;

public class Light {
    public static final int BYTES = 60;
    public final int id;
    public final Vector3f position = new Vector3f();
    public final Vector3f ambient = new Vector3f();
    public final Vector3f diffuse = new Vector3f();
    public final Vector3f specular = new Vector3f();
    public final Vector3f attenuation = new Vector3f();
    private final Vector3f temp = new Vector3f();

    public Light(int id) {
        this.id = id;
    }

    public void write(ByteBuffer buffer, Camera camera) {
        temp.set(position).mulPosition(camera.view);
        buffer.putFloat(temp.x);
        buffer.putFloat(temp.y);
        buffer.putFloat(temp.z);
        buffer.putFloat(ambient.x);
        buffer.putFloat(ambient.y);
        buffer.putFloat(ambient.z);
        buffer.putFloat(diffuse.x);
        buffer.putFloat(diffuse.y);
        buffer.putFloat(diffuse.z);
        buffer.putFloat(specular.x);
        buffer.putFloat(specular.y);
        buffer.putFloat(specular.z);
        buffer.putFloat(attenuation.x);
        buffer.putFloat(attenuation.y);
        buffer.putFloat(attenuation.z);
    }
}
