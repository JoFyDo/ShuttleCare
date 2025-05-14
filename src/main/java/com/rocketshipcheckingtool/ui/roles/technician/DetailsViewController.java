package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.datamodel.Task;
import com.rocketshipcheckingtool.ui.helper.*;
import com.rocketshipcheckingtool.ui.roles.masterController.DetailsViewControllerMaster;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing the details view in the technician role.
 * Extends the DetailsViewControllerMaster to provide specific functionality for technicians.
 */
public class DetailsViewController extends DetailsViewControllerMaster {

    public Button landedButton; // Button for marking the shuttle as landed.
    public Button inspection1Button; // Button for marking the shuttle as in inspection 1.
    public Button inMaintenanceButton; // Button for marking the shuttle as in maintenance.
    public Button inspection2Button; // Button for marking the shuttle as in inspection 2.
    public Button freigegebenButton; // Button for marking the shuttle as released.
    public HBox progressBar; // HBox for displaying the progress bar.
    public VBox taskBox; // VBox for displaying maintenance tasks.
    public VBox additionalTaskBox; // VBox for displaying additional tasks.
    public Button releaseButton; // Button for releasing the shuttle.

    private final static Logger logger = LoggerFactory.getLogger(DetailsViewController.class); // Logger instance for logging activities.

    /**
     * Initializes the controller and sets up the necessary configurations.
     * Logs the initialization process.
     */
    @FXML
    public void initialize() {
        super.initialize();
        logger.info("DetailsViewController initialized");
    }

    /**
     * Retrieves the list of shuttles.
     *
     * @return A list of shuttles.
     * @throws IOException If an error occurs while retrieving the shuttle list.
     */
    @Override
    protected ArrayList<Shuttle> getShuttleList() throws IOException {
        return ShuttleUtil.getShuttles(clientRequests, user);
    }

    /**
     * Reloads the details view for the selected shuttle.
     * Loads the maintenance protocol, additional tasks, and progress bar.
     */
    public void reload() {
        logger.debug("Reloading details view for shuttle '{}'", shuttleSelected != null ? shuttleSelected.getShuttleName() : "none");
        loadMaintenanceProtocol();
        loadAdditionalTasks();
        loadLoadingBar();
    }

    /**
     * Loads additional tasks for the selected shuttle.
     * Clears the additional task box and populates it with tasks.
     */
    private void loadAdditionalTasks() {
        additionalTaskBox.getChildren().clear();

        if (shuttleSelected == null) {
            logger.warn("No shuttle selected when loading additional tasks");
            return;
        }

        List<Task> tasksForShuttle = null;
        try {
            tasksForShuttle = TaskUtil.getActiveTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
            logger.info("Loaded {} additional tasks for shuttle '{}'", tasksForShuttle.size(), shuttleSelected.getShuttleName());
        } catch (IOException e) {
            logger.error("Failed to load additional tasks: {}", e.getMessage(), e);
            Util.showErrorDialog("Failed to load additional tasks: " + e.getMessage());
            return;
        }

        for (Task task : tasksForShuttle) {
            HBox taskItem = new HBox();
            taskItem.setSpacing(10);
            taskItem.setMaxWidth(Double.MAX_VALUE);
            taskItem.setStyle("-fx-alignment: CENTER_LEFT;");

            Label taskLabel = new Label(task.getTask());
            taskLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(taskLabel, Priority.ALWAYS);

            CheckBox checkBox = new CheckBox();

            boolean disableCheckbox = !shuttleSelected.getStatus().equals("Inspektion 1");
            checkBox.setDisable(disableCheckbox);

            checkBox.setOnAction(event -> {
                try {
                    if (checkBox.isSelected()) {
                        logger.info("Task '{}' marked as completed", task.getTask());
                        TaskUtil.updateTaskStatus(clientRequests, user, task.getId(), "true");
                    } else {
                        logger.info("Task '{}' marked as not completed", task.getTask());
                        TaskUtil.updateTaskStatus(clientRequests, user, task.getId(), "false");
                    }
                } catch (Exception e) {
                    logger.error("Error updating task status: {}", e.getMessage(), e);
                    Util.showErrorDialog("Error updating task status: " + e.getMessage());
                }
            });
            checkBox.setSelected(task.getStatus());
            taskItem.getChildren().addAll(taskLabel, checkBox);
            additionalTaskBox.getChildren().add(taskItem);
        }
    }

    /**
     * Loads the maintenance protocol for the selected shuttle.
     * Clears the task box and populates it with maintenance tasks.
     */
    private void loadMaintenanceProtocol() {
        if (shuttleSelected == null) {
            logger.warn("No shuttle selected when loading maintenance protocol");
            return;
        }

        taskBox.getChildren().clear();

        ArrayList<Task> tasks = null;
        try {
            tasks = GeneralTaskUtil.getGeneralTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
            logger.info("Loaded {} maintenance tasks for shuttle '{}'", tasks.size(), shuttleSelected.getShuttleName());
        } catch (IOException e) {
            logger.error("Failed to load maintenance protocol: {}", e.getMessage(), e);
            Util.showErrorDialog("Failed to load maintenance protocol: " + e.getMessage());
            return;
        }

        for (Task task : tasks) {
            HBox taskItem = new HBox();
            taskItem.setSpacing(10);
            taskItem.setMaxWidth(Double.MAX_VALUE);
            taskItem.setStyle("-fx-alignment: CENTER_LEFT;");
            Label taskLabel = new Label(task.getTask());
            taskLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(taskLabel, Priority.ALWAYS);

            CheckBox checkBox = new CheckBox();
            boolean disableCheckbox = shuttleSelected.getStatus().equals("Freigegeben") ||
                    shuttleSelected.getStatus().equals("Verschrottet") || shuttleSelected.getStatus().equals("Inspektion 2") || shuttleSelected.getStatus().equals("Flug");
            checkBox.setDisable(disableCheckbox);
            checkBox.setOnAction(event -> {
                try {
                    logger.info("General task '{}' set to {}", task.getTask(), checkBox.isSelected());
                    GeneralTaskUtil.updateGeneralTask(clientRequests, user, task.getId(), checkBox.isSelected());
                } catch (IOException e) {
                    logger.error("Error updating general task: {}", e.getMessage(), e);
                    Util.showErrorDialog("Error updating general task: " + e.getMessage());
                }

            });
            checkBox.setSelected(task.getStatus());

            taskItem.getChildren().addAll(taskLabel, checkBox);
            taskBox.getChildren().add(taskItem);
        }
    }

    /**
     * Loads the progress bar for the selected shuttle.
     * Updates the progress bar buttons based on the shuttle's current status.
     */
    private void loadLoadingBar() {
        if (shuttleSelected == null) {
            logger.warn("No shuttle selected when loading progress bar");
            return;
        }
        List<Button> steps = List.of(
                landedButton,
                inspection1Button,
                inMaintenanceButton,
                inspection2Button,
                freigegebenButton
        );
        onLoadingBarButton(landedButton, "Flug", "Gelandet");
        onLoadingBarButton(inspection1Button, "Gelandet", "Inspektion 1");
        onLoadingBarButton(inMaintenanceButton, "Inspektion 1", "In Wartung");
        onLoadingBarButton(inspection2Button, "In Wartung", "Inspektion 2");

        for (Button step : steps) {
            step.getStyleClass().removeAll("progressLabel-complete", "progressLabel-current");
        }

        int activeStep = switch (shuttleSelected.getStatus()) {
            case "Flug" -> 0;
            case "Gelandet" -> 1;
            case "Inspektion 1" -> 2;
            case "In Wartung" -> 3;
            case "Inspektion 2" -> 4;
            case "Platzhalter" -> 5;
            case "Freigegeben" -> 6;
            default -> -1;
        };
        for (int i = 0; i < steps.size(); i++) {
            if (i < activeStep) {
                steps.get(i).getStyleClass().add("progressLabel-complete");
            } else if (i == activeStep) {
                steps.get(i).getStyleClass().add("progressLabel-current");
            }
        }

        if (activeStep < 4) {
            releaseButton.setText("Weiter");
        } else {
            releaseButton.setText("Freigeben");
        }
        logger.debug("Progress bar loaded for shuttle '{}', current step: {}", shuttleSelected.getShuttleName(), activeStep);

    }

    /**
     * Selects a shuttle and loads its content.
     * If shuttle is null, loads default content.
     *
     * @param shuttle The shuttle to be selected, can be null
     */
    public void selectShuttle(Shuttle shuttle) {
        if (shuttle != null) {
            logger.info("Shuttle '{}' selected", shuttle.getShuttleName());
            loadShuttleContent(shuttle.getShuttleName());
        } else {
            logger.info("No shuttle selected, loading default content");
            loadShuttleContent();
        }
    }

    /**
     * Handles the click event for creating a new task.
     * Creates a new task for the selected shuttle if it's in an appropriate status.
     *
     * @param actionEvent The action event triggering this method
     */
    public void onNeueAufgabeClick(ActionEvent actionEvent) {
        if (shuttleSelected == null) {
            logger.warn("Attempted to create new task with no shuttle selected");
            selectShuttleInfoPopUp();
            return;
        }
        if (shuttleSelected.getStatus().equals("Gelandet") || shuttleSelected.getStatus().equals("In Wartung")) {
            try {
                logger.info("New task button clicked for shuttle '{}'", shuttleSelected.getShuttleName());
                Util.newTaskForShuttle(clientRequests, user, shuttleSelected, null);
                loadShuttleContent(shuttleSelected.getShuttleName());

            } catch (IOException e) {
                logger.error("Error creating new task: {}", e.getMessage(), e);
                Util.showErrorDialog("Error creating new task: " + e.getMessage());
            }
        } else {
            logger.info("Cannot create new task for shuttle '{}' in status '{}'", shuttleSelected.getShuttleName(), shuttleSelected.getStatus());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Es können keine Aufgaben erstellt werden");
            alert.setHeaderText(null);
            alert.setContentText("Zu dem jetzigen Status können keine neuen Aufgaben erstellt werden");
            alert.showAndWait();
        }
    }

    /**
     * Handles the click event for releasing a shuttle.
     * Checks if all tasks are completed before allowing the shuttle to be released.
     *
     * @param actionEvent The action event triggering this method
     * @throws IOException If an error occurs during the release process
     */
    public void onFreigebenButtonClick(ActionEvent actionEvent) throws IOException {
        if (shuttleSelected == null) {
            logger.warn("Attempted to release shuttle with no shuttle selected");
            selectShuttleInfoPopUp();
            return;
        }
        ArrayList<Task> activeTasks = null;
        ArrayList<Task> generalTasks = null;
        try {
            activeTasks = TaskUtil.getActiveTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
            generalTasks = GeneralTaskUtil.getGeneralTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
            logger.debug("Loaded {} active and {} general tasks for release check", activeTasks.size(), generalTasks.size());
        } catch (Exception e){
            logger.error("Error loading tasks for release: {}", e.getMessage(), e);
            Util.showErrorDialog("Error loading tasks for release: " + e.getMessage());
            return;
        }

        assert activeTasks != null;
        assert generalTasks != null;
        if (checkUpdateRocketshipStage()){
            logger.info("Rocketship stage updated, skipping release");
            return;
        }
        boolean check = false;
        for (Task task : activeTasks) {
            if (!task.getStatus()){
                check = true;
            }
        }

        for (Task task : generalTasks) {
            if (!task.getStatus()){
                check = true;
            }
        }

        if (!shuttleSelected.getStatus().equals("Inspektion 2")) {
            logger.info("Shuttle '{}' not ready for release (status: {})", shuttleSelected.getShuttleName(), shuttleSelected.getStatus());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Shuttle nicht bereit");
            alert.setHeaderText(null);
            alert.setContentText("Das Shuttle ist nicht bereit zur Freigabe");
            alert.showAndWait();
            return;
        }

        if (check) {
            logger.info("Not all tasks completed for shuttle '{}', cannot release", shuttleSelected.getShuttleName());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aufgaben nicht erledigt");
            alert.setHeaderText(null);
            alert.setContentText("Es müssen erst alle Aufgaben erledigt werden");
            alert.showAndWait();
        } else {
            logger.info("All tasks completed for shuttle '{}', requesting release confirmation", shuttleSelected.getShuttleName());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bitte bestätigen Sie die Freigabe");
            alert.setHeaderText(null);
            alert.setTitle("Bestätigung erforderlich");
            alert.show();
            alert.setOnHidden(event -> {
                if (alert.getResult() == ButtonType.OK) {
                    try {
                        logger.info("Shuttle '{}' released", shuttleSelected.getShuttleName());
                        ShuttleUtil.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Freigegeben");
                        TaskUtil.updateAllTasksBelongToShuttle(clientRequests, user, shuttleSelected.getId(), false);
                        GeneralTaskUtil.updateAllGeneralTasksStatusBelongToShuttle(clientRequests, user, shuttleSelected.getId(), false);
                        ShuttleUtil.updatePredictedReleaseTime(clientRequests, user, shuttleSelected.getId(), null);
                        //loadLoadingBar();
                        loadShuttleContent(shuttleSelected.getShuttleName());
                    } catch (IOException e) {
                        logger.error("Error during shuttle release: {}", e.getMessage(), e);
                        Util.showErrorDialog("Error during shuttle release: " + e.getMessage());
                    }
                } else {
                    logger.info("Release confirmation dialog cancelled for shuttle '{}'", shuttleSelected.getShuttleName());
                }
            });
        }
    }

    /**
     * Handles the click event for scrapping a shuttle.
     * Requests confirmation and a reason for scrapping before proceeding.
     *
     * @param actionEvent The action event triggering this method
     */
    public void onVerschrottenButtonClick(ActionEvent actionEvent) {
        if (shuttleSelected == null) {
            logger.warn("Attempted to scrap shuttle with no shuttle selected");
            selectShuttleInfoPopUp();
            return;
        }
        logger.info("Scrap button clicked for shuttle '{}'", shuttleSelected.getShuttleName());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bitte bestätigen Sie die Verschrottung");
        alert.setHeaderText(null);
        alert.setTitle("Bestätigung erforderlich");
        alert.show();
        alert.setOnHidden(event -> {
            if (alert.getResult() == ButtonType.OK) {
                try {
                    TextInputDialog reasonDialog = new TextInputDialog();
                    reasonDialog.setTitle("Grund für Verschrottung");
                    reasonDialog.setHeaderText("Bitte geben Sie den Grund für die Verschrottung ein");
                    reasonDialog.setContentText("Grund:");

                    Optional<String> result = reasonDialog.showAndWait();
                    String reason = result.orElse("Kein Grund angegeben");
                    ShuttleUtil.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Verschrottet");
                    NotificationUtil.createNotification(clientRequests, user, shuttleSelected.getId(), "Das Shuttle:" + shuttleSelected.getShuttleName() +" wurde verschrottet", user, reason);
                    TaskUtil.updateAllTasksBelongToShuttle(clientRequests, user, shuttleSelected.getId(), false);
                    shuttleSelected = null;
                    viewManagerController.showHome();
                } catch (IOException e) {
                    logger.error("Error during scrapping: {}", e.getMessage(), e);
                    Util.showErrorDialog("Error during scrapping: " + e.getMessage());
                }
            } else {
                logger.info("Scrapping confirmation dialog cancelled for shuttle '{}'", shuttleSelected.getShuttleName());
            }
        });
    }

    /**
     * Handles the click event for ordering parts or supplies.
     * Delegates to the view manager controller to handle the order process.
     *
     * @param actionEvent The action event triggering this method
     */
    public void onBestellenButtonClick(ActionEvent actionEvent) {
        logger.info("Order button clicked in details view");
        viewManagerController.handleBestellenButton();
    }

    /**
     * Displays an information popup alerting the user that no shuttle is selected.
     * Used when operations requiring a shuttle selection are attempted without a selection.
     */
    public void selectShuttleInfoPopUp() {
        logger.info("No shuttle selected, showing info popup");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Kein Shuttle ausgewählt");
        alert.setContentText("Es muss zuerst ein Shuttle ausgewählt werden");
        alert.show();
    }
    /**
     * Sets up an action event handler for a progress bar button.
     * When clicked, it updates the shuttle status if conditions are met.
     *
     * @param button The button to configure
     * @param compareStatus The status required for the action to proceed
     * @param newStatus The new status to set if the action proceeds
     */
    private void onLoadingBarButton(Button button, String compareStatus, String newStatus) {
        button.setOnAction(event -> {
            if (shuttleSelected == null) {
                logger.warn("No shuttle selected when clicking progress bar button");
                selectShuttleInfoPopUp();
                return;
            }
            if (shuttleSelected.getStatus().equals(compareStatus)) {
                try {
                    logger.info("Progress bar button clicked: changing status from '{}' to '{}'", compareStatus, newStatus);
                    if (!checkUpdateRocketshipStage()){
                        ShuttleUtil.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), newStatus);
                    }
                    loadShuttleContent(shuttleSelected.getShuttleName());
                } catch (IOException e) {
                    logger.error("Error updating shuttle status: {}", e.getMessage(), e);
                    Util.showErrorDialog("Error updating shuttle status: " + e.getMessage());
                }
            }
        });

    }

    /**
     * Checks and updates the stage (status) of the selected shuttle based on its current status.
     * Different rules apply for different stages in the shuttle's maintenance lifecycle.
     *
     * @return boolean True if the shuttle stage was updated, false otherwise
     */
    public boolean checkUpdateRocketshipStage() {
        try {
            ArrayList<Task> activeTasks = TaskUtil.getActiveTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
            ArrayList<Task> generalTasks = GeneralTaskUtil.getGeneralTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
            if (!shuttleSelected.getStatus().equals("Inspektion 2")) {
                if (!shuttleSelected.getStatus().equals("Freigegeben")) {
                    switch (shuttleSelected.getStatus()) {
                        case "Flug":
                            logger.info("Updating shuttle '{}' status from 'Flug' to 'Gelandet'", shuttleSelected.getShuttleName());
                            ShuttleUtil.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Gelandet");
                            break;
                        case "Gelandet":
                            if (CommentUtil.allCommandsDone(clientRequests, user, shuttleSelected.getId())) {
                                logger.info("All manager comments done, updating shuttle '{}' status to 'Inspektion 1'", shuttleSelected.getShuttleName());
                                ShuttleUtil.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Inspektion 1");
                            } else {
                                logger.info("Manager comments pending for shuttle '{}', prompting user", shuttleSelected.getShuttleName());
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Aufgaben ausstehend");
                                alert.setHeaderText("Der Manager hat noch offene Kommentare");
                                alert.setContentText("Willst du fortfahren?");
                                alert.showAndWait();
                                if (alert.getResult() == ButtonType.OK) {
                                    logger.info("User chose to proceed despite pending comments for shuttle '{}'", shuttleSelected.getShuttleName());
                                    ShuttleUtil.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Inspektion 1");
                                }
                            }
                            break;
                        case "Inspektion 1":
                            boolean hasOpenTasks = false;
                            for (Task task : activeTasks) {
                                if (!task.getStatus()) {
                                    hasOpenTasks = true;
                                    break;
                                }
                            }
                            if (!hasOpenTasks) {
                                for (Task task : generalTasks) {
                                    if (!task.getStatus()) {
                                        hasOpenTasks = true;
                                        break;
                                    }
                                }
                            }

                            if (hasOpenTasks) {
                                logger.info("Open tasks found for shuttle '{}', cannot proceed to 'In Wartung'", shuttleSelected.getShuttleName());
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Aufgaben nicht erledigt");
                                alert.setHeaderText(null);
                                alert.setContentText("Es müssen erst alle Aufgaben erledigt werden");
                                alert.showAndWait();
                            } else {
                                logger.info("All tasks completed, updating shuttle '{}' status to 'In Wartung'", shuttleSelected.getShuttleName());
                                ShuttleUtil.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "In Wartung");
                            }
                            break;
                        case "In Wartung":
                            logger.info("Updating shuttle '{}' status from 'In Wartung' to 'Inspektion 2'", shuttleSelected.getShuttleName());
                            ShuttleUtil.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Inspektion 2");
                            break;
                    }
                    loadShuttleContent(shuttleSelected.getShuttleName());
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("Error updating Shuttle stage: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim updaten von der Shuttle stage: " + e.getMessage());
            return false;
        }
    }
}