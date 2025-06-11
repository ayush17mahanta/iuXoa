// GameViewModel.java
package com.iuxoa.paradoxone;

import com.iuxoa.paradoxone.GameModel;

public class GameViewModel {

    public interface GameEventListener {
        void onScoreUpdated(int newScore);
        void onLevelChanged(int newLevel);
        void onPlayerDeath();
        void onRunCountChanged(int newRunCount);
    }

    private GameModel model;
    private GameEventListener listener;

    public GameViewModel(GameModel model) {
        this.model = model;
    }

    public void setGameEventListener(GameEventListener listener) {
        this.listener = listener;
    }

    public void addScore(int points) {
        int newScore = model.getScore() + points;
        model.setScore(newScore);
        model.save();
        if (listener != null) {
            listener.onScoreUpdated(newScore);
        }

        // Level progression example
        int newLevel = model.getCurrentLevel();
        if (newScore > newLevel * 10) {
            newLevel++;
            model.setCurrentLevel(newLevel);
            model.save();
            if (listener != null) {
                listener.onLevelChanged(newLevel);
            }
        }
    }

    public void playerDied() {
        model.incrementRunCount();
        model.save();
        if (listener != null) {
            listener.onPlayerDeath();
            listener.onRunCountChanged(model.getRunCount());
        }
    }

    public int getScore() {
        return model.getScore();
    }

    public int getCurrentLevel() {
        return model.getCurrentLevel();
    }

    public int getRunCount() {
        return model.getRunCount();
    }

    public void resetGame() {
        model.reset();
        if (listener != null) {
            listener.onScoreUpdated(0);
            listener.onLevelChanged(1);
        }
    }
}
