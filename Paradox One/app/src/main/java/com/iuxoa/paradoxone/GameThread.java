package com.iuxoa.paradoxone;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

    private static final int MAX_FPS = 60;
    private static final int FRAME_PERIOD = 1000 / MAX_FPS; // in ms

    private boolean running;
    private SurfaceHolder surfaceHolder;
    private GameView gameView;

    private OnThreadStateListener threadStateListener;

    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
        this.running = false;
    }

    public void setOnThreadStateListener(OnThreadStateListener listener) {
        this.threadStateListener = listener;
    }

    public void setRunning(boolean running) {
        this.running = running;
        if (!running && threadStateListener != null) {
            threadStateListener.onThreadStopped();
        }
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long sleepTime;
        long frameCount = 0;
        long totalTime = 0;

        // For catch-up logic
        long nextFrameTime = System.currentTimeMillis();

        while (running) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();

                synchronized (surfaceHolder) {
                    startTime = System.currentTimeMillis();

                    // Update game state
                    gameView.update();

                    // Draw frame
                    if (canvas != null) {
                        gameView.draw(canvas);
                    }

                    // Calculate time to next frame based on fixed frame period
                    nextFrameTime += FRAME_PERIOD;
                    long now = System.currentTimeMillis();
                    waitTime = nextFrameTime - now;

                    if (waitTime > 0) {
                        // Sleep to maintain FPS
                        try {
                            Thread.sleep(waitTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // We're running behind, catch up by skipping frames (no sleep)
                        nextFrameTime = now;
                    }

                    // Frame stats for debugging
                    timeMillis = System.currentTimeMillis() - startTime;
                    totalTime += timeMillis;
                    frameCount++;
                    if (frameCount == MAX_FPS) {
                        double averageFPS = 1000.0 / ((double) totalTime / frameCount);
                        System.out.println(String.format("Average FPS: %.2f", averageFPS));
                        frameCount = 0;
                        totalTime = 0;
                    }
                }

            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        // Notify thread stopped when exiting run loop
        if (threadStateListener != null) {
            threadStateListener.onThreadStopped();
        }
    }

    // Listener interface for thread lifecycle events
    public interface OnThreadStateListener {
        void onThreadStopped();
    }
}
