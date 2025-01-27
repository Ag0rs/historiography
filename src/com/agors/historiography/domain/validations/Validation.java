package com.agors.historiography.domain.validations;

public class Validation {

    public static boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9._]{3,30}$");
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        return email.matches(regex);
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
