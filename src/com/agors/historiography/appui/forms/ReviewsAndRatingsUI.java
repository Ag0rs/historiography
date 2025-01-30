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

/**
 * Клас для відображення інтерфейсу користувача для перегляду та додавання відгуків та рейтингів для
 * історичних місць.
 */
public class ReviewsAndRatingsUI {

    private final HistoricalPlaceRepository repository;
    private final Screen screen;
    private final ReviewRepository reviewRepository;
    private Runnable onExitCallback;

    /**
     * Конструктор класу.
     *
     * @param repository       Репозиторій історичних місць.
     * @param screen           Екран для відображення інтерфейсу.
     * @param reviewRepository Репозиторій відгуків.
     */
    public ReviewsAndRatingsUI(HistoricalPlaceRepository repository, Screen screen,
        ReviewRepository reviewRepository) {
        this.repository = repository;
        this.screen = screen;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Встановлює функцію зворотного виклику для виходу з інтерфейсу.
     *
     * @param onExitCallback Функція, що викликається при виході.
     */
    public void setOnExitCallback(Runnable onExitCallback) {
        this.onExitCallback = onExitCallback;
    }

    /**
     * Відображає головне меню відгуків та рейтингів. Меню дозволяє переглядати відгуки, додавати
     * новий відгук або повернутися назад.
     *
     * @throws IOException Якщо виникає помилка при взаємодії з екраном.
     */
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

    /**
     * Відображає список відгуків для історичних місць.
     *
     * @throws IOException Якщо виникає помилка при взаємодії з екраном.
     */
    private void viewReviews() throws IOException {
        List<Review> reviews = reviewRepository.getReviews();
        int selectedIndex = 0;
        int pageStartIndex = 0;
        final int REVIEWS_PER_PAGE = 5;

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

                textGraphics.putString(10, yPosition, "Місце: " + review.getPlaceName());
                textGraphics.putString(50, yPosition, "Рейтинг: " + review.getRating());

                yPosition++;
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

    /**
     * Дозволяє додати новий відгук для історичного місця.
     *
     * @throws IOException Якщо виникає помилка при взаємодії з екраном.
     */
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
        int maxVisiblePlaces = 13;

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

            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(10, maxVisiblePlaces + 7,
                "↑ Вгору   ↓ Вниз   Enter - Переглянути   Esc - Вихід");

            screen.refresh();
            KeyStroke keyStroke = screen.readInput();

            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    if (selectedIndex < filteredPlaces.size() - 1) {
                        selectedIndex++;
                    }
                    if (selectedIndex >= startIndex + maxVisiblePlaces) {
                        startIndex++;
                    }
                    break;
                case ArrowUp:
                    if (selectedIndex > 0) {
                        selectedIndex--;
                    }
                    if (selectedIndex < startIndex) {
                        startIndex--;
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

    /**
     * Фільтрує історичні місця за запитом пошуку.
     *
     * @param places Список всіх історичних місць.
     * @param query  Запит для фільтрації.
     * @return Список відфільтрованих історичних місць.
     */
    private List<HistoricalPlace> filterPlacesByQuery(List<HistoricalPlace> places, String query) {
        return places.stream()
            .filter(place -> place.getName().toLowerCase().contains(query.toLowerCase()))
            .toList();
    }

    /**
     * Вводить відгук для вибраного історичного місця.
     *
     * @param selectedPlace Вибране історичне місце.
     * @throws IOException Якщо виникає помилка при взаємодії з екраном.
     */
    private void enterReview(HistoricalPlace selectedPlace) throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();

        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(10, 2, "Відгук для: " + selectedPlace.getName());

        StringBuilder reviewText = new StringBuilder();
        int rating = -1;
        int selectedField = 0;
        boolean exit = false;

        while (!exit) {
            screen.clear();
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(10, 2, "Відгук для: " + selectedPlace.getName());

            textGraphics.setForegroundColor(
                selectedField == 0 ? TextColor.ANSI.GREEN : TextColor.ANSI.YELLOW);
            textGraphics.putString(10, 4, "Введіть текст відгуку:");

            textGraphics.setForegroundColor(
                selectedField == 0 ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
            textGraphics.putString(10, 6, reviewText.toString());

            textGraphics.setForegroundColor(
                selectedField == 1 ? TextColor.ANSI.GREEN : TextColor.ANSI.YELLOW);
            textGraphics.putString(10, 10, "Введіть оцінку від 0 до 9:");

            textGraphics.setForegroundColor(
                selectedField == 1 ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
            textGraphics.putString(10, 12, rating == -1 ? "" : Integer.toString(rating));

            drawButton(textGraphics, 10, 14, "Залишити відгук", selectedField == 2);
            drawButton(textGraphics, 10, 16, "Назад", selectedField == 3);

            screen.refresh();

            KeyStroke keyStroke = screen.readInput();

            if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                selectedField = (selectedField + 1) % 4;
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                selectedField = (selectedField - 1 + 4) % 4;
            }

            if (selectedField == 0) {
                if (keyStroke.getKeyType() == KeyType.Backspace && reviewText.length() > 0) {
                    reviewText.deleteCharAt(
                        reviewText.length() - 1);
                } else if (keyStroke.getCharacter() != null) {
                    char c = keyStroke.getCharacter();
                    if (!Character.isISOControl(c) && reviewText.length() < 50) {
                        reviewText.append(c);
                    }
                }
            }

            if (selectedField == 1) {
                if (keyStroke.getKeyType() == KeyType.Backspace) {
                    rating = -1;
                } else if (keyStroke.getCharacter() != null && Character.isDigit(
                    keyStroke.getCharacter())) {
                    int tempRating = Character.getNumericValue(keyStroke.getCharacter());
                    if (tempRating >= 0 && tempRating <= 9) {
                        rating = tempRating;
                    }
                }
            }

            if (keyStroke.getKeyType() == KeyType.Enter) {
                if (selectedField == 2) {
                    if (!reviewText.toString().isEmpty() && rating != -1) {
                        String userName = "anonymous";
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
                } else if (selectedField == 3) {
                    exit = true;
                }
            }

            if (keyStroke.getKeyType() == KeyType.Escape) {
                exit = true;
            }
        }
    }

    /**
     * Малює кнопку на екрані з заданими координатами, етикеткою та станом вибору.
     *
     * @param textGraphics Об'єкт для малювання тексту на екрані.
     * @param x            Координата X, де повинна бути розміщена кнопка.
     * @param y            Координата Y, де повинна бути розміщена кнопка.
     * @param label        Текст, що буде відображатися на кнопці.
     * @param isSelected   Булевий параметр, який вказує, чи є кнопка вибраною. Якщо кнопка вибрана,
     *                     її текст буде зеленим, в іншому випадку — білим.
     */
    private void drawButton(TextGraphics textGraphics, int x, int y, String label,
        boolean isSelected) {
        textGraphics.setForegroundColor(isSelected ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
        textGraphics.putString(x, y, "[ " + label + " ]");
    }
}