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
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(10, 2, "Перегляд відгуків");

        List<Review> reviews = reviewRepository.getReviews();

        if (reviews.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 4, "Немає відгуків.");
        } else {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            int y = 4;
            for (Review review : reviews) {
                String reviewText = String.format(
                    "%s (Рейтинг: %d) - %s",
                    review.getPlaceName(),
                    review.getRating(),
                    review.getText()
                );
                textGraphics.putString(10, y++, reviewText);
            }
        }

        screen.refresh();
        screen.readInput();
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

            // Введення тексту відгуку
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(10, 4, "Введіть текст відгуку:");

            // Підсвітка текстового поля для відгуку
            textGraphics.setForegroundColor(
                selectedField == 0 ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
            textGraphics.putString(10, 6, reviewText.toString());  // Виводимо текст відгуку

            // Введення оцінки
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
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
                if (keyStroke.getCharacter() != null) {
                    char c = keyStroke.getCharacter();
                    if (!Character.isISOControl(c) && reviewText.length() < 50) {
                        reviewText.append(c);  // Додаємо символ до тексту відгуку
                    }
                } else if (keyStroke.getKeyType() == KeyType.Backspace) {
                    // Перевірка наявності символів для видалення
                    if (reviewText.length() > 0) {
                        reviewText.deleteCharAt(
                            reviewText.length() - 1);  // Видаляємо останній символ
                    }
                }
            }

            // Обробка вводу для оцінки
            if (selectedField == 1) {
                if (keyStroke.getCharacter() != null && Character.isDigit(
                    keyStroke.getCharacter())) {
                    rating = Character.getNumericValue(keyStroke.getCharacter());
                    if (rating < 0 || rating > 9) {
                        rating = -1;  // Якщо оцінка не в межах допустимих значень
                    }
                } else if (keyStroke.getKeyType() == KeyType.Backspace && rating != -1) {
                    rating = -1;  // Скидаємо оцінку
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
