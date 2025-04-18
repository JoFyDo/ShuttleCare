package com.rocketshipcheckingtool.ui.technician;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class DetailsViewController {
    public ComboBox<String> shuttleComboBox;
    public String shuttleSelected;
    public List<String> shuttleList;
    public List<Task> taskListFull;
    public Button gelandetButton;
    public Button inspektion1Button;
    public Button inWartungButton;
    public Button inspektion2LButton;
    public Button freigegebenButton;
    public HBox progressBar;
    public VBox aufgabenBox;
    public VBox zusaetzlichAufgabenBox;
    //public VBox crewFeedbackBox;

    private ClientRequests clientRequests;
    private final String user = "technician";
    private final static Logger logger = LoggerFactory.getLogger(DetailsViewController.class);


    @FXML
    public void initialize() {
        shuttleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadAufgaben(newVal);
                loadZusaetzlicheAufgaben(newVal);
                //loadCrewFeedback(newVal);
                System.out.println("[Details] selected Shuttle: " + newVal);
            }
        });

        //text alignment: center
        shuttleComboBox.getStyleClass().add("comboBox");
    }

//    private void loadCrewFeedback(String newVal) {
//    }

    private void loadZusaetzlicheAufgaben(String shuttleName) {
        zusaetzlichAufgabenBox.getChildren().clear();

        if (taskListFull == null || shuttleName == null) return;

        List<Task> tasksForShuttle = taskListFull.stream()
                .filter(task -> shuttleName.equals(task.getShuttleName()))
                .toList();

        for (Task task : tasksForShuttle) {
            HBox taskItem = new HBox();
            taskItem.setSpacing(10);
            taskItem.setMaxWidth(Double.MAX_VALUE);
            taskItem.setStyle("-fx-alignment: CENTER_LEFT;");

            Label taskLabel = new Label(task.getTask());
            taskLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(taskLabel, Priority.ALWAYS);

            CheckBox checkBox = new CheckBox();

            taskItem.getChildren().addAll(taskLabel, checkBox);
            zusaetzlichAufgabenBox.getChildren().add(taskItem);
        }
    }

    private void loadAufgaben(String shuttleName) {
    }

    //no preselected shuttle
    private void loadShuttleContent() {
        loadShuttleContent(null);
    }

    //preselected shuttle
    private void loadShuttleContent(String preSelectedShuttle) {
        try {
            //Shuttle
            String shuttlesJson = clientRequests.request("/requestShuttleOverview", user);
            Gson shuttleGson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (jsonElement, type, context) -> Date.valueOf(jsonElement.getAsString()))
                    .registerTypeAdapter(Time.class, (JsonDeserializer<Time>) (jsonElement, type, context) -> Time.valueOf(jsonElement.getAsString()))
                    .create();
            Type shuttleListType = new TypeToken<ArrayList<Shuttle>>() {}.getType();
            ArrayList<Shuttle> shuttles = shuttleGson.fromJson(shuttlesJson, shuttleListType);

            shuttleList = shuttles.stream()
                    .map(Shuttle::getShuttleName)
                    .toList();
            shuttleComboBox.getItems().addAll(shuttleList);

            System.out.println("[Details] shuttle list: " + shuttleList);
            shuttleSelected = shuttleComboBox.getValue();
            System.out.println("[Details] selected Shuttle: " + shuttleSelected);

            //Task
            String tasksJson = clientRequests.request("/requestShuttleTasks", user);
            Gson taskGson = new Gson();
            Type taskListType = new TypeToken<ArrayList<Task>>() {}.getType();
            taskListFull = taskGson.fromJson(tasksJson, taskListType);

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

    // preselected Shuttle
    public void setClientRequests(ClientRequests clientRequests, String preSelectedShuttle) {
        this.clientRequests = clientRequests;
        loadShuttleContent(preSelectedShuttle);
    }

}
