package com.mycompany.motorph.model;

public class NotificationEntry {

    private final String timestamp;
    private final String actor;
    private final String role;
    private final String action;
    private final String details;
    private boolean read;

    public NotificationEntry(String timestamp,
                             String actor,
                             String role,
                             String action,
                             String details) {
        this(timestamp, actor, role, action, details, false);
    }

    public NotificationEntry(String timestamp,
                             String actor,
                             String role,
                             String action,
                             String details,
                             boolean read) {
        this.timestamp = timestamp;
        this.actor = actor;
        this.role = role;
        this.action = action;
        this.details = details;
        this.read = read;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getActor() {
        return actor;
    }

    public String getRole() {
        return role;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
