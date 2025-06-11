package com.iuxoa.paradoxone;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player {

    private float x, y;           // Current position (center of circle)
    private final float radius;   // Radius of player circle

    private float velocityX = 0f; // Velocity for smoothing
    private float velocityY = 0f;

    private final float maxSpeed = 15f;
    private final float acceleration = 1.0f;
    private final float friction = 0.85f;

    private final int screenWidth;
    private final int screenHeight;

    private final Paint paint;

    /**
     * Constructor
     * @param startX Initial X position
     * @param startY Initial Y position
     * @param radius Player circle radius
     * @param screenWidth Screen width for boundary checks
     * @param screenHeight Screen height for boundary checks
     */
    public Player(float startX, float startY, float radius, int screenWidth, int screenHeight) {
        this.x = startX;
        this.y = startY;
        this.radius = radius;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setAntiAlias(true);
    }

    /**
     * Update player position based on input with smoothing and boundary clamping.
     * @param inputX Horizontal input direction (-1 to 1)
     * @param inputY Vertical input direction (-1 to 1)
     */
    public void update(float inputX, float inputY) {
        // Apply acceleration to velocity
        velocityX += inputX * acceleration;
        velocityY += inputY * acceleration;

        // Clamp velocity to maxSpeed
        velocityX = clamp(velocityX, -maxSpeed, maxSpeed);
        velocityY = clamp(velocityY, -maxSpeed, maxSpeed);

        // Apply friction to slow down when no input
        velocityX *= friction;
        velocityY *= friction;

        // Update position
        x += velocityX;
        y += velocityY;

        // Clamp position within screen bounds considering radius
        x = clamp(x, radius, screenWidth - radius);
        y = clamp(y, radius, screenHeight - radius);
    }

    /**
     * Draw the player circle on the provided canvas.
     * @param canvas Canvas to draw on
     */
    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    /**
     * Check collision between the circular player and a rectangular obstacle.
     * Uses AABB circle-rectangle collision detection.
     * @param rectX Rectangle top-left X
     * @param rectY Rectangle top-left Y
     * @param rectWidth Rectangle width
     * @param rectHeight Rectangle height
     * @return True if collision detected
     */
    public boolean checkCollision(float rectX, float rectY, float rectWidth, float rectHeight) {
        // Find closest point on rectangle to circle center
        float closestX = clamp(x, rectX, rectX + rectWidth);
        float closestY = clamp(y, rectY, rectY + rectHeight);

        // Calculate distance from circle center to closest point
        float distanceX = x - closestX;
        float distanceY = y - closestY;

        // Check if distance is less than radius
        float distanceSquared = distanceX * distanceX + distanceY * distanceY;
        return distanceSquared < radius * radius;
    }

    /**
     * Clamp a value between min and max.
     */
    private float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    // Getters for position and radius if needed externally
    public float getX() { return x; }
    public float getY() { return y; }
    public float getRadius() { return radius; }
}
