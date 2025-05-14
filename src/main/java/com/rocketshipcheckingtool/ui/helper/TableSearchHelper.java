package com.rocketshipcheckingtool.ui.helper;

import javafx.application.Platform;
import javafx.scene.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A helper class for adding search functionality to a JavaFX TableView.
 * It provides a search field with suggestions and allows selecting items in the table based on the search input.
 *
 * @param <T> The type of items in the TableView.
 */
public class TableSearchHelper<T> {

    private static final Logger logger = LoggerFactory.getLogger(TableSearchHelper.class); // Logger instance for logging activities.

    private final TableView<T> tableView; // The TableView to which the search functionality is applied.
    private final TextField searchField; // The TextField used for entering search queries.
    private final Function<T, String> searchableTextProvider; // A function to extract searchable text from table items.
    private final ContextMenu suggestionsPopup = new ContextMenu(); // A popup menu for displaying search suggestions.
    private List<T> allItems = List.of(); // The list of all items in the TableView.

    /**
     * Constructs a TableSearchHelper instance.
     *
     * @param tableView The TableView to which the search functionality is applied.
     * @param searchField The TextField used for entering search queries.
     * @param searchableTextProvider A function to extract searchable text from table items.
     */
    public TableSearchHelper(TableView<T> tableView, TextField searchField, Function<T, String> searchableTextProvider) {
        this.tableView = tableView;
        this.searchField = searchField;
        this.searchableTextProvider = searchableTextProvider;

        initializeSearchFieldListener();
        logger.debug("TableSearchHelper initialized");
    }

    /**
     * Initializes the listener for the search field to handle text changes and search actions.
     */
    private void initializeSearchFieldListener() {
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            logger.debug("Search field changed from '{}' to '{}'", oldText, newText);
            if (newText == null || newText.isEmpty()) {
                suggestionsPopup.hide();
                logger.debug("Suggestions popup hidden due to empty search");
            } else {
                List<String> matches = allItems.stream()
                        .map(searchableTextProvider)
                        .filter(name -> name.toLowerCase().contains(newText.toLowerCase()))
                        .limit(5)
                        .collect(Collectors.toList());

                if (matches.isEmpty()) {
                    suggestionsPopup.hide();
                    logger.debug("No matches found for '{}', suggestions popup hidden", newText);
                } else {
                    showSuggestions(matches);
                    logger.debug("Showing {} suggestions for '{}'", matches.size(), newText);
                }
            }
        });

        searchField.setOnAction(e -> {
            String input = searchField.getText();
            logger.info("Search action triggered for '{}'", input);
            selectItem(input);
            suggestionsPopup.hide();
        });
    }

    /**
     * Displays the suggestions popup with the provided list of suggestions.
     *
     * @param suggestions The list of suggestions to display.
     */
    private void showSuggestions(List<String> suggestions) {
        List<CustomMenuItem> menuItems = suggestions.stream().map(name -> {
            Label entryLabel = new Label(name);
            entryLabel.setStyle("-fx-padding: 8px 16px; -fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 13px");
            entryLabel.setMaxWidth(Double.MAX_VALUE);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(e -> {
                logger.info("Suggestion '{}' selected from popup", name);
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

    /**
     * Selects an item in the TableView based on the provided search text.
     *
     * @param searchText The text to search for in the TableView items.
     */
    private void selectItem(String searchText) {
        for (int i = 0; i < allItems.size(); i++) {
            String value = searchableTextProvider.apply(allItems.get(i));
            if (value.equalsIgnoreCase(searchText)) {
                final int index = i;
                logger.info("Selecting item at index {} for search text '{}'", index, searchText);
                Platform.runLater(() -> {
                    tableView.scrollTo(index);
                    tableView.getSelectionModel().clearAndSelect(index);
                });
                break;
            }
        }
    }

    /**
     * Sets the list of items in the TableView.
     *
     * @param items The list of items to set.
     */
    public void setItems(List<T> items) {
        this.allItems = items;
        logger.debug("Items set in TableSearchHelper: {} items", items != null ? items.size() : 0);
    }
}