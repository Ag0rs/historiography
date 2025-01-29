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
            newUser.addProperty("role", user.getRole()); // Додаємо роль в JSON

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
                // Перевірка по логіну або email
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
                        String role = user.get("role").getAsString(); // Отримуємо роль
                        return new User(username, user.get("email").getAsString(),
                            user.get("password").getAsString(), role); // Повертаємо користувача
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Якщо користувача не знайдено або пароль невірний
    }

    // Додано метод для отримання користувача за логіном
    public User getUserByUsername(String username) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            // Перевірка на наявність користувача за логіном
            if (users.has(username)) {
                JsonObject user = users.getAsJsonObject(username);
                String email = user.get("email").getAsString();
                String password = user.get("password").getAsString();
                String role = user.get("role").getAsString();

                return new User(username, email, password, role); // Повертаємо об'єкт User
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Якщо користувача не знайдено, повертаємо null
    }

    // Додано метод для отримання користувача за email
    public User getUserByEmail(String email) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            for (String username : users.keySet()) {
                JsonObject user = users.getAsJsonObject(username);
                // Перевірка на наявність користувача за email
                if (user.get("email").getAsString().equals(email)) {
                    String password = user.get("password").getAsString();
                    String role = user.get("role").getAsString();

                    return new User(username, email, password, role); // Повертаємо об'єкт User
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Якщо користувача не знайдено, повертаємо null
    }

    // Додано метод для отримання користувача за логіном або email
    public User getUserByUsernameOrEmail(String identifier) {
        // Перевірка чи це логін або пошта
        User user = getUserByUsername(identifier); // Перший пошук за логіном
        if (user == null) {
            user = getUserByEmail(identifier); // Якщо не знайдено, шукаємо за поштою
        }
        return user;
    }
}
