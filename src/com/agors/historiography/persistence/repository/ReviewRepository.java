package com.agors.historiography.persistence.repository;

import com.agors.historiography.domain.entity.Review;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Репозиторій для роботи з відгуками. Цей клас надає методи для додавання, видалення, завантаження
 * та збереження відгуків. Відгуки зберігаються у файлі формату JSON.
 */
public class ReviewRepository {

    private static final String FILE_NAME = "data/reviews.json";
    private List<Review> reviews;

    /**
     * Конструктор, який ініціалізує репозиторій і завантажує відгуки з файлу.
     */
    public ReviewRepository() {
        reviews = new ArrayList<>();
        loadReviews();
    }

    /**
     * Додає новий відгук до списку. Після додавання відгука, дані зберігаються у файл.
     *
     * @param placeName назва історичного місця, до якого належить відгук.
     * @param text      текст відгуку.
     * @param rating    рейтинг відгуку.
     * @param author    автор відгуку.
     */
    public void addReview(String placeName, String text, int rating, String author) {
        int id = reviews.size() + 1;
        Review review = new Review(id, placeName, text, rating, author);
        reviews.add(review);
        saveReviews();
    }

    /**
     * Повертає список усіх відгуків.
     *
     * @return список відгуків.
     */
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Видаляє відгук за вказаним ідентифікатором. Після видалення відгука, дані зберігаються у
     * файл.
     *
     * @param reviewId ідентифікатор відгуку, який потрібно видалити.
     * @return true, якщо відгук успішно видалено, інакше false.
     */
    public boolean deleteReview(int reviewId) {
        boolean isRemoved = reviews.removeIf(review -> review.getId() == reviewId);
        if (isRemoved) {
            saveReviews();
        }
        return isRemoved;
    }

    /**
     * Завантажує відгуки з файлу `data/reviews.json`. Якщо файл не існує або сталася помилка,
     * відгуки не завантажуються.
     */
    private void loadReviews() {
        try {
            Path path = Paths.get(FILE_NAME);
            if (Files.exists(path)) {
                String json = new String(Files.readAllBytes(path));
                Gson gson = new Gson();
                Review[] reviewArray = gson.fromJson(json, Review[].class);
                reviews = new ArrayList<>(Arrays.asList(reviewArray));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Зберігає поточний список відгуків у файл `data/reviews.json`.
     */
    private void saveReviews() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(reviews);
            Files.write(Paths.get(FILE_NAME), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
