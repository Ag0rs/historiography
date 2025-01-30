package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.Review;
import com.agors.historiography.persistence.repository.ReviewRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.List;

public class ReviewManager {

    private static final int REVIEWS_PER_PAGE = 5; // Зменшено кількість відгуків на сторінці
    private final ReviewRepository reviewRepository;
    private final TextGraphics textGraphics;
    private final Screen screen;

    // Конструктор
    public ReviewManager(ReviewRepository reviewRepository, TextGraphics textGraphics,
        Screen screen) {
        this.reviewRepository = reviewRepository;
        this.textGraphics = textGraphics;
        this.screen = screen;
    }

    public void manageReviews() throws IOException {
        List<Review> reviews = reviewRepository.getReviews();  // Замінено getAllReviews() на getReviews()
        int selectedReviewIndex = 0; // Індекс вибраного відгуку
        int pageStartIndex = 0;      // Початковий індекс для поточної сторінки

        // Перевірка на наявність відгуків
        if (reviews.isEmpty()) {
            clearScreen();
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 5, "Немає відгуків.");
            screen.refresh();
            screen.readInput();  // Чекаємо натискання клавіші для виходу
            return;
        }

        // Основний цикл для управління відгуками
        while (true) {
            clearScreen();
            int yPosition = 3; // Початкова вертикальна позиція для відображення відгуків
            int pageEndIndex = Math.min(pageStartIndex + REVIEWS_PER_PAGE, reviews.size());

            // Виведення відгуків вертикально
            for (int i = pageStartIndex; i < pageEndIndex; i++) {
                Review review = reviews.get(i);
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

                // Виділення вибраного відгуку
                if (i == selectedReviewIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                }

                // Виведення назви місця
                textGraphics.putString(10, yPosition, "Місце: " + review.getPlaceName());
                // Виведення рейтингу
                textGraphics.putString(50, yPosition, "Рейтинг: " + review.getRating());
                // Виведення тексту відгуку
                yPosition++;
                String reviewText = review.getText();
                if (reviewText.length() > 50) { // Скорочення тексту до 50 символів
                    reviewText = reviewText.substring(0, 50) + "...";
                }
                textGraphics.putString(10, yPosition, "Відгук: " + reviewText);

                yPosition++; // Переміщаємося вниз на наступний рядок
            }

            // Виведення фіксованого тексту внизу
            textGraphics.setForegroundColor(ANSI.YELLOW);
            textGraphics.putString(10, REVIEWS_PER_PAGE + 10,
                "Натисніть Enter для видалення відгуку, або ESC для виходу");
            textGraphics.putString(10, REVIEWS_PER_PAGE + 11, "↑ Вгору   ↓ Вниз");
            screen.refresh();

            // Обробка натискання клавіші
            KeyStroke keyStroke = screen.readInput();

            // Перевірка для стрілочок і ESC
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    if (selectedReviewIndex < reviews.size() - 1) {
                        selectedReviewIndex++; // Переміщаємося вниз по списку
                    } else if (pageStartIndex + REVIEWS_PER_PAGE < reviews.size()) {
                        // Прокручування вниз, якщо вибраний відгук в кінці списку
                        selectedReviewIndex = pageStartIndex + REVIEWS_PER_PAGE - 1;
                        pageStartIndex++; // Прокручуємо список
                    }
                    break;

                case ArrowUp:
                    if (selectedReviewIndex > 0) {
                        selectedReviewIndex--; // Переміщаємося вгору по списку
                    } else if (pageStartIndex > 0) {
                        // Прокручування вгору, якщо вибраний відгук на початку списку
                        selectedReviewIndex = pageStartIndex;
                        pageStartIndex--; // Прокручуємо список
                    }
                    break;

                case Enter:
                    // Якщо вибрано відгук
                    Review selectedReview = reviews.get(selectedReviewIndex);
                    confirmDeletion(selectedReview); // Підтвердження перед видаленням
                    return; // після виконання дії виходимо з циклу

                case Escape:
                    return; // вихід без змін
            }

            // Перевірка на автоматичне прокручування
            if (selectedReviewIndex >= pageStartIndex + REVIEWS_PER_PAGE - 1
                && pageStartIndex + REVIEWS_PER_PAGE < reviews.size()) {
                pageStartIndex++; // Прокручуємо сторінку
            }
            if (selectedReviewIndex <= pageStartIndex && pageStartIndex > 0) {
                pageStartIndex--; // Прокручуємо сторінку вгору
            }
        }
    }

    private void confirmDeletion(Review selectedReview) throws IOException {
        // Очищаємо екран та виводимо повідомлення про підтвердження
        clearScreen();
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(10, 5, "Ви дійсно хочете видалити цей відгук? (y/n)");
        screen.refresh();

        // Чекаємо на введення
        KeyStroke keyStroke = screen.readInput();

        switch (keyStroke.getKeyType()) {
            case Character:
                if (keyStroke.getCharacter() == 'y' || keyStroke.getCharacter() == 'Y') {
                    // Видалення відгуку
                    reviewRepository.deleteReview(selectedReview.getId());
                    clearScreen();
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    textGraphics.putString(10, 5, "Відгук успішно видалено.");
                } else {
                    // Скасування видалення
                    clearScreen();
                    textGraphics.setForegroundColor(TextColor.ANSI.RED);
                    textGraphics.putString(10, 5, "Видалення скасовано.");
                }
                break;

            case Escape:
                // Скасування видалення
                clearScreen();
                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                textGraphics.putString(10, 5, "Видалення скасовано.");
                break;
        }
        screen.refresh();
        screen.readInput(); // Чекаємо натискання клавіші для повернення
        manageReviews(); // Повертаємось до перегляду списку
    }

    // Додатковий метод для очищення екрану
    private void clearScreen() {
        screen.clear();
    }
}
