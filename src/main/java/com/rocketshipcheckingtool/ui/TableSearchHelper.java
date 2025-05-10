package com.rocketshipcheckingtool.ui;

import javafx.application.Platform;
import javafx.scene.control.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TableSearchHelper<T> {

    private final TableView<T> tableView;
    private final TextField searchField;
    private final Function<T, String> searchableTextProvider;
    private final ContextMenu suggestionsPopup = new ContextMenu();
    private List<T> allItems = List.of();

    public TableSearchHelper(TableView<T> tableView, TextField searchField, Function<T, String> searchableTextProvider) {
        this.tableView = tableView;
        this.searchField = searchField;
        this.searchableTextProvider = searchableTextProvider;

        initializeSearchFieldListener();
    }

    private void initializeSearchFieldListener() {
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                suggestionsPopup.hide();
            } else {
                List<String> matches = allItems.stream()
                        .map(searchableTextProvider)
                        .filter(name -> name.toLowerCase().contains(newText.toLowerCase()))
                        .limit(5)
                        .collect(Collectors.toList());

                if (matches.isEmpty()) {
                    suggestionsPopup.hide();
                } else {
                    showSuggestions(matches);
                }
            }
        });

        searchField.setOnAction(e -> {
            String input = searchField.getText();
            selectItem(input);
            suggestionsPopup.hide();
        });
    }

    private void showSuggestions(List<String> suggestions) {
        List<CustomMenuItem> menuItems = suggestions.stream().map(name -> {
            Label entryLabel = new Label(name);
            entryLabel.setStyle("-fx-padding: 8px 16px; -fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 13px");
            entryLabel.setMaxWidth(Double.MAX_VALUE);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(e -> {
                searchField.setText(name);
                selectItem(name);
                suggestionsPopup.hide();
            });
            return item;
        }).collect(Collectors.toList());

        suggestionsPopup.getItems().setAll(menuItems);
        if (!suggestionsPopup.isShowing()) {
            suggestionsPopup.show(searchField, javafx.geometry.Side.BOTTOM, 0, 0);
        }
    }

    private void selectItem(String searchText) {
        for (int i = 0; i < allItems.size(); i++) {
            String value = searchableTextProvider.apply(allItems.get(i));
            if (value.equalsIgnoreCase(searchText)) {
                final int index = i;
                Platform.runLater(() -> {
                    tableView.scrollTo(index);
                    tableView.getSelectionModel().clearAndSelect(index);
                });
                break;
            }
        }
    }

    public void setItems(List<T> items) {
        this.allItems = items;
    }
}