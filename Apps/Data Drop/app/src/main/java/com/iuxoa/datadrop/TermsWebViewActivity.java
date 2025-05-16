package com.iuxoa.datadrop; // Replace with your actual package name

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class TermsWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final String TERMS_URL = "https://yourdomain.com/terms"; // Replace with your actual URL

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_web_view);  // Make sure this is your layout filename

        // Initialize Views
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Enable JavaScript if your page needs it
        webView.getSettings().setJavaScriptEnabled(true);

        // Set the WebViewClient
        webView.setWebViewClient(new CustomWebViewClient());

        // Load the Terms URL
        webView.loadUrl(TERMS_URL);

        // Swipe to refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            errorTextView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.reload();
        });

        // Initially show progress bar, hide error
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            errorTextView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            String host = uri.getHost();

            // Load your domain URLs inside WebView, open external in browser
            if (host != null && host.contains("yourdomain.com")) {
                return false; // Load in WebView
            } else {
                // Open external links in browser
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            // Show error message and hide WebView and progress bar
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText("Failed to load content. Please check your internet connection.");
            webView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
