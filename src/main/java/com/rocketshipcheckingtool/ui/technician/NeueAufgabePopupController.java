package com.rocketshipcheckingtool.ui.technician;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NeueAufgabePopupController {

    private Stage stage;

    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField mechanic;
    @FXML
    private Button createButton;

    public void initialize() {
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
