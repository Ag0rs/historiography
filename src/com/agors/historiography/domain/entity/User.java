package com.agors.historiography.domain.entity;

/**
 * Клас, що представляє користувача в системі. Зберігає інформацію про користувача, таку як ім'я
 * користувача, електронну пошту, пароль та роль користувача.
 */
public class User {

    private String username;
    private String email;
    private String password;
    private String role;

    /**
     * Конструктор класу {@code User} для створення користувача з усіма параметрами.
     *
     * @param username Ім'я користувача.
     * @param email    Електронна пошта користувача.
     * @param password Пароль користувача.
     * @param role     Роль користувача (наприклад, "Admin" або "User").
     */
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Конструктор класу {@code User} для створення користувача з дефолтною роллю "User".
     *
     * @param username Ім'я користувача.
     * @param email    Електронна пошта користувача.
     * @param password Пароль користувача.
     */
    public User(String username, String email, String password) {
        this(username, email, password, "User");
    }

    /**
     * Отримує ім'я користувача.
     *
     * @return Ім'я користувача.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Встановлює ім'я користувача.
     *
     * @param username Ім'я користувача.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Отримує електронну пошту користувача.
     *
     * @return Електронна пошта користувача.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Встановлює електронну пошту користувача.
     *
     * @param email Електронна пошта користувача.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Отримує пароль користувача.
     *
     * @return Пароль користувача.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Встановлює пароль користувача.
     *
     * @param password Пароль користувача.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Отримує роль користувача.
     *
     * @return Роль користувача.
     */
    public String getRole() {
        return role;
    }

    /**
     * Встановлює роль користувача.
     *
     * @param role Роль користувача (наприклад, "Admin" або "User").
     */
    public void setRole(String role) {
        this.role = role;
    }
}
