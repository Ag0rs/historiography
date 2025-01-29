package com.agors.historiography.domain.message;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public class MessageManager {

    private String successMessage = "";
    private String successMessage2 = "";
    private String errorMessage = "";

    // Метод для встановлення успішного повідомлення
    public void setSuccessMessage(String message) {
        this.successMessage = message;
    }

    // Метод для встановлення другого успішного повідомлення
    public void setSuccessMessage2(String message) {
        this.successMessage2 = message;
    }

    // Метод для встановлення повідомлення про помилку
    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    // Метод для очищення повідомлень
    public void clearMessages() {
        this.successMessage = "";
        this.successMessage2 = "";
        this.errorMessage = "";
    }

    // Метод для виведення повідомлень на стандартну позицію
    public void displayMessages(TextGraphics textGraphics) {
        if (!errorMessage.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 11, errorMessage);  // Виведення помилки
        }
        if (!successMessage.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
            textGraphics.putString(10, 11, successMessage);  // Виведення успішного повідомлення
        }
    }

    // Метод для виведення повідомлень в іншому місці (для реєстрації)
    public void displayMessagesReg(TextGraphics textGraphics) {
        if (!errorMessage.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 17, errorMessage);  // Виведення помилки
        }
        if (!successMessage2.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
            textGraphics.putString(10, 17,
                successMessage2);  // Виведення другого успішного повідомлення
        }
    }
}
