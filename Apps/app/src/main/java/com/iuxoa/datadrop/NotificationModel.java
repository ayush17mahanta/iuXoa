package com.iuxoa.datadrop;

public class NotificationModel {
    private String id;
    private String title;
    private String message;
    private long timestamp;
    private boolean isRead;

    public NotificationModel() { }

    public NotificationModel(String id, String title, String message, long timestamp, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setRead(boolean read) { isRead = read; }
}
