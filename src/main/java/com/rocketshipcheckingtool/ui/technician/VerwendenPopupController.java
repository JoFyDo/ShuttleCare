package com.rocketshipcheckingtool.ui.technician;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VerwendenPopupController {
    public Button minusButton;
    public TextField quantityField;
    public Button plusButton;
    public Button verwendenButton;
    public Label preis;
    public Label teil;
    public Label gesamtPreis;
    public ComboBox<String> shuttleComboBox;

    private int maxQuantity = 0;
    private int quantity = 1;
    private boolean isVerwendenButton = false;

    public void initialize() {
        quantityField.setText(String.valueOf(quantity));
        setupButtons();
    }

    private void setupButtons() {
        minusButton.setOnAction(event -> {
            if (quantity > 1) {
                quantity--;
                quantityField.setText(String.valueOf(quantity));
                updateGesamtPreis();
            }
        });

        plusButton.setOnAction(event -> {
            if (quantity >= maxQuantity) {
                return; // Prevent increasing quantity beyond max
            }
            quantity++;
            quantityField.setText(String.valueOf(quantity));
            updateGesamtPreis();
        });

        quantityField.setOnAction(event -> {
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity < 1) {
                    quantity = 1;
                }
                if (quantity > maxQuantity) {
                    quantity = maxQuantity; // Prevent increasing quantity beyond max
                }
                quantityField.setText(String.valueOf(quantity));
                updateGesamtPreis();
            } catch (NumberFormatException e) {
                quantityField.setText("1");
                quantity = 1;
            }
        });

        verwendenButton.setOnAction(event -> {
            System.out.println("[Verwenden Popup] quantity: " + quantity);
            Stage stage = (Stage) verwendenButton.getScene().getWindow();
            isVerwendenButton = true;
            stage.close();
        });
    }

    private void updateGesamtPreis() {
        String preisText = preis.getText().replace(",", ".").trim();
        if (preisText.isEmpty()) {
            gesamtPreis.setText("0.00 €");
            return;
        }

        try {
            double preisValue = Double.parseDouble(preisText);
            double total = preisValue * quantity;
            gesamtPreis.setText(String.format("%.2f €", total));
        } catch (NumberFormatException e) {
            gesamtPreis.setText("Ungültiger Preis");
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPreis(String preis) {
        this.preis.setText(preis);
        updateGesamtPreis(); // Ensure total price is set after price is injected
    }

    public void setTeil(String teil) {
        this.teil.setText(teil);
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public boolean getIsVerwendenButton() {
        return isVerwendenButton;
    }
}
