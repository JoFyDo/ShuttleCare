package com.rocketshipcheckingtool.ui.technician;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VerwendenPopupController {
    public Button minusButton;
    public TextField quantityField;
    public Button plusButton;
    public Button verwendenButton;

    private int quantity = 1;

    public void initialize() {
        quantityField.setText(String.valueOf(quantity));

        minusButton.setOnAction(event -> {
            if (quantity > 1) {
                quantity--;
                quantityField.setText(String.valueOf(quantity));
            }
        });

        plusButton.setOnAction(event -> {
            quantity++;
            quantityField.setText(String.valueOf(quantity));
        });

        verwendenButton.setOnAction(event -> {
            System.out.println("[Verwenden Popup] quantity: " + quantity);
            Stage stage = (Stage) verwendenButton.getScene().getWindow();
            stage.close();
        });
    }

    public int getQuantity() {
        return quantity;
    }
}
