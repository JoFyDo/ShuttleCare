package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import com.rocketshipcheckingtool.ui.DetailsViewControllerMaster;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailsViewController extends DetailsViewControllerMaster {

    public Button gelandetButton;
    public Button inspektion1Button;
    public Button inWartungButton;
    public Button inspektion2Button;
    public Button freigegebenButton;
    public HBox progressBar;
    public VBox aufgabenBox;
    public VBox zusaetzlichAufgabenBox;
    public Button freigebenButton;
    private ViewManagerController viewManagerController;

    private final static Logger logger = LoggerFactory.getLogger(DetailsViewController.class);


    @FXML
    public void initialize() {
        super.initialize();
    }

    public void reload(){
        loadMaintenanceProtocol();
        loadAdditionalTasks();
        loadLoadingBar();
    }


    private void loadAdditionalTasks() {
        zusaetzlichAufgabenBox.getChildren().clear();

        if (shuttleSelected == null) return;

        List<Task> tasksForShuttle = null;
        try {
            tasksForShuttle = Util.getActiveTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
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
                    if(checkBox.isSelected()) {
                        Util.updateTaskStatus(clientRequests, user, task.getId(), "Erledigt");
                    }else {
                        Util.updateTaskStatus(clientRequests, user, task.getId(), "Offen");
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            });
            if (task.getStatus().equals("Erledigt")){
                checkBox.setSelected(true);
            }
            taskItem.getChildren().addAll(taskLabel, checkBox);
            zusaetzlichAufgabenBox.getChildren().add(taskItem);
        }
    }

    private void loadMaintenanceProtocol() {
        if (shuttleSelected == null) return;

        aufgabenBox.getChildren().clear();

        ArrayList<Task> tasks = null;
        try {
            tasks = Util.getGeneralTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
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
                    Util.updateGeneralTask(clientRequests, user, task.getId(), String.valueOf(checkBox.isSelected()));
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }

            });
            if (task.getStatus().equals("true")){
                checkBox.setSelected(true);
            }

            taskItem.getChildren().addAll(taskLabel, checkBox);
            aufgabenBox.getChildren().add(taskItem);
        }
    }

    private void loadLoadingBar() {
        List<Button> steps = List.of(
                gelandetButton,
                inspektion1Button,
                inWartungButton,
                inspektion2Button,
                freigegebenButton
        );
        onLoadingBarButton(gelandetButton, "Flug", "Gelandet");
        onLoadingBarButton(inspektion1Button, "Gelandet", "Inspektion 1");
        onLoadingBarButton(inWartungButton, "Inspektion 1", "In Wartung");
        onLoadingBarButton(inspektion2Button, "In Wartung", "Inspektion 2");

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
            freigebenButton.setText("Weiter");
        }else {
            freigebenButton.setText("Freigeben");
        }
        System.out.println(activeStep);

    }




    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleContent();
    }

    public void selectShuttle(Shuttle shuttle) {
        if (shuttleSelected != null) {
            loadShuttleContent(shuttle.getShuttleName());
        } else {
            loadShuttleContent();
        }
    }

    public void onNeueAufgabeClick(ActionEvent actionEvent) {
        if (shuttleSelected == null) {
            selectShuttleInfoPopUp();
            return;
        }
        if (shuttleSelected.getStatus().equals("Gelandet") || shuttleSelected.getStatus().equals("In Wartung")) {
            try {
                System.out.println("[Details] Neue Aufgabe Button Clicked");
                Util.newTaskForShuttle(clientRequests, user, shuttleSelected, null);
                loadShuttleContent(shuttleSelected.getShuttleName());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Es können keine Aufgaben erstellt werden");
            alert.setHeaderText(null);
            alert.setContentText("Zu dem jetzigen Status können keine neuen Aufgaben erstellt werden");
            alert.showAndWait();
        }
    }

    public void onFreigebenButtonClick(ActionEvent actionEvent) throws IOException {
        if (shuttleSelected == null) {
            selectShuttleInfoPopUp();
            return;
        }
        ArrayList<Task> activeTasks = null;
        ArrayList<Task> generalTasks = null;
        try {
            activeTasks = Util.getActiveTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
            generalTasks = Util.getGeneralTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
        } catch (Exception e){
            logger.error(e.getMessage());
        }

        assert activeTasks != null;
        assert generalTasks != null;
        if (checkUpdateRocketshipStage()){
            return;
        }
        boolean check = false;
        for (Task task : activeTasks) {
            if (task.getStatus().equals("Offen")){
                check = true;
            }
        }

        for (Task task : generalTasks) {
            if (task.getStatus().equals("false")){
                check = true;
            }
        }

        if (!shuttleSelected.getStatus().equals("Inspektion 2")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Shuttle nicht bereit");
            alert.setHeaderText(null);
            alert.setContentText("Das Shuttle ist nicht bereit zur Freigabe");
            alert.showAndWait();
            return;
        }

        if (check) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aufgaben nicht erledigt");
            alert.setHeaderText(null);
            alert.setContentText("Es müssen erst alle Aufgaben erledigt werden");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bitte bestätigen Sie die Freigabe");
            alert.setHeaderText(null);
            alert.setTitle("Bestätigung erforderlich");
            alert.show();
            alert.setOnHidden(event -> {
                if (alert.getResult() == ButtonType.OK) {
                    try {
                        Util.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Freigegeben");
                        Util.updateAllTasksBelongToShuttle(clientRequests, user, shuttleSelected.getId(), false);
                        Util.updatePredictedReleaseTime(clientRequests, user, shuttleSelected.getId(), null);
                        loadShuttleContent(shuttleSelected.getShuttleName());
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    public void onVerschrottenButtonClick(ActionEvent actionEvent) {
        if (shuttleSelected == null) {
            selectShuttleInfoPopUp();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bitte bestätigen Sie die Verschrottung");
        alert.setHeaderText(null);
        alert.setTitle("Bestätigung erforderlich");
        alert.show();
        alert.setOnHidden(event -> {
            if (alert.getResult() == ButtonType.OK) {
                try {
                    Util.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Verschrottet");
                    Util.updateAllTasksBelongToShuttle(clientRequests, user, shuttleSelected.getId(), false);
                    shuttleSelected = null;
                    viewManagerController.showHome();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void onBestellenButtonClick(ActionEvent actionEvent) {
        viewManagerController.handleBestellenButton();
    }

    public void selectShuttleInfoPopUp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Kein Shuttle ausgewählt");
        alert.setContentText("Es muss zuerst ein Shuttle ausgewählt werden");
        alert.show();
    }

    private void onLoadingBarButton(Button button, String compareStatus, String newStatus) {
        button.setOnAction(event -> {
            if (shuttleSelected == null) {
                selectShuttleInfoPopUp();
                return;
            }
            if (shuttleSelected.getStatus().equals(compareStatus)) {
                try {
                    if (!checkUpdateRocketshipStage()){
                        Util.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), newStatus);
                    }
                    loadShuttleContent(shuttleSelected.getShuttleName());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

    }

    public boolean checkUpdateRocketshipStage(){
        try {
            ArrayList<Task> activeTasks = Util.getActiveTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
            ArrayList<Task> generalTasks = Util.getGeneralTasksByShuttleID(clientRequests, user, shuttleSelected.getId());
            if (!shuttleSelected.getStatus().equals("Inspektion 2")) {
                if (!shuttleSelected.getStatus().equals("Freigegeben")) {
                switch (shuttleSelected.getStatus()) {
                    case "Flug":
                        Util.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Gelandet");
                        break;
                    case "Gelandet":
                        Util.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Inspektion 1");
                        break;
                    case "Inspektion 1":
                        boolean hasOpenTasks = false;
                        for (Task task : activeTasks) {
                            if (task.getStatus().equals("Offen")) {
                                hasOpenTasks = true;
                                break;
                            }
                        }
                        if (!hasOpenTasks) {
                            for (Task task : generalTasks) {
                                if (task.getStatus().equals("false")) {
                                    hasOpenTasks = true;
                                    break;
                                }
                            }
                        }

                        if (hasOpenTasks) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Aufgaben nicht erledigt");
                            alert.setHeaderText(null);
                            alert.setContentText("Es müssen erst alle Aufgaben erledigt werden");
                            alert.showAndWait();
                        } else {
                            Util.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "In Wartung");
                        }
                        break;
                    case "In Wartung":
                        Util.updateShuttleStatus(clientRequests, user, shuttleSelected.getId(), "Inspektion 2");
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
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
    }
}
