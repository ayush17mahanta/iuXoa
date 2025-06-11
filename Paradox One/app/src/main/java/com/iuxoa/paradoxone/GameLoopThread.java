package com.iuxoa.paradoxone;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoopThread extends Thread {
    private boolean running;
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private static final long FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;

    public GameLoopThread(GameView view) {
        this.gameView = view;
        this.surfaceHolder = view.getHolder();
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;

        while (running) {
            startTime = System.currentTimeMillis();
            Canvas canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    gameView.update();
                    gameView.draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            // Frame rate control
            timeMillis = System.currentTimeMillis() - startTime;
            waitTime = FRAME_TIME - timeMillis;

            try {
                if (waitTime > 0) {
                    sleep(waitTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}