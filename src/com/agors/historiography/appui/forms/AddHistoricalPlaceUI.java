package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.HistoricalPlace;
import com.agors.historiography.domain.validations.HistoricalPlaceValidator;
import com.agors.historiography.persistence.repository.HistoricalPlaceRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;

/**
 * Клас для відображення інтерфейсу додавання історичного місця. Цей клас відповідає за відображення
 * форми для введення даних історичного місця, перевірку коректності введених даних та їх
 * збереження.
 */
public class AddHistoricalPlaceUI {

    private final HistoricalPlaceRepository repository;
    private final Screen screen;

    /**
     * Конструктор для ініціалізації об'єкта інтерфейсу.
     *
     * @param repository Репозиторій для збереження історичних місць.
     * @param screen     Об'єкт для роботи з екраном.
     */
    public AddHistoricalPlaceUI(HistoricalPlaceRepository repository, Screen screen) {
        this.repository = repository;
        this.screen = screen;
    }

    /**
     * Виводить форму для введення даних історичного місця і обробляє взаємодію з користувачем.
     * Включає перевірку введених даних і додавання нового історичного місця до репозиторію.
     *
     * @throws IOException Якщо виникає помилка при роботі з екраном.
     */
    public void show() throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();

        // Поля для введення
        String[] fields = {"Назва", "Опис", "Локація", "Категорія"};
        String[] inputs = {"", "", "", ""};
        int selectedFieldIndex = 0;

        // Кнопки
        String[] buttons = {"Зберегти", "Вийти"};

        final int MAX_LINE_WIDTH = 55;
        final int VERTICAL_OFFSET = 5;
        final int MAX_INPUT_LENGTH = 165;

        // Основний цикл взаємодії з користувачем
        while (true) {
            screen.clear();

            // Виведення полів для введення
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

            // Виведення кнопок
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

            // Обробка натискання клавіші
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
                            textGraphics.putString(25, 22, "Будь ласка, заповніть всі поля!");
                            screen.refresh();
                            screen.readInput();
                        } else {
                            int id = repository.getHistoricalPlaces().size() + 1;
                            HistoricalPlace place = new HistoricalPlace(id, inputs[0], inputs[1],
                                inputs[2], inputs[3]);
                            repository.addHistoricalPlace(place);

                            textGraphics.setForegroundColor(TextColor.ANSI.GREEN);

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

                case Escape:
                    return;

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