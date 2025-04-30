package com.iuxoa.marki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import soup.neumorphism.NeumorphButton;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText emailField, passwordField;
    private NeumorphButton loginButton;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView backButton, passwordToggle, googleSignIn, twitterSignIn, githubSignIn;
    private boolean isPasswordVisible = false;
    private DatabaseReference usersRef;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleGoogleSignInResult(task);
                    } else {
                        hideProgress();
                        Toast.makeText(this, "Google sign-in cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailField = findViewById(R.id.id_number);
        passwordField = findViewById(R.id.id_password);
        loginButton = findViewById(R.id.signIn);
        backButton = findViewById(R.id.back_button);
        passwordToggle = findViewById(R.id.password_toggle);
        googleSignIn = findViewById(R.id.googleSignIn);
        twitterSignIn = findViewById(R.id.twitterSignIn);
        githubSignIn = findViewById(R.id.githubSignIn);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        backButton.setOnClickListener(v -> finish());
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        googleSignIn.setOnClickListener(v -> signInWithGoogle());
        twitterSignIn.setOnClickListener(v -> twitterLogin());
        githubSignIn.setOnClickListener(v -> githubLogin());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_eye_closed);
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_eye_open);
        }
        isPasswordVisible = !isPasswordVisible;
        passwordField.setSelection(passwordField.length());
    }

    private void loginUser(String email, String password) {
        showProgress();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    hideProgress();
                    if (task.isSuccessful()) {
                        fetchAndSaveUserDetails();
                    } else {
                        Toast.makeText(this, "Login failed: " + getErrorMessage(task.getException()), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "signInWithEmail:failure", task.getException());
                    }
                });
    }

    private String getErrorMessage(Exception exception) {
        if (exception == null) return "Unknown error";
        String error = exception.getMessage();
        if (error.contains("no user record")) return "Account not found";
        if (error.contains("password is invalid")) return "Incorrect password";
        if (error.contains("network error")) return "No internet connection";
        return error;
    }

    private void signInWithGoogle() {
        showProgress();
        int playServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (playServicesAvailable != ConnectionResult.SUCCESS) {
            hideProgress();
            GoogleApiAvailability.getInstance().getErrorDialog(this, playServicesAvailable, 0).show();
            return;
        }

        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
            } else {
                hideProgress();
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            hideProgress();
            Log.w(TAG, "Google sign-in failed", e);
            Toast.makeText(this, "Google sign-in failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAccountMerge(AuthCredential credential, String email) {
        // Fetch providers for this email
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String provider = task.getResult().getSignInMethods().get(0);
                        if (provider.equals("password")) {
                            // Prompt user to enter their password to link accounts
                            showAccountMergeDialog(email, credential);
                        } else {
                            hideProgress();
                            Toast.makeText(this,
                                    "Account already exists with different provider",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        hideProgress();
                        Toast.makeText(this,
                                "Error checking account: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void linkAccounts(String email, String password, AuthCredential credential) {
        showProgress();
        // First sign in with email/password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Then link with Google credential
                        Objects.requireNonNull(mAuth.getCurrentUser())
                                .linkWithCredential(credential)
                                .addOnCompleteListener(linkTask -> {
                                    hideProgress();
                                    if (linkTask.isSuccessful()) {
                                        fetchAndSaveUserDetails();
                                    } else {
                                        Toast.makeText(this,
                                                "Failed to link accounts: " +
                                                        linkTask.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        hideProgress();
                        Toast.makeText(this,
                                "Incorrect password",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showAccountMergeDialog(String email, AuthCredential credential) {
        // Create a dialog to ask for password
        EditText passwordInput = new EditText(this);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Account Exists")
                .setMessage("An account already exists with this email. Enter your password to link accounts.")
                .setView(passwordInput)
                .setPositiveButton("Link Accounts", (dialog, which) -> {
                    String password = passwordInput.getText().toString();
                    linkAccounts(email, password, credential);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    hideProgress();
                    dialog.dismiss();
                })
                .show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        // First try to sign in directly
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        fetchAndSaveUserDetails();
                    } else {
                        // If failed, check if it's due to account collision
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            handleAccountMerge(credential, acct.getEmail());
                        } else {
                            hideProgress();
                            Toast.makeText(this, "Google sign-in failed: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void twitterLogin() {
        showProgress();
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        mAuth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(authResult -> fetchAndSaveUserDetails())
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this, "Twitter login failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Twitter sign-in failed", e);
                });
    }

    private void githubLogin() {
        showProgress();
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
        mAuth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(authResult -> fetchAndSaveUserDetails())
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this, "GitHub login failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "GitHub sign-in failed", e);
                });
    }

    private void fetchAndSaveUserDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            hideProgress();
            return;
        }

        usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hideProgress();

                String name = snapshot.child("name").getValue(String.class);
                String accountType = snapshot.child("accountType").getValue(String.class);
                String username = snapshot.child("username").getValue(String.class);

                SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                editor.putString("userName", username != null ? username : "User");
                editor.putString("fullName", name != null ? name : "");
                editor.putString("accountType", accountType != null ? accountType : "");
                editor.apply();

                navigateBasedOnAccountType(accountType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgress();
                Toast.makeText(LoginActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateBasedOnAccountType(String accountType) {
        Intent intent = new Intent(this, ChooseAccountTypeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



    public void redirectToSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this, UserNameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgress() {
        // Show loading indicator (e.g., ProgressDialog or custom loader)
    }

    private void hideProgress() {
        // Hide loading indicator
    }
}
