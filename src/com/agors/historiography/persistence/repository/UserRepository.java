package com.agors.historiography.persistence.repository;

import com.agors.historiography.domain.entitys.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserRepository {

    private static final String USERS_FILE = "data/users.json";

    public UserRepository() {
        // Перевірка на наявність файлу та його створення, якщо він не існує
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        File file = new File(USERS_FILE);

        // Якщо файл не існує, створюємо новий
        if (!file.exists()) {
            try {
                new File("data").mkdirs(); // Створюємо папку "data", якщо її немає
                file.createNewFile(); // Створюємо сам файл

                // Ініціалізуємо файл порожнім JSON-об'єктом з порожнім об'єктом користувачів
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("{\"users\":{}}");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isEmailTaken(String email) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            for (String username : users.keySet()) {
                JsonObject user = users.getAsJsonObject(username);
                if (user.get("email").getAsString().equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsernameTaken(String username) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            if (users.has(username)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUser(User user) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            JsonObject newUser = new JsonObject();
            newUser.addProperty("email", user.getEmail());
            newUser.addProperty("password", user.getPassword());
            users.add(user.getUsername(), newUser);

            // Створюємо Gson з форматуванням для красивого виведення
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

            try (FileWriter writer = new FileWriter(USERS_FILE)) {
                // Записуємо JSON з форматуванням
                prettyGson.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserExists(String usernameOrEmail, String password) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            for (String username : users.keySet()) {
                JsonObject user = users.getAsJsonObject(username);
                // Перевірка по логіну
                if (user.get("email").getAsString().equals(usernameOrEmail) || username.equals(
                    usernameOrEmail)) {
                    if (user.get("password").getAsString().equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUser(String usernameOrEmail, String password) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            for (String username : users.keySet()) {
                JsonObject user = users.getAsJsonObject(username);
                if (user.get("email").getAsString().equals(usernameOrEmail) || username.equals(
                    usernameOrEmail)) {
                    if (user.get("password").getAsString().equals(password)) {
                        return new User(username, user.get("email").getAsString(),
                            user.get("password").getAsString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
