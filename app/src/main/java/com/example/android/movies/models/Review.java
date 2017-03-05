package com.example.android.movies.models;

public class Review {
    //review id
    private String id;
    //review name
    private String author;
    //review key
    private String content;
    //review video site
    private String url;

    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
