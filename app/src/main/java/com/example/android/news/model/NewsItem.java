package com.example.android.news.model;

public class NewsItem {
    private String title;
    private String section;
    private String author;
    private String date;
    private String webUrl;

    public NewsItem(String title, String section, String author, String date, String webUrl) {
        this.title = title;
        this.section = section;
        this.author = author;
        this.date = date;
        this.webUrl = webUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getWebUrl() {
        return webUrl;
    }
}
