package com.iuxoa.paradoxone;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Obstacle {
    private float swayPhase = (float)(Math.random() * 2 * Math.PI);
    private float swayAmplitude = 10f;
    private float speedY = 12;
    private boolean active = false;
    private float screenWidth;
    private float screenHeight;

    public float x, y;
    public float width, height;
    private Paint paint;
    private String type; // "Time", "Fear", "Memory"

    public Obstacle(float x, float y, float width, float height,
                    String type, float screenWidth, float screenHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.paint = new Paint();
        setStyleByType();
    }

    // Add a simplified constructor for the pool initialization
    public Obstacle(float x, float y, float width, float height, String type) {
        this(x, y, width, height, type, 0, 0); // screen dimensions will be set when spawned
    }

    public void setDimensions(float width, float height) {
        this.width = width;
        this.height = height;
    }

    private void setStyleByType() {
        switch (type) {
            case "Time":
                paint.setColor(Color.parseColor("#FFD700")); // Gold
                break;
            case "Fear":
                paint.setColor(Color.parseColor("#FF4500")); // OrangeRed
                break;
            case "Memory":
                paint.setColor(Color.parseColor("#00CED1")); // DarkTurquoise
                break;
            default:
                paint.setColor(Color.WHITE);
                break;
        }
    }

    public void update(float gameSpeed) {
        if (!active) return;

        // Base movement
        y += gameSpeed;

        switch (type) {
            case "Time":
                y += gameSpeed * 0.8f - gameSpeed;
                break;
            case "Fear":
                y += gameSpeed * 1.2f - gameSpeed;
                x += (Math.random() - 0.5f) * 8f;
                // Clamp to screen bounds
                x = Math.max(0, Math.min(x, screenWidth - width));
                break;
            case "Memory":
                y += gameSpeed * 0.6f - gameSpeed;
                x += (float) Math.sin(y / 30.0f + swayPhase) * swayAmplitude;
                // Clamp to screen bounds
                x = Math.max(0, Math.min(x, screenWidth - width));
                break;
        }
    }

    public void draw(Canvas canvas) {
        if (active) {
            canvas.drawRect(x, y, x + width, y + height, paint);
        }
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.active = true;
    }

    public boolean isOffScreen(float screenHeight) {
        return y > screenHeight;
    }

    public void setType(String type) {
        this.type = type;
        setStyleByType();
    }

    public void setScreenDimensions(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public int getColor() {
        return paint.getColor();
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public String getType() { return type; }
    public boolean isActive() { return active; }
}