package com.rocketshipcheckingtool.ui.roles.technician;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsePartPopupController {
    public Button subtractButton;
    public TextField quantityField;
    public Button addButton;
    public Button useButton;
    public Label price;
    public Label part;
    public Label totalPrice;
    public ComboBox<String> shuttleComboBox;

    private static final Logger logger = LoggerFactory.getLogger(UsePartPopupController.class);

    private int maxQuantity = 0;
    private int quantity = 1;
    private boolean isUseButton = false;

    public void initialize() {
        logger.debug("Initializing UsePartPopupController with quantity={}", quantity);
        quantityField.setText(String.valueOf(quantity));
        setupButtons();
    }

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
                logger.error("Invalid quantity input: '{}', resetting to 1", quantityField.getText());
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

    public int getQuantity() {
        return quantity;
    }

    public void setPrice(String price) {
        this.price.setText(price);
        updateGesamtPreis(); // Ensure total price is set after price is injected
    }

    public void setPart(String part) {
        this.part.setText(part);
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public boolean getIsVerwendenButton() {
        return isUseButton;
    }
}
