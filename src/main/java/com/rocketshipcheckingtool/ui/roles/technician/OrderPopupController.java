package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class OrderPopupController {
    private static final Logger log = LoggerFactory.getLogger(OrderPopupController.class);
    public Button subtractButton;
    public TextField quantityField;
    public Button addButton;
    public Button orderButton;
    public Label price;
    public Label totalPrice;
    public Label part;
    public ComboBox<String> shuttleComboBox;

    private int quantity = 1;
    private boolean isOrderButton = false;
    private ClientRequests clientRequests;
    private final String user = UserSession.getRole().name().toLowerCase();
    private ArrayList<Shuttle> shuttles;

    public void initialize() {
        quantityField.setText(String.valueOf(quantity));
        shuttleComboBox.getStyleClass().add("comboBox");
        setupButtons();
        log.debug("OrderPopupController initialized with quantity={}", quantity);
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setPrice(String price) {
        this.price.setText(price);
        updateGesamtPreis(); // Ensure total price is set after price is injected
        log.debug("Set price label to '{}'", price);
    }

    public void setPart(String part) {
        this.part.setText(part);
        log.debug("Set part label to '{}'", part);
    }

    public boolean getIsBestellenButton() {
        return isOrderButton;
    }

    public void loadCombobox(){
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
        } catch (Exception e){
            log.error("Error loading shuttles: {}", e.getMessage(), e);
        }
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadCombobox();
        log.debug("ClientRequests set and combobox loaded");
    }

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
