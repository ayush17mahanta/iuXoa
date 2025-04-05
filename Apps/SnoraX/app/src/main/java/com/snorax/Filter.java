package com.snorax;

public class Filter {
    private String type; // "keyword", "sender", or "app"
    private String value; // The keyword, sender, or app name
    private boolean isBlocked; // true = block, false = allow

    public Filter(String type, String value, boolean isBlocked) {
        this.type = type;
        this.value = value;
        this.isBlocked = isBlocked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}