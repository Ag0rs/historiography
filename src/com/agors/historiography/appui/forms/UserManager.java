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

/**
 * Клас для керування користувачами в системі. Забезпечує можливість переглядати список
 * користувачів, вибирати та видаляти користувачів.
 */
public class UserManager {

    private static final int USERS_PER_PAGE = 10;  // Кількість користувачів на сторінці
    private final UserRepository userRepository;  // Репозиторій для отримання та видалення користувачів
    private final TextGraphics textGraphics;  // Об'єкт для малювання тексту на екрані
    private final Screen screen;  // Екран, на якому відображається інтерфейс

    /**
     * Конструктор класу UserManager.
     *
     * @param userRepository репозиторій для доступу до даних користувачів.
     * @param textGraphics   об'єкт для малювання тексту на екрані.
     * @param screen         екран для відображення інтерфейсу.
     */
    public UserManager(UserRepository userRepository, TextGraphics textGraphics, Screen screen) {
        this.userRepository = userRepository;
        this.textGraphics = textGraphics;
        this.screen = screen;
    }

    /**
     * Відображає меню керування користувачами, дозволяючи переглядати список користувачів, вибирати
     * їх та видаляти.
     *
     * @throws IOException якщо сталася помилка при відображенні на екрані.
     */
    public void manageUsers() throws IOException {
        List<User> users = userRepository.getAllUsers();  // Отримуємо список всіх користувачів
        int selectedUserIndex = 0;  // Індекс вибраного користувача
        int pageStartIndex = 0;  // Індекс початку сторінки

        while (true) {
            clearScreen();  // Очищаємо екран
            if (users.isEmpty()) {
                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                textGraphics.putString(10, 5, "Немає зареєстрованих користувачів.");
            } else {
                int yPosition = 5;
                int pageEndIndex = Math.min(pageStartIndex + USERS_PER_PAGE, users.size());

                // Відображаємо користувачів на поточній сторінці
                for (int i = pageStartIndex; i < pageEndIndex; i++) {
                    User user = users.get(i);
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

                    if (i == selectedUserIndex) {
                        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    }

                    textGraphics.putString(10, yPosition, "Логін: " + user.getUsername());
                    textGraphics.putString(30, yPosition, "Пошта: " + user.getEmail());
                    textGraphics.putString(50, yPosition, "Роль: " + user.getRole());
                    yPosition++;
                }
            }

            textGraphics.setForegroundColor(ANSI.YELLOW);
            textGraphics.putString(10, USERS_PER_PAGE + 7,
                "Натисніть Enter для видалення користувача, або ESC для виходу");
            textGraphics.putString(10, USERS_PER_PAGE + 8, "↑ Вгору   ↓ Вниз");
            screen.refresh();  // Оновлюємо екран

            KeyStroke keyStroke = screen.readInput();  // Читаємо введення користувача

            // Обробка введених клавіш
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    // Переміщення вниз по списку
                    if (selectedUserIndex < users.size() - 1) {
                        selectedUserIndex++;
                    } else if (pageStartIndex + USERS_PER_PAGE < users.size()) {
                        selectedUserIndex = pageStartIndex + USERS_PER_PAGE - 1;
                        pageStartIndex++;
                    }
                    break;

                case ArrowUp:
                    // Переміщення вгору по списку
                    if (selectedUserIndex > 0) {
                        selectedUserIndex--;
                    } else if (pageStartIndex > 0) {
                        selectedUserIndex = pageStartIndex;
                        pageStartIndex--;
                    }
                    break;

                case Enter:
                    // Видалення вибраного користувача
                    User selectedUser = users.get(selectedUserIndex);
                    confirmDeletion(selectedUser);
                    return;

                case Escape:
                    return;  // Вихід з меню
            }

            // Перехід на наступну сторінку при досягненні кінця списку
            if (selectedUserIndex >= pageStartIndex + USERS_PER_PAGE - 1
                && pageStartIndex + USERS_PER_PAGE < users.size()) {
                pageStartIndex++;
            }
            if (selectedUserIndex <= pageStartIndex && pageStartIndex > 0) {
                pageStartIndex--;
            }
        }
    }

    /**
     * Підтверджує видалення користувача.
     *
     * @param selectedUser користувач, якого потрібно видалити.
     * @throws IOException якщо сталася помилка при відображенні на екрані.
     */
    private void confirmDeletion(User selectedUser) throws IOException {
        clearScreen();
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(10, 5, "Ви дійсно хочете видалити цього користувача? (y/n)");
        screen.refresh();

        KeyStroke keyStroke = screen.readInput();  // Читання введення користувача

        // Обробка підтвердження або скасування видалення
        switch (keyStroke.getKeyType()) {
            case Character:
                if (keyStroke.getCharacter() == 'y' || keyStroke.getCharacter() == 'Y') {
                    if (canDeleteUser(selectedUser)) {
                        userRepository.deleteUser(
                            selectedUser.getUsername());  // Видалення користувача
                        clearScreen();
                        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                        textGraphics.putString(10, 5,
                            "Користувача " + selectedUser.getUsername() + " успішно видалено.");
                    } else {
                        clearScreen();
                        textGraphics.setForegroundColor(TextColor.ANSI.RED);
                        textGraphics.putString(10, 5,
                            "Не можна видалити цього користувача (Admin).");
                    }
                } else {
                    clearScreen();
                    textGraphics.setForegroundColor(TextColor.ANSI.RED);
                    textGraphics.putString(10, 5, "Видалення скасовано.");
                }
                break;

            case Escape:
                clearScreen();
                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                textGraphics.putString(10, 5, "Видалення скасовано.");
                break;
        }
        screen.refresh();
        screen.readInput();
        manageUsers();  // Повертаємося до меню керування користувачами
    }

    /**
     * Перевіряє, чи можна видалити користувача.
     *
     * @param user користувач, якого перевіряють.
     * @return true, якщо користувач може бути видалений, false — якщо ні.
     */
    private boolean canDeleteUser(User user) {
        return user.getRole().trim()
            .equalsIgnoreCase("USER");  // Не можна видалити користувача з роллю "Admin"
    }

    /**
     * Очищає екран.
     */
    private void clearScreen() {
        screen.clear();
    }
}
