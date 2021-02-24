package com.tonmatsu.raypathing.commons;

public class TickerProfiler {
    private final float[] history;
    private int size;
    private int index;

    private float average;
    private boolean dirty;

    public TickerProfiler(int capacity) {
        this.history = new float[capacity];
    }

    public void update(Ticker ticker) {
        if (size < history.length)
            size++;
        history[index] = ticker.delta;
        index = ++index % history.length;
        dirty = true;
    }

    public float getAverage() {
        if (dirty) {
            average = 0.0f;
            for (int i = 0; i < size; i++)
                average += history[i];
            average /= size;
            dirty = false;
        }
        return average;
    }
}
