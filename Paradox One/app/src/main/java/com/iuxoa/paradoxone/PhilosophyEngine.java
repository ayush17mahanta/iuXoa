package com.iuxoa.paradoxone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhilosophyEngine {
    private List<String> quotes;
    private Random random;

    public PhilosophyEngine() {
        quotes = new ArrayList<>();
        random = new Random();
        loadQuotes();
    }

    private void loadQuotes() {
        quotes.add("The unexamined life is not worth living. - Socrates");
        quotes.add("I think, therefore I am. - Descartes");
        // Add more quotes here
    }

    public String getRandomQuote() {
        int index = random.nextInt(quotes.size());
        return quotes.get(index);
    }
}
