package com.iuxoa.datadrop;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 onboardingViewPager;
    private LinearLayout layoutOnboardingIndicators;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonNext = findViewById(R.id.buttonOnboardingAction);

        List<OnboardingItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(R.drawable.page1, "Sell Your Data", "Monetize your habits ethically."));
        onboardingItems.add(new OnboardingItem(R.drawable.page2, "Full Control", "Decide what you share."));
        onboardingItems.add(new OnboardingItem(R.drawable.page3, "Get Paid", "Earn for every approved data use."));

        onboardingViewPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPager.setAdapter(new OnboardingAdapter(onboardingItems));

        setupIndicators(onboardingItems.size());
        setCurrentIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        buttonNext.setOnClickListener(v -> {
            if (onboardingViewPager.getCurrentItem() + 1 < onboardingItems.size()) {
                onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            int drawableId = (i == index) ? R.drawable.onboarding_indicator_active
                    : R.drawable.onboarding_indicator_inactive;
            imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), drawableId));
        }

        if (index == onboardingViewPager.getAdapter().getItemCount() - 1) {
            buttonNext.setText("Get Started");
        } else {
            buttonNext.setText("Next");
        }
    }
}