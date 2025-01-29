package com.agors.historiography.domain.entity;

public class User {

    private String username;
    private String email;
    private String password;
    private String role; // Додане поле ролі

    // Оновлений конструктор з підтримкою ролі
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Конструктор без ролі (за потреби, для сумісності)
    public User(String username, String email, String password) {
        this(username, email, password, "User"); // Роль за замовчуванням — "User"
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() { // Геттер для ролі
        return role;
    }

    public void setRole(String role) { // Сеттер для ролі
        this.role = role;
    }
}
