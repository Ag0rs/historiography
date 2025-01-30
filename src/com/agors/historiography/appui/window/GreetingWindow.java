package com.agors.historiography.appui.window;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;

/**
 * Клас, що відповідає за виведення вітання в графічному інтерфейсі користувача. Виводить
 * повідомлення на екран і чекає на натискання клавіші для продовження.
 */
public class GreetingWindow {

    private final Screen screen;
    private final TextGraphics textGraphics;

    /**
     * Конструктор класу, ініціалізує екран та графічні налаштування.
     *
     * @param screen екран, на якому буде виведено вітання
     */
    public GreetingWindow(Screen screen) {
        this.screen = screen;
        this.textGraphics = screen.newTextGraphics();
    }

    /**
     * Виводить вітальне повідомлення на екран по буквах. Після виведення основного повідомлення
     * чекає натискання клавіші для продовження.
     *
     * @throws IOException якщо виникне помилка при роботі з екраном
     */
    public void showGreeting() throws IOException {
        try {
            clearScreen();
            String message = "Вітаємо в програмі Historiography!";
            int xPos = 10;
            int yPos = 5;

            // Виведення кожної букви окремо
            for (int i = 0; i < message.length(); i++) {
                textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
                textGraphics.putString(xPos + i, yPos, String.valueOf(message.charAt(i)));
                screen.refresh();
                Thread.sleep(100);
            }

            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            String continueMessage = "Натисніть будь-яку клавішу для продовження...";
            textGraphics.putString(xPos, yPos + 2, continueMessage);
            screen.refresh();

            screen.readInput();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Очищає екран та оновлює його.
     *
     * @throws IOException якщо виникне помилка при очищенні екрану
     */
    private void clearScreen() throws IOException {
        screen.clear();
        screen.refresh();
    }
}
