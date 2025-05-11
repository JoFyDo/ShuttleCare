package com.rocketshipcheckingtool.ui.roles.technician;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewTaskPopupController {

    private static final Logger logger = LoggerFactory.getLogger(NewTaskPopupController.class);

    public ComboBox<String> mechanicComboBox;
    private Stage stage;

    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField mechanic;
    @FXML
    private Button createButton;

    public void initialize() {
        mechanicComboBox.setItems(FXCollections.observableArrayList(
                "Alois", "Boris", "Christian", "Deniz"
        ));

        createButton.setOnAction(event -> {
            if (stage.isShowing()) {
                if (getMechanic() == null || getDescription() == null || getDescription().isEmpty() || getMechanic().isEmpty()) {
                    logger.warn("Attempted to create task with missing fields: mechanic='{}', description='{}'", getMechanic(), getDescription());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Alle Felder müssen ausgefüllt sein!");
                    alert.showAndWait();
                } else {
                    logger.info("Creating new task for mechanic='{}' with description='{}'", getMechanic(), getDescription());
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
        return mechanicComboBox.getValue();
    }

    public void setDescription(String description) {
        this.descriptionArea.setText(description);
    }
}
