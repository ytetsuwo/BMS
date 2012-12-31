package com.tetsuwo.amazon;

public class Book {
    String author = null;
    String title = null;
    String publicationDate = null;
    String URL = null;

    public void setAuthor(String author) {
	this.author = author;
    }
    public String getAuthor() {
	return author;
    }
    public void setTitle(String title) {
	this.title = title;
    }
    public String getTitle() {
	return title;
    }
    public void setPublicationDate(String date) {
	this.publicationDate = date;
    }
    public String getPublicationDate() {
	return publicationDate;
    }
    public void setURL(String URL) {
	this.URL = URL;
    }
    public String getURL() {
	return URL;
    }
}