package com.rocketshipcheckingtool.ui.helper;

import com.rocketshipcheckingtool.ui.datamodel.*;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import com.rocketshipcheckingtool.ui.roles.technician.NewTaskPopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility class providing helper methods for various UI-related operations.
 */
public class Util {

    private final static Logger logger = LoggerFactory.getLogger(Util.class); // Logger instance for logging activities.

    /**
     * Opens a popup to create a new task for a specific shuttle.
     * Allows the user to input task details and assigns the task to a mechanic.
     *
     * @param clientRequests The ClientRequests instance for server communication.
     * @param user The username of the user creating the task.
     * @param shuttle The shuttle for which the task is being created.
     * @param preset An optional preset description for the task.
     * @throws IOException If an error occurs while loading the popup view.
     */
    public static void newTaskForShuttle(ClientRequests clientRequests, String user, Shuttle shuttle, String preset) throws IOException {
        FXMLLoader loader = new FXMLLoader(Util.class.getResource("/com/rocketshipcheckingtool/ui/roles/technician/NewTaskPopupView.fxml"));
        Parent popupRoot = loader.load();

        // New Stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Neue Aufgabe");
        popupStage.setScene(new Scene(popupRoot));
        popupStage.initModality(Modality.APPLICATION_MODAL);

        NewTaskPopupController popupController = loader.getController();
        popupController.setClientRequests(clientRequests);
        if (preset != null) {
            popupController.setDescription(preset);
        }
        popupController.setStage(popupStage);
        popupController.initialize();
        popupStage.showAndWait();

        // Retrieve data from the popup
        String description = popupController.getDescription();
        Mechanic mechanic = popupController.getMechanic();
        if (!description.equals("") || !mechanic.equals("")) {
            description = description.strip();
            TaskUtil.createTask(clientRequests, user, mechanic, description, shuttle.getId());
            if (shuttle.getStatus().equals("In Wartung")) {
                ShuttleUtil.updateShuttleStatus(clientRequests, user, shuttle.getId(), "Inspektion 1");
            }
        }
    }

    /**
     * Displays an error dialog with the specified message.
     *
     * @param message The error message to display.
     */
    public static void showErrorDialog(String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}