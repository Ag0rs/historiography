package com.agors.historiography.appui.forms;

import com.agors.historiography.domain.entity.HistoricalPlace;
import com.agors.historiography.persistence.repository.HistoricalPlaceRepository;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.List;

public class ViewHistoricalPlacesUI {

    private final HistoricalPlaceRepository repository;
    private final Screen screen;

    public ViewHistoricalPlacesUI(HistoricalPlaceRepository repository, Screen screen) {
        this.repository = repository;
        this.screen = screen;
    }

    public void show() throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();

        List<HistoricalPlace> places = repository.getHistoricalPlaces();

        if (places.isEmpty()) {
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(10, 5, "Немає доданих історичних місць!");
            screen.refresh();
            screen.readInput();
            return;
        }

        int selectedIndex = 0;
        while (true) {
            screen.clear();

            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(10, 2, "Список історичних місць:");

            for (int i = 0; i < places.size(); i++) {
                HistoricalPlace place = places.get(i);

                if (i == selectedIndex) {
                    textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }

                textGraphics.putString(10, 4 + i, (i + 1) + ". " + place.getName());
            }

            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(10, places.size() + 6,
                "↑ Вгору   ↓ Вниз   Enter - Переглянути   Esc - Вихід");

            screen.refresh();
            KeyStroke keyStroke = screen.readInput();

            switch (keyStroke.getKeyType()) {
                case ArrowUp:
                    if (selectedIndex > 0) {
                        selectedIndex--;
                    }
                    break;
                case ArrowDown:
                    if (selectedIndex < places.size() - 1) {
                        selectedIndex++;
                    }
                    break;
                case Enter:
                    showPlaceDetails(places.get(selectedIndex));
                    break;
                case Escape:
                    return;
                default:
                    break;
            }
        }
    }

    private void showPlaceDetails(HistoricalPlace place) throws IOException {
        screen.clear();
        TextGraphics textGraphics = screen.newTextGraphics();

        String[] fields = {"Назва", "Опис", "Локація", "Категорія"};
        String[] values = {place.getName(), place.getDescription(), place.getLocation(),
            place.getCategory()};

        final int MAX_LINE_WIDTH = 55;
        final int VERTICAL_OFFSET = 5;

        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(10, 2, "Детальна інформація:");

        for (int i = 0; i < fields.length; i++) {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(9, 3 + i * VERTICAL_OFFSET,
                "┌─────────────────────────────────────────────────────────────────┐");
            textGraphics.putString(9, 4 + i * VERTICAL_OFFSET,
                "│                                                                 │");
            textGraphics.putString(9, 5 + i * VERTICAL_OFFSET,
                "│                                                                 │");
            textGraphics.putString(9, 6 + i * VERTICAL_OFFSET,
                "│                                                                 │");
            textGraphics.putString(9, 7 + i * VERTICAL_OFFSET,
                "└─────────────────────────────────────────────────────────────────┘");

            textGraphics.setForegroundColor(ANSI.WHITE);
            textGraphics.putString(10, 4 + i * VERTICAL_OFFSET, fields[i] + ": ");

            String value = values[i];
            int yOffset = 3 + i * VERTICAL_OFFSET + 1;
            for (int j = 0; j < value.length(); j += MAX_LINE_WIDTH) {
                int endIndex = Math.min(j + MAX_LINE_WIDTH, value.length());
                textGraphics.putString(20, yOffset, value.substring(j, endIndex));
                yOffset++;
            }
        }

        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(10, fields.length * VERTICAL_OFFSET + 8,
            "Натисніть будь-яку клавішу для повернення...");

        screen.refresh();
        screen.readInput(); // Очікуємо натискання клавіші перед виходом
    }
}
