package com.iuxoa.paradoxone;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

public class Player {

    private float x, y;
    private float radius;
    private Paint paint;

    public Player(float startX, float startY, float radius) {
        this.x = startX;
        this.y = startY;
        this.radius = radius;

        paint = new Paint();
        paint.setColor(Color.CYAN);
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean collidesWith(Obstacle obstacle) {
        // Simple AABB collision using circle and rectangle
        float closestX = clamp(x, obstacle.getX(), obstacle.getX() + obstacle.getWidth());
        float closestY = clamp(y, obstacle.getY(), obstacle.getY() + obstacle.getHeight());

        float dx = x - closestX;
        float dy = y - closestY;

        return (dx * dx + dy * dy) < (radius * radius);
    }

    private float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
