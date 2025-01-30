package com.agors.historiography.domain.validations;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Утилітний клас, що містить допоміжні методи для роботи з даними. Зокрема, клас реалізує хешування
 * паролів за допомогою алгоритму SHA-256.
 */
public class Utils {

    /**
     * Хешує переданий пароль за допомогою алгоритму SHA-256. Метод використовує стандартний
     * алгоритм хешування SHA-256 для генерації хешу пароля у вигляді рядка шістнадцяткових чисел.
     *
     * @param password Пароль, який потрібно захешувати.
     * @return Хеш пароля у вигляді шістнадцяткового рядка.
     * @throws RuntimeException якщо виникає помилка при отриманні алгоритму хешування.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(
                "SHA-256");
            byte[] hashedBytes = messageDigest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
