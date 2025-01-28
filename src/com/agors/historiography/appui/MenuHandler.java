package com.agors.historiography.appui;

import com.agors.historiography.domain.entitys.User;
import com.agors.historiography.domain.message.MessageManager;
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
    private final MessageManager messageManager; // Додали поле для менеджера повідомлень

    public MenuHandler(Screen screen, TextGraphics textGraphics, UserRepository userRepository) {
        this.screen = screen;
        this.textGraphics = textGraphics;
        this.userRepository = userRepository;
        this.messageManager = new MessageManager(); // ініціалізуємо менеджер повідомлень
    }

    public void showGreeting() throws IOException, InterruptedException {
        clearScreen();
        String message = "Вітаємо в програмі Historiography!";
        int xPos = 10;
        int yPos = 5;

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

    public void showMainMenu() throws IOException {
        String[] menuOptions = {"Реєстрація", "Вхід", "Правила", "Вихід"};
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
                        showLoginWindow(); // Перехід до входу
                    } else if (menuOptions[selectedIndex].equals("Правила")) {
                        showRegistrationRules();
                    } else if (menuOptions[selectedIndex].equals("Вихід")) {
                        screen.stopScreen();
                        System.exit(0);
                    }
                    break;
            }
        }
    }

    private void showRegistrationWindow() throws IOException {
        clearScreen();

        String[] fields = {"Логін", "Пошта", "Пароль"};
        String[] inputs = {"", "", ""};
        int selectedFieldIndex = 0;

        final int MAX_INPUT_LENGTH = 30;

        String[] buttons = {"Зареєструватися", "Вихід"};

        while (true) {
            clearScreen();

            // Виведення полів вводу
            for (int i = 0; i < fields.length; i++) {
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.putString(8, 4 + i * 4, "┌───────────────────────────────────────┐");
                textGraphics.putString(8, 5 + i * 4, "│                                       │");
                textGraphics.putString(8, 6 + i * 4, "└───────────────────────────────────────┘");

                if (i == selectedFieldIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }

                textGraphics.putString(10, 5 + i * 4, fields[i] + ": ");
                String inputToShow =
                    inputs[i].length() > MAX_INPUT_LENGTH ? inputs[i].substring(0, MAX_INPUT_LENGTH)
                        : inputs[i];
                textGraphics.putString(17, 5 + i * 4, inputToShow);
            }

            // Виведення кнопок
            for (int i = 0; i < buttons.length; i++) {
                if (i == selectedFieldIndex - fields.length) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
                textGraphics.putString(10, 17 + i, buttons[i]);
            }

            // Виведення повідомлень про помилку чи успіх
            messageManager.displayMessagesReg(textGraphics);

            screen.refresh();
            messageManager.clearMessages();
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
                        // Очистити повідомлення перед відображенням успіху
                        if (inputs[0].isEmpty() || inputs[1].isEmpty() || inputs[2].isEmpty()) {
                            messageManager.setErrorMessage("Будь ласка, заповніть всі поля.");
                        } else if (Validation.isValidUsername(inputs[0]) &&
                            Validation.isValidEmail(inputs[1]) &&
                            Validation.isValidPassword(inputs[2])) {
                            // Очищаємо повідомлення про помилку перед виведенням успіху
                            messageManager.clearMessages();

                            // Реєстрація успішна
                            User user = new User(inputs[0], inputs[1], inputs[2]);
                            userRepository.addUser(user);
                            messageManager.setSuccessMessage("Реєстрація успішна!");
                            messageManager.displayMessages(
                                textGraphics); // Виводимо успішне повідомлення
                            screen.refresh();

                            try {
                                Thread.sleep(1000); // Очікуємо 1 секунду
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.out.println("Thread was interrupted: " + e.getMessage());
                            }
                            showMainMenu();
                            return;
                        } else {
                            messageManager.setErrorMessage("Некоректні дані.");
                        }
                    } else if (selectedFieldIndex == fields.length + 1) {
                        showMainMenu();
                        return;
                    }
                    break;

                case Backspace:
                    if (selectedFieldIndex >= 0 && selectedFieldIndex < fields.length) {
                        if (inputs[selectedFieldIndex].length() > 0) {
                            inputs[selectedFieldIndex] = inputs[selectedFieldIndex].substring(0,
                                inputs[selectedFieldIndex].length() - 1);
                        }
                    }
                    break;

                default:
                    if (keyStroke.getCharacter() != null && selectedFieldIndex >= 0
                        && selectedFieldIndex < fields.length) {
                        if (inputs[selectedFieldIndex].length() < MAX_INPUT_LENGTH) {
                            inputs[selectedFieldIndex] += keyStroke.getCharacter();
                        }
                    }
                    break;
            }
        }
    }


    public void showLoginWindow() throws IOException {
        clearScreen();

        String[] fields = {"Логін/Пошта", "Пароль"};
        String[] inputs = {"", ""};
        int selectedFieldIndex = 0;

        messageManager.clearMessages(); // Очищаємо повідомлення перед кожним введенням

        final int MAX_INPUT_LENGTH = 30;
        String[] buttons = {"Увійти", "Вихід"};

        while (true) {
            clearScreen();

            for (int i = 0; i < fields.length; i++) {
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.putString(8, 4 + i * 4,
                    "┌───────────────────────────────────────────┐");
                textGraphics.putString(8, 5 + i * 4,
                    "│                                           │");
                textGraphics.putString(8, 6 + i * 4,
                    "└───────────────────────────────────────────┘");

                if (i == selectedFieldIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }

                textGraphics.putString(10, 5 + i * 4, fields[i] + ": ");
                String inputToShow =
                    inputs[i].length() > MAX_INPUT_LENGTH ? inputs[i].substring(0, MAX_INPUT_LENGTH)
                        : inputs[i];
                textGraphics.putString(22, 5 + i * 4, inputToShow);
            }

            for (int i = 0; i < buttons.length; i++) {
                if (i == selectedFieldIndex - fields.length) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
                textGraphics.putString(10, 13 + i, buttons[i]);
            }

            messageManager.displayMessages(textGraphics); // Виводимо повідомлення

            screen.refresh();  // Оновлюємо екран після виведення всього

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
                        if (inputs[0].isEmpty() || inputs[1].isEmpty()) {
                            messageManager.setErrorMessage("Будь ласка, заповніть всі поля.");
                        } else if (userRepository.isUserExists(inputs[0], inputs[1])) {
                            messageManager.clearMessages(); // Очищаємо помилку перед виведенням успіху
                            messageManager.setSuccessMessage("Успішний вхід! Ласкаво просимо!");
                            messageManager.displayMessages(
                                textGraphics); // Виводимо успішне повідомлення
                            screen.refresh();  // Оновлюємо екран після повідомлення
                            messageManager.clearMessages();

                            try {
                                Thread.sleep(1000); // Очікуємо 1 секунду
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.out.println("Thread was interrupted: " + e.getMessage());
                            }
                            showMainMenu();
                            return;
                        } else {
                            messageManager.setErrorMessage("Невірний логін або пошта!");
                        }
                    } else if (selectedFieldIndex == fields.length + 1) {
                        messageManager.clearMessages();
                        showMainMenu();
                        return;
                    }
                    break;

                case Backspace:
                    if (selectedFieldIndex >= 0 && selectedFieldIndex < fields.length) {
                        if (inputs[selectedFieldIndex].length() > 0) {
                            inputs[selectedFieldIndex] = inputs[selectedFieldIndex].substring(0,
                                inputs[selectedFieldIndex].length() - 1);
                        }
                    }
                    break;

                default:
                    if (keyStroke.getCharacter() != null && selectedFieldIndex >= 0
                        && selectedFieldIndex < fields.length) {
                        if (inputs[selectedFieldIndex].length() < MAX_INPUT_LENGTH) {
                            inputs[selectedFieldIndex] += keyStroke.getCharacter();
                        }
                    }
                    break;
            }
        }
    }


    private void clearScreen() throws IOException {
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        // Use a loop to fill the screen with spaces
        int screenWidth = screen.getTerminalSize().getColumns();
        int screenHeight = screen.getTerminalSize().getRows();

        for (int y = 0; y < screenHeight; y++) {
            textGraphics.putString(0, y,
                " ".repeat(screenWidth)); // Repeat space characters to fill each line
        }
    }

    private void showRegistrationRules() throws IOException {
        clearScreen();

        // Текст правил реєстрації:
        String[] rules = {
            "Правила реєстрації:",
            "1. Логін та електронна пошта повинні бути унікальними.",
            "2. Логін може містити латинські букви та цифри, не менше 3 символів.",
            "3. Пошта має бути валідною (наприклад, user@example.com).",
            "4. Пароль повинен містити не менше 6 символів.",
            "Натисніть будь-яку клавішу для повернення до меню."
        };

        // Виведення правил на екран
        for (int i = 0; i < rules.length; i++) {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(10, 5 + i, rules[i]);  // Виводимо кожне правило на новому рядку
        }

        screen.refresh();

        // Чекаємо на натискання клавіші для повернення до головного меню
        screen.readInput();
        showMainMenu(); // Повертаємось до головного меню
    }
}
