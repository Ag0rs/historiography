package com.agors.historiography.appui.window;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;

public class GreetingWindow {

    private final Screen screen;
    private final TextGraphics textGraphics;

    public GreetingWindow(Screen screen) {
        this.screen = screen;
        this.textGraphics = screen.newTextGraphics();
    }

    public void showGreeting() throws IOException {
        try {
            clearScreen();
            String message = "Вітаємо в програмі Historiography!";
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

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Відновлюємо статус переривання
        }
    }

    private void clearScreen() throws IOException {
        screen.clear();
        screen.refresh();
    }
}
