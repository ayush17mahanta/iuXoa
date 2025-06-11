package com.iuxoa.paradoxone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.iuxoa.paradoxone.GameModel;
import com.iuxoa.paradoxone.GameViewModel;

public class GameActivity extends AppCompatActivity implements GameViewModel.GameEventListener {

    private GameView gameView;

    private TextView scoreText, levelText, runCountText;
    private GameViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize GameView from layout (if exists)
        gameView = findViewById(R.id.game_view);

        scoreText = findViewById(R.id.score_text);
        levelText = findViewById(R.id.level_text);
        runCountText = findViewById(R.id.run_count_text);

        // Initialize ViewModel & set listener
        viewModel = new GameViewModel(new GameModel(this));
        viewModel.setGameEventListener(this);

        updateUI();

        Button leaderboardButton = findViewById(R.id.btn_leaderboard);
        leaderboardButton.setOnClickListener(v -> {
            startActivity(new Intent(GameActivity.this, LeaderboardActivity.class));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) {
            gameView.resume();
        }
    }

    private void updateUI() {
        scoreText.setText("Score: " + viewModel.getScore());
        levelText.setText("Level: " + viewModel.getCurrentLevel());
        runCountText.setText("Runs: " + viewModel.getRunCount());
    }

    // Call this when player gains points
    private void onPlayerScored(int points) {
        viewModel.addScore(points);
    }

    // Call this when player dies
    private void onPlayerDied() {
        viewModel.playerDied();
    }

    @Override
    public void onScoreUpdated(int newScore) {
        runOnUiThread(() -> scoreText.setText("Score: " + newScore));
    }

    @Override
    public void onLevelChanged(int newLevel) {
        runOnUiThread(() -> levelText.setText("Level: " + newLevel));
    }

    @Override
    public void onPlayerDeath() {
        runOnUiThread(() -> {
            // Show game over dialog or UI update here
        });
    }

    @Override
    public void onRunCountChanged(int newRunCount) {
        runOnUiThread(() -> runCountText.setText("Runs: " + newRunCount));
    }
}
