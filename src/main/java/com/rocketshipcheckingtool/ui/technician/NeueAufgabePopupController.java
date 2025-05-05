package com.rocketshipcheckingtool.ui.technician;

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

        mechanikerComboBox.setOnAction(event -> {
            String selected = mechanikerComboBox.getValue();
            if (selected != null) {
                mechanic.setText(selected);
            }
        });

        createButton.setOnAction(event -> {
            if (stage.isShowing()) {
                if (getDescription().equals("") || getMechanic().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Please fill all the required fields");
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
        return mechanic.getText();
    }
}
