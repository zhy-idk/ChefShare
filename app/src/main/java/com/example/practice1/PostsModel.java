package com.example.practice1;

import java.util.Map;
import java.util.Objects;

public class PostsModel {
    private String title;
    private String body;
    private String author;
    //private Map<String, LikeModel> likes;

    public PostsModel(String title, String body, String author) {
        this.title = title;
        this.body = body;
        this.author = author;
        //this.likes = likes;
    }

    public PostsModel() {
    }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


}
