package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.User;
import com.agors.historiography.persistence.repository.UserRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.List;

public class UserManager {

    private static final int USERS_PER_PAGE = 10; // максимальна кількість користувачів, яку можна відобразити за один раз
    private final UserRepository userRepository;
    private final TextGraphics textGraphics;
    private final Screen screen;

    // Конструктор без currentUser
    public UserManager(UserRepository userRepository, TextGraphics textGraphics, Screen screen) {
        this.userRepository = userRepository;
        this.textGraphics = textGraphics;
        this.screen = screen;
    }

    public void manageUsers() throws IOException {
        List<User> users = userRepository.getAllUsers();
        int selectedUserIndex = 0; // Індекс вибраного користувача
        int pageStartIndex = 0;    // Початковий індекс для поточної сторінки

        // Перевірка на наявність користувачів
        while (true) {
            clearScreen();
            if (users.isEmpty()) {
                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                textGraphics.putString(10, 5, "Немає зареєстрованих користувачів.");
            } else {
                int yPosition = 5; // Початкова вертикальна позиція для відображення користувачів
                int pageEndIndex = Math.min(pageStartIndex + USERS_PER_PAGE, users.size());

                // Виведення користувачів вертикально
                for (int i = pageStartIndex; i < pageEndIndex; i++) {
                    User user = users.get(i);
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

                    // Виділення вибраного користувача
                    if (i == selectedUserIndex) {
                        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    }

                    textGraphics.putString(10, yPosition, "Логін: " + user.getUsername());
                    textGraphics.putString(30, yPosition, "Пошта: " + user.getEmail());
                    textGraphics.putString(50, yPosition, "Роль: " + user.getRole());
                    yPosition++; // Переміщаємо вниз на наступний рядок
                }
            }

            // Виведення фіксованого тексту внизу
            textGraphics.setForegroundColor(ANSI.YELLOW);
            textGraphics.putString(10, USERS_PER_PAGE + 7,
                "Натисніть Enter для видалення користувача, або ESC для виходу");
            textGraphics.putString(10, USERS_PER_PAGE + 8, "↑ Вгору   ↓ Вниз");
            screen.refresh();

            // Обробка натискання клавіші
            KeyStroke keyStroke = screen.readInput();

            // Перевірка для стрілочок і ESC
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    if (selectedUserIndex < users.size() - 1) {
                        selectedUserIndex++; // Переміщаємося вниз по списку
                    } else if (pageStartIndex + USERS_PER_PAGE < users.size()) {
                        // Прокручування вниз, якщо вибраний користувач в кінці списку
                        selectedUserIndex = pageStartIndex + USERS_PER_PAGE - 1;
                        pageStartIndex++; // Прокручуємо список
                    }
                    break;

                case ArrowUp:
                    if (selectedUserIndex > 0) {
                        selectedUserIndex--; // Переміщаємося вгору по списку
                    } else if (pageStartIndex > 0) {
                        // Прокручування вгору, якщо вибраний користувач на початку списку
                        selectedUserIndex = pageStartIndex;
                        pageStartIndex--; // Прокручуємо список
                    }
                    break;

                case Enter:
                    // Якщо вибрано користувача
                    User selectedUser = users.get(selectedUserIndex);
                    handleUserAction(selectedUser);
                    return; // після виконання дії виходимо з циклу

                case Escape:
                    return; // вихід без змін
            }

            // Перевірка на автоматичне прокручування
            if (selectedUserIndex >= pageStartIndex + USERS_PER_PAGE - 1
                && pageStartIndex + USERS_PER_PAGE < users.size()) {
                pageStartIndex++; // Прокручуємо сторінку
            }
            if (selectedUserIndex <= pageStartIndex && pageStartIndex > 0) {
                pageStartIndex--; // Прокручуємо сторінку вгору
            }
        }
    }

    private void handleUserAction(User selectedUser) throws IOException {
        if (canDeleteUser(selectedUser)) {
            userRepository.deleteUser(selectedUser.getUsername());
            clearScreen();
            textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
            textGraphics.putString(10, 5,
                "Користувача " + selectedUser.getUsername() + " успішно видалено.");
        } else {
            clearScreen();
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 5, "Не можна видалити цього користувача (Admin).");
        }
        screen.refresh();
        screen.readInput(); // Чекаємо натискання клавіші для повернення
        manageUsers(); // Повертаємось до перегляду списку
    }

    private boolean canDeleteUser(User user) {
        // Перевіряємо роль з урахуванням можливих пробілів або регістру
        return user.getRole().trim().equalsIgnoreCase("USER");
    }

    // Додатковий метод для очищення екрану
    private void clearScreen() {
        screen.clear();
    }
}
