package com.iuxoa.marki;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    // UI Components
    private EditText emailField, passwordField, fullNameField;
    private ImageView togglePassword;
    private String username;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;

    // State
    private boolean isPasswordVisible = false;

    // Google Sign-In Launcher
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleGoogleSignInResult(result.getData());
                } else {
                    Toast.makeText(this, "Google sign in cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Get username from previous activity
        username = getIntent().getStringExtra("username");
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailField = findViewById(R.id.id_email);
        passwordField = findViewById(R.id.id_password);
        fullNameField = findViewById(R.id.profileName);
        togglePassword = findViewById(R.id.password_toggle);

        // Set username in UI
        TextView usernameView = findViewById(R.id.username_display);
        usernameView.setText(username);
    }

    private void setupClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        togglePassword.setOnClickListener(v -> togglePasswordVisibility());
        findViewById(R.id.signUp).setOnClickListener(v -> registerUser());
        findViewById(R.id.googleSignIn).setOnClickListener(v -> signInWithGoogle());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePassword.setImageResource(R.drawable.ic_eye_closed);
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePassword.setImageResource(R.drawable.ic_eye_open);
        }
        isPasswordVisible = !isPasswordVisible;
        passwordField.setSelection(passwordField.length());
    }

    private void registerUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String fullName = fullNameField.getText().toString().trim();

        if (!validateInputs(email, password, fullName)) return;

        showProgress();

        // First check if email exists
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            // No account exists - proceed with creation
                            createNewAccount(email, password, fullName);
                        } else {
                            // Account exists - prompt to login instead
                            hideProgress();
                            new AlertDialog.Builder(this)
                                    .setTitle("Account Exists")
                                    .setMessage("An account already exists with this email. Would you like to log in instead?")
                                    .setPositiveButton("Login", (d, w) -> {
                                        Intent intent = new Intent(this, LoginActivity.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    } else {
                        hideProgress();
                        Toast.makeText(this, "Error checking account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs(String email, String password, String fullName) {
        if (TextUtils.isEmpty(fullName)) {
            fullNameField.setError("Full name is required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Invalid email format");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            return false;
        } else if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private void handleRegistrationError(Exception e) {
        String errorMessage = "Registration failed";
        if (e != null) {
            if (e instanceof FirebaseAuthWeakPasswordException) {
                errorMessage = "Password is too weak";
            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                errorMessage = "Invalid email format";
            } else if (e instanceof FirebaseAuthUserCollisionException) {
                errorMessage = "Email already in use";
            } else {
                errorMessage = e.getMessage();
            }
            Log.e(TAG, "Signup error: " + e.getClass().getSimpleName(), e);
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void signInWithGoogle() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear any previous sign-in
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            Toast.makeText(this, "Google sign in failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgress();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String fullName = acct.getDisplayName();
                            String email = acct.getEmail();
                            String photoUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : null;
                            saveUserToFirestore(user.getUid(), username, fullName, email, photoUrl);
                        }
                    } else {
                        hideProgress();
                        handleGoogleSignInError(task.getException());
                    }
                });
    }

    private void handleGoogleSignInError(Exception e) {
        String errorMessage = "Google sign-in failed";
        if (e instanceof FirebaseAuthUserCollisionException) {
            errorMessage = "This Google account is already linked with another method";
        } else if (e != null) {
            errorMessage = e.getMessage();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Google sign-in error", e);
    }

    private void saveUserToFirestore(String userId, String username, String fullName,
                                     String email, String photoUrl) {
        showProgress();

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("name", fullName);
        user.put("email", email);
        if (photoUrl != null) {
            user.put("photoUrl", photoUrl);
        }
        user.put("createdAt", FieldValue.serverTimestamp());

        Map<String, Object> usernameRef = new HashMap<>();
        usernameRef.put("userId", userId);

        WriteBatch batch = db.batch();
        batch.set(db.collection("users").document(userId), user);
        batch.set(db.collection("usernames").document(username), usernameRef);

        batch.commit()
                .addOnCompleteListener(task -> {
                    hideProgress();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registration complete!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        handleFirestoreError(task.getException());
                    }
                });
    }

    private void handleFirestoreError(Exception e) {
        String errorMessage = "Failed to save user data";
        if (e != null) {
            errorMessage = "Error: " + e.getMessage();
            Log.e(TAG, "Firestore save error", e);

            // Clean up auth user if Firestore fails
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                currentUser.delete().addOnCompleteListener(deleteTask -> {
                    if (!deleteTask.isSuccessful()) {
                        Log.e(TAG, "Failed to clean up auth user", deleteTask.getException());
                    }
                });
            }
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void showProgress() {
        findViewById(R.id.progress_overlay).setVisibility(View.VISIBLE);
    }
    private void createNewAccount(String email, String password, String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), username, fullName, email, null);
                        }
                    } else {
                        hideProgress();
                        Toast.makeText(this, "Registration failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void hideProgress() {
        findViewById(R.id.progress_overlay).setVisibility(View.GONE);
    }
}