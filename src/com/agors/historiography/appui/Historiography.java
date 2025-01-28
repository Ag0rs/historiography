package com.agors.historiography.appui;

import com.agors.historiography.persistence.repository.UserRepository;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import java.io.IOException;

public class Historiography {

    public static void main(String[] args) {
        try {
            // Створюємо екран через DefaultTerminalFactory
            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();
            var textGraphics = screen.newTextGraphics();

            // Створюємо об'єкт UserRepository
            UserRepository userRepository = new UserRepository();

            // Створення об'єкта MenuHandler, передаємо всі необхідні параметри
            MenuHandler menuHandler = new MenuHandler(screen, textGraphics, userRepository);

            // Показуємо вікно привітання
            menuHandler.showGreeting();

            // Після цього показуємо головне меню
            menuHandler.showMainMenu();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
