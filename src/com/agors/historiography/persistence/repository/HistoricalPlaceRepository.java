package com.agors.historiography.persistence.repository;

import com.agors.historiography.domain.entity.HistoricalPlace;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricalPlaceRepository {

    private static final String FILE_NAME = "data/historicalplaces.json";
    private final List<HistoricalPlace> historicalPlaces;
    private final Gson gson;
    private List<String> searchCriteria; // Зберігаємо критерії пошуку

    public HistoricalPlaceRepository() {
        gson = new Gson();
        historicalPlaces = loadHistoricalPlaces();
        loadSearchCriteria(); // Завантажуємо критерії пошуку
    }

    // Завантажуємо критерії пошуку з JSON
    private void loadSearchCriteria() {
        try (Reader reader = new FileReader("data/searchCriteria.json")) {
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            searchCriteria = gson.fromJson(reader, listType);
        } catch (IOException e) {
            searchCriteria = new ArrayList<>();
        }
    }

    public List<HistoricalPlace> getHistoricalPlaces() {
        return historicalPlaces;
    }

    public void addHistoricalPlace(HistoricalPlace place) {
        historicalPlaces.add(place);
        saveHistoricalPlaces();
    }

    // Метод для пошуку місць за критеріями
    public List<HistoricalPlace> searchHistoricalPlaces(String query) {
        return historicalPlaces.stream()
            .filter(place -> searchCriteria.stream().anyMatch(criterion -> {
                switch (criterion.toLowerCase()) {
                    case "name":
                        return place.getName().toLowerCase().contains(query.toLowerCase());
                    case "description":
                        return place.getDescription().toLowerCase().contains(query.toLowerCase());
                    case "location":
                        return place.getLocation().toLowerCase().contains(query.toLowerCase());
                    case "category":
                        return place.getCategory().toLowerCase().contains(query.toLowerCase());
                    default:
                        return false;
                }
            }))
            .collect(Collectors.toList());
    }

    private List<HistoricalPlace> loadHistoricalPlaces() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type listType = new TypeToken<List<HistoricalPlace>>() {
            }.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // Збереження списку місць в JSON
    public void saveHistoricalPlaces() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(historicalPlaces, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
