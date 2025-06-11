package com.iuxoa.paradoxone;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhilosophyEngine {

    private static final String TAG = "PhilosophyEngine";
    private static final String QUOTES_FILE = "philosophy_quotes.json";

    private final List<Quote> quotes = new ArrayList<>();
    private final Random random = new Random();

    public PhilosophyEngine(Context context) {
        loadQuotesFromAssets(context);
    }

    private void loadQuotesFromAssets(Context context) {
        AssetManager assetManager = context.getAssets();

        try (InputStream is = assetManager.open(QUOTES_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            parseQuotesJson(jsonBuilder.toString());

        } catch (IOException e) {
            Log.e(TAG, "Error reading quotes file", e);
        }
    }

    private void parseQuotesJson(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String quoteText = obj.optString("quote", "No quote");
                String author = obj.optString("author", "Unknown");
                JSONArray tagsArray = obj.optJSONArray("tags");
                List<String> tags = new ArrayList<>();
                if(tagsArray != null) {
                    for(int j = 0; j < tagsArray.length(); j++) {
                        tags.add(tagsArray.getString(j));
                    }
                }
                quotes.add(new Quote(quoteText, author, tags));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing quotes JSON", e);
        }
    }

    /**
     * Get a random quote from all quotes
     */
    public Quote getRandomQuote() {
        if (quotes.isEmpty()) return null;
        return quotes.get(random.nextInt(quotes.size()));
    }

    /**
     * Get a random quote filtered by tag/category.
     * Returns null if no matching quote found.
     * @param tag The category/tag to filter quotes by, e.g., "Time", "Fear", "Memory"
     */
    public Quote getRandomQuoteByTag(String tag) {
        List<Quote> filtered = new ArrayList<>();
        for (Quote q : quotes) {
            if (q.getTags().contains(tag)) {
                filtered.add(q);
            }
        }
        if (filtered.isEmpty()) return null;
        return filtered.get(random.nextInt(filtered.size()));
    }

    /**
     * Quote data holder
     */
    public static class Quote {
        private final String text;
        private final String author;
        private final List<String> tags;

        public Quote(String text, String author, List<String> tags) {
            this.text = text;
            this.author = author;
            this.tags = tags;
        }

        public String getText() {
            return text;
        }

        public String getAuthor() {
            return author;
        }

        public List<String> getTags() {
            return tags;
        }

        @Override
        public String toString() {
            return "\"" + text + "\" - " + author;
        }
    }
}
