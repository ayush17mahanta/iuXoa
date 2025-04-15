package com.iuxoa.matridna;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import soup.neumorphism.NeumorphButton;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private EditText passwordEditText;
    private ImageView passwordToggle;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout main_layout = findViewById(R.id.main_layout);
        NeumorphButton signIn = findViewById(R.id.signIn);
        Animation animation_fade = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);

        passwordEditText = findViewById(R.id.id_password);
        passwordToggle = findViewById(R.id.password_toggle);

        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye_closed);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordToggle.setImageResource(R.drawable.ic_eye_open);
            }
            passwordEditText.setSelection(passwordEditText.length());
            isPasswordVisible = !isPasswordVisible;
        });




        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        signIn.setOnClickListener(v -> {
            progressDialog.show();
            new Handler().postDelayed(() -> {
                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }, 1000);
        });

        postponeEnterTransition();
        new Handler().postDelayed(() -> {
            main_layout.setVisibility(View.VISIBLE);
            main_layout.startAnimation(animation_fade);
            startPostponedEnterTransition();
        }, 1000);
    }
}
