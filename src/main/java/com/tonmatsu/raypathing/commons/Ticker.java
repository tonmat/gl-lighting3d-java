package com.tonmatsu.raypathing.commons;

public class Ticker {
    public float delta;
    public float elapsedTime;

    private float lastUpdateTime;

    public void update(float time) {
        if (lastUpdateTime == 0.0f) {
            lastUpdateTime = time;
            return;
        }

        delta = time - lastUpdateTime;
        lastUpdateTime = time;
        elapsedTime += delta;
    }
}
