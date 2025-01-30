package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.HistoricalPlace;
import com.agors.historiography.domain.validations.HistoricalPlaceValidator;
import com.agors.historiography.persistence.repository.HistoricalPlaceRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.List;

/**
 * Клас, який відповідає за інтерфейс редагування історичних місць. Реалізує функціональність
 * перегляду, редагування та видалення історичних місць.
 */
public class EditHistoricalPlaceUI {

    private static final int MAX_VISIBLE_PLACES = 10;
    private final HistoricalPlaceRepository historicalPlaceRepository;
    private final Screen screen;
    private final MenuHandler menuHandler;

    /**
     * Конструктор класу.
     *
     * @param historicalPlaceRepository репозиторій для роботи з історичними місцями
     * @param screen                    екран для відображення інтерфейсу
     * @param menuHandler               обробник меню
     */
    public EditHistoricalPlaceUI(HistoricalPlaceRepository historicalPlaceRepository,
        Screen screen,
        MenuHandler menuHandler) {
        this.historicalPlaceRepository = historicalPlaceRepository;
        this.screen = screen;
        this.menuHandler = menuHandler;
    }

    /**
     * Показує список історичних місць для редагування. Дозволяє вибирати місце для редагування чи
     * видалення.
     *
     * @throws IOException у разі проблем з відображенням на екрані
     */
    public void show() throws IOException {
        List<HistoricalPlace> places = historicalPlaceRepository.getHistoricalPlaces();

        if (places.isEmpty()) {
            showNoPlacesMessage();
            return;
        }

        int selectedIndex = 0;
        int startIndex = 0;

        while (true) {
            screen.clear();
            TextGraphics textGraphics = screen.newTextGraphics();
            displayPlaces(places, selectedIndex, startIndex, textGraphics);
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            KeyType keyType = keyStroke.getKeyType();

            switch (keyType) {
                case ArrowDown:
                    if (selectedIndex < places.size() - 1) {
                        selectedIndex++;
                    }
                    if (selectedIndex >= startIndex + MAX_VISIBLE_PLACES) {
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
                    HistoricalPlace selectedPlace = places.get(selectedIndex);
                    showEditMenu(selectedPlace);
                    return;
                case Escape:
                    return;
            }
        }
    }

    /**
     * Відображає список історичних місць на екрані.
     *
     * @param places        список історичних місць
     * @param selectedIndex індекс вибраного місця
     * @param startIndex    індекс початку видимого списку
     * @param textGraphics  об'єкт для малювання на екрані
     */
    private void displayPlaces(List<HistoricalPlace> places, int selectedIndex, int startIndex,
        TextGraphics textGraphics) {
        int endIndex = Math.min(places.size(),
            startIndex + MAX_VISIBLE_PLACES);

        // Відображення історичних місць
        for (int i = startIndex; i < endIndex; i++) {
            int displayIndex = i - startIndex;
            if (i == selectedIndex) {
                textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                textGraphics.putString(10, 5 + displayIndex, "▶ " + places.get(i).getName());
            } else {
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.putString(10, 5 + displayIndex, places.get(i).getName());
            }
        }

        textGraphics.setForegroundColor(ANSI.YELLOW);
        textGraphics.putString(10, MAX_VISIBLE_PLACES + 7,
            "↑ Вгору   ↓ Вниз   Enter - Переглянути   Esc - Вихід");
    }

    /**
     * Відображає повідомлення, що немає доступних історичних місць для редагування.
     *
     * @throws IOException у разі проблем з відображенням на екрані
     */
    private void showNoPlacesMessage() throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.putString(10, 5, "Немає доступних історичних місць для редагування.");
        screen.refresh();
        screen.readInput();
    }

    /**
     * Показує меню редагування для вибраного історичного місця.
     *
     * @param place історичне місце, яке редагується
     * @throws IOException у разі проблем з відображенням на екрані
     */
    private void showEditMenu(HistoricalPlace place) throws IOException {
        String[] options = {"Змінити", "Видалити", "Назад"};
        int selectedOption = 0;

        TextGraphics textGraphics = screen.newTextGraphics();
        while (true) {
            screen.clear();
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(10, 2, "Редагування історичного місця: " + place.getName());

            for (int i = 0; i < options.length; i++) {
                if (i == selectedOption) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    textGraphics.putString(8, 5 + i, "▶ " + options[i]);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    textGraphics.putString(10, 5 + i, options[i]);
                }
            }
            screen.refresh();
            KeyStroke keyStroke = screen.readInput();
            KeyType keyType = keyStroke.getKeyType();

            switch (keyType) {
                case ArrowDown:
                    selectedOption = (selectedOption + 1) % options.length;
                    break;
                case ArrowUp:
                    selectedOption = (selectedOption - 1 + options.length) % options.length;
                    break;
                case Enter:
                    if (selectedOption == 0) {
                        editPlaceDetails(place);
                    } else if (selectedOption == 1) {
                        deleteHistoricalPlace(place);
                    } else if (selectedOption == 2) {
                        return;
                    }
                    break;
                case Escape:
                    return;
            }
        }
    }

    /**
     * Дозволяє редагувати деталі вибраного історичного місця.
     *
     * @param place історичне місце, яке редагується
     * @throws IOException у разі проблем з відображенням на екрані
     */
    private void editPlaceDetails(HistoricalPlace place) throws IOException {
        screen.clear();
        String[] fields = {"Назва", "Опис", "Локація", "Категорія"};
        String[] inputs = {place.getName(), place.getDescription(), place.getLocation(),
            place.getCategory()};

        int selectedFieldIndex = 0;
        int selectedButtonIndex = -1;
        String[] buttons = {"Змінити", "Назад"};

        final int MAX_LINE_WIDTH = 55;
        final int VERTICAL_OFFSET = 5;
        final int MAX_INPUT_LENGTH = 165;

        TextGraphics textGraphics = screen.newTextGraphics();
        while (true) {
            screen.clear();

            for (int i = 0; i < fields.length; i++) {
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.putString(9, 1 + i * VERTICAL_OFFSET,
                    "┌─────────────────────────────────────────────────────────────────┐");
                textGraphics.putString(9, 2 + i * VERTICAL_OFFSET,
                    "│                                                                 │");
                textGraphics.putString(9, 3 + i * VERTICAL_OFFSET,
                    "│                                                                 │");
                textGraphics.putString(9, 4 + i * VERTICAL_OFFSET,
                    "│                                                                 │");
                textGraphics.putString(9, 5 + i * VERTICAL_OFFSET,
                    "└─────────────────────────────────────────────────────────────────┘");

                if (i == selectedFieldIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }

                textGraphics.putString(10, 2 + i * VERTICAL_OFFSET, fields[i] + ": ");
                String inputText = inputs[i].length() > MAX_INPUT_LENGTH
                    ? inputs[i].substring(0, MAX_INPUT_LENGTH)
                    : inputs[i];

                int yOffset = 2 + i * VERTICAL_OFFSET;
                for (int j = 0; j < inputText.length(); j += MAX_LINE_WIDTH) {
                    int endIndex = Math.min(j + MAX_LINE_WIDTH, inputText.length());
                    textGraphics.putString(20, yOffset, inputText.substring(j, endIndex));
                    yOffset += 1;
                }
            }
            for (int i = 0; i < buttons.length; i++) {
                int buttonY = 21 + i;
                if (selectedFieldIndex == fields.length + i) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
                textGraphics.putString(10, buttonY, buttons[i]);
            }

            screen.refresh();
            KeyStroke keyStroke = screen.readInput();

            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    if (selectedFieldIndex < fields.length + buttons.length - 1) {
                        selectedFieldIndex++;
                    }
                    break;

                case ArrowUp:
                    if (selectedFieldIndex > 0) {
                        selectedFieldIndex--;
                    }
                    break;

                case Enter:
                    if (selectedFieldIndex == fields.length) {
                        if (!HistoricalPlaceValidator.isValid(inputs[0], inputs[1], inputs[2],
                            inputs[3])) {
                            textGraphics.setForegroundColor(TextColor.ANSI.RED);
                            textGraphics.putString(25, 22,
                                "Будь ласка, заповніть всі поля!");
                            screen.refresh();

                            screen.readInput();
                        } else {
                            place.setName(inputs[0]);
                            place.setDescription(inputs[1]);
                            place.setLocation(inputs[2]);
                            place.setCategory(inputs[3]);
                            historicalPlaceRepository.saveHistoricalPlaces();
                            return;
                        }
                    } else if (selectedFieldIndex == fields.length + 1) {
                        return;
                    }
                    break;

                case Backspace:
                    if (selectedFieldIndex >= 0 && selectedFieldIndex < 4) {
                        if (!inputs[selectedFieldIndex].isEmpty()) {
                            inputs[selectedFieldIndex] = inputs[selectedFieldIndex]
                                .substring(0, inputs[selectedFieldIndex].length() - 1);
                        }
                    }
                    break;

                default:
                    if (keyStroke.getCharacter() != null && selectedFieldIndex >= 0
                        && selectedFieldIndex < 4) {
                        if (inputs[selectedFieldIndex].length() < MAX_INPUT_LENGTH) {
                            inputs[selectedFieldIndex] += keyStroke.getCharacter();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Видаляє вибране історичне місце.
     *
     * @param place історичне місце, яке потрібно видалити
     * @throws IOException у разі проблем з відображенням на екрані
     */
    private void deleteHistoricalPlace(HistoricalPlace place) throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(10, 5, "Ви дійсно хочете видалити це історичне місце? (y/n)");

        screen.refresh();

        KeyStroke keyStroke = screen.readInput();
        Character response = keyStroke.getCharacter();

        if (response != null && (response == 'y' || response == 'Y')) {
            historicalPlaceRepository.getHistoricalPlaces().remove(place);
            historicalPlaceRepository.saveHistoricalPlaces();

            screen.clear();
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 5,
                "Історичне місце '" + place.getName() + "' було успішно видалено.");
            screen.refresh();

            screen.readInput();
        } else {
            return;
        }
        menuHandler.showAdminMenu();
    }
}
