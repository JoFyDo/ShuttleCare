package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import com.rocketshipcheckingtool.ui.technician.Util;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.util.List;

public abstract class DetailsViewControllerMaster {

    @FXML
    protected ComboBox<String> shuttleComboBox;
    protected Shuttle shuttleSelected;
    protected List<Shuttle> shuttles;
    protected List<String> shuttleList;
    protected ClientRequests clientRequests;
    protected final String user = UserSession.getRole().name().toLowerCase();

    public void initialize() {
        shuttleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                shuttleSelected = shuttles.stream()
                        .filter(sh -> sh.getShuttleName().equals(newVal))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Shuttle not found"));
                reload();
                System.out.println("[Details] selected Shuttle: " + newVal);
            }
        });

        shuttleComboBox.getStyleClass().add("comboBox");
    }

    //no preselected shuttle
    protected void loadShuttleContent() {
        loadShuttleContent(null);
    }

    //preselected shuttle
    protected void loadShuttleContent(String preSelectedShuttle) {
        try {
            //Shuttle
            shuttles = Util.getShuttles(clientRequests, user);

            shuttleList = shuttles.stream()
                    .map(Shuttle::getShuttleName)
                    .toList();

            shuttleComboBox.getItems().clear();
            shuttleComboBox.getItems().addAll(shuttleList);

            shuttleSelected = shuttles.stream()
                    .filter(sh -> sh.getShuttleName().equals(preSelectedShuttle))
                    .findFirst()
                    .orElse(null);

            if (preSelectedShuttle != null && shuttleList.contains(preSelectedShuttle)) {
                shuttleComboBox.setValue(preSelectedShuttle);
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    protected abstract void reload();
}
