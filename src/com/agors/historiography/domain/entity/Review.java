package com.agors.historiography.domain.entity;

public class Review {

    private final int id;
    private final String placeName; // Назва місця
    private final String text;      // Текст відгуку
    private final int rating;       // Рейтинг
    private final String author;    // Автор відгуку

    public Review(int id, String placeName, String text, int rating, String author) {
        this.id = id;
        this.placeName = placeName;
        this.text = text;
        this.rating = rating;
        this.author = author;
    }

    // Геттери
    public int getId() {
        return id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public String getAuthor() {
        return author;
    }
}
