package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.User;
import com.agors.historiography.domain.message.MessageManager;
import com.agors.historiography.domain.validations.Validation;
import com.agors.historiography.persistence.repository.HistoricalPlaceRepository;
import com.agors.historiography.persistence.repository.ReviewRepository;
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
    private final MessageManager messageManager;

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
        messageManager.clearMessages();
        String[] menuOptions = {"Реєстрація", "Вхід", "Правила", "Вихід"};
        int selectedIndex = 0;

        while (true) {
            clearScreen();
            for (int i = 0; i < menuOptions.length; i++) {
                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    textGraphics.putString(8, 5 + i,
                        "▶ " + menuOptions[i]); // Стрілочка для активного пункту
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    textGraphics.putString(10, 5 + i, menuOptions[i]);
                }
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

        String[] fields = {"Логін", "Пошта", "Пароль", "Роль"};
        String[] inputs = {"", "", "", "User"}; // За замовчуванням роль — User
        int selectedFieldIndex = 0;

        final int MAX_INPUT_LENGTH = 30;

        String[] roles = {"User", "Admin"}; // Доступні ролі
        int selectedRoleIndex = 0;

        String[] buttons = {"Зареєструватися", "Вихід"};

        while (true) {
            clearScreen();

            // Виведення полів вводу
            for (int i = 0; i < fields.length; i++) {
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.putString(8, 2 + i * 4, "┌───────────────────────────────────────┐");
                textGraphics.putString(8, 3 + i * 4, "│                                       │");
                textGraphics.putString(8, 4 + i * 4, "└───────────────────────────────────────┘");

                if (i == selectedFieldIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }

                textGraphics.putString(10, 3 + i * 4, fields[i] + ": ");
                if (i < 3) {
                    String inputToShow = (i == 2) ? "☆".repeat(inputs[i].length()) :
                        (inputs[i].length() > MAX_INPUT_LENGTH ? inputs[i].substring(0,
                            MAX_INPUT_LENGTH) : inputs[i]);
                    textGraphics.putString(17, 3 + i * 4, inputToShow);
                } else { // Поле для вибору ролі
                    textGraphics.putString(17, 3 + i * 4, roles[selectedRoleIndex]);
                }
            }

            // Виведення кнопок
            for (int i = 0; i < buttons.length; i++) {
                if (i == selectedFieldIndex - fields.length) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
                textGraphics.putString(10, 19 + i, buttons[i]);
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

                case ArrowLeft:
                    if (selectedFieldIndex == 3) { // Зміна ролі
                        selectedRoleIndex = (selectedRoleIndex - 1 + roles.length) % roles.length;
                    }
                    break;

                case ArrowRight:
                    if (selectedFieldIndex == 3) { // Зміна ролі
                        selectedRoleIndex = (selectedRoleIndex + 1) % roles.length;
                    }
                    break;

                case Enter:
                    if (selectedFieldIndex == fields.length) {
                        // Перевірка на порожні поля
                        if (inputs[0].isEmpty() || inputs[1].isEmpty() || inputs[2].isEmpty()) {
                            messageManager.setErrorMessage("Будь ласка, заповніть всі поля.");
                        } else if (Validation.isValidUsername(inputs[0]) &&
                            Validation.isValidEmail(inputs[1]) &&
                            Validation.isValidPassword(inputs[2])) {

                            // Перевірка на унікальність логіна та пошти
                            if (userRepository.isUsernameTaken(inputs[0])) {
                                messageManager.setErrorMessage("Логін вже зайнятий.");
                            } else if (userRepository.isEmailTaken(inputs[1])) {
                                messageManager.setErrorMessage("Пошта вже зареєстрована.");
                            } else {
                                // Очищаємо повідомлення про помилку перед виведенням успіху
                                messageManager.clearMessages();

                                // Реєстрація успішна
                                User user = new User(inputs[0], inputs[1], inputs[2],
                                    roles[selectedRoleIndex]);
                                userRepository.addUser(user);

                                // Встановлюємо успішне повідомлення
                                messageManager.setSuccessMessage2("Реєстрація успішна!");

                                // Виводимо успішні повідомлення
                                messageManager.displayMessagesReg(textGraphics);

                                screen.refresh();

                                try {
                                    Thread.sleep(2000); // Затримка на 2 секунди
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    System.out.println("Thread was interrupted: " + e.getMessage());
                                }
                                showMainMenu();
                                return;
                            }
                        } else {
                            messageManager.setErrorMessage("Некоректні дані.");
                        }
                    } else if (selectedFieldIndex == fields.length + 1) {
                        showMainMenu();
                        return;
                    }
                    break;

                case Backspace:
                    if (selectedFieldIndex >= 0 && selectedFieldIndex < 3) {
                        if (inputs[selectedFieldIndex].length() > 0) {
                            inputs[selectedFieldIndex] = inputs[selectedFieldIndex].substring(0,
                                inputs[selectedFieldIndex].length() - 1);
                        }
                    }
                    break;

                default:
                    if (keyStroke.getCharacter() != null && selectedFieldIndex >= 0
                        && selectedFieldIndex < 3) {
                        if (inputs[selectedFieldIndex].length() < MAX_INPUT_LENGTH) {
                            inputs[selectedFieldIndex] += keyStroke.getCharacter();
                        }
                    }
                    break;
            }
        }
    }

    private void showLoginWindow() throws IOException {
        clearScreen();

        String[] fields = {"Логін/Пошта", "Пароль"};
        String[] inputs = {"", ""};
        int selectedFieldIndex = 0;

        messageManager.clearMessages(); // Очищаємо повідомлення перед кожним введенням

        final int MAX_INPUT_LENGTH = 30;
        String[] buttons = {"Увійти", "Вихід"};

        while (true) {
            clearScreen();

            // Виведення полів для введення
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
                String inputToShow = (i == 1) ? "☆".repeat(inputs[i].length()) :
                    (inputs[i].length() > MAX_INPUT_LENGTH ? inputs[i].substring(0,
                        MAX_INPUT_LENGTH) : inputs[i]);
                textGraphics.putString(22, 5 + i * 4, inputToShow);
            }

            // Виведення кнопок
            for (int i = 0; i < buttons.length; i++) {
                if (i == selectedFieldIndex - fields.length) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
                textGraphics.putString(10, 13 + i, buttons[i]);
            }

            // Виведення повідомлень про помилки чи успіх
            messageManager.displayMessages(textGraphics);

            screen.refresh();
            messageManager.clearMessages();

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
                        // Перевірка на заповненість полів
                        if (inputs[0].isEmpty() || inputs[1].isEmpty()) {
                            messageManager.setErrorMessage("Будь ласка, заповніть всі поля.");
                        } else {
                            // Перевірка користувача за логіном або поштою
                            User loggedInUser = userRepository.getUserByUsernameOrEmail(inputs[0]);

                            // Перевірка, чи користувач існує та чи правильний пароль
                            if (loggedInUser != null && loggedInUser.getPassword()
                                .equals(inputs[1])) {
                                messageManager.clearMessages();
                                messageManager.setSuccessMessage("Успішний вхід! Ласкаво просимо!");
                                messageManager.displayMessages(textGraphics);
                                screen.refresh(); // Оновлення екрану після повідомлення

                                try {
                                    Thread.sleep(1000); // Очікуємо 1 секунду
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    System.out.println("Thread was interrupted: " + e.getMessage());
                                }

                                // Перевірка ролі користувача
                                if ("Admin".equals(loggedInUser.getRole())) {
                                    // Запитуємо ключ для входу в адмін-меню
                                    showAdminKeyInputWindow(); // Показуємо вікно для введення ключа
                                } else {
                                    showUserMenu();  // Показуємо меню користувача
                                }
                                return;
                            } else {
                                messageManager.setErrorMessage("Невірний логін/пошта або пароль!");
                            }
                        }
                    } else if (selectedFieldIndex == fields.length + 1) {
                        showMainMenu(); // Якщо натиснуто "Вихід"
                        return;
                    }
                    break;

                case Backspace:
                    if (selectedFieldIndex >= 0 && selectedFieldIndex < 2) {
                        if (inputs[selectedFieldIndex].length() > 0) {
                            inputs[selectedFieldIndex] = inputs[selectedFieldIndex].substring(0,
                                inputs[selectedFieldIndex].length() - 1);
                        }
                    }
                    break;

                default:
                    if (keyStroke.getCharacter() != null && selectedFieldIndex >= 0
                        && selectedFieldIndex < 2) {
                        if (inputs[selectedFieldIndex].length() < MAX_INPUT_LENGTH) {
                            inputs[selectedFieldIndex] += keyStroke.getCharacter();
                        }
                    }
                    break;
            }
        }
    }

    private void showAdminKeyInputWindow() throws IOException {
        clearScreen();

        String[] fields = {"Ключ Адміна"};
        String[] inputs = {""};
        int selectedFieldIndex = 0;

        final int MAX_INPUT_LENGTH = 30;
        String[] buttons = {"Вхід", "Вихід"};

        String errorMessage = ""; // Поле для повідомлення про помилку

        while (true) {
            clearScreen();

            // Виведення полів для введення
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
                String inputToShow = (inputs[i].length() > MAX_INPUT_LENGTH ? inputs[i].substring(0,
                    MAX_INPUT_LENGTH) : inputs[i]);
                textGraphics.putString(22, 5 + i * 4, inputToShow);
            }

            // Виведення повідомлення про помилку (якщо воно є)
            if (!errorMessage.isEmpty()) {
                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                textGraphics.putString(10, 7, errorMessage);
            }

            // Виведення кнопок
            for (int i = 0; i < buttons.length; i++) {
                if (i == selectedFieldIndex - fields.length) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
                textGraphics.putString(10, 10 + i, buttons[i]);
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
                        if (inputs[0].equals("0000")) {
                            showAdminMenu(); // Якщо ключ правильний, відкриваємо меню адміна
                            return;
                        } else {
                            errorMessage = "Невірний ключ!";
                        }
                    } else if (selectedFieldIndex == fields.length + 1) {
                        showLoginWindow(); // Якщо натиснуто "Вихід"
                        return;
                    }
                    break;

                case Backspace:
                    if (inputs[selectedFieldIndex].length() > 0) {
                        inputs[selectedFieldIndex] = inputs[selectedFieldIndex].substring(0,
                            inputs[selectedFieldIndex].length() - 1);
                    }
                    break;

                default:
                    if (keyStroke.getCharacter() != null && selectedFieldIndex == 0) {
                        if (inputs[selectedFieldIndex].length() < MAX_INPUT_LENGTH) {
                            inputs[selectedFieldIndex] += keyStroke.getCharacter();
                        }
                    }
                    break;
            }
        }
    }

    public void showAdminMenu() throws IOException {
        clearScreen();

        String[] adminMenuOptions = {
            "Управління користувачами",
            "Історичні місця",
            "Додати історичне місце",
            "Редагувати історичне місце",
            "Відгуки",  // Додали пункт для відгуків
            "Налаштування",  // Налаштування після відгуків
            "Вихід з програми"
        };

        int selectedIndex = 0;

        UserManager userManager = new UserManager(userRepository, textGraphics, screen);
        HistoricalPlaceRepository historicalPlaceRepository = new HistoricalPlaceRepository();
        AddHistoricalPlaceUI addHistoricalPlaceUI = new AddHistoricalPlaceUI(
            historicalPlaceRepository, screen);
        ViewHistoricalPlacesUI viewHistoricalPlacesUI = new ViewHistoricalPlacesUI(
            historicalPlaceRepository, screen);
        EditHistoricalPlaceUI editHistoricalPlaceUI = new EditHistoricalPlaceUI(
            historicalPlaceRepository, screen, this);

        // Створюємо екземпляр ReviewManager
        ReviewRepository reviewRepository = new ReviewRepository();  // Репозиторій для відгуків
        ReviewManager reviewManager = new ReviewManager(reviewRepository, textGraphics,
            screen);  // Менеджер відгуків

        MenuHandler menuHandler = new MenuHandler(screen, textGraphics, userRepository);
        SettingsUI settingsUI = new SettingsUI(screen, menuHandler);

        while (true) {
            clearScreen();
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(10, 2, "Меню адміністратора");
            textGraphics.putString(10, 3, "────────────────────");

            for (int i = 0; i < adminMenuOptions.length; i++) {
                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    textGraphics.putString(8, 5 + i, "▶ " + adminMenuOptions[i]);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    textGraphics.putString(10, 5 + i, adminMenuOptions[i]);
                }
            }

            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    selectedIndex = (selectedIndex + 1) % adminMenuOptions.length;
                    break;
                case ArrowUp:
                    selectedIndex =
                        (selectedIndex - 1 + adminMenuOptions.length) % adminMenuOptions.length;
                    break;
                case Enter:
                    switch (selectedIndex) {
                        case 0:
                            userManager.manageUsers();
                            break;
                        case 1:
                            viewHistoricalPlacesUI.show();
                            break;
                        case 2:
                            addHistoricalPlaceUI.show();
                            break;
                        case 3:
                            editHistoricalPlaceUI.show();
                            break;
                        case 4:
                            reviewManager.manageReviews();  // Відкриваємо меню відгуків
                            break;
                        case 5:
                            settingsUI.show(); // Відкриваємо меню налаштувань
                            break;
                        case 6:
                            screen.stopScreen();
                            System.exit(0);
                            break;
                    }
                    break;
                // Видаляємо case Escape
            }
        }
    }

    public void showUserMenu() {
        try {
            clearScreen();

            String[] userMenuOptions = {
                "Перегляд історичних місць",
                "Відгуки та рейтинги",
                "Налаштування",
                "Вихід з програми"
            };

            int selectedIndex = 0;

            HistoricalPlaceRepository historicalPlaceRepository = new HistoricalPlaceRepository();
            ReviewRepository reviewRepository = new ReviewRepository();

            ViewHistoricalPlacesUI viewHistoricalPlacesUI = new ViewHistoricalPlacesUI(
                historicalPlaceRepository, screen);

            ReviewsAndRatingsUI reviewsAndRatingsUI = new ReviewsAndRatingsUI(
                historicalPlaceRepository, screen, reviewRepository);

            reviewsAndRatingsUI.setOnExitCallback(this::showUserMenu);

            MenuHandler menuHandler = new MenuHandler(screen, textGraphics, userRepository);
            SettingsUI settingsUI = new SettingsUI(screen, menuHandler);

            while (true) {
                clearScreen();
                textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
                textGraphics.putString(10, 2, "Меню користувача");
                textGraphics.putString(10, 3, "────────────────────");

                for (int i = 0; i < userMenuOptions.length; i++) {
                    if (i == selectedIndex) {
                        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                        textGraphics.putString(8, 5 + i, "▶ " + userMenuOptions[i]);
                    } else {
                        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                        textGraphics.putString(10, 5 + i, userMenuOptions[i]);
                    }
                }

                screen.refresh();

                KeyStroke keyStroke = screen.readInput();
                switch (keyStroke.getKeyType()) {
                    case ArrowDown:
                        selectedIndex = (selectedIndex + 1) % userMenuOptions.length;
                        break;
                    case ArrowUp:
                        selectedIndex =
                            (selectedIndex - 1 + userMenuOptions.length) % userMenuOptions.length;
                        break;
                    case Enter:
                        switch (selectedIndex) {
                            case 0:
                                viewHistoricalPlacesUI.show();
                                break;
                            case 1:
                                reviewsAndRatingsUI.show();
                                break;
                            case 2:
                                settingsUI.show();
                                break;
                            case 3:
                                screen.stopScreen();
                                System.exit(0);
                                break;
                        }
                        break;
                    // Видаляємо case Escape
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearScreen() throws IOException {
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        // Use a loop to fill the screen with spaces
        int screenWidth = screen.getTerminalSize().getColumns();
        int screenHeight = screen.getTerminalSize().getRows();

        for (int y = 0; y < screenHeight; y++) {
            textGraphics.putString(0, y,
                " ".repeat(screenWidth));
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
