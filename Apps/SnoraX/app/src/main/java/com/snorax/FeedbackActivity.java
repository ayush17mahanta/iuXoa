package com.snorax;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private EditText etFeedback;
    private Button btnSubmitFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize Views
        etFeedback = findViewById(R.id.etFeedback);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);

        // Set up the Submit Feedback button
        btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedback = etFeedback.getText().toString().trim();
        if (feedback.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send feedback via email (example)
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:feedback@snorax.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SnoraX Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, feedback);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send feedback via email"));
        } catch (Exception e) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }
}