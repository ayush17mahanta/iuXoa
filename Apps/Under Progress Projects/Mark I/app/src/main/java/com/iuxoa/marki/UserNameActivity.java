package com.iuxoa.marki;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class UserNameActivity extends AppCompatActivity {

    private EditText userNameField;
    private ImageView usernameStatus;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private boolean isUsernameAvailable = false;
    private boolean isChecking = false;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9_]{3,15}$");
    private static final String TAG = "UserNameActivity";
    private static final long DEBOUNCE_DELAY = 500;
    private static final long TIMEOUT_DELAY = 10000;

    private Handler handler = new Handler();
    private Handler timeoutHandler = new Handler();
    private Runnable debounceRunnable;
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        userNameField = findViewById(R.id.userName);
        usernameStatus = findViewById(R.id.usernameAvailability);

        // Set up anonymous authentication
        signInAnonymously();

        // Set up UI components
        setupUsernameField();
        setupButtons();
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        showToast("Authentication failed. Some features may not work.");
                    }
                });
    }

    private void setupUsernameField() {
        // Prevent whitespace in usernames
        userNameField.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (Character.isWhitespace(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }});

        // Text watcher with debounce
        userNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(debounceRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentText = s.toString().toLowerCase();

                // Force lowercase
                if (!s.toString().equals(currentText)) {
                    userNameField.removeTextChangedListener(this);
                    userNameField.setText(currentText);
                    userNameField.setSelection(currentText.length());
                    userNameField.addTextChangedListener(this);
                    return;
                }

                handler.removeCallbacks(debounceRunnable);
                debounceRunnable = () -> {
                    if (!currentText.isEmpty() && userNameField.getText().toString().equals(currentText)) {
                        checkUsernameAvailability(currentText);
                    } else {
                        usernameStatus.setVisibility(View.INVISIBLE);
                        isUsernameAvailable = false;
                    }
                };
                handler.postDelayed(debounceRunnable, DEBOUNCE_DELAY);
            }
        });
    }

    private void setupButtons() {
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        findViewById(R.id.next_button).setOnClickListener(v -> {
            String username = userNameField.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                showToast("Username is required");
            } else if (!USERNAME_PATTERN.matcher(username).matches()) {
                showToast("3–15 chars: lowercase letters, numbers, underscore");
            } else if (isChecking) {
                showToast("Please wait, checking availability...");
            } else if (!isUsernameAvailable) {
                showToast("Please choose an available username");
            } else {
                proceedWithUsername(username);
            }
        });
    }

    private void checkUsernameAvailability(String username) {
        if (!isNetworkAvailable()) {
            handleUsernameCheckError(new FirebaseFirestoreException(
                    "No network",
                    FirebaseFirestoreException.Code.UNAVAILABLE
            ));
            return;
        }

        if (username.length() < 3 || !USERNAME_PATTERN.matcher(username).matches()) {
            usernameStatus.setVisibility(View.VISIBLE);
            usernameStatus.setImageResource(R.drawable.ic_cross);
            isUsernameAvailable = false;
            isChecking = false;
            return;
        }

        usernameStatus.setVisibility(View.VISIBLE);
        usernameStatus.setImageResource(R.drawable.ic_loading);
        isChecking = true;

        // Set timeout
        timeoutRunnable = () -> {
            if (isChecking) {
                handleUsernameCheckError(new TimeoutException("Username check timed out"));
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_DELAY);

        // Check username availability
        db.collection("usernames").document(username).get()
                .addOnCompleteListener(task -> {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    if (task.isSuccessful()) {
                        boolean exists = task.getResult().exists();
                        isUsernameAvailable = !exists;
                        usernameStatus.setImageResource(
                                exists ? R.drawable.ic_cross : R.drawable.ic_tick);
                    } else {
                        handleUsernameCheckError(task.getException());
                    }
                    isChecking = false;
                });
    }

    private void handleUsernameCheckError(Exception e) {
        isUsernameAvailable = false;
        usernameStatus.setImageResource(R.drawable.ic_cross);
        isChecking = false;

        if (e instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
            switch (firestoreException.getCode()) {
                case UNAVAILABLE:
                case CANCELLED:
                    showToast("Network error. Please check your connection.");
                    break;
                case PERMISSION_DENIED:
                    showToast("Server error. Please try again later.");
                    break;
                default:
                    showToast("Error checking username");
            }
        } else if (e instanceof TimeoutException) {
            showToast("Request timed out");
        } else {
            showToast("Unknown error occurred");
        }
        Log.e(TAG, "Username check failed", e);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void proceedWithUsername(String username) {
        Intent intent = new Intent(UserNameActivity.this, SignUpActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handlers to prevent memory leaks
        handler.removeCallbacks(debounceRunnable);
        timeoutHandler.removeCallbacks(timeoutRunnable);
    }
}