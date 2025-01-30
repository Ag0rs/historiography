package com.agors.historiography.appui;

import com.agors.historiography.appui.forms.MenuHandler;
import com.agors.historiography.persistence.repository.UserRepository;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import java.io.IOException;

/**
 * Головний клас програми Historiography. Відповідає за запуск додатку, створення екрану за
 * допомогою Lanterna та ініціалізацію основних компонентів інтерфейсу користувача.
 */
public class Historiography {

    /**
     * Основний метод програми. Створює екран, ініціалізує необхідні компоненти, та виводить
     * привітальне повідомлення і головне меню.
     *
     * @param args аргументи командного рядка (не використовуються в даному випадку).
     */
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
