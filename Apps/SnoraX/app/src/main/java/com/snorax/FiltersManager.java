package com.snorax;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FiltersManager {
    private static final String FILTERS_KEY = "filters";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public FiltersManager(Context context) {
        sharedPreferences = context.getSharedPreferences("filters_prefs", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveFilters(List<Filter> filters) {
        String filtersJson = gson.toJson(filters);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FILTERS_KEY, filtersJson);
        editor.apply();
    }

    public List<Filter> getFilters() {
        String filtersJson = sharedPreferences.getString(FILTERS_KEY, null);
        if (filtersJson == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Filter>>() {}.getType();
        return gson.fromJson(filtersJson, type);
    }

    public void addFilter(Filter filter) {
        List<Filter> filters = getFilters();
        filters.add(filter);
        saveFilters(filters);
    }

    public void updateFilter(int index, Filter filter) {
        List<Filter> filters = getFilters();
        filters.set(index, filter);
        saveFilters(filters);
    }

    public void deleteFilter(int index) {
        List<Filter> filters = getFilters();
        filters.remove(index);
        saveFilters(filters);
    }
}