package com.example.android.movies.models;

public class Trailer {
    //trailer id
    private String id;
    //trailer name
    private String name;
    //video key
    private String key;
    //trailer video site
    private String site;

    public Trailer(String id, String name, String key, String site) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getSite() {
        return site;
    }
}
