package com.iuxoa.datadrop;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeSurveyActivity extends AppCompatActivity {

    TextView surveyTitle, payoutText;
    LinearLayout questionsLayout;
    Button submitBtn;

    String surveyId;
    String title;
    double payout;
    List<Map<String, Object>> questions;
    Map<Integer, EditText> answerInputs = new HashMap<>();

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_take_survey);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.surveyScroll), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        surveyTitle = findViewById(R.id.surveyTitle);
        payoutText = findViewById(R.id.payoutText);
        questionsLayout = findViewById(R.id.questionsLayout);
        submitBtn = findViewById(R.id.submitSurvey);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        surveyId = getIntent().getStringExtra("surveyId");
        title = getIntent().getStringExtra("title");
        payout = getIntent().getDoubleExtra("payout", 0.0);

        surveyTitle.setText(title);
        payoutText.setText("Reward: $" + String.format("%.2f", payout));

        loadSurveyQuestions();

        submitBtn.setOnClickListener(this::submitSurvey);
    }

    private void loadSurveyQuestions() {
        db.collection("surveys").document(surveyId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                questions = (List<Map<String, Object>>) documentSnapshot.get("questions");

                if (questions != null) {
                    for (int i = 0; i < questions.size(); i++) {
                        Map<String, Object> q = questions.get(i);
                        String questionText = (String) q.get("q");  // Use "q" key per your model

                        TextView qView = new TextView(this);
                        qView.setText((i + 1) + ". " + questionText);
                        qView.setTextSize(16);
                        qView.setPadding(0, 16, 0, 4);

                        EditText aInput = new EditText(this);
                        aInput.setHint("Your answer...");
                        aInput.setBackgroundResource(R.drawable.edittext_background); // optional custom background
                        answerInputs.put(i, aInput);

                        questionsLayout.addView(qView);
                        questionsLayout.addView(aInput);
                    }
                }
            } else {
                Toast.makeText(this, "Survey not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    public void submitSurvey(View view) {
        // Disable button to prevent multiple submissions
        submitBtn.setEnabled(false);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            submitBtn.setEnabled(true);
            return;
        }
        String userId = currentUser.getUid();

        // Validate all answers filled
        for (int i = 0; i < answerInputs.size(); i++) {
            String ans = answerInputs.get(i).getText().toString().trim();
            if (ans.isEmpty()) {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
                submitBtn.setEnabled(true);
                return;
            }
        }

        // Prepare answers map
        Map<String, Object> response = new HashMap<>();
        for (int i = 0; i < answerInputs.size(); i++) {
            String ans = answerInputs.get(i).getText().toString().trim();
            response.put("Q" + (i + 1), ans);
        }

        // Prepare submission data
        Map<String, Object> submission = new HashMap<>();
        submission.put("userId", userId);
        submission.put("surveyId", surveyId);
        submission.put("answers", response);
        submission.put("submitted_at", System.currentTimeMillis());

        // Save submission under survey_responses with doc ID "userId_surveyId"
        String docId = userId + "_" + surveyId;

        db.collection("survey_responses").document(docId).set(submission)
                .addOnSuccessListener(unused -> {
                    // Update user's balance and completed surveys list atomically
                    db.collection("users").document(userId).get()
                            .addOnSuccessListener(userDoc -> {
                                double currentBalance = 0.0;
                                if (userDoc.exists() && userDoc.contains("balance")) {
                                    currentBalance = userDoc.getDouble("balance");
                                }
                                double newBalance = currentBalance + payout;

                                db.collection("users").document(userId)
                                        .update(
                                                "balance", newBalance,
                                                "completed_surveys", FieldValue.arrayUnion(surveyId)
                                        )
                                        .addOnSuccessListener(unused2 -> {
                                            Toast.makeText(this, "Survey submitted! $" + payout + " credited.", Toast.LENGTH_LONG).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to update balance/completed surveys.", Toast.LENGTH_SHORT).show();
                                            submitBtn.setEnabled(true);
                                        });

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                                submitBtn.setEnabled(true);
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Submission failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    submitBtn.setEnabled(true);
                });
    }
}
