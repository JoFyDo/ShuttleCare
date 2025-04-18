package com.rocketshipcheckingtool.ui.technician;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class HomeViewController {

    public PieChart pieChart;
    private ClientRequests clientRequests;
    private final String user = "technician";
    private final static Logger logger = LoggerFactory.getLogger(HomeViewController.class);

    @FXML
    public TableView<Shuttle> shuttleTableView;
    @FXML
    private TableColumn<Shuttle, String> shuttleOverviewColumn;
    @FXML
    private TableColumn<Shuttle, String> statusOverviewColumn;
    @FXML
    private TableColumn<Shuttle, String> landungOverviewColumn;
    @FXML
    private TableColumn<Shuttle, String> mechanikerOverviewColumn;
    @FXML
    private TableColumn<Shuttle, Void> detailsOverviewColumn;

    @FXML
    private TableView<Task> aufgabenTableView;
    @FXML
    private TableColumn<Task, String> aufgabeTaskColumn;
    @FXML
    private TableColumn<Task, String> shuttleTaskColumn;
    @FXML
    private TableColumn<Task, String> mechanikerTaskColumn;
    @FXML
    private TableColumn<Task, String> statusTaskColumn;

    @FXML
    public void initialize() {
        setupTableColumns();
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(new PieChart.Data("new", 25), new PieChart.Data("in use", 35), new PieChart.Data("worn", 15), new PieChart.Data("critical", 25));
        pieChart.setData(data);
    }

    private void setupTableColumns() {
        shuttleOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        statusOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        landungOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("landungDate"));
        mechanikerOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));

        aufgabeTaskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        shuttleTaskColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        mechanikerTaskColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));
        statusTaskColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // resize column width
        detailsOverviewColumn.setResizable(false);
        detailsOverviewColumn.setPrefWidth(140);
        shuttleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        aufgabenTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void setupDetailsButtonColumn() {
        detailsOverviewColumn.setCellValueFactory(param -> null);
        detailsOverviewColumn.setCellFactory(param -> new TableCell<Shuttle, Void>() {
            private final Button detailsButton = new Button("Details");

            {
                detailsButton.getStyleClass().add("details-button");
                detailsButton.setOnAction(event -> {
                    Shuttle shuttle = getTableView().getItems().get(getIndex());
                    showShuttleDetails(shuttle);
                });
                setPadding(new Insets(0, 20, 0, 20));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        });
    }

    private void showShuttleDetails(Shuttle shuttle) {
        System.out.println("[Home] Showing details for shuttle: " + shuttle.getShuttleName());
    }

    private void loadTableContent() {
        try {
            //System.out.println("[Home] shuttleTableView: " + shuttleTableView);
            //Shuttle
            String shuttlesJson = clientRequests.request("/requestShuttleOverview", user);

            Gson shuttleGson = new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (jsonElement, type, context) -> Date.valueOf(jsonElement.getAsString())).registerTypeAdapter(Time.class, (JsonDeserializer<Time>) (jsonElement, type, context) -> Time.valueOf(jsonElement.getAsString())).create();
            Type shuttleListType = new TypeToken<ArrayList<Shuttle>>() {
            }.getType();
            ArrayList<Shuttle> shuttles = shuttleGson.fromJson(shuttlesJson, shuttleListType);

            shuttleTableView.setItems(FXCollections.observableArrayList(shuttles));
            //System.out.println("[Home] shuttleTableView: " + shuttleTableView);

            //Details button
            setupDetailsButtonColumn();

            // Task
            String tasksJson = clientRequests.request("/requestShuttleTasks", user);
            Gson taskGson = new Gson();
            Type taskListType = new TypeToken<ArrayList<Task>>() {
            }.getType();
            ArrayList<Task> tasks = taskGson.fromJson(tasksJson, taskListType);
            aufgabenTableView.setItems(FXCollections.observableArrayList(tasks));

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
        loadTableContent();
    }
}
