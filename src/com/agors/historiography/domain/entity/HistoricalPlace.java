package com.agors.historiography.domain.entity;

/**
 * Клас, що представляє історичне місце в довіднику. Зберігає інформацію про історичне місце, таку
 * як його ідентифікатор, назва, опис, локація та категорія.
 */
public class HistoricalPlace {

    private int id;
    private String name;
    private String description;
    private String location;
    private String category;

    /**
     * Конструктор класу {@code HistoricalPlace}. Створює новий екземпляр історичного місця з
     * заданими параметрами.
     *
     * @param id          Ідентифікатор історичного місця.
     * @param name        Назва історичного місця.
     * @param description Опис історичного місця.
     * @param location    Локація історичного місця.
     * @param category    Категорія історичного місця.
     */
    public HistoricalPlace(int id, String name, String description, String location,
        String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.category = category;
    }

    /**
     * Отримує ідентифікатор історичного місця.
     *
     * @return Ідентифікатор історичного місця.
     */
    public int getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор історичного місця.
     *
     * @param id Ідентифікатор історичного місця.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Отримує назву історичного місця.
     *
     * @return Назва історичного місця.
     */
    public String getName() {
        return name;
    }

    /**
     * Встановлює назву історичного місця.
     *
     * @param name Назва історичного місця.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Отримує опис історичного місця.
     *
     * @return Опис історичного місця.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Встановлює опис історичного місця.
     *
     * @param description Опис історичного місця.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Отримує локацію історичного місця.
     *
     * @return Локація історичного місця.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Встановлює локацію історичного місця.
     *
     * @param location Локація історичного місця.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Отримує категорію історичного місця.
     *
     * @return Категорія історичного місця.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Встановлює категорію історичного місця.
     *
     * @param category Категорія історичного місця.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Повертає рядкове представлення об'єкта {@code HistoricalPlace}. Використовується для
     * виведення інформації про історичне місце.
     *
     * @return Рядкове представлення об'єкта.
     */
    @Override
    public String toString() {
        return "HistoricalPlace{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", location='" + location + '\'' +
            ", category='" + category + '\'' +
            '}';
    }
}
