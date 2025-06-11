package com.iuxoa.paradoxone;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends Activity {

    private ProfileManager profileManager;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        profileManager = new ProfileManager(this);
        listView = findViewById(R.id.leaderboard_list);

        List<Integer> scores = profileManager.getAllScores();
        Collections.sort(scores, Collections.reverseOrder()); // Sort descending (though ProfileManager already does this)

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, scores);

        listView.setAdapter(adapter);
    }
}