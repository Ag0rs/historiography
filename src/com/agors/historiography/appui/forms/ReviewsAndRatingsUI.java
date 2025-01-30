package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.HistoricalPlace;
import com.agors.historiography.domain.entity.Review;
import com.agors.historiography.persistence.repository.HistoricalPlaceRepository;
import com.agors.historiography.persistence.repository.ReviewRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.List;

public class ReviewsAndRatingsUI {

    private final HistoricalPlaceRepository repository;
    private final Screen screen;
    private final ReviewRepository reviewRepository;
    private Runnable onExitCallback;

    public ReviewsAndRatingsUI(HistoricalPlaceRepository repository, Screen screen,
        ReviewRepository reviewRepository) {
        this.repository = repository;
        this.screen = screen;
        this.reviewRepository = reviewRepository;
    }

    public void setOnExitCallback(Runnable onExitCallback) {
        this.onExitCallback = onExitCallback;
    }

    public void show() throws IOException {
        String[] options = {
            "Переглянути відгуки",
            "Додати відгук",
            "Повернутися назад"
        };

        int selectedIndex = 0;

        while (true) {
            screen.clear();
            TextGraphics textGraphics = screen.newTextGraphics();
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(10, 2, "Меню відгуків та рейтингів");

            for (int i = 0; i < options.length; i++) {
                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    textGraphics.putString(8, 5 + i, "▶ " + options[i]);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    textGraphics.putString(10, 5 + i, options[i]);
                }
            }

            screen.refresh();
            KeyStroke keyStroke = screen.readInput();

            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    selectedIndex = (selectedIndex + 1) % options.length;
                    break;
                case ArrowUp:
                    selectedIndex = (selectedIndex - 1 + options.length) % options.length;
                    break;
                case Enter:
                    switch (selectedIndex) {
                        case 0:
                            viewReviews();
                            break;
                        case 1:
                            addReview();
                            break;
                        case 2:
                            if (onExitCallback != null) {
                                onExitCallback.run();
                            }
                            return;
                    }
                    break;
                case Escape:
                    if (onExitCallback != null) {
                        onExitCallback.run();
                    }
                    return;
            }
        }
    }

    private void viewReviews() throws IOException {
        List<Review> reviews = reviewRepository.getReviews();
        int selectedIndex = 0;
        int pageStartIndex = 0;
        final int REVIEWS_PER_PAGE = 5; // Кількість відгуків на одній сторінці

        if (reviews.isEmpty()) {
            screen.clear();
            TextGraphics textGraphics = screen.newTextGraphics();
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 5, "Немає відгуків.");
            screen.refresh();
            screen.readInput();
            return;
        }

        while (true) {
            screen.clear();
            TextGraphics textGraphics = screen.newTextGraphics();
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(5, 2, "Перегляд відгуків");

            int yPosition = 5;
            int pageEndIndex = Math.min(pageStartIndex + REVIEWS_PER_PAGE, reviews.size());

            for (int i = pageStartIndex; i < pageEndIndex; i++) {
                Review review = reviews.get(i);

                textGraphics.setForegroundColor(
                    i == selectedIndex ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);

                // Виведення назви місця
                textGraphics.putString(10, yPosition, "Місце: " + review.getPlaceName());
                // Виведення рейтингу
                textGraphics.putString(50, yPosition, "Рейтинг: " + review.getRating());

                yPosition++;
                // Виведення тексту відгуку
                textGraphics.putString(10, yPosition, "Відгук: " + review.getText());
                yPosition += 2;
            }

            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(10, REVIEWS_PER_PAGE * 2 + 11, "↑ Вгору   ↓ Вниз   Esc - Вихід");
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();

            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    if (selectedIndex < reviews.size() - 1) {
                        selectedIndex++;
                        if (selectedIndex >= pageStartIndex + REVIEWS_PER_PAGE) {
                            pageStartIndex++;
                        }
                    }
                    break;

                case ArrowUp:
                    if (selectedIndex > 0) {
                        selectedIndex--;
                        if (selectedIndex < pageStartIndex) {
                            pageStartIndex--;
                        }
                    }
                    break;

                case Escape:
                    return;
            }
        }
    }

    private void addReview() throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();

        List<HistoricalPlace> places = repository.getHistoricalPlaces();
        if (places.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 5, "Немає історичних місць для відгуків!");
            screen.refresh();
            screen.readInput();
            return;
        }

        StringBuilder searchQuery = new StringBuilder();
        int selectedIndex = 0;
        int startIndex = 0;
        int maxVisiblePlaces = 13;  // Кількість елементів, які відображаються на екрані

        while (true) {
            screen.clear();
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(10, 2, "Введіть назву історичного місця для пошуку:");

            textGraphics.putString(10, 4, "Пошук: " + searchQuery);

            List<HistoricalPlace> filteredPlaces = filterPlacesByQuery(places,
                searchQuery.toString());

            if (filteredPlaces.isEmpty()) {
                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                textGraphics.putString(10, 6, "Нічого не знайдено.");
            } else {
                // Прокрутка списку знайдених місць
                int endIndex = Math.min(filteredPlaces.size(), startIndex + maxVisiblePlaces);
                for (int i = startIndex; i < endIndex; i++) {
                    if (i == selectedIndex) {
                        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                        textGraphics.putString(8, 6 + (i - startIndex),
                            "▶ " + filteredPlaces.get(i).getName());
                    } else {
                        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                        textGraphics.putString(10, 6 + (i - startIndex),
                            filteredPlaces.get(i).getName());
                    }
                }
            }

            // Додаємо інструкції для управління
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(10, maxVisiblePlaces + 7,
                "↑ Вгору   ↓ Вниз   Enter - Переглянути   Esc - Вихід");

            screen.refresh();
            KeyStroke keyStroke = screen.readInput();

            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    // Прокрутка вниз
                    if (selectedIndex < filteredPlaces.size() - 1) {
                        selectedIndex++;
                    }
                    if (selectedIndex >= startIndex + maxVisiblePlaces) {
                        startIndex++; // Прокручувати вниз список
                    }
                    break;
                case ArrowUp:
                    // Прокрутка вгору
                    if (selectedIndex > 0) {
                        selectedIndex--;
                    }
                    if (selectedIndex < startIndex) {
                        startIndex--; // Прокручувати вверх список
                    }
                    break;
                case Enter:
                    if (!filteredPlaces.isEmpty()) {
                        HistoricalPlace selectedPlace = filteredPlaces.get(selectedIndex);
                        enterReview(selectedPlace);
                        return;
                    }
                    break;
                case Escape:
                    return;
                case Backspace:
                    // Видалення символу з пошукового запиту
                    if (searchQuery.length() > 0) {
                        searchQuery.deleteCharAt(searchQuery.length() - 1);
                    }
                    break;
                case Character:
                    if (keyStroke.getCharacter() != null) {
                        searchQuery.append(keyStroke.getCharacter());
                    }
                    break;
            }
        }
    }

    private List<HistoricalPlace> filterPlacesByQuery(List<HistoricalPlace> places, String query) {
        return places.stream()
            .filter(place -> place.getName().toLowerCase().contains(query.toLowerCase()))
            .toList();
    }

    private void enterReview(HistoricalPlace selectedPlace) throws IOException {
        screen.clear();  // Очистка екрану перед малюванням нового контенту
        TextGraphics textGraphics = screen.newTextGraphics();

        // Назва місця
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(10, 2, "Відгук для: " + selectedPlace.getName());

        // Введення тексту відгуку
        StringBuilder reviewText = new StringBuilder();
        int rating = -1;
        int selectedField = 0;  // Індекс для переміщення між полями
        boolean exit = false;  // Флаг для виходу з циклу

        while (!exit) {
            screen.clear();  // Очищаємо екран перед відображенням нової інформації
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(10, 2, "Відгук для: " + selectedPlace.getName());

            // Підсвітка рядка для введення тексту відгуку
            textGraphics.setForegroundColor(
                selectedField == 0 ? TextColor.ANSI.GREEN : TextColor.ANSI.YELLOW);
            textGraphics.putString(10, 4, "Введіть текст відгуку:");

            // Підсвітка текстового поля для відгуку
            textGraphics.setForegroundColor(
                selectedField == 0 ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
            textGraphics.putString(10, 6, reviewText.toString());  // Виводимо текст відгуку

            // Підсвітка рядка для введення оцінки
            textGraphics.setForegroundColor(
                selectedField == 1 ? TextColor.ANSI.GREEN : TextColor.ANSI.YELLOW);
            textGraphics.putString(10, 10, "Введіть оцінку від 0 до 9:");

            // Підсвітка текстового поля для оцінки
            textGraphics.setForegroundColor(
                selectedField == 1 ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
            textGraphics.putString(10, 12, rating == -1 ? "" : Integer.toString(rating));

            // Кнопки "Залишити відгук" та "Назад"
            drawButton(textGraphics, 10, 14, "Залишити відгук", selectedField == 2);
            drawButton(textGraphics, 10, 16, "Назад", selectedField == 3);

            screen.refresh();  // Оновлення екрану

            KeyStroke keyStroke = screen.readInput();

            // Переміщення між полями
            if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                selectedField = (selectedField + 1) % 4;
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                selectedField = (selectedField - 1 + 4) % 4;
            }

            // Обробка вводу для тексту відгуку
            if (selectedField == 0) {
                if (keyStroke.getKeyType() == KeyType.Backspace && reviewText.length() > 0) {
                    reviewText.deleteCharAt(
                        reviewText.length() - 1);  // Видалення останнього символу
                } else if (keyStroke.getCharacter() != null) {
                    char c = keyStroke.getCharacter();
                    if (!Character.isISOControl(c) && reviewText.length() < 50) {
                        reviewText.append(c);  // Додаємо символ до тексту відгуку
                    }
                }
            }

            // Обробка вводу для оцінки
            if (selectedField == 1) {
                if (keyStroke.getKeyType() == KeyType.Backspace) {
                    rating = -1;  // Видалення оцінки
                } else if (keyStroke.getCharacter() != null && Character.isDigit(
                    keyStroke.getCharacter())) {
                    int tempRating = Character.getNumericValue(keyStroke.getCharacter());
                    if (tempRating >= 0 && tempRating <= 9) {
                        rating = tempRating;  // Збереження коректної оцінки
                    }
                }
            }

            // Обробка натискання Enter
            if (keyStroke.getKeyType() == KeyType.Enter) {
                if (selectedField == 2) {  // Кнопка "Залишити відгук"
                    if (!reviewText.toString().isEmpty() && rating != -1) {
                        // Вводимо додатковий параметр userName (можна замінити на фактичне ім'я користувача)
                        String userName = "anonymous";  // Замініть на реальне ім'я користувача, якщо потрібно
                        reviewRepository.addReview(selectedPlace.getName(), reviewText.toString(),
                            rating, userName);
                        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                        textGraphics.putString(10, 18, "Відгук додано!");
                        screen.refresh();
                        screen.readInput();
                        exit = true;
                    } else {
                        textGraphics.setForegroundColor(TextColor.ANSI.RED);
                        textGraphics.putString(10, 18, "Будь ласка, введіть відгук і оцінку.");
                    }
                } else if (selectedField == 3) {  // Кнопка "Назад"
                    exit = true;  // Виходимо з циклу
                }
            }

            // Обробка натискання клавіші ESC
            if (keyStroke.getKeyType() == KeyType.Escape) {
                exit = true;  // Виходимо з циклу
            }
        }
    }

    private void drawButton(TextGraphics textGraphics, int x, int y, String label,
        boolean isSelected) {
        // Малюємо кнопку
        textGraphics.setForegroundColor(isSelected ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
        textGraphics.putString(x, y, "[ " + label + " ]");
    }
}
