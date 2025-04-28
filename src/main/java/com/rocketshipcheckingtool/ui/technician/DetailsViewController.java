package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
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

public class DetailsViewController {
    public ComboBox<String> shuttleComboBox;
    public Shuttle shuttleSelected;
    public List<String> shuttleList;
    public List<Shuttle> shuttles;
    public Button gelandetButton;
    public Button inspektion1Button;
    public Button inWartungButton;
    public Button inspektion2Button;
    public Button freigegebenButton;
    public Button erledigtButton;
    public HBox progressBar;
    public VBox aufgabenBox;
    public VBox zusaetzlichAufgabenBox;
    private ViewManagerController viewManagerController;

    private ClientRequests clientRequests;
    private final String user = "technician";
    private final static Logger logger = LoggerFactory.getLogger(DetailsViewController.class);


    @FXML
    public void initialize() {
        shuttleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                shuttleSelected = shuttles.stream()
                        .filter(sh -> sh.getShuttleName().equals(newVal))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Shuttle not found"));
                loadAufgaben();
                loadZusaetzlicheAufgaben();
                loadLoadingBar();
                System.out.println("[Details] selected Shuttle: " + newVal);
            }
        });

        shuttleComboBox.getStyleClass().add("comboBox");
    }


    private void loadZusaetzlicheAufgaben() {
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

    private void loadAufgaben() {
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
                erledigtButton,
                freigegebenButton
        );

        for (Button step : steps) {
            step.getStyleClass().removeAll("progressLabel-complete", "progressLabel-current");
        }

        int activeStep = switch (shuttleSelected.getStatus()) {
            case "Gelandet" -> 0;
            case "Inspektion 1" -> 1;
            case "In Wartung" -> 2;
            case "Inspektion 2" -> 3;
            case "Erledigt - Warte auf Freigabe" -> 4;
            case "Freigegeben" -> 5;
            default -> -1;
        };

        for (int i = 0; i < steps.size(); i++) {
            if (i < activeStep) {
                steps.get(i).getStyleClass().add("progressLabel-complete");
            } else if (i == activeStep) {
                steps.get(i).getStyleClass().add("progressLabel-current");
            }
        }
    }


    //no preselected shuttle
    private void loadShuttleContent() {
        loadShuttleContent(null);
    }

    //preselected shuttle
    private void loadShuttleContent(String preSelectedShuttle) {
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

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleContent();
    }

    public void selectShuttle(Shuttle shuttle) {
        loadShuttleContent(shuttle.getShuttleName());
    }

    public void onNeueAufgabeClick(ActionEvent actionEvent) {
        if (shuttleSelected == null) {
            selectShuttleInfoPopUp();
            return;
        }
        try {
            System.out.println("[Details] Neue Aufgabe Button Clicked");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/technician/NeueAufgabePopupView.fxml"));
            Parent popupRoot = loader.load();

            // New Stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Neue Aufgabe");
            popupStage.setScene(new Scene(popupRoot));
            popupStage.initModality(Modality.APPLICATION_MODAL);

            NeueAufgabePopupController popupController = loader.getController();
            popupController.setStage(popupStage);
            popupController.initialize();
            popupStage.showAndWait();

            // Retrieve data from the popup
            String description = popupController.getDescription();
            String mechanic = popupController.getMechanic();
            if (!description.equals("") || !mechanic.equals("")) {
                description = description.strip();
                mechanic = mechanic.strip();
                Util.createTask(clientRequests, user, mechanic, description, shuttleSelected.getId());
                loadShuttleContent(shuttleSelected.getShuttleName());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onFreigebenButtonClick(ActionEvent actionEvent) {
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

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
    }
}
