package com.agors.historiography.domain.validations;

import java.util.regex.Pattern;

public class Validation {

    public static boolean isValidUsername(String username) {
        // Простий приклад перевірки на мінімум 3 символи
        return username.length() >= 3;
    }

    public static boolean isValidEmail(String email) {
        // Перевірка на правильність формату пошти
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    public static boolean isValidPassword(String password) {
        // Приклад перевірки пароля на довжину не менше 6 символів
        return password.length() >= 6;
    }
}
