package com.iuxoa.datadrop;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class WalletActivity extends AppCompatActivity {

    TextView creditText;
    Button backToDashboard;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wallet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.walletLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        creditText = findViewById(R.id.creditText);
        backToDashboard = findViewById(R.id.backToDashboard);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        double credits = doc.getDouble("credits");
                        creditText.setText("Credits: $" + String.format("%.2f", credits));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load credits", Toast.LENGTH_SHORT).show();
                });

        backToDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });
    }
}