package com.iuxoa.datadrop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SurveyListActivity extends AppCompatActivity implements SurveyAdapter.OnSurveyClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SurveyAdapter adapter;
    private List<SurveyModel> surveyList = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_survey_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();

        adapter = new SurveyAdapter(surveyList, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadSurveys();
    }

    private void loadSurveys() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("surveys").get().addOnSuccessListener(query -> {
            surveyList.clear();
            for (com.google.firebase.firestore.QueryDocumentSnapshot doc : query) {
                String id = doc.getId();
                String title = doc.getString("title");
                Double payout = doc.getDouble("payout");
                List<Map<String, Object>> questions = (List<Map<String, Object>>) doc.get("questions");

                if (title != null && payout != null && questions != null) {
                    surveyList.add(new SurveyModel(id, title, payout, questions));
                }
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load surveys", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onSurveyClick(SurveyModel model) {
        Intent intent = new Intent(this, TakeSurveyActivity.class);
        intent.putExtra("surveyId", model.getId());
        intent.putExtra("title", model.getTitle());
        intent.putExtra("payout", model.getPayout());
        // Pass questions or fetch again in TakeSurveyActivity
        startActivity(intent);
    }
}
