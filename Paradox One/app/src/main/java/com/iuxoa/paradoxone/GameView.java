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
import java.util.Iterator;
import java.util.Map;

public class GameView extends SurfaceView implements Runnable {

    private Paint comboPaint;
    private int comboDisplayX = 50;
    private int comboDisplayY = 100;

    private int comboCount = 0;
    private int baseScorePerObstacle = 1;
    private int failCount = 0;
    private int currentLevelNumber = 1;
    private ProgressionMap progressionMap;
    private enum Side { LEFT, RIGHT }
    private Side lastObstacleSide = Side.RIGHT;
    private boolean showProgressionMap = false;
    private boolean collisionDetected = false;
    // Add these to your GameView class variables
    private boolean isTouchingScreen = false;
    private long lastPressTime = 0;
    private int rapidPressCount = 0;
    private float targetDotX;
    private PhilosophyEngine engine;
    private String currentTheme = "Time";

    private boolean isPaused = false;
    private boolean askedPhilosophy = false;
    private String philosophicalChoice = "";
    private int currentColor;
    private int targetColor;
    private float morphProgress = 0f;
    private boolean morphing = false;

    // Philosophical upgrades
    private boolean embraceChaos = false;
    private boolean resistMemory = false;
    private boolean surrender = false;
    private ParticleManager particleManager = new ParticleManager();

    private Thread thread;
    private boolean isPlaying = false;
    private SurfaceHolder holder;
    private Paint paint;
    private int screenX, screenY;
    private float fearEffectAmplitude = 10f;
    private float fearEffectFrequency = 0.1f;
    private float fearEffectTime = 0f;
    private boolean fearEffectActive = false;

    // Player dot
    private float dotX, dotY, dotRadius = 30;
    private float speed = 10;
    private float normalSpeed = 10;
    private float slowSpeed = 3;
    private boolean onLeftWall = true;
    private ProfileManager profileManager;
    private long gameStartTime;
    private ObstacleManager obstacleManager;
    private int score = 0;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        holder = getHolder();
        paint = new Paint();
        engine = new PhilosophyEngine(getContext());

        screenX = getResources().getDisplayMetrics().widthPixels;
        screenY = getResources().getDisplayMetrics().heightPixels;
        comboPaint = new Paint();
        comboPaint.setColor(Color.YELLOW);
        comboPaint.setTextSize(60);
        comboPaint.setTypeface(Typeface.DEFAULT_BOLD);
        comboPaint.setShadowLayer(8, 2, 2, Color.BLACK);
        // In init() method:
        obstacleManager = new ObstacleManager(screenX, screenY);  // Changed from screenWidth/screenHeight
        dotX = screenX * 0.25f;
        dotY = screenY / 2f;
        targetDotX = dotX;
        profileManager = new ProfileManager(getContext());
        progressionMap = new ProgressionMap(getContext());

        progressionMap.onFail();
    }

    @Override
    public void run() {
        while (isPlaying) {
            if (!isPaused) {
                update();
            }
            draw();
            sleep();
        }
    }

    public void update() {
        // Physics constants
        final float gravity = 0.5f;
        final float maxSpeed = 25f;
        final float baseBounceCoefficient = 1.0f;
        final float rapidPressBoost = 0.3f; // Extra bounce when pressing rapidly
        final float rapidPressThreshold = 0.2f; // Time window for rapid presses (in seconds)

        // Track rapid presses
        long currentTime = System.currentTimeMillis();
        if (isTouchingScreen) {
            if (currentTime - lastPressTime < rapidPressThreshold * 1000) {
                rapidPressCount++;
            } else {
                rapidPressCount = 1;
            }
            lastPressTime = currentTime;
            isTouchingScreen = false; // Reset for next frame
        }

        // Apply gravity
        speed += gravity;

        // Cap maximum speed
        speed = Math.min(speed, maxSpeed);

        // Update position
        dotY += speed;

        // Calculate dynamic bounce based on rapid presses
        float currentBounceCoefficient = baseBounceCoefficient;
        if (rapidPressCount >= 3) { // If 3+ rapid presses
            currentBounceCoefficient += rapidPressBoost;
            rapidPressCount = 0; // Reset after applying boost
        }

        // Bounce physics
        if (dotY > screenY - dotRadius) {
            dotY = screenY - dotRadius;
            speed = -Math.abs(speed) * currentBounceCoefficient;

            // Visual feedback for boosted bounce
            if (currentBounceCoefficient > baseBounceCoefficient) {
                particleManager.spawnParticles(dotX, dotY, Color.YELLOW);
            }
        }
        else if (dotY < dotRadius) {
            dotY = dotRadius;
            speed = Math.abs(speed) * currentBounceCoefficient;
        }

        // Philosophical effects
        if (embraceChaos) {
            targetDotX += (Math.random() - 0.5) * 5;
            targetDotX = Math.max(dotRadius, Math.min(targetDotX, screenX - dotRadius));
            dotX = lerp(dotX, targetDotX, 0.1f);
        }

        if (resistMemory && speed < maxSpeed) {
            speed += 0.01f;
        }

        // Theme effects
        if ("Fear".equals(currentTheme)) {
            fearEffectActive = true;
            fearEffectTime += 0.1f;
        } else {
            fearEffectActive = false;
            fearEffectTime = 0;
        }

        // Update particles and color morphing
        particleManager.update();
        if (morphing) {
            morphProgress += 0.02f;
            if (morphProgress >= 1f) {
                morphProgress = 1f;
                morphing = false;
                currentColor = targetColor;
            } else {
                currentColor = interpolateColor(currentColor, targetColor, morphProgress);
            }
        }

        // Obstacle management
        obstacleManager.update(speed);

        // Scoring and obstacle collision
        for (Obstacle o : obstacleManager.getObstacles()) {
            if (o.isOffScreen(screenY)) {
                comboCount++;
                score += comboCount * baseScorePerObstacle;
                continue;
            }

            boolean collisionX = dotX + dotRadius > o.getX() && dotX - dotRadius < o.getX() + o.getWidth();
            boolean collisionY = dotY + dotRadius > o.getY() && dotY - dotRadius < o.getY() + o.getHeight();

            if (collisionX && collisionY) {
                particleManager.spawnParticles(
                        dotX + dotRadius/2,
                        dotY + dotRadius/2,
                        o.getColor()
                );

                comboCount = 0;
                isPaused = true;
                isPlaying = false;
                profileManager.saveScore(score);
                profileManager.incrementRunCount();
                showGameOverDialog();
                break;
            }
        }

        // Progression
        if (score > currentLevelNumber * 10) {
            currentLevelNumber++;
            progressionMap.moveToLevel(currentLevelNumber);
            if (currentLevelNumber >= 3) {
                showProgressionMap = true;
                isPaused = true;
                showProgressionMapDialog();
            }
        }

        if (collisionDetected) {
            progressionMap.onFail();
            if (showProgressionMap) {
                isPaused = true;
                showProgressionMapDialog();
            }
        }

        Iterator<Map.Entry<Integer, Level>> iterator = progressionMap.getLevels().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Level> entry = iterator.next();
            if (entry.getValue().isCollapsed()) {
                iterator.remove();
            }
        }

        // Philosophy and meta prompts
        long elapsedSeconds = (System.currentTimeMillis() - gameStartTime) / 1000;
        if (elapsedSeconds > 10 && !askedPhilosophy) {
            PhilosophyEngine.Quote quote = engine.getRandomQuoteByTag(currentTheme);
            if (quote != null) {
                isPaused = true;
                showPhilosophyPrompt(quote);
                askedPhilosophy = true;
            }
        }

        if (profileManager.getRunCount() > 10 && score > 20) {
            isPaused = true;
            isPlaying = false;
            showMetaPlaybackDialog();
        }
    }

    private int interpolateColor(int color1, int color2, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio));

        int a1 = Color.alpha(color1);
        int r1 = Color.red(color1);
        int g1 = Color.green(color1);
        int b1 = Color.blue(color1);

        int a2 = Color.alpha(color2);
        int r2 = Color.red(color2);
        int g2 = Color.green(color2);
        int b2 = Color.blue(color2);

        int a = (int)(a1 + (a2 - a1) * ratio);
        int r = (int)(r1 + (r2 - r1) * ratio);
        int g = (int)(g1 + (g2 - g1) * ratio);
        int b = (int)(b1 + (b2 - b1) * ratio);

        return Color.argb(a, r, g, b);
    }

    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    private void draw() {
        if (!holder.getSurface().isValid()) return;

        Canvas canvas = holder.lockCanvas();
        try {
            canvas.drawColor(Color.BLACK);

            if (fearEffectActive) {
                int saveCount = canvas.save();
                for (int y = 0; y < screenY; y += 10) {
                    float dx = (float) (fearEffectAmplitude * Math.sin(y * fearEffectFrequency + fearEffectTime));
                    canvas.save();
                    canvas.clipRect(0, y, screenX, y + 10);
                    canvas.translate(dx, 0);
                    drawGameElements(canvas);
                    canvas.restore();
                }
                canvas.restoreToCount(saveCount);
            } else {
                drawGameElements(canvas);
            }

            particleManager.draw(canvas);

            paint.setColor(Color.WHITE);
            paint.setTextSize(60);
            canvas.drawText("Score: " + score, 50, 100, paint);

            paint.setTextSize(50);
            paint.setColor(Color.YELLOW);
            canvas.drawText("Best: " + profileManager.getHighScore(), 50, 160, paint);

            if (comboCount > 1) {
                canvas.drawText("Combo: " + comboCount + "x", comboDisplayX, comboDisplayY, comboPaint);
            }

        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawGameElements(Canvas canvas) {
        // Draw player
        paint.setColor(Color.CYAN);
        canvas.drawCircle(dotX, dotY, dotRadius, paint);

        // Draw obstacles
        obstacleManager.draw(canvas);

        // Debug info (optional)
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        canvas.drawText("Active Obstacles: " + obstacleManager.getObstacles().size(), 50, 200, paint);
    }


    private void sleep() {
        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        isPlaying = false;
        isPaused = true;
        try {
            if (thread != null) thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPaused = false;
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
        gameStartTime = System.currentTimeMillis();
        askedPhilosophy = false;
        score = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isTouchingScreen = true;
            onLeftWall = !onLeftWall;
            dotX = onLeftWall ? screenX * 0.25f : screenX * 0.75f;
        }
        return true;
    }

    private void showPhilosophyPrompt(PhilosophyEngine.Quote quote) {
        ((GameActivity) getContext()).runOnUiThread(() -> {
            SpannableString title = new SpannableString(quote.getAuthor());
            title.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, title.length(), 0);
            title.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, title.length(), 0);

            SpannableString message = new SpannableString(quote.getText() + "\n\nWhat is the opposite of control?");
            message.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message.length(), 0);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Embrace Chaos", (dialog, which) -> {
                        philosophicalChoice = "Embrace Chaos";
                        embraceChaos = true;
                        resistMemory = false;
                        surrender = false;
                        isPaused = false;
                        dialog.dismiss();
                    })
                    .setNeutralButton("Resist Memory", (dialog, which) -> {
                        philosophicalChoice = "Resist Memory";
                        resistMemory = true;
                        embraceChaos = false;
                        surrender = false;
                        speed += 5;
                        isPaused = false;
                        dialog.dismiss();
                    })
                    .setNegativeButton("Surrender", (dialog, which) -> {
                        philosophicalChoice = "Surrender";
                        surrender = true;
                        embraceChaos = false;
                        resistMemory = false;
                        if (Math.random() < 0.5) {
                            embraceChaos = true;
                        } else {
                            resistMemory = true;
                            speed += 5;
                        }
                        isPaused = false;
                        dialog.dismiss();
                    })
                    .setCancelable(false);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(d -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.MAGENTA);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.CYAN);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.YELLOW);
            });
            dialog.show();
        });
    }

    private void showGameOverDialog() {
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

    private void showMetaPlaybackDialog() {
        ((GameActivity) getContext()).runOnUiThread(() -> {
            SpannableString title = new SpannableString("Realization");
            title.setSpan(new ForegroundColorSpan(Color.CYAN), 0, title.length(), 0);
            title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), 0);

            SpannableString message = new SpannableString(
                    "There is no final boss.\nThere is no final level.\n\n" +
                            "🎭 This isn't a game you beat. It's a mirror.\n\n" +
                            "It replays how you responded.\nIt questions your fear.\nIt remembers your choices.\n\n" +
                            "\"Why do you keep playing when you know you can't win?\"\n\n" +
                            "You don't finish this game.\nYou finish yourself."
            );
            message.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, message.length(), 0);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Continue", (dialog, which) -> {
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
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.CYAN);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
            });
            dialog.show();
        });
    }

    private void showProgressionMapDialog() {
        ((GameActivity) getContext()).runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Level Progress")
                    .setMessage("You've reached level " + currentLevelNumber + "!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        isPaused = false;
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    private void resetGame() {
        dotY = screenY / 2f;
        dotX = screenX * 0.25f;
        targetDotX = dotX;
        onLeftWall = true;
        speed = normalSpeed;

        // Clear obstacles
        obstacleManager.clearObstacles();

        // Reset game state
        gameStartTime = System.currentTimeMillis();
        askedPhilosophy = false;
        score = 0;
        comboCount = 0;
        isPaused = false;
        embraceChaos = false;
        resistMemory = false;
        surrender = false;

        // Reset visual effects
        morphing = false;
        fearEffectActive = false;
    }

}