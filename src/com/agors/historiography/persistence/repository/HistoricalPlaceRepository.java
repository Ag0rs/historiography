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

public class HistoricalPlaceRepository {

    private static final String FILE_NAME = "data/historicalplaces.json";
    private final List<HistoricalPlace> historicalPlaces;
    private final Gson gson;

    public HistoricalPlaceRepository() {
        gson = new Gson();
        historicalPlaces = loadHistoricalPlaces();
    }

    public List<HistoricalPlace> getHistoricalPlaces() {
        return historicalPlaces;
    }

    public void addHistoricalPlace(HistoricalPlace place) {
        historicalPlaces.add(place);
        saveHistoricalPlaces();
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

    // Змінено з private на public
    public void saveHistoricalPlaces() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(historicalPlaces, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
