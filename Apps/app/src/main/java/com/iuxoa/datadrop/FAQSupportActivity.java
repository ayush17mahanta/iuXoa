package com.iuxoa.datadrop;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;


public class FAQSupportActivity extends AppCompatActivity {

    ExpandableListView faqListView;
    EditText editEmail, editMessage;
    Button btnSend;

    FirebaseFirestore db;

    List<String> faqQuestions;
    HashMap<String, List<String>> faqAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faqsupport);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize FAQ RecyclerView
        faqListView = findViewById(R.id.faqListView);
        editEmail = findViewById(R.id.editEmail);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        db = FirebaseFirestore.getInstance();

        loadFAQs();

        btnSend.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            String message = editMessage.getText().toString().trim();

            if (email.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> supportRequest = new HashMap<>();
            supportRequest.put("email", email);
            supportRequest.put("message", message);
            supportRequest.put("timestamp", System.currentTimeMillis());

            db.collection("support_requests")
                    .add(supportRequest)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
                        editEmail.setText("");
                        editMessage.setText("");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to send. Try again.", Toast.LENGTH_SHORT).show()
                    );
        });

        ProgressBar progressBar = findViewById(R.id.progressBar);

        btnSend.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            String message = editMessage.getText().toString().trim();

            if (email.isEmpty() || message.isEmpty()) {
                Snackbar.make(findViewById(R.id.coordinatorLayout), "Please fill all fields", Snackbar.LENGTH_LONG).show();
                return;
            }

            // Show loading spinner
            progressBar.setVisibility(View.VISIBLE);
            btnSend.setEnabled(false);

            HashMap<String, Object> supportRequest = new HashMap<>();
            supportRequest.put("email", email);
            supportRequest.put("message", message);
            supportRequest.put("timestamp", System.currentTimeMillis());

            db.collection("support_requests")
                    .add(supportRequest)
                    .addOnSuccessListener(documentReference -> {
                        progressBar.setVisibility(View.GONE);
                        btnSend.setEnabled(true);

                        editEmail.setText("");
                        editMessage.setText("");

                        Snackbar.make(findViewById(R.id.coordinatorLayout), "Message sent!", Snackbar.LENGTH_SHORT).show();

                        LottieAnimationView successAnim = findViewById(R.id.successAnim);
                        successAnim.setVisibility(View.VISIBLE);
                        successAnim.playAnimation();

                        successAnim.postDelayed(() -> successAnim.setVisibility(View.GONE), 2000);
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        btnSend.setEnabled(true);

                        Snackbar.make(findViewById(R.id.coordinatorLayout), "Failed to send. Retry?", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", v -> btnSend.performClick())
                                .show();
                    });
        });
    }

    private void loadFAQs() {
        faqQuestions = new ArrayList<>();
        faqAnswers = new HashMap<>();

        faqQuestions.add("What is DataDrop?");
        faqQuestions.add("How is my data used?");
        faqQuestions.add("How do I get paid?");
        faqQuestions.add("Can I delete my account?");

        List<String> ans1 = new ArrayList<>();
        ans1.add("DataDrop allows you to sell your anonymized data ethically to companies.");

        List<String> ans2 = new ArrayList<>();
        ans2.add("Your data is anonymized and used for research and analysis purposes.");

        List<String> ans3 = new ArrayList<>();
        ans3.add("You earn credits every time your data is accessed. These can be withdrawn as cash or rewards.");

        List<String> ans4 = new ArrayList<>();
        ans4.add("Yes, go to Profile > Delete Account. Your data will be wiped permanently.");

        faqAnswers.put(faqQuestions.get(0), ans1);
        faqAnswers.put(faqQuestions.get(1), ans2);
        faqAnswers.put(faqQuestions.get(2), ans3);
        faqAnswers.put(faqQuestions.get(3), ans4);

        FaqAdapter adapter = new FaqAdapter(this, faqQuestions, faqAnswers);
        faqListView.setAdapter(adapter);
    }
}
