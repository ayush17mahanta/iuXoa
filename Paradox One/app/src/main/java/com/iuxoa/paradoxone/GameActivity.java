package com.iuxoa.paradoxone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        // Initialize GameView from layout or add it dynamically
        gameView = findViewById(R.id.game_view);  // if included in XML with that ID

        Button leaderboardButton = findViewById(R.id.btn_leaderboard);
        leaderboardButton.setOnClickListener(v -> {
            startActivity(new Intent(GameActivity.this, LeaderboardActivity.class));
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
