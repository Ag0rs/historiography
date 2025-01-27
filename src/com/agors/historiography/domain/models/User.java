package com.agors.historiography.domain.models;

// У класі User
public class User {

    private final String username;
    private final String email;
    private final String password;

    // Конструктор
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Геттери
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Сеттери, якщо необхідно
}
