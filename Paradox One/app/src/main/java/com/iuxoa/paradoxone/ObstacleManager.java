package com.iuxoa.paradoxone;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ObstacleManager {

    private ArrayList<Obstacle> obstacles;
    private int screenWidth, screenHeight;
    private int spawnTimer = 0;
    private int spawnInterval = 60; // frames between spawn
    private Random random;

    private String currentTheme = "Time"; // Default theme

    public ObstacleManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        obstacles = new ArrayList<>();
        random = new Random();
    }

    public void setTheme(String theme) {
        currentTheme = theme;
        obstacles.clear();
    }

    public void update() {
        spawnTimer++;

        if (spawnTimer > spawnInterval) {
            spawnTimer = 0;
            spawnObstacle();
        }

        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();
            obstacle.update();

            if (obstacle.isOffScreen(screenHeight)) {
                iterator.remove();
            }
        }
    }

    private void spawnObstacle() {
        float width = 60;
        float height = 150;
        float x;

        // Spawn on left or right side randomly
        boolean leftSide = random.nextBoolean();
        x = leftSide ? screenWidth * 0.25f - width / 2 : screenWidth * 0.75f - width / 2;

        // Choose obstacle type based on current theme
        String type = currentTheme;

        obstacles.add(new Obstacle(x, -height, width, height, type));
    }

    public void draw(Canvas canvas) {
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(canvas);
        }
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
}
