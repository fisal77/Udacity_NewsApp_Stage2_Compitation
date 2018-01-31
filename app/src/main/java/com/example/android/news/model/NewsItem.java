package com.example.android.news.model;

public class NewsItem {
    private String title;
    private String section;
    private String author;
    private String date;
    private String webUrl;
    private String thumbnail;

    public NewsItem(String title, String section, String author, String date, String webUrl, String thumbnail) {
        this.title = title;
        this.section = section;
        this.author = author;
        this.date = date;
        this.webUrl = webUrl;
        this.thumbnail = thumbnail;
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

    public String getThumbnail() {
        return thumbnail;
    }
}
