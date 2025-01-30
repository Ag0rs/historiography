package com.agors.historiography.persistence.repository;

import com.agors.historiography.domain.entity.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторій для роботи з користувачами. Цей клас надає методи для додавання, видалення, перевірки
 * наявності користувача, а також для завантаження користувачів з файлу формату JSON.
 */
public class UserRepository {

    private static final String USERS_FILE = "data/users.json";
    private User currentUser;

    /**
     * Конструктор, який перевіряє існування файлу користувачів та створює його, якщо він не існує.
     */
    public UserRepository() {
        createFileIfNotExists();
    }

    /**
     * Створює файл з користувачами, якщо він не існує.
     */
    private void createFileIfNotExists() {
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            try {
                new File("data").mkdirs();
                file.createNewFile();

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("{\"users\":{}}");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Повертає поточного користувача.
     *
     * @return поточного користувача.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Встановлює поточного користувача.
     *
     * @param user новий поточний користувач.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Перевіряє, чи існує користувач з таким електронною поштою.
     *
     * @param email електронна пошта користувача.
     * @return true, якщо електронна пошта вже використовується, інакше false.
     */
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

    /**
     * Перевіряє, чи існує користувач з таким ім'ям користувача.
     *
     * @param username ім'я користувача.
     * @return true, якщо ім'я користувача вже використовується, інакше false.
     */
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

    /**
     * Додає нового користувача.
     *
     * @param user користувач для додавання.
     */
    public void addUser(User user) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            JsonObject newUser = new JsonObject();
            newUser.addProperty("email", user.getEmail());
            newUser.addProperty("password", user.getPassword());
            newUser.addProperty("role", user.getRole());

            users.add(user.getUsername(), newUser);

            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

            try (FileWriter writer = new FileWriter(USERS_FILE)) {
                prettyGson.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Перевіряє, чи існує користувач з таким ім'ям користувача або електронною поштою та паролем.
     *
     * @param usernameOrEmail ім'я користувача або електронна пошта.
     * @param password        пароль користувача.
     * @return true, якщо користувач існує, інакше false.
     */
    public boolean isUserExists(String usernameOrEmail, String password) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            for (String username : users.keySet()) {
                JsonObject user = users.getAsJsonObject(username);
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

    /**
     * Повертає користувача за ім'ям користувача або електронною поштою.
     *
     * @param usernameOrEmail ім'я користувача або електронна пошта.
     * @param password        пароль користувача.
     * @return користувача, якщо знайдений, інакше null.
     */
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
                        String role = user.get("role").getAsString();
                        return new User(username, user.get("email").getAsString(),
                            user.get("password").getAsString(), role);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Повертає користувача за ім'ям користувача.
     *
     * @param username ім'я користувача.
     * @return користувача, якщо знайдений, інакше null.
     */
    public User getUserByUsername(String username) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            if (users.has(username)) {
                JsonObject user = users.getAsJsonObject(username);
                String email = user.get("email").getAsString();
                String password = user.get("password").getAsString();
                String role = user.get("role").getAsString();

                return new User(username, email, password, role);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Повертає користувача за електронною поштою.
     *
     * @param email електронна пошта користувача.
     * @return користувача, якщо знайдений, інакше null.
     */
    public User getUserByEmail(String email) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            for (String username : users.keySet()) {
                JsonObject user = users.getAsJsonObject(username);
                if (user.get("email").getAsString().equals(email)) {
                    String password = user.get("password").getAsString();
                    String role = user.get("role").getAsString();

                    return new User(username, email, password, role);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Повертає користувача за ім'ям користувача або електронною поштою.
     *
     * @param identifier ім'я користувача або електронна пошта.
     * @return користувача, якщо знайдений, інакше null.
     */
    public User getUserByUsernameOrEmail(String identifier) {
        User user = getUserByUsername(identifier);
        if (user == null) {
            user = getUserByEmail(identifier);
        }
        return user;
    }

    /**
     * Повертає список усіх користувачів.
     *
     * @return список користувачів.
     */
    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            for (String username : users.keySet()) {
                JsonObject userJson = users.getAsJsonObject(username);
                String email = userJson.get("email").getAsString();
                String password = userJson.get("password").getAsString();
                String role = userJson.get("role").getAsString();

                usersList.add(new User(username, email, password, role));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    /**
     * Видаляє користувача за ім'ям користувача.
     *
     * @param username ім'я користувача для видалення.
     */
    public void deleteUser(String username) {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonObject users = data.getAsJsonObject("users");

            if (users.has(username)) {
                users.remove(username);

                Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

                try (FileWriter writer = new FileWriter(USERS_FILE)) {
                    prettyGson.toJson(data, writer);
                }
            } else {
                System.out.println("Користувач не знайдений.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}