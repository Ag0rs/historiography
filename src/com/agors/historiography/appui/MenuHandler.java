package com.agors.historiography.appui;

import com.agors.historiography.domain.entitys.User;
import com.agors.historiography.domain.validations.Validation;
import com.agors.historiography.persistence.repository.UserRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;

public class MenuHandler {

    private final Screen screen;
    private final TextGraphics textGraphics;
    private final UserRepository userRepository;

    public MenuHandler(Screen screen, TextGraphics textGraphics, UserRepository userRepository) {
        this.screen = screen;
        this.textGraphics = textGraphics;
        this.userRepository = userRepository;
    }

    // Метод для відображення привітання
    public void showGreeting() throws IOException, InterruptedException {
        clearScreen();
        String message = "Вітаємо в програмі 'Historiography'!";
        int xPos = 10;
        int yPos = 5;

        // Виводимо текст по буквах
        for (int i = 0; i < message.length(); i++) {
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(xPos + i, yPos, String.valueOf(message.charAt(i)));
            screen.refresh();
            Thread.sleep(100); // Затримка між виведенням кожної літери
        }

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        String continueMessage = "Натисніть будь-яку клавішу для продовження...";
        textGraphics.putString(xPos, yPos + 2, continueMessage);
        screen.refresh();

        screen.readInput(); // Очікуємо натискання клавіші
    }

    // Метод для відображення головного меню
    // Метод для відображення головного меню
    // Метод для відображення головного меню
    public void showMainMenu() throws IOException {
        String[] menuOptions = {"Реєстрація", "Вхід", "Вихід"};
        int selectedIndex = 0;

        while (true) {
            clearScreen();
            for (int i = 0; i < menuOptions.length; i++) {
                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
                textGraphics.putString(10, 5 + i, menuOptions[i]);
            }
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    selectedIndex = (selectedIndex + 1) % menuOptions.length;
                    break;
                case ArrowUp:
                    selectedIndex = (selectedIndex - 1 + menuOptions.length) % menuOptions.length;
                    break;
                case Enter:
                    if (menuOptions[selectedIndex].equals("Реєстрація")) {
                        showRegistrationWindow();
                    } else if (menuOptions[selectedIndex].equals("Вхід")) {
                        // Реалізувати вхід
                    } else if (menuOptions[selectedIndex].equals("Вихід")) {
                        screen.stopScreen();  // Правильне завершення екрану
                        System.exit(0);  // Завершення програми
                        return;
                    }
                    break;
            }
        }
    }


    // Метод для відображення вікна реєстрації
    private void showRegistrationWindow() throws IOException {
        clearScreen();

        // Масив для введених значень
        String[] fields = {"Логін", "Пошта", "Пароль"};
        String[] inputs = {"", "", ""};  // Масив для введених значень
        int selectedFieldIndex = 0; // Початково вибрано перше поле (логін)
        String[] buttons = {"Зареєструватися", "Вихід"};
        String errorMessage = "";  // Змінна для збереження повідомлення про помилку

        while (true) {
            clearScreen();

            // Виводимо прямокутники для кожного поля
            for (int i = 0; i < fields.length; i++) {
                // Малюємо прямокутник
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.putString(8, 4 + i * 4, "┌────────────────────────────┐");
                textGraphics.putString(8, 5 + i * 4, "│                            │");
                textGraphics.putString(8, 6 + i * 4, "└────────────────────────────┘");

                // Виводимо текст всередині прямокутника
                if (i == selectedFieldIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }

                textGraphics.putString(10, 5 + i * 4, fields[i] + ": ");
                textGraphics.putString(24, 5 + i * 4, inputs[i]);
            }

            // Виводимо кнопки після полів введення
            for (int i = 0; i < buttons.length; i++) {
                if (i == selectedFieldIndex - fields.length) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    textGraphics.putString(10, 15 + i, buttons[i]);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    textGraphics.putString(10, 15 + i, buttons[i]);
                }
            }

            // Виводимо повідомлення про помилку, якщо є
            if (!errorMessage.isEmpty()) {
                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                textGraphics.putString(10, 19, errorMessage);
            }

            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    // Переміщення між полями та кнопками
                    if (selectedFieldIndex < fields.length + buttons.length - 1) {
                        selectedFieldIndex++;
                    }
                    break;

                case ArrowUp:
                    // Переміщення між полями та кнопками
                    if (selectedFieldIndex > 0) {
                        selectedFieldIndex--;
                    }
                    break;

                case Enter:
                    // Якщо натиснута кнопка "Зареєструватися"
                    if (selectedFieldIndex == fields.length) {
                        // Перевірка на пусті поля
                        if (inputs[0].isEmpty() || inputs[1].isEmpty() || inputs[2].isEmpty()) {
                            errorMessage = "Будь ласка, заповніть всі поля.";
                        } else {
                            // Перевірка введених даних
                            if (Validation.isValidUsername(inputs[0]) && Validation.isValidEmail(
                                inputs[1]) && Validation.isValidPassword(inputs[2])) {
                                if (userRepository.isEmailTaken(inputs[1])) {
                                    errorMessage = "Пошта вже використовується!";
                                } else if (userRepository.isUsernameTaken(inputs[0])) {
                                    errorMessage = "Логін вже використовується!";
                                } else {
                                    User user = new User(inputs[0], inputs[1], inputs[2]);
                                    userRepository.addUser(user);
                                    textGraphics.putString(10, 19, "Вітаємо, реєстрація успішна!");
                                    screen.refresh();
                                    return;
                                }
                            } else {
                                errorMessage = "Некоректні дані, перевірте введені дані.";
                            }
                        }
                    } else if (selectedFieldIndex == fields.length + 1) {
                        showMainMenu();
                        return;
                    }
                    break;

                case Backspace:
                    // Видалення символів у полях введення
                    if (selectedFieldIndex >= 0 && selectedFieldIndex < fields.length) {
                        StringBuilder currentInput = new StringBuilder(inputs[selectedFieldIndex]);
                        if (currentInput.length() > 0) {
                            currentInput.deleteCharAt(currentInput.length() - 1);
                        }
                        inputs[selectedFieldIndex] = currentInput.toString();
                    }
                    break;

                default:
                    // Введення символів у поля
                    if (keyStroke.getCharacter() != null && selectedFieldIndex >= 0
                        && selectedFieldIndex < fields.length) {
                        inputs[selectedFieldIndex] =
                            inputs[selectedFieldIndex] + keyStroke.getCharacter();
                    }
                    break;
            }
        }
    }

    // Метод для очищення екрану
    private void clearScreen() throws IOException {
        int screenWidth = screen.getTerminalSize().getColumns();
        int screenHeight = screen.getTerminalSize().getRows();
        textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
        for (int y = 0; y < screenHeight; y++) {
            textGraphics.putString(0, y, " ".repeat(screenWidth));
        }
        screen.refresh();
    }
}
