package com.iuxoa.paradoxone;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoopThread extends Thread {
    private boolean running;
    private SurfaceHolder surfaceHolder;
    private GameView gameView;

    public GameLoopThread(GameView view) {
        this.gameView = view;
        this.surfaceHolder = view.getHolder();
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        while (running) {
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
        }
    }
}
