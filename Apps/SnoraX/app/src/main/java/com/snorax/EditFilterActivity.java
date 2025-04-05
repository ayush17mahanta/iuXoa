package com.snorax;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditFilterActivity extends AppCompatActivity {

    private EditText etFilterValue;
    private RadioGroup rgFilterType, rgFilterAction;
    private Button btnSaveFilter;
    private FiltersManager filtersManager;
    private int filterIndex = -1; // -1 means new filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_filter);

        // Initialize Views
        etFilterValue = findViewById(R.id.etFilterValue);
        rgFilterType = findViewById(R.id.rgFilterType);
        rgFilterAction = findViewById(R.id.rgFilterAction);
        btnSaveFilter = findViewById(R.id.btnSaveFilter);

        // Initialize FiltersManager
        filtersManager = new FiltersManager(this);

        // Check if editing an existing filter
        if (getIntent().hasExtra("filter_index")) {
            filterIndex = getIntent().getIntExtra("filter_index", -1);
            String filterType = getIntent().getStringExtra("filter_type");
            String filterValue = getIntent().getStringExtra("filter_value");
            boolean filterBlocked = getIntent().getBooleanExtra("filter_blocked", true);

            etFilterValue.setText(filterValue);

            if (filterType.equals("keyword")) {
                rgFilterType.check(R.id.rbKeyword);
            } else if (filterType.equals("sender")) {
                rgFilterType.check(R.id.rbSender);
            } else if (filterType.equals("app")) {
                rgFilterType.check(R.id.rbApp);
            }

            if (filterBlocked) {
                rgFilterAction.check(R.id.rbBlock);
            } else {
                rgFilterAction.check(R.id.rbAllow);
            }
        }

        // Set up the Save Filter button
        btnSaveFilter.setOnClickListener(v -> saveFilter());
    }

    private void saveFilter() {
        String filterValue = etFilterValue.getText().toString().trim();
        if (filterValue.isEmpty()) {
            Toast.makeText(this, "Filter value cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedTypeId = rgFilterType.getCheckedRadioButtonId();
        String filterType = "";
        if (selectedTypeId == R.id.rbKeyword) {
            filterType = "keyword";
        } else if (selectedTypeId == R.id.rbSender) {
            filterType = "sender";
        } else if (selectedTypeId == R.id.rbApp) {
            filterType = "app";
        }

        boolean isBlocked = rgFilterAction.getCheckedRadioButtonId() == R.id.rbBlock;

        Filter filter = new Filter(filterType, filterValue, isBlocked);

        if (filterIndex == -1) {
            // Add new filter
            filtersManager.addFilter(filter);
        } else {
            // Update existing filter
            filtersManager.updateFilter(filterIndex, filter);
        }

        Toast.makeText(this, "Filter saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}