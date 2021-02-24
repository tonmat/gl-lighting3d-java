package com.tonmatsu.raypathing;

import com.tonmatsu.raypathing.commons.*;
import com.tonmatsu.raypathing.core.*;
import org.joml.*;
import org.lwjgl.opengl.*;

import java.util.concurrent.atomic.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Application {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final String TITLE = "Lighting 3D";

    public void run() {
        if (!glfwInit())
            throw new RuntimeException("could not initialize glfw");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        final var window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("could not create window");

        final var monitor = glfwGetPrimaryMonitor();
        final var videoMode = glfwGetVideoMode(monitor);
        if (videoMode != null) {
            glfwSetWindowPos(window,
                    (videoMode.width() - WIDTH) / 2,
                    (videoMode.height() - HEIGHT) / 2);
        }

        final var viewport = new Vector2i(WIDTH, HEIGHT);
        final var viewportDirty = new AtomicBoolean();

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);

        glfwSetFramebufferSizeCallback(window, (winHnd, width, height) -> {
            viewport.set(width, height);
            viewportDirty.set(true);
        });

        final var scene = new Scene();
        final var thread = new Thread(() -> {
            final var ticker = new Ticker();
            final var tickerProfiler = new TickerProfiler(100);
            final var tickerAlarm = new TickerAlarm(1.0f, id -> {
                if (id == 0) {
                    final var average = tickerProfiler.getAverage();
                    if (average > 0.0f) {
                        final var fps = 1.0f / average;
                        glfwSetWindowTitle(window, String.format("%s  %7.1f FPS", TITLE, fps));
                    }
                }
            });
            glfwMakeContextCurrent(window);
            GL.createCapabilities();
            glfwSwapInterval(0);
            scene.onStart(window);
            while (!glfwWindowShouldClose(window)) {
                ticker.update((float) glfwGetTime());
                tickerProfiler.update(ticker);
                tickerAlarm.update(ticker);
                if (ticker.delta > 0.0f) {
                    if (viewportDirty.getAndSet(false))
                        scene.onViewportResized(viewport);
                    scene.onUpdate(ticker);
                    glfwSwapBuffers(window);
                }
            }
            scene.onStop();
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

        glfwShowWindow(window);

        while (!glfwWindowShouldClose(window))
            glfwWaitEvents();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
