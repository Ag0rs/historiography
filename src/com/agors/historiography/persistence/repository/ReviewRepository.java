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

public class ReviewRepository {

    private static final String FILE_NAME = "data/reviews.json"; // Шлях до файлу для зберігання відгуків
    private List<Review> reviews;

    public ReviewRepository() {
        reviews = new ArrayList<>();
        loadReviews();
    }

    // Додано параметр `placeName` та `author`, щоб уникнути помилки з методом `addReview`
    public void addReview(String placeName, String text, int rating, String author) {
        int id = reviews.size() + 1;  // Генерація ID для нового відгуку
        Review review = new Review(id, placeName, text, rating, author);  // Передаємо автора
        reviews.add(review);
        saveReviews();
    }

    // Повертає список усіх відгуків
    public List<Review> getReviews() {
        return reviews;
    }

    // Видалити відгук за ID
    public boolean deleteReview(int reviewId) {
        boolean isRemoved = reviews.removeIf(review -> review.getId() == reviewId);
        if (isRemoved) {
            saveReviews(); // Оновлюємо файл після видалення
        }
        return isRemoved;
    }

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
            e.printStackTrace();  // Обробка помилок зчитування
        }
    }

    private void saveReviews() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(reviews);
            Files.write(Paths.get(FILE_NAME), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();  // Обробка помилок запису
        }
    }
}
