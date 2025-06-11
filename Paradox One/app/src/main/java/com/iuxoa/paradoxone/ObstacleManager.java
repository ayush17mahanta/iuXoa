package com.iuxoa.paradoxone;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ObstacleManager {
    private final List<Obstacle> pool;
    private final List<Obstacle> activeObstacles;
    private final float screenWidth;
    private final float screenHeight;
    private int spawnTimer = 0;
    private int baseSpawnInterval = 60;
    private float gameSpeed = 12f;
    private Random random;
    private String currentTheme = "Time";
    private int difficultyCounter = 0;

    public ObstacleManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.random = new Random();
        this.pool = new ArrayList<>();
        this.activeObstacles = new ArrayList<>();

        // Preload pool with 20 obstacles using simplified constructor
        for (int i = 0; i < 20; i++) {
            pool.add(new Obstacle(0, 0, 60, 150, "Time"));
        }
    }

    public void setTheme(String theme) {
        currentTheme = theme;
        clearObstacles();
    }

    public void clearObstacles() {
        activeObstacles.clear();
    }

    public void update(float deltaTime) {
        spawnTimer++;
        difficultyCounter++;

        if (spawnTimer >= getDynamicInterval()) {
            spawnTimer = 0;
            spawnObstacle();
        }

        Iterator<Obstacle> iterator = activeObstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();
            obstacle.update(gameSpeed);

            if (obstacle.isOffScreen(screenHeight)) {
                iterator.remove();
                pool.add(obstacle);
            }
        }
    }

    private int getDynamicInterval() {
        return Math.max(20, baseSpawnInterval - difficultyCounter / 500);
    }

    private void spawnObstacle() {
        if (pool.isEmpty()) {
            pool.add(new Obstacle(0, 0, 60, 150, "Time"));
        }

        Obstacle obstacle = pool.remove(0);
        float width = 60f;
        float height = 150f;
        float x = 50f + random.nextFloat() * (screenWidth - 100f);
        float y = -height;
        String type = getRandomTypeForTheme();

        obstacle.setScreenDimensions(screenWidth, screenHeight);
        obstacle.setPosition(x, y);
        obstacle.setType(type);
        obstacle.setDimensions(width, height);

        activeObstacles.add(obstacle);
    }

    private String getRandomTypeForTheme() {
        if ("Time".equals(currentTheme)) return "Time";
        if ("Fear".equals(currentTheme)) return random.nextBoolean() ? "Fear" : "Memory";
        if ("Memory".equals(currentTheme)) return random.nextBoolean() ? "Memory" : "Fear";
        return "Time";
    }

    public void draw(Canvas canvas) {
        for (Obstacle o : activeObstacles) {
            o.draw(canvas);
        }
    }

    public List<Obstacle> getObstacles() {
        return activeObstacles;
    }

    public void setGameSpeed(float speed) {
        this.gameSpeed = speed;
    }
}