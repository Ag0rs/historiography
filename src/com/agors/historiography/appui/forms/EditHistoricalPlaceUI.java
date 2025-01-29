package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.HistoricalPlace;
import com.agors.historiography.domain.validations.HistoricalPlaceValidator;
import com.agors.historiography.persistence.repository.HistoricalPlaceRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.List;

public class EditHistoricalPlaceUI {

    private final HistoricalPlaceRepository historicalPlaceRepository;
    private final Screen screen;
    private final MenuHandler menuHandler;

    public EditHistoricalPlaceUI(HistoricalPlaceRepository historicalPlaceRepository,
        Screen screen,
        MenuHandler menuHandler) {
        this.historicalPlaceRepository = historicalPlaceRepository;
        this.screen = screen;
        this.menuHandler = menuHandler;
    }

    public void show() throws IOException {
        List<HistoricalPlace> places = historicalPlaceRepository.getHistoricalPlaces();

        if (places.isEmpty()) {
            showNoPlacesMessage();
            return;
        }

        int selectedIndex = 0;
        while (true) {
            screen.clear();
            TextGraphics textGraphics = screen.newTextGraphics();
            displayPlaces(places, selectedIndex, textGraphics);
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            KeyType keyType = keyStroke.getKeyType(); // Correctly get the KeyType

            switch (keyType) {
                case ArrowDown:
                    selectedIndex = (selectedIndex + 1) % places.size();
                    break;
                case ArrowUp:
                    selectedIndex = (selectedIndex - 1 + places.size()) % places.size();
                    break;
                case Enter:
                    HistoricalPlace selectedPlace = places.get(selectedIndex);
                    showEditMenu(selectedPlace);
                    return;
                case Escape:
                    return; // Назад до попереднього меню
            }
        }
    }

    private void displayPlaces(List<HistoricalPlace> places, int selectedIndex,
        TextGraphics textGraphics) {
        for (int i = 0; i < places.size(); i++) {
            if (i == selectedIndex) {
                textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                textGraphics.putString(10, 5 + i, "▶ " + places.get(i).getName());
            } else {
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.putString(10, 5 + i, places.get(i).getName());
            }
        }
    }

    private void showNoPlacesMessage() throws IOException {
        screen.clear();  // Очищаємо екран перед виведенням повідомлення
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.putString(10, 5, "Немає доступних історичних місць для редагування.");
        screen.refresh();
        screen.readInput();
    }

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
            KeyType keyType = keyStroke.getKeyType(); // Correctly get the KeyType

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
                        return; // Назад
                    }
                    break;
                case Escape:
                    return; // Назад до попереднього меню
            }
        }
    }

    private void editPlaceDetails(HistoricalPlace place) throws IOException {
        screen.clear();
        String[] fields = {"Назва", "Опис", "Локація", "Категорія"};
        String[] inputs = {place.getName(), place.getDescription(), place.getLocation(),
            place.getCategory()};

        int selectedFieldIndex = 0;
        int selectedButtonIndex = -1;  // -1, якщо кнопки не вибрані
        String[] buttons = {"Змінити", "Назад"};

        final int MAX_LINE_WIDTH = 55;
        final int VERTICAL_OFFSET = 5;
        final int MAX_INPUT_LENGTH = 165;

        TextGraphics textGraphics = screen.newTextGraphics();
        while (true) {
            screen.clear();

            // Відображення полів для редагування
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

                int yOffset = 2 + i * VERTICAL_OFFSET; // Зміщення на один рядок вище
                for (int j = 0; j < inputText.length(); j += MAX_LINE_WIDTH) {
                    int endIndex = Math.min(j + MAX_LINE_WIDTH, inputText.length());
                    textGraphics.putString(20, yOffset, inputText.substring(j, endIndex));
                    yOffset += 1;
                }
            }

            // Відображення кнопок "Змінити" та "Назад"
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
                        // Перевірка на порожні поля
                        if (!HistoricalPlaceValidator.isValid(inputs[0], inputs[1], inputs[2],
                            inputs[3])) {
                            textGraphics.setForegroundColor(TextColor.ANSI.RED);
                            textGraphics.putString(25, 22,
                                "Будь ласка, заповніть всі поля!"); // Перемістив вище
                            screen.refresh(); // Оновлюємо екран

                            // Очікуємо будь-яке натискання клавіші перед очищенням повідомлення
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

    private void deleteHistoricalPlace(HistoricalPlace place) throws IOException {
        // Видалення історичного місця зі сховища
        historicalPlaceRepository.getHistoricalPlaces().remove(place);
        historicalPlaceRepository.saveHistoricalPlaces();

        // Відображення повідомлення про успішне видалення
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.putString(10, 5,
            "Історичне місце '" + place.getName() + "' було успішно видалено.");
        screen.refresh();

        // Очікування натискання будь-якої клавіші перед поверненням
        screen.readInput();

        // Повернення до адміністративного меню
        menuHandler.showAdminMenu(); // викликаємо showAdminMenu після видалення
    }

}
