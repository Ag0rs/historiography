package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.HistoricalPlace;
import com.agors.historiography.domain.validations.HistoricalPlaceValidator;
import com.agors.historiography.persistence.repository.HistoricalPlaceRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;

public class AddHistoricalPlaceUI {

    private final HistoricalPlaceRepository repository;
    private final Screen screen;

    public AddHistoricalPlaceUI(HistoricalPlaceRepository repository, Screen screen) {
        this.repository = repository;
        this.screen = screen;
    }

    public void show() throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();

        String[] fields = {"Назва", "Опис", "Локація", "Категорія"};
        String[] inputs = {"", "", "", ""};
        int selectedFieldIndex = 0;

        String[] buttons = {"Зберегти", "Вийти"};

        final int MAX_LINE_WIDTH = 55;
        final int VERTICAL_OFFSET = 5;
        final int MAX_INPUT_LENGTH = 165;

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

                // Зменшуємо yOffset на 1
                int yOffset = 2 + i * VERTICAL_OFFSET; // Зміщення на один рядок вище
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
                                "Будь ласка, заповніть всі поля!"); // Перемістив вище
                            screen.refresh(); // Оновлюємо екран

                            // Очікуємо будь-яке натискання клавіші перед очищенням повідомлення
                            screen.readInput();
                        } else {
                            int id = repository.getHistoricalPlaces().size() + 1;
                            HistoricalPlace place = new HistoricalPlace(id, inputs[0], inputs[1],
                                inputs[2], inputs[3]);
                            repository.addHistoricalPlace(place);
                            textGraphics.putString(25, 22, "Місце успішно додано!");
                            screen.refresh();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
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
}
