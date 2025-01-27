package com.agors.historiography.repository;

import com.agors.historiography.domain.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String FILE_PATH = "users.json";
    private final List<User> users;

    public UserRepository() {
        users = loadUsers();
    }

    private List<User> loadUsers() {
        try {
            if (Files.exists(Paths.get(FILE_PATH))) {
                String json = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
                Gson gson = new Gson();
                User[] userArray = gson.fromJson(json, User[].class);
                if (userArray != null) {
                    return new ArrayList<>(List.of(userArray));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void saveUsers() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public boolean isEmailTaken(String email) {
        return users.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public boolean isUsernameTaken(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }

    // Метод для отримання користувача за ім'ям
    public User getUserByUsername(String username) {
        return users.stream()
            .filter(user -> user.getUsername().equalsIgnoreCase(username))
            .findFirst()
            .orElse(null); // Якщо користувача не знайдено, повертаємо null
    }

    // Метод для отримання пароля користувача
    public String getPasswordByUsername(String username) {
        User user = getUserByUsername(username);
        return user != null ? user.getPassword()
            : null; // Повертаємо пароль, якщо користувач знайдений
    }
}
