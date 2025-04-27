package com.iuxoa.marki;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewPaymentActivity extends AppCompatActivity {

    private EditText paymentAmount, paymentDescription;
    private Spinner paymentMethodSpinner;
    private Button submitPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        paymentAmount = findViewById(R.id.paymentAmount);
        paymentDescription = findViewById(R.id.paymentDescription);
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);
        submitPaymentButton = findViewById(R.id.submitPaymentButton);

        // Set up the payment method spinner (e.g., Credit Card, PayPal, etc.)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Credit Card", "PayPal", "Bank Transfer"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        submitPaymentButton.setOnClickListener(v -> {
            String amount = paymentAmount.getText().toString();
            String description = paymentDescription.getText().toString();
            String method = paymentMethodSpinner.getSelectedItem().toString();

            // Validate input
            if (amount.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate saving payment details (you can implement actual logic here)
            Toast.makeText(this, "Payment of " + amount + " via " + method + " submitted!", Toast.LENGTH_SHORT).show();

            // You can save to database or call API for actual payment processing
        });
    }
}