package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.HistoricalPlace;
import com.agors.historiography.persistence.repository.HistoricalPlaceRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Клас для відображення та керування списком історичних місць. Зокрема, дозволяє переглядати
 * історичні місця, виконувати пошук, переглядати деталі, а також переміщатися по сторінках списку.
 */
public class ViewHistoricalPlacesUI {

    private static final int PLACES_PER_PAGE = 10; // Кількість місць на сторінці
    private final HistoricalPlaceRepository repository; // Репозиторій для отримання історичних місць
    private final Screen screen; // Екран для відображення інтерфейсу

    /**
     * Конструктор для ініціалізації інтерфейсу для перегляду історичних місць.
     *
     * @param repository Репозиторій для роботи з історичними місцями
     * @param screen     Екран для відображення інтерфейсу
     */
    public ViewHistoricalPlacesUI(HistoricalPlaceRepository repository, Screen screen) {
        this.repository = repository;
        this.screen = screen;
    }

    /**
     * Метод для відображення списку історичних місць. Використовує пейджинг для перегляду місць, а
     * також надає можливість вибору місця для перегляду деталей.
     *
     * @throws IOException Якщо виникла помилка при взаємодії з екраном
     */
    public void show() throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();

        List<HistoricalPlace> places = repository.getHistoricalPlaces();

        // Якщо місць немає, відображається відповідне повідомлення
        if (places.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 5, "Немає доданих історичних місць!");
            screen.refresh();
            screen.readInput();
            return;
        }

        int selectedIndex = 0; // Індекс вибраного місця
        int pageStartIndex = 0; // Початковий індекс для сторінки

        // Основний цикл для навігації по списку місць
        while (true) {
            screen.clear();
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(10, 2, "Список історичних місць:");

            int pageEndIndex = Math.min(pageStartIndex + PLACES_PER_PAGE, places.size());

            // Виведення місць на екран
            for (int i = pageStartIndex; i < pageEndIndex; i++) {
                HistoricalPlace place = places.get(i);

                // Якщо місце вибране, його колір змінюється на зелений
                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }

                textGraphics.putString(10, 4 + (i - pageStartIndex),
                    (i + 1) + ". " + place.getName());
            }

            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(10, PLACES_PER_PAGE + 6,
                "↑ Вгору   ↓ Вниз   Enter - Переглянути   Esc - Вихід   s/і - Пошук");

            screen.refresh();
            KeyStroke keyStroke = screen.readInput();

            switch (keyStroke.getKeyType()) {
                case ArrowUp:
                    if (selectedIndex > 0) {
                        selectedIndex--;
                    } else if (pageStartIndex > 0) {
                        selectedIndex = pageStartIndex;
                        pageStartIndex--;
                    }
                    break;

                case ArrowDown:
                    if (selectedIndex < places.size() - 1) {
                        selectedIndex++;
                    } else if (pageStartIndex + PLACES_PER_PAGE < places.size()) {
                        selectedIndex = pageStartIndex + PLACES_PER_PAGE - 1;
                        pageStartIndex++;
                    }
                    break;

                case Enter:
                    showPlaceDetails(places.get(selectedIndex)); // Перегляд деталей місця
                    break;

                case Escape:
                    return;

                case Character:
                    if (keyStroke.getCharacter() == 'с' || keyStroke.getCharacter() == 'і'
                        || keyStroke.getCharacter() == 's') {
                        searchPlaces(places); // Виконати пошук місць
                    }
                    break;
            }

            // Переміщення між сторінками, якщо треба
            if (selectedIndex >= pageStartIndex + PLACES_PER_PAGE - 1
                && pageStartIndex + PLACES_PER_PAGE < places.size()) {
                pageStartIndex++;
            }
            if (selectedIndex <= pageStartIndex && pageStartIndex > 0) {
                pageStartIndex--;
            }
        }
    }

    /**
     * Метод для виконання пошуку історичних місць за назвою або категорією.
     *
     * @param places Список усіх історичних місць
     * @throws IOException Якщо виникла помилка при взаємодії з екраном
     */
    private void searchPlaces(List<HistoricalPlace> places) throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();

        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(10, 1, "Пошук історичних місць:");
        textGraphics.setForegroundColor(ANSI.YELLOW);
        textGraphics.putString(10, 2, "Введіть запит для пошуку (назва чи категорія):");

        StringBuilder searchQuery = new StringBuilder();
        int selectedIndex = 0;
        int pageStartIndex = 0;

        // Цикл пошуку з можливістю перегляду результатів
        while (true) {
            screen.clear();
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(10, 1, "Пошук історичних місць:");
            textGraphics.putString(10, 2, "Введіть запит для пошуку:");

            textGraphics.setForegroundColor(ANSI.WHITE);
            textGraphics.putString(10, 4, "Поточний запит: " + searchQuery);

            List<HistoricalPlace> filteredPlaces = places.stream()
                .filter(place -> place.getName().toLowerCase()
                    .contains(searchQuery.toString().toLowerCase()) ||
                    place.getCategory().toLowerCase()
                        .contains(searchQuery.toString().toLowerCase()))
                .collect(Collectors.toList());

            int pageEndIndex = Math.min(pageStartIndex + PLACES_PER_PAGE, filteredPlaces.size());
            for (int i = pageStartIndex; i < pageEndIndex; i++) {
                HistoricalPlace place = filteredPlaces.get(i);

                // Якщо місце вибране, його колір змінюється на зелений
                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }

                textGraphics.putString(10, 6 + (i - pageStartIndex),
                    (i + 1) + ". " + place.getName() + " (" + place.getCategory() + ")");
            }

            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(10, PLACES_PER_PAGE + 8,
                "↑ Вгору   ↓ Вниз   Enter - Вибрати   Esc - Вихід");
            textGraphics.putString(10, PLACES_PER_PAGE + 9, "Backspace - Видалити символ");

            screen.refresh();
            KeyStroke keyStroke = screen.readInput();

            switch (keyStroke.getKeyType()) {
                case ArrowUp:
                    if (selectedIndex > 0) {
                        selectedIndex--;
                    } else if (pageStartIndex > 0) {
                        selectedIndex = pageStartIndex;
                        pageStartIndex--;
                    }
                    break;

                case ArrowDown:
                    if (selectedIndex < filteredPlaces.size() - 1) {
                        selectedIndex++;
                    } else if (pageStartIndex + PLACES_PER_PAGE < filteredPlaces.size()) {
                        selectedIndex = pageStartIndex + PLACES_PER_PAGE - 1;
                        pageStartIndex++;
                    }
                    break;

                case Enter:
                    if (!filteredPlaces.isEmpty()) {
                        showPlaceDetails(
                            filteredPlaces.get(selectedIndex)); // Перегляд деталей місця
                    }
                    break;

                case Escape:
                    return;

                case Backspace:
                    if (searchQuery.length() > 0) {
                        searchQuery.deleteCharAt(
                            searchQuery.length() - 1); // Видалити останній символ
                    }
                    break;

                default:
                    if (keyStroke.getCharacter() != null) {
                        searchQuery.append(keyStroke.getCharacter());
                    }
            }

            // Переміщення між сторінками під час пошуку
            if (selectedIndex >= pageStartIndex + PLACES_PER_PAGE - 1
                && pageStartIndex + PLACES_PER_PAGE < filteredPlaces.size()) {
                pageStartIndex++;
            }
            if (selectedIndex < pageStartIndex && pageStartIndex > 0) {
                pageStartIndex--;
            }
        }
    }

    /**
     * Метод для відображення деталей обраного історичного місця.
     *
     * @param place Історичне місце для перегляду деталей
     * @throws IOException Якщо виникла помилка при взаємодії з екраном
     */
    private void showPlaceDetails(HistoricalPlace place) throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();

        String[] fields = {"Назва", "Опис", "Локація", "Категорія"};
        String[] values = {place.getName(), place.getDescription(), place.getLocation(),
            place.getCategory()};

        final int MAX_LINE_WIDTH = 55;
        final int VERTICAL_OFFSET = 5;

        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(10, 1, "Детальна інформація");
        textGraphics.setForegroundColor(ANSI.YELLOW);
        textGraphics.putString(10, 2, "Натисніть будь-яку клавішу для повернення...");

        for (int i = 0; i < fields.length; i++) {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(9, 3 + i * VERTICAL_OFFSET,
                "┌─────────────────────────────────────────────────────────────────┐");
            textGraphics.putString(9, 4 + i * VERTICAL_OFFSET,
                "│                                                                 │");
            textGraphics.putString(9, 5 + i * VERTICAL_OFFSET,
                "│                                                                 │");
            textGraphics.putString(9, 6 + i * VERTICAL_OFFSET,
                "│                                                                 │");
            textGraphics.putString(9, 7 + i * VERTICAL_OFFSET,
                "└─────────────────────────────────────────────────────────────────┘");

            textGraphics.setForegroundColor(ANSI.WHITE);
            textGraphics.putString(10, 4 + i * VERTICAL_OFFSET, fields[i] + ": ");

            String value = values[i];
            int yOffset = 3 + i * VERTICAL_OFFSET + 1;
            for (int j = 0; j < value.length(); j += MAX_LINE_WIDTH) {
                int endIndex = Math.min(j + MAX_LINE_WIDTH, value.length());
                textGraphics.putString(20, yOffset, value.substring(j, endIndex));
                yOffset++;
            }
        }

        screen.refresh();
        screen.readInput();
    }
}
