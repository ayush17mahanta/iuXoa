package com.iuxoa.paradoxone;

import android.graphics.Canvas;


import java.util.ArrayList;
import java.util.Iterator;

public class ParticleManager {
    private ArrayList<Particle> particles = new ArrayList<>();

    public void update() {
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle p = iter.next();
            p.update();
            if (p.isDead()) {
                iter.remove();
            }
        }
    }

    public void draw(Canvas canvas) {
        for (Particle p : particles) {
            p.draw(canvas);
        }
    }

    public void spawnParticles(float x, float y, int baseColor) {
        int count = 20;
        for (int i = 0; i < count; i++) {
            float velocityX = (float) (Math.random() * 6 - 3);
            float velocityY = (float) (Math.random() * -6 - 2);
            float size = (float) (Math.random() * 5 + 3);

            int color = modifyColor(baseColor, (int) (Math.random() * 50 - 25));
            particles.add(new Particle(x, y, velocityX, velocityY, size, color, 1.0f));
        }
    }

    private int modifyColor(int baseColor, int variation) {
        int r = Math.min(255, Math.max(0, ((baseColor >> 16) & 0xFF) + variation));
        int g = Math.min(255, Math.max(0, ((baseColor >> 8) & 0xFF) + variation));
        int b = Math.min(255, Math.max(0, (baseColor & 0xFF) + variation));
        return 0xFF << 24 | (r << 16) | (g << 8) | b;
    }
}