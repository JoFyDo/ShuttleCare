package com.rocketshipcheckingtool.ui.roles.technician;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class NeueAufgabePopupController {

    public ComboBox<String> mechanikerComboBox;
    private Stage stage;

    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField mechanic;
    @FXML
    private Button createButton;

    public void initialize() {
        mechanikerComboBox.setItems(FXCollections.observableArrayList(
                "Alois", "Boris", "Christian", "Deniz"
        ));

        createButton.setOnAction(event -> {
            if (stage.isShowing()) {
                if (getMechanic() == null || getDescription() == null || getDescription().isEmpty() || getMechanic().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Alle Felder müssen ausgefüllt sein!");
                    alert.showAndWait();
                }else {
                    stage.close();
                }
            }
        });
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public String getDescription() {
        return descriptionArea.getText();
    }

    public String getMechanic() {
        return mechanikerComboBox.getValue();
    }

    public void setDescription(String description) {
        this.descriptionArea.setText(description);
    }
}
