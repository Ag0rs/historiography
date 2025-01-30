package com.agors.historiography.domain.validations;

/**
 * Клас, що відповідає за валідацію даних для історичних місць. Перевіряє, чи не є дані порожніми
 * або null.
 */
public class HistoricalPlaceValidator {

    /**
     * Перевіряє, чи є всі передані параметри валідними. Для цього перевіряється, що жоден з
     * параметрів не є null і не є порожнім рядком після видалення зайвих пробілів.
     *
     * @param name        Назва історичного місця.
     * @param description Опис історичного місця.
     * @param location    Локація історичного місця.
     * @param category    Категорія історичного місця.
     * @return true, якщо всі параметри валідні; false в іншому випадку.
     */
    public static boolean isValid(String name, String description, String location,
        String category) {
        return name != null && !name.trim().isEmpty()
            && description != null && !description.trim().isEmpty()
            && location != null && !location.trim().isEmpty()
            && category != null && !category.trim().isEmpty();
    }
}
