package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.helper.Util;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Controller class for managing the popup window for ordering parts.
 * Handles user input, validation, and order processing logic.
 */
public class OrderPopupController {
    private static final Logger log = LoggerFactory.getLogger(OrderPopupController.class); // Logger instance for logging activities.

    public Button subtractButton; // Button to decrease the quantity.
    public TextField quantityField; // TextField to display and edit the quantity.
    public Button addButton; // Button to increase the quantity.
    public Button orderButton; // Button to confirm the order.
    public Label price; // Label to display the price of a single part.
    public Label totalPrice; // Label to display the total price based on quantity.
    public Label part; // Label to display the part name.
    public ComboBox<String> shuttleComboBox; // ComboBox to select a shuttle.

    private int quantity = 1; // Current quantity of the part to be ordered.
    private boolean isOrderButton = false; // Flag to indicate if the order button was clicked.
    private ClientRequests clientRequests; // ClientRequests instance for making HTTP requests.
    private final String user = UserSession.getRole().name().toLowerCase(); // Current user's role in lowercase.
    private ArrayList<Shuttle> shuttles; // List of shuttles available for selection.

    /**
     * Initializes the controller and sets up the UI components.
     * Configures the buttons and sets the default quantity.
     */
    public void initialize() {
        quantityField.setText(String.valueOf(quantity));
        shuttleComboBox.getStyleClass().add("comboBox");
        setupButtons();
        log.debug("OrderPopupController initialized with quantity={}", quantity);
    }

    /**
     * Configures the actions for the buttons (subtract, add, quantity field, and order).
     * Handles user interactions and updates the total price accordingly.
     */
    private void setupButtons() {
        subtractButton.setOnAction(event -> {
            if (quantity > 1) {
                quantity--;
                quantityField.setText(String.valueOf(quantity));
                updateGesamtPreis();
                log.debug("Decreased quantity to {}", quantity);
            }
        });

        addButton.setOnAction(event -> {
            quantity++;
            quantityField.setText(String.valueOf(quantity));
            updateGesamtPreis();
            log.debug("Increased quantity to {}", quantity);
        });

        quantityField.setOnAction(event -> {
            try {
                int oldQuantity = quantity;
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity < 1) {
                    log.warn("Entered quantity < 1, resetting to 1");
                    quantity = 1;
                }
                if (quantity > 999) {
                    log.warn("Entered quantity > 999, resetting to 999");
                    quantity = 999;
                }
                quantityField.setText(String.valueOf(quantity));
                updateGesamtPreis();
                log.debug("Quantity field updated from {} to {}", oldQuantity, quantity);
            } catch (NumberFormatException e) {
                log.error("Invalid quantity input '{}', resetting to 1", quantityField.getText());
                quantityField.setText("1");
                quantity = 1;
            }
        });

        orderButton.setOnAction(event -> {
            log.info("Order button clicked with quantity={}", quantity);
            Stage stage = (Stage) orderButton.getScene().getWindow();
            isOrderButton = true;
            stage.close();
        });
    }

    /**
     * Updates the total price label based on the current quantity and price.
     * Handles invalid or empty price values gracefully.
     */
    private void updateGesamtPreis() {
        String preisText = price.getText().replace(",", ".").trim();
        if (preisText.isEmpty()) {
            totalPrice.setText("0.00 €");
            log.warn("Price label is empty, setting total price to 0.00 €");
            return;
        }

        try {
            double preisValue = Double.parseDouble(preisText);
            double total = preisValue * quantity;
            totalPrice.setText(String.format("%.2f €", total));
            log.debug("Updated total price to {} € for quantity {}", total, quantity);
        } catch (NumberFormatException e) {
            totalPrice.setText("Invalid price");
            log.error("Invalid price format: '{}'", preisText);
        }
    }

    /**
     * Retrieves the current quantity of the part to be ordered.
     *
     * @return The current quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the price label and updates the total price.
     *
     * @param price The price of a single part as a string.
     */
    public void setPrice(String price) {
        this.price.setText(price);
        updateGesamtPreis(); // Ensure total price is set after price is injected
        log.debug("Set price label to '{}'", price);
    }

    /**
     * Sets the part label to display the part name.
     *
     * @param part The name of the part.
     */
    public void setPart(String part) {
        this.part.setText(part);
        log.debug("Set part label to '{}'", part);
    }

    /**
     * Checks if the order button was clicked.
     *
     * @return True if the order button was clicked, false otherwise.
     */
    public boolean getIsBestellenButton() {
        return isOrderButton;
    }

    /**
     * Loads the shuttle names into the ComboBox.
     * Filters shuttles based on their status and adds them to the ComboBox.
     */
    public void loadCombobox() {
        shuttleComboBox.getItems().clear();
        try {
            shuttles = ShuttleUtil.getShuttles(clientRequests, user);
            shuttleComboBox.getItems().add("Kein Shuttle");
            for (Shuttle shuttle : shuttles) {
                if (shuttle.getStatus().equals("Gelandet") || shuttle.getStatus().equals("Inspektion 1") || shuttle.getStatus().equals("InWartung")) {
                    shuttleComboBox.getItems().add(shuttle.getShuttleName());
                }
            }
            shuttleComboBox.setValue("Kein Shuttle");
            log.info("Loaded {} shuttles into combobox", shuttles != null ? shuttles.size() : 0);
        } catch (Exception e) {
            log.error("Error loading shuttles: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Laden der Shuttles: " + e.getMessage());
        }
    }

    /**
     * Sets the ClientRequests instance and loads the shuttle ComboBox.
     *
     * @param clientRequests The ClientRequests instance to be set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadCombobox();
        log.debug("ClientRequests set and combobox loaded");
    }

    /**
     * Retrieves the selected shuttle from the ComboBox.
     *
     * @return The selected Shuttle object, or null if no valid shuttle is selected.
     */
    public Shuttle getSelectedShuttle() {
        String selectedShuttleName = shuttleComboBox.getValue();
        if (selectedShuttleName != null && !selectedShuttleName.equals("Kein Shuttle")) {
            Shuttle selected = shuttles.stream()
                    .filter(shuttle -> shuttle.getShuttleName().equals(selectedShuttleName))
                    .findFirst()
                    .orElse(null);
            log.debug("Selected shuttle: '{}'", selectedShuttleName);
            return selected;
        }
        log.debug("No shuttle selected or 'Kein Shuttle' chosen");
        return null;
    }
}