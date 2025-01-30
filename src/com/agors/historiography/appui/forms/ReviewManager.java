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

/**
 * Клас, що відповідає за управління відгуками в програмі. Дозволяє переглядати, видаляти та
 * навігувати по списку відгуків.
 */
public class ReviewManager {

    /**
     * Кількість відгуків, що відображаються на одній сторінці.
     */
    private static final int REVIEWS_PER_PAGE = 5;

    private final ReviewRepository reviewRepository;
    private final TextGraphics textGraphics;
    private final Screen screen;

    /**
     * Конструктор класу ReviewManager.
     *
     * @param reviewRepository репозиторій для роботи з відгуками.
     * @param textGraphics     об'єкт для малювання тексту на екрані.
     * @param screen           екран для відображення інтерфейсу.
     */
    public ReviewManager(ReviewRepository reviewRepository, TextGraphics textGraphics,
        Screen screen) {
        this.reviewRepository = reviewRepository;
        this.textGraphics = textGraphics;
        this.screen = screen;
    }

    /**
     * Метод для управління відгуками. Виводить список відгуків на екран, дозволяє навігувати по
     * відгуках та видаляти їх.
     *
     * @throws IOException у разі помилки вводу/виводу.
     */
    public void manageReviews() throws IOException {
        List<Review> reviews = reviewRepository.getReviews();
        int selectedReviewIndex = 0;
        int pageStartIndex = 0;

        if (reviews.isEmpty()) {
            clearScreen();
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 5, "Немає відгуків.");
            screen.refresh();
            screen.readInput();
            return;
        }

        while (true) {
            clearScreen();
            int yPosition = 3;
            int pageEndIndex = Math.min(pageStartIndex + REVIEWS_PER_PAGE, reviews.size());

            for (int i = pageStartIndex; i < pageEndIndex; i++) {
                Review review = reviews.get(i);
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

                if (i == selectedReviewIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                }

                textGraphics.putString(10, yPosition, "Місце: " + review.getPlaceName());
                textGraphics.putString(50, yPosition, "Рейтинг: " + review.getRating());
                yPosition++;
                String reviewText = review.getText();
                if (reviewText.length() > 50) {
                    reviewText = reviewText.substring(0, 50) + "...";
                }
                textGraphics.putString(10, yPosition, "Відгук: " + reviewText);

                yPosition++;
            }

            textGraphics.setForegroundColor(ANSI.YELLOW);
            textGraphics.putString(10, REVIEWS_PER_PAGE + 10,
                "Натисніть Enter для видалення відгуку, або ESC для виходу");
            textGraphics.putString(10, REVIEWS_PER_PAGE + 11, "↑ Вгору   ↓ Вниз");
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();

            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    if (selectedReviewIndex < reviews.size() - 1) {
                        selectedReviewIndex++;
                    } else if (pageStartIndex + REVIEWS_PER_PAGE < reviews.size()) {
                        selectedReviewIndex = pageStartIndex + REVIEWS_PER_PAGE - 1;
                        pageStartIndex++;
                    }
                    break;

                case ArrowUp:
                    if (selectedReviewIndex > 0) {
                        selectedReviewIndex--;
                    } else if (pageStartIndex > 0) {
                        selectedReviewIndex = pageStartIndex;
                        pageStartIndex--;
                    }
                    break;

                case Enter:
                    Review selectedReview = reviews.get(selectedReviewIndex);
                    confirmDeletion(selectedReview);
                    return;

                case Escape:
                    return;
            }

            if (selectedReviewIndex >= pageStartIndex + REVIEWS_PER_PAGE - 1
                && pageStartIndex + REVIEWS_PER_PAGE < reviews.size()) {
                pageStartIndex++;
            }
            if (selectedReviewIndex <= pageStartIndex && pageStartIndex > 0) {
                pageStartIndex--;
            }
        }
    }

    /**
     * Метод для підтвердження видалення відгуку. Запитує у користувача, чи хоче він видалити
     * вибраний відгук.
     *
     * @param selectedReview вибраний для видалення відгук.
     * @throws IOException у разі помилки вводу/виводу.
     */
    private void confirmDeletion(Review selectedReview) throws IOException {
        clearScreen();
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(10, 5, "Ви дійсно хочете видалити цей відгук? (y/n)");
        screen.refresh();

        KeyStroke keyStroke = screen.readInput();

        switch (keyStroke.getKeyType()) {
            case Character:
                if (keyStroke.getCharacter() == 'y' || keyStroke.getCharacter() == 'Y') {
                    reviewRepository.deleteReview(selectedReview.getId());
                    clearScreen();
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    textGraphics.putString(10, 5, "Відгук успішно видалено.");
                } else {
                    clearScreen();
                    textGraphics.setForegroundColor(TextColor.ANSI.RED);
                    textGraphics.putString(10, 5, "Видалення скасовано.");
                }
                break;

            case Escape:
                clearScreen();
                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                textGraphics.putString(10, 5, "Видалення скасовано.");
                break;
        }
        screen.refresh();
        screen.readInput();
        manageReviews();
    }

    /**
     * Очищає екран.
     */
    private void clearScreen() {
        screen.clear();
    }
}