package com.agors.historiography.appui.forms;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;

public class SettingsUI {

    private final Screen screen;
    private final TextGraphics textGraphics;
    private final MenuHandler menuHandler;  // Замість MainMenuUI, використовуємо MenuHandler

    // Оновлений конструктор
    public SettingsUI(Screen screen, MenuHandler menuHandler) {
        this.screen = screen;
        this.textGraphics = screen.newTextGraphics();
        this.menuHandler = menuHandler;  // Ініціалізуємо посилання на MenuHandler
    }

    public void show() throws IOException {
        clearScreen();

        String[] settingsMenuOptions = {
            "Вийти з облікового запису",
            "Повернутися в головне меню"
        };

        int selectedIndex = 0;

        while (true) {
            clearScreen();
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(10, 2, "Меню налаштувань");
            textGraphics.putString(10, 3, "──────────────────");

            // Відображення кнопок з символом стрілочки
            for (int i = 0; i < settingsMenuOptions.length; i++) {
                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    textGraphics.putString(8, 5 + i, "▶ " + settingsMenuOptions[i]);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    textGraphics.putString(10, 5 + i, settingsMenuOptions[i]);
                }
            }

            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    selectedIndex = (selectedIndex + 1) % settingsMenuOptions.length;
                    break;
                case ArrowUp:
                    selectedIndex =
                        (selectedIndex - 1 + settingsMenuOptions.length)
                            % settingsMenuOptions.length;
                    break;
                case Enter:
                    switch (selectedIndex) {
                        case 0:
                            logOut();  // Виклик логіки для виходу з облікового запису
                            break;
                        case 1:
                            return; // Повернення в головне меню
                    }
                    break;
                case Escape:
                    return; // Повернення в головне меню
            }
        }
    }

    // Метод для очищення екрану
    private void clearScreen() throws IOException {
        screen.clear();
    }

    // Логіка для виходу з облікового запису з підтвердженням
    private void logOut() throws IOException {
        screen.clear();
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(10, 2, "Ви дійсно хочете вийти з облікового запису? (y/n)");

        screen.refresh();
        KeyStroke keyStroke = screen.readInput();

        if (keyStroke.getCharacter() != null && keyStroke.getCharacter().toString()
            .equalsIgnoreCase("y")) {
            textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
            textGraphics.putString(10, 4, "Ви вийшли з облікового запису.");
            screen.refresh();
            screen.readInput();
            menuHandler.showMainMenu();  // Викликаємо головне меню з MenuHandler
        } else {
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(10, 4, "Операція скасована.");
            screen.refresh();
            screen.readInput();
        }
    }
}
