package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.helper.Util;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for managing the popup window for using parts.
 * Handles user input, validation, and interaction logic for using parts from inventory.
 */
public class UsePartPopupController {
    public Button subtractButton; // Button to decrease the quantity.
    public TextField quantityField; // TextField to display and edit the quantity.
    public Button addButton; // Button to increase the quantity.
    public Button useButton; // Button to confirm the use of the part.
    public Label price; // Label to display the price of a single part.
    public Label part; // Label to display the part name.
    public Label totalPrice; // Label to display the total price based on quantity.
    public ComboBox<String> shuttleComboBox; // ComboBox to select a shuttle.

    private static final Logger logger = LoggerFactory.getLogger(UsePartPopupController.class); // Logger instance for logging activities.

    private int maxQuantity = 0; // Maximum quantity of the part that can be used.
    private int quantity = 1; // Current quantity of the part to be used.
    private boolean isUseButton = false; // Flag to indicate if the use button was clicked.

    /**
     * Initializes the controller and sets up the UI components.
     * Configures the buttons and sets the default quantity.
     */
    public void initialize() {
        logger.debug("Initializing UsePartPopupController with quantity={}", quantity);
        quantityField.setText(String.valueOf(quantity));
        setupButtons();
    }

    /**
     * Configures the actions for the buttons (subtract, add, quantity field, and use).
     * Handles user interactions and updates the total price accordingly.
     */
    private void setupButtons() {
        subtractButton.setOnAction(event -> {
            if (quantity > 1) {
                quantity--;
                quantityField.setText(String.valueOf(quantity));
                updateGesamtPreis();
                logger.debug("Decreased quantity to {}", quantity);
            }
        });

        addButton.setOnAction(event -> {
            if (quantity >= maxQuantity) {
                logger.info("Attempted to increase quantity above max ({})", maxQuantity);
                return; // Prevent increasing quantity beyond max
            }
            quantity++;
            quantityField.setText(String.valueOf(quantity));
            updateGesamtPreis();
            logger.debug("Increased quantity to {}", quantity);
        });

        quantityField.setOnAction(event -> {
            try {
                int oldQuantity = quantity;
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity < 1) {
                    logger.warn("Entered quantity < 1, resetting to 1");
                    quantity = 1;
                }
                if (quantity > maxQuantity) {
                    logger.warn("Entered quantity > maxQuantity ({}), resetting to maxQuantity", maxQuantity);
                    quantity = maxQuantity; // Prevent increasing quantity beyond max
                }
                quantityField.setText(String.valueOf(quantity));
                updateGesamtPreis();
                logger.debug("Quantity field updated from {} to {}", oldQuantity, quantity);
            } catch (NumberFormatException e) {
                logger.error("Invalid input for Quantity: '{}', set to 1", quantityField.getText());
                Util.showErrorDialog("Ungültige Eingabe für Menge. Bitte geben Sie eine Zahl ein.");
                quantityField.setText("1");
                quantity = 1;
            }
        });

        useButton.setOnAction(event -> {
            logger.info("Use button clicked with quantity={}", quantity);
            Stage stage = (Stage) useButton.getScene().getWindow();
            isUseButton = true;
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
            logger.warn("Price label is empty, setting total price to 0.00 €");
            return;
        }

        try {
            double preisValue = Double.parseDouble(preisText);
            double total = preisValue * quantity;
            totalPrice.setText(String.format("%.2f €", total));
            logger.debug("Updated total price to {} € for quantity {}", total, quantity);
        } catch (NumberFormatException e) {
            totalPrice.setText("Invalid price");
            logger.error("Invalid price format: '{}'", preisText);
        }
    }

    /**
     * Retrieves the current quantity of the part to be used.
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
    }

    /**
     * Sets the part label to display the part name.
     *
     * @param part The name of the part.
     */
    public void setPart(String part) {
        this.part.setText(part);
    }

    /**
     * Sets the maximum quantity of the part that can be used.
     *
     * @param maxQuantity The maximum quantity.
     */
    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    /**
     * Checks if the use button was clicked.
     *
     * @return True if the use button was clicked, false otherwise.
     */
    public boolean getIsVerwendenButton() {
        return isUseButton;
    }
}