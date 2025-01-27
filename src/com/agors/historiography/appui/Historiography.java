package com.agors.historiography.appui;

import com.agors.historiography.domain.models.User;
import com.agors.historiography.domain.validations.Validation;
import com.agors.historiography.repository.UserRepository;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import java.io.IOException;

public class Historiography {

    public static void main(String[] args) throws IOException {
        UserRepository userRepository = new UserRepository();
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        TextGraphics textGraphics = screen.newTextGraphics();

        // Анімація привітання
        showWelcomeAnimation(screen, textGraphics);

        String[] menuOptions = {"Реєстрація", "Вхід", "Вихід"};
        int selectedIndex = 0;

        while (true) {
            // Очищаємо екран і відображаємо меню
            clearScreen(screen, textGraphics);
            for (int i = 0; i < menuOptions.length; i++) {
                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
                textGraphics.putString(10, 5 + i, menuOptions[i], SGR.BOLD);
            }
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                selectedIndex = (selectedIndex + 1) % menuOptions.length;
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                selectedIndex = (selectedIndex - 1 + menuOptions.length) % menuOptions.length;
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                if (menuOptions[selectedIndex].equals("Реєстрація")) {
                    registerUser(screen, textGraphics, userRepository);
                } else if (menuOptions[selectedIndex].equals("Вхід")) {
                    textGraphics.putString(10, 9, "Функція входу ще не реалізована!", SGR.BOLD);
                    screen.refresh();
                    screen.readInput();
                } else if (menuOptions[selectedIndex].equals("Вихід")) {
                    screen.stopScreen();
                    return;
                }
            }
        }
    }

    private static void showWelcomeAnimation(Screen screen, TextGraphics textGraphics)
        throws IOException {
        String welcomeText = "Ласкаво просимо до Historiography!";
        for (int i = 0; i < welcomeText.length(); i++) {
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(10, 2, welcomeText.substring(0, i + 1), SGR.BOLD);
            screen.refresh();
            try {
                Thread.sleep(100); // Затримка для ефекту анімації
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void registerUser(Screen screen, TextGraphics textGraphics,
        UserRepository userRepository) throws IOException {
        clearScreen(screen, textGraphics);

        String[] fields = {"Логін", "Пошта", "Пароль"};
        String[] inputs = new String[3];
        int selectedIndex = 0;  // Інтерфейс переміщення між полями вводу

        String errorMessage = null;  // Повідомлення про помилку

        while (true) {
            clearScreen(screen,
                textGraphics);  // Очищаємо екран кожен раз перед виведенням полів для вводу

            // Виводимо поля для вводу
            for (int i = 0; i < fields.length; i++) {
                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
                textGraphics.putString(10, 5 + i, fields[i] + ": ", SGR.BOLD);
                // Виведення введеного тексту збоку
                textGraphics.putString(30, 5 + i, (inputs[i] == null ? "" : inputs[i]), SGR.BOLD);
            }

            // Виведення кнопок
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            if (selectedIndex == fields.length) {
                textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
            }
            textGraphics.putString(10, 9, "Зареєструватися", SGR.BOLD);

            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            if (selectedIndex == fields.length + 1) {
                textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
            }
            textGraphics.putString(10, 10, "Вихід", SGR.BOLD);

            // Виведення повідомлення про помилку
            if (errorMessage != null) {
                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                textGraphics.putString(10, 12, errorMessage, SGR.BOLD);
            }

            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                selectedIndex = (selectedIndex + 1) % (fields.length
                    + 2);  // Переміщаємо між полями вводу та кнопками
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                selectedIndex = (selectedIndex - 1 + (fields.length + 2)) % (fields.length
                    + 2);  // Переміщаємо вгору
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                if (selectedIndex == fields.length) {  // Кнопка "Зареєструватися"
                    String username = inputs[0];
                    String email = inputs[1];
                    String password = inputs[2];

                    // Перевірка на порожні поля
                    if (username == null || username.isEmpty() || email == null || email.isEmpty()
                        || password == null || password.isEmpty()) {
                        errorMessage = "Заповніть усі поля!";
                        continue;
                    }

                    // Перевірка на правильність логіна
                    if (!Validation.isValidUsername(username)) {
                        errorMessage = "Логін латинською (3-30, . _ -)";
                        continue;
                    }

                    // Перевірка на правильність пошти
                    if (!Validation.isValidEmail(email)) {
                        errorMessage = "Пошта (example@mail.com)";
                        continue;
                    }

                    // Перевірка на мінімальну довжину пароля
                    if (!Validation.isValidPassword(password)) {
                        errorMessage = "Пароль (мін. 6 символів)";
                        continue;
                    }

                    // Додаємо користувача
                    User user = new User(username, email, Integer.toHexString(password.hashCode()));
                    userRepository.addUser(user);
                    textGraphics.putString(10, 14,
                        "Реєстрація успішна! Натисніть Enter для повернення в меню.", SGR.BOLD);
                    screen.refresh();
                    screen.readInput();
                    return;
                } else if (selectedIndex == fields.length + 1) {  // Кнопка "Вихід"
                    return;
                }
            } else if (keyStroke.getCharacter() != null) {
                // Оновлюємо введений текст
                if (selectedIndex == 0) {
                    inputs[0] = updateInput(inputs[0], keyStroke.getCharacter());
                } else if (selectedIndex == 1) {
                    inputs[1] = updateInput(inputs[1], keyStroke.getCharacter());
                } else if (selectedIndex == 2) {
                    inputs[2] = updateInput(inputs[2], keyStroke.getCharacter());
                }

                // Скидаємо повідомлення про помилку при натисканні на клавішу
                errorMessage = null;
            } else if (keyStroke.getKeyType() == KeyType.Backspace) {
                // Обробка кнопки Backspace для видалення останнього символу
                if (selectedIndex == 0 && inputs[0] != null && inputs[0].length() > 0) {
                    inputs[0] = inputs[0].substring(0, inputs[0].length() - 1);
                } else if (selectedIndex == 1 && inputs[1] != null && inputs[1].length() > 0) {
                    inputs[1] = inputs[1].substring(0, inputs[1].length() - 1);
                } else if (selectedIndex == 2 && inputs[2] != null && inputs[2].length() > 0) {
                    inputs[2] = inputs[2].substring(0, inputs[2].length() - 1);
                }
            }
        }
    }

    private static String updateInput(String currentInput, char newChar) {
        if (currentInput == null) {
            currentInput = "";
        }
        return currentInput + newChar;
    }

    private static void clearScreen(Screen screen, TextGraphics textGraphics) throws IOException {
        // Очищаємо екран за допомогою заповнення всього екрану пробілами
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        for (int row = 0; row < screen.getTerminalSize().getRows(); row++) {
            textGraphics.putString(0, row, " ".repeat(screen.getTerminalSize().getColumns()));
        }
        screen.refresh();
    }
}
