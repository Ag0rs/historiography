package com.agors.historiography.domain.entity;

public class HistoricalPlace {

    private int id;
    private String name;
    private String description;
    private String location;
    private String category;

    public HistoricalPlace(int id, String name, String description, String location,
        String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

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

