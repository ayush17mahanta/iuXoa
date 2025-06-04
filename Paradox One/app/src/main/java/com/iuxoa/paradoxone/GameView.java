package com.iuxoa.paradoxone;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.DialogInterface;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable {

    private MemoryManager memoryManager;
    private Thread thread;
    private boolean isPlaying;
    private SurfaceHolder holder;
    private Paint paint;
    private int screenX, screenY;
    private float dotX, dotY, dotRadius = 30;
    private float speed = 10;
    private boolean onLeftWall = true;

    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private int obstacleTimer = 0;

    private long gameStartTime;
    private boolean askedPhilosophy = false;

    private int score = 0; // Score variable

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        holder = getHolder();
        paint = new Paint();
        screenX = getResources().getDisplayMetrics().widthPixels;
        screenY = getResources().getDisplayMetrics().heightPixels;

        dotX = screenX * 0.25f;
        dotY = screenY / 2f;

        memoryManager = new MemoryManager(getContext());
    }



    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }



    public void update() {
        // Move dot up and down
        dotY += speed;
        if (dotY > screenY - dotRadius || dotY < dotRadius) {
            speed = -speed;
        }

        // Obstacle generation
        obstacleTimer++;
        if (obstacleTimer > 60) {
            float obsWidth = 60;
            float obsHeight = 150;
            float x = onLeftWall ? screenX * 0.25f - obsWidth / 2 : screenX * 0.75f - obsWidth / 2;
            // Example: cycle through themes or choose randomly
            String[] themes = {"Time", "Fear", "Memory"};
            String chosenType = themes[(int)(Math.random() * themes.length)];

            obstacles.add(new Obstacle(x, -obsHeight, obsWidth, obsHeight, chosenType));

            obstacleTimer = 0;
        }

        // Update obstacles and check collisions
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            Obstacle o = obstacles.get(i);
            o.update();
            if (o.isOffScreen(screenY)) {
                obstacles.remove(i);
                score++;  // Increase score for each obstacle passed
                continue;
            }

            // Collision detection
            if (dotX > o.x && dotX < o.x + o.width &&
                    dotY + dotRadius > o.y && dotY - dotRadius < o.y + o.height) {
                isPlaying = false;
                showGameOverDialog();
                break;
            }
        }

        // Trigger philosophy popup after 10 seconds
        long elapsed = (System.currentTimeMillis() - gameStartTime) / 1000;
        if (elapsed > 10 && !askedPhilosophy) {
            showPhilosophyPrompt();
            askedPhilosophy = true;
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            // Draw the player dot
            paint.setColor(Color.CYAN);
            canvas.drawCircle(dotX, dotY, dotRadius, paint);

            // Draw obstacles
            for (Obstacle o : obstacles) {
                o.draw(canvas);
            }

            // Draw score
            paint.setColor(Color.WHITE);
            paint.setTextSize(60);
            canvas.drawText("Score: " + score, 50, 100, paint);

            // Draw best score
            paint.setColor(Color.YELLOW);
            paint.setTextSize(50);
            canvas.drawText("Best: " + memoryManager.getBestScore(), 50, 160, paint);

            holder.unlockCanvasAndPost(canvas);
        }
    }


    private void sleep() {
        try {
            Thread.sleep(16); // ~60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            if (thread != null)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
        gameStartTime = System.currentTimeMillis();
        askedPhilosophy = false;
        score = 0; // reset score on resume
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onLeftWall = !onLeftWall;
            dotX = onLeftWall ? screenX * 0.25f : screenX * 0.75f;
        }
        return true;
    }

    private void showPhilosophyPrompt() {
        ((GameActivity) getContext()).runOnUiThread(() -> {
            SpannableString title = new SpannableString("A Thought Appears");
            title.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, title.length(), 0);
            title.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, title.length(), 0);

            SpannableString message = new SpannableString("What is the opposite of control?");
            message.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message.length(), 0);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Chaos", (dialog, which) -> {
                        speed += 3;
                        dialog.dismiss();
                    })
                    .setNegativeButton("Silence", (dialog, which) -> {
                        dotRadius *= 0.8f;
                        dialog.dismiss();
                    })
                    .setCancelable(false);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(d -> {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.MAGENTA);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.CYAN);
            });
            dialog.show();
        });
    }

    private void showGameOverDialog() {
        // Save score before showing dialog
        memoryManager.saveScore(score);

        ((GameActivity) getContext()).runOnUiThread(() -> {
            SpannableString title = new SpannableString("Game Over");
            title.setSpan(new ForegroundColorSpan(Color.RED), 0, title.length(), 0);
            title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), 0);

            SpannableString message = new SpannableString("You collided with a concept.\nScore: " + score + "\nTry again?");
            message.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message.length(), 0);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Restart", (dialog, which) -> {
                        resetGame();
                        resume();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Exit", (dialog, which) -> {
                        ((GameActivity) getContext()).finish();
                    })
                    .setCancelable(false);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(d -> {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
            });
            dialog.show();
        });
    }

    private void resetGame() {
        dotY = screenY / 2f;
        dotX = screenX * 0.25f;
        onLeftWall = true;
        speed = 10;
        obstacles.clear();
        gameStartTime = System.currentTimeMillis();
        askedPhilosophy = false;
        score = 0;
    }

}