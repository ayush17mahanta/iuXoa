package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentActivity extends AppCompatActivity {

    private RecyclerView paymentRecyclerView;
    private Button newPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        paymentRecyclerView = findViewById(R.id.paymentRecyclerView);
        newPaymentButton = findViewById(R.id.newPaymentButton);

        paymentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // You can use a PaymentAdapter to show data here

        newPaymentButton.setOnClickListener(v -> {
            // Navigate to the New Payment Activity
            Intent intent = new Intent(PaymentActivity.this, NewPaymentActivity.class);
            startActivity(intent);
        });
    }
}