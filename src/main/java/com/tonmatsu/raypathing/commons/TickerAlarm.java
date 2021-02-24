package com.tonmatsu.raypathing.commons;

public class TickerAlarm {
    private final float interval;
    private final Callback callback;
    private float delta;

    public TickerAlarm(float interval, Callback callback) {
        this.interval = interval;
        this.callback = callback;
    }

    public void update(Ticker ticker) {
        delta += ticker.delta;
        var id = 0;
        while (delta >= interval) {
            delta -= interval;
            callback.handle(id++);
        }
    }

    @FunctionalInterface
    public interface Callback {
        void handle(int id);
    }
}
