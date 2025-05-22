package com.snorax;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import soup.neumorphism.NeumorphButton;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private NeumorphButton signInBtn;
    private ImageView backButton, passwordToggle, googleSignIn;
    private boolean isPasswordVisible = false;
    private ProgressDialog progressDialog;
    private TextView forgetPasswordTextView;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Redirect if already logged in
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();

        // Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI
        emailEditText = findViewById(R.id.id_number);
        passwordEditText = findViewById(R.id.id_password);
        signInBtn = findViewById(R.id.signIn);
        backButton = findViewById(R.id.back_button);
        passwordToggle = findViewById(R.id.password_toggle);
        forgetPasswordTextView = findViewById(R.id.forgetPassword);
        googleSignIn = findViewById(R.id.googleSignIn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        signInBtn.setOnClickListener(v -> loginUser());
        backButton.setOnClickListener(v -> finish());
        googleSignIn.setOnClickListener(v -> signInWithGoogle());
        findViewById(R.id.signupRedirect).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        forgetPasswordTextView.setOnClickListener(v -> showForgotPasswordDialog());
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter Email");
            emailEditText.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Enter Password");
            passwordEditText.requestFocus();
            return;
        }

        signInBtn.setEnabled(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    signInBtn.setEnabled(true);
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        saveLoginState();
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.setMessage("Signing in with Google...");
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        saveLoginState();
                        Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Authentication Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_eye_closed);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_eye_open);
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        final EditText emailInput = view.findViewById(R.id.forgot_password_email);
        builder.setView(view);

        builder.setPositiveButton("Send", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button sendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            sendButton.setOnClickListener(v -> {
                String email = emailInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Enter your email");
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError("Enter a valid email");
                    return;
                }

                ProgressDialog resetDialog = new ProgressDialog(this);
                resetDialog.setMessage("Sending reset email...");
                resetDialog.setCancelable(false);
                resetDialog.show();

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            resetDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            });
        });

        dialog.show();
    }

    private void saveLoginState() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("isLoggedIn", true).apply();
    }
}
