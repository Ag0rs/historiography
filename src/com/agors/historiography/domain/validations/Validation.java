package com.agors.historiography.domain.validations;

import java.util.regex.Pattern;

/**
 * Клас для валідації різних даних, таких як ім'я користувача, електронна пошта та пароль. Містить
 * статичні методи для перевірки коректності введених значень.
 */
public class Validation {

    /**
     * Перевіряє, чи є ім'я користувача валідним. Ім'я користувача вважається валідним, якщо його
     * довжина не менша за 3 символи.
     *
     * @param username Ім'я користувача, яке потрібно перевірити.
     * @return true, якщо ім'я користувача валідне, інакше false.
     */
    public static boolean isValidUsername(String username) {
        return username.length() >= 3;
    }

    /**
     * Перевіряє, чи є електронна пошта валідною. Пошта перевіряється за допомогою регулярного
     * виразу для стандартного формату.
     *
     * @param email Електронна пошта, яку потрібно перевірити.
     * @return true, якщо пошта валідна, інакше false.
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * Перевіряє, чи є пароль валідним. Пароль вважається валідним, якщо його довжина не менша за 6
     * символів.
     *
     * @param password Пароль, який потрібно перевірити.
     * @return true, якщо пароль валідний, інакше false.
     */
    public static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
