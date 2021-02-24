package com.tonmatsu.raypathing.core.camera;

import org.joml.*;

public abstract class Camera {
    public final Matrix4f projection = new Matrix4f();
    public final Matrix4f view = new Matrix4f();
    public final Matrix4f combined = new Matrix4f();
    public final Matrix3f normal = new Matrix3f();

    public void update() {
        onUpdateView(view);
        combined.set(projection).mul(view);
        view.normal(normal);
    }

    protected abstract void onUpdateView(Matrix4f view);
}
