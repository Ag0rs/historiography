package com.agors.historiography.domain.message;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Клас, що відповідає за керування повідомленнями, такими як успішні та помилкові. Зберігає та
 * відображає повідомлення в інтерфейсі програми.
 */
public class MessageManager {

    private String successMessage = "";
    private String successMessage2 = "";
    private String errorMessage = "";

    /**
     * Встановлює успішне повідомлення для першого випадку.
     *
     * @param message Текст успішного повідомлення.
     */
    public void setSuccessMessage(String message) {
        this.successMessage = message;
    }

    /**
     * Встановлює успішне повідомлення для другого випадку.
     *
     * @param message Текст успішного повідомлення.
     */
    public void setSuccessMessage2(String message) {
        this.successMessage2 = message;
    }

    /**
     * Встановлює повідомлення про помилку.
     *
     * @param message Текст повідомлення про помилку.
     */
    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    /**
     * Очищає всі повідомлення (успішні та помилкові).
     */
    public void clearMessages() {
        this.successMessage = "";
        this.successMessage2 = "";
        this.errorMessage = "";
    }

    /**
     * Виводить повідомлення в інтерфейсі користувача. Якщо є помилка, вона відображається червоним
     * кольором, успішне повідомлення — зеленим.
     *
     * @param textGraphics Графічний об'єкт для відображення тексту на екрані.
     */
    public void displayMessages(TextGraphics textGraphics) {
        if (!errorMessage.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 11, errorMessage);
        }
        if (!successMessage.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
            textGraphics.putString(10, 11, successMessage);
        }
    }

    /**
     * Виводить повідомлення для реєстрації в інтерфейсі користувача. Якщо є помилка, вона
     * відображається червоним кольором, успішне повідомлення — зеленим.
     *
     * @param textGraphics Графічний об'єкт для відображення тексту на екрані.
     */
    public void displayMessagesReg(TextGraphics textGraphics) {
        if (!errorMessage.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 17, errorMessage);
        }
        if (!successMessage2.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
            textGraphics.putString(10, 17, successMessage2);
        }
    }
}
