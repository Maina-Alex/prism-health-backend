package com.prismhealth.Models;

public class PushContent {
    private String title;
    private String body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public PushContent(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public PushContent() {
    }

    @Override
    public String toString() {
        return "[body=" + body + ", title=" + title + "]";
    }

}
