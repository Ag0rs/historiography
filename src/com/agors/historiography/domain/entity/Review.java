package com.agors.historiography.domain.entity;

/**
 * Клас, що представляє відгук користувача про історичне місце. Зберігає інформацію про відгук, таку
 * як ідентифікатор, назву місця, текст відгуку, рейтинг та автора відгуку.
 */
public class Review {

    private final int id;
    private final String placeName;
    private final String text;
    private final int rating;
    private final String author;

    /**
     * Конструктор класу {@code Review}. Створює новий екземпляр відгуку з заданими параметрами.
     *
     * @param id        Ідентифікатор відгуку.
     * @param placeName Назва історичного місця, на яке залишено відгук.
     * @param text      Текст відгуку.
     * @param rating    Рейтинг, наданий автором відгуку.
     * @param author    Автор відгуку.
     */
    public Review(int id, String placeName, String text, int rating, String author) {
        this.id = id;
        this.placeName = placeName;
        this.text = text;
        this.rating = rating;
        this.author = author;
    }

    /**
     * Отримує ідентифікатор відгуку.
     *
     * @return Ідентифікатор відгуку.
     */
    public int getId() {
        return id;
    }

    /**
     * Отримує назву історичного місця, на яке залишено відгук.
     *
     * @return Назва історичного місця.
     */
    public String getPlaceName() {
        return placeName;
    }

    /**
     * Отримує текст відгуку.
     *
     * @return Текст відгуку.
     */
    public String getText() {
        return text;
    }

    /**
     * Отримує рейтинг, наданий автором відгуку.
     *
     * @return Рейтинг відгуку.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Отримує ім'я автора відгуку.
     *
     * @return Автор відгуку.
     */
    public String getAuthor() {
        return author;
    }
}
