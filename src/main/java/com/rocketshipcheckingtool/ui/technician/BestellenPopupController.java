package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Shuttle;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class BestellenPopupController {
    private static final Logger log = LoggerFactory.getLogger(BestellenPopupController.class);
    public Button minusButton;
    public TextField quantityField;
    public Button plusButton;
    public Button bestellenButton;
    public Label preis;
    public Label gesamtPreis;
    public Label teil;
    public ComboBox<String> shuttleComboBox;

    private int quantity = 1;
    private boolean isBestellenButton = false;
    private ClientRequests clientRequests;
    private String user = "technician";
    private ArrayList<Shuttle> shuttles;

    public void initialize() {
        quantityField.setText(String.valueOf(quantity));
        shuttleComboBox.getStyleClass().add("comboBox");
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

        quantityField.setOnAction(event -> {
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity < 1) {
                    quantity = 1;
                }
                if (quantity > 999) {
                    quantity = 999; // Prevent increasing quantity beyond 100
                }
                quantityField.setText(String.valueOf(quantity));
                updateGesamtPreis();
            } catch (NumberFormatException e) {
                quantityField.setText("1");
                quantity = 1;
            }
        });

        bestellenButton.setOnAction(event -> {
            System.out.println("[Bestellen Popup] quantity: " + quantity);
            Stage stage = (Stage) bestellenButton.getScene().getWindow();
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

    public boolean getIsBestellenButton() {
        return isBestellenButton;
    }

    public void loadCombobox(){
        shuttleComboBox.getItems().clear();
        try {
            shuttles = Util.getShuttles(clientRequests, user);
            shuttleComboBox.getItems().add("Kein Shuttle");
            for (Shuttle shuttle : shuttles) {
                shuttleComboBox.getItems().add(shuttle.getShuttleName());
            }
        } catch (Exception e){
            System.out.println("[Bestellen Popup] Error loading shuttles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadCombobox();
    }

    public Shuttle getSelectedShuttle() {
        String selectedShuttleName = shuttleComboBox.getValue();
        if (selectedShuttleName != null && !selectedShuttleName.equals("Kein Shuttle")) {
            return shuttles.stream()
                    .filter(shuttle -> shuttle.getShuttleName().equals(selectedShuttleName))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
