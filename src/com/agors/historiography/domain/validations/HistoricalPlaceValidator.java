package com.agors.historiography.domain.validations;

public class HistoricalPlaceValidator {

    public static boolean isValid(String name, String description, String location,
        String category) {
        return name != null && !name.trim().isEmpty()
            && description != null && !description.trim().isEmpty()
            && location != null && !location.trim().isEmpty()
            && category != null && !category.trim().isEmpty();
    }
}
