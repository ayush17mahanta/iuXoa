package com.snorax;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FiltersActivity extends AppCompatActivity {

    private ListView listFilters;
    private Button btnAddFilter;
    private FiltersManager filtersManager;
    private List<Filter> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        // Initialize Views
        listFilters = findViewById(R.id.listFilters);
        btnAddFilter = findViewById(R.id.btnAddFilter);

        // Initialize FiltersManager
        filtersManager = new FiltersManager(this);

        // Load filters
        filters = filtersManager.getFilters();

        // Set up the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getFilterDescriptions());
        listFilters.setAdapter(adapter);

        // Set up the Add Filter button
        btnAddFilter.setOnClickListener(v -> {
            Intent intent = new Intent(FiltersActivity.this, EditFilterActivity.class);
            startActivity(intent);
        });

        // Set up item click listener for the ListView
        listFilters.setOnItemClickListener((parent, view, position, id) -> {
            Filter selectedFilter = filters.get(position);
            Intent intent = new Intent(FiltersActivity.this, EditFilterActivity.class);
            intent.putExtra("filter_index", position);
            intent.putExtra("filter_type", selectedFilter.getType());
            intent.putExtra("filter_value", selectedFilter.getValue());
            intent.putExtra("filter_blocked", selectedFilter.isBlocked());
            startActivity(intent);
        });
    }

    private List<String> getFilterDescriptions() {
        List<String> filterDescriptions = new ArrayList<>();
        for (Filter filter : filters) {
            String description = filter.getType() + ": " + filter.getValue() + " (" + (filter.isBlocked() ? "Block" : "Allow") + ")";
            filterDescriptions.add(description);
        }
        return filterDescriptions;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the filters list
        filters = filtersManager.getFilters();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getFilterDescriptions());
        listFilters.setAdapter(adapter);
    }
}