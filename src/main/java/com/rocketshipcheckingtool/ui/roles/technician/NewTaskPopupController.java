package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.datamodel.Mechanic;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.helper.MechanicUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class NewTaskPopupController {

    private static final Logger logger = LoggerFactory.getLogger(NewTaskPopupController.class);

    @FXML
    public ComboBox<String> mechanicComboBox;
    private Stage stage;

    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField mechanic;
    @FXML
    private Button createButton;

    private ClientRequests clientRequests;
    protected final String user = UserSession.getRole().name().toLowerCase();


    public void initialize() {

        createButton.setOnAction(event -> {
            if (stage.isShowing()) {
                if (getMechanic() == null || getDescription() == null || getDescription().isEmpty() || getMechanic() == null) {
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

    public Mechanic getMechanic() {
        String selectedMechanicName = mechanicComboBox.getValue();
        if (selectedMechanicName == null || selectedMechanicName.isEmpty()) {
            return null;
        }

        try {
            ArrayList<Mechanic> mechanics = MechanicUtil.getMechanics(clientRequests, user);
            return mechanics.stream()
                    .filter(m -> m.getName().equals(selectedMechanicName))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            logger.error("Error retrieving mechanic: {}", e.getMessage(), e);
            return null;
        }
    }

    public void setDescription(String description) {
        this.descriptionArea.setText(description);
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        ArrayList<Mechanic> mechanics = null;
        try {
            mechanics = MechanicUtil.getMechanics(clientRequests, user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mechanicComboBox.setItems(mechanics.stream().map(Mechanic::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }
}
