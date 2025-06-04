package com.iuxoa.paradoxone;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Obstacle {

    public float x, y;
    public float width, height;

    private Paint paint;

    private String type;  // "Time", "Fear", "Memory"

    private float speedY;

    public Obstacle(float x, float y, float width, float height, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        paint = new Paint();
        setStyleByType();

        speedY = 12;  // Default speed
    }

    private void setStyleByType() {
        switch (type) {
            case "Time":
                paint.setColor(Color.parseColor("#FFD700")); // Gold/yellow
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
    // In Obstacle.java
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }


    public void update() {
        // Movement differs by type for some variation
        switch (type) {
            case "Time":
                y += speedY; // steady fall
                break;
            case "Fear":
                y += speedY * 1.2f; // faster fall
                // Optional: jitter side to side
                x += (Math.random() - 0.5) * 8;
                break;
            case "Memory":
                y += speedY * 0.8f; // slower fall
                // Optional: float side to side softly
                x += (float) Math.sin(y / 30) * 4;
                break;
            default:
                y += speedY;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight;
    }
}
