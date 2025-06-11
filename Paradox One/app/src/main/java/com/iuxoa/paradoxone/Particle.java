package com.iuxoa.paradoxone;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Iterator;

public class Particle {
    public float x, y;
    public float vx, vy;
    public float size;
    public float life;
    private Paint paint;

    public Particle(float x, float y, float vx, float vy, float size, int color, float life) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
        this.life = life;

        paint = new Paint();
        paint.setColor(color);
    }

    public void update() {
        x += vx;
        y += vy;
        life -= 0.03f; // decrease life
    }

    public void draw(Canvas canvas) {
        if (life > 0) {
            paint.setAlpha((int)(255 * life));
            canvas.drawCircle(x, y, size, paint);
        }
    }

    public boolean isDead() {
        return life <= 0;
    }
}