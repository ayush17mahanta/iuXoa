package com.iuxoa.datadrop;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SurveyModel implements Serializable {
    private String id;
    private String title;
    private double payout;
    private List<Map<String, Object>> questions;

    public SurveyModel(String id, String title, double payout, List<Map<String, Object>> questions) {
        this.id = id;
        this.title = title;
        this.payout = payout;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPayout() {
        return payout;
    }

    public List<Map<String, Object>> getQuestions() {
        return questions;
    }
}
