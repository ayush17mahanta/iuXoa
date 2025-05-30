package com.iuxoa.practicemarki;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private EditText emailField, passwordField;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Ensure this matches your XML file name

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        TextView forgetPasswordField = findViewById(R.id.forgetPasswordField);
        View loginButton = findViewById(R.id.loginButton);
        View googleButton = findViewById(R.id.googleButton);

        mAuth = FirebaseAuth.getInstance();

        // Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your web client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginButton.setOnClickListener(v -> loginWithEmail());

        googleButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        forgetPasswordField.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            if (!isValidEmail(email)) {
                emailField.setError("Enter a valid email to reset password");
            } else {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void loginWithEmail() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (!isValidEmail(email)) {
            emailField.setError("Invalid email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Enter your password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Google Sign-In Success", Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(this, MainActivity.class));
                    } else {
                        Toast.makeText(this, "Firebase Auth Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
