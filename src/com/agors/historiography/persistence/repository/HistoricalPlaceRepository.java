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

/**
 * Репозиторій для роботи з історичними місцями. Цей клас надає методи для завантаження, збереження
 * та пошуку історичних місць у файлі формату JSON.
 */
public class HistoricalPlaceRepository {

    private static final String FILE_NAME = "data/historicalplaces.json";
    private final List<HistoricalPlace> historicalPlaces;
    private final Gson gson;
    private List<String> searchCriteria;

    /**
     * Конструктор, який ініціалізує репозиторій, завантажує історичні місця та критерії пошуку з
     * відповідних файлів.
     */
    public HistoricalPlaceRepository() {
        gson = new Gson();
        historicalPlaces = loadHistoricalPlaces();
        loadSearchCriteria();
    }

    /**
     * Завантажує критерії пошуку з файлу `data/searchCriteria.json`. Якщо файл не знайдено або
     * сталася помилка, створюється порожній список критеріїв.
     */
    private void loadSearchCriteria() {
        try (Reader reader = new FileReader("data/searchCriteria.json")) {
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            searchCriteria = gson.fromJson(reader, listType);
        } catch (IOException e) {
            searchCriteria = new ArrayList<>();
        }
    }

    /**
     * Повертає список усіх історичних місць.
     *
     * @return список історичних місць.
     */
    public List<HistoricalPlace> getHistoricalPlaces() {
        return historicalPlaces;
    }

    /**
     * Додає нове історичне місце до списку та зберігає зміни у файл.
     *
     * @param place історичне місце, яке потрібно додати.
     */
    public void addHistoricalPlace(HistoricalPlace place) {
        historicalPlaces.add(place);
        saveHistoricalPlaces();
    }

    /**
     * Шукає історичні місця за вказаним запитом. Пошук здійснюється за кожним із критеріїв,
     * зазначених у файлі `searchCriteria.json`, таких як: "name", "description", "location",
     * "category".
     *
     * @param query запит для пошуку.
     * @return список історичних місць, що відповідають запиту.
     */
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

    /**
     * Завантажує історичні місця з файлу `data/historicalplaces.json`. Якщо файл не знайдено або
     * сталася помилка, повертається порожній список.
     *
     * @return список історичних місць.
     */
    private List<HistoricalPlace> loadHistoricalPlaces() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type listType = new TypeToken<List<HistoricalPlace>>() {
            }.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Зберігає поточний список історичних місць у файл `data/historicalplaces.json`.
     */
    public void saveHistoricalPlaces() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(historicalPlaces, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
