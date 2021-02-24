package com.tonmatsu.raypathing.core.camera;

import org.joml.*;

public class FirstPersonCamera extends Camera {
    public final Vector3f position = new Vector3f();
    public final Vector2f rotation = new Vector2f();

    private final Vector3f forward = new Vector3f();
    private final Vector3f right = new Vector3f();
    private final Vector3f up = new Vector3f();

    public void moveForward(float delta) {
        position.fma(delta, forward);
    }

    public void moveRight(float delta) {
        position.fma(delta, right);
    }

    public void moveUp(float delta) {
        position.fma(delta, up);
    }

    @Override
    protected void onUpdateView(Matrix4f view) {
        view.identity()
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .translate(-position.x, -position.y, -position.z);
        view.positiveZ(forward).mul(-1.0f);
        view.positiveX(right);
        view.positiveY(up);
    }
}
