package com.example.step_tracking;

public class Note {
    private String content;
    private String timestamp;

    public Note(String content, String timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }
}