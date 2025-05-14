package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.datamodel.Mechanic;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.helper.MechanicUtil;
import com.rocketshipcheckingtool.ui.helper.Util;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Controller class for managing the popup window for creating a new task.
 * Handles user input, validation, and task creation logic.
 */
public class NewTaskPopupController {

    private static final Logger logger = LoggerFactory.getLogger(NewTaskPopupController.class); // Logger instance for logging activities.

    @FXML
    public ComboBox<String> mechanicComboBox; // ComboBox for selecting a mechanic.
    private Stage stage; // Reference to the popup stage.

    @FXML
    private TextArea descriptionArea; // TextArea for entering the task description.
    @FXML
    private TextField mechanic; // TextField for mechanic input (not used in current implementation).
    @FXML
    private Button createButton; // Button for creating a new task.

    private ClientRequests clientRequests; // ClientRequests instance for making HTTP requests.
    protected final String user = UserSession.getRole().name().toLowerCase(); // Current user's role in lowercase.

    /**
     * Initializes the controller and sets up the create button's action.
     * Validates input fields and handles task creation.
     */
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

    /**
     * Sets the stage for the popup window.
     *
     * @param stage The stage to be set.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Retrieves the task description entered by the user.
     *
     * @return The task description as a string.
     */
    public String getDescription() {
        return descriptionArea.getText();
    }

    /**
     * Retrieves the selected mechanic from the ComboBox.
     *
     * @return The selected Mechanic object, or null if no mechanic is selected.
     */
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
            Util.showErrorDialog("Fehler beim Laden des Mechanikers: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sets the task description in the TextArea.
     *
     * @param description The description to be set.
     */
    public void setDescription(String description) {
        this.descriptionArea.setText(description);
    }

    /**
     * Sets the ClientRequests instance and populates the ComboBox with mechanics.
     *
     * @param clientRequests The ClientRequests instance to be set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        ArrayList<Mechanic> mechanics = null;
        try {
            mechanics = MechanicUtil.getMechanics(clientRequests, user);
        } catch (IOException e) {
            logger.error("Error retrieving mechanic: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Laden der Mechaniker: " + e.getMessage());
            mechanics = new ArrayList<>();
        }
        mechanicComboBox.setItems(mechanics.stream().map(Mechanic::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }
}