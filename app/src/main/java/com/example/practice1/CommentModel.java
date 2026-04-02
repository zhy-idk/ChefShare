package com.example.practice1;

public class CommentModel {
    String body;
    String author;

    public CommentModel(String body, String author) {
        this.body = body;
        this.author = author;
    }

    public CommentModel() {
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
