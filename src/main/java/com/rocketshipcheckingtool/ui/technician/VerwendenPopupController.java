package com.rocketshipcheckingtool.ui.technician;

import javafx.scene.control.Button;
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

    private int quantity = 1;

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
            quantity++;
            quantityField.setText(String.valueOf(quantity));
            updateGesamtPreis();
        });

        verwendenButton.setOnAction(event -> {
            System.out.println("[Verwenden Popup] quantity: " + quantity);
            Stage stage = (Stage) verwendenButton.getScene().getWindow();
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
}
