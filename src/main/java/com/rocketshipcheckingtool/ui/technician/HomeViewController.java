package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class HomeViewController {

    public PieChart pieChart;
    //public BarChart FortschrittBarChart;
    private ClientRequests clientRequests;
    private final String user = UserSession.getRole().name().toLowerCase();
    private final static Logger logger = LoggerFactory.getLogger(HomeViewController.class);
    private ViewManagerController viewManagerController;

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

    }

    private void setupTableColumns() {
        shuttleOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        statusOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        landungOverviewColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLandungDate() + " " + cellData.getValue().getLandungTime()));
        mechanikerOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));

        aufgabeTaskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        shuttleTaskColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        mechanikerTaskColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));
        statusTaskColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // resize column width
        detailsOverviewColumn.setResizable(false);
        detailsOverviewColumn.setPrefWidth(140);
        shuttleTableView.setSelectionModel(null);
        aufgabenTableView.setSelectionModel(null);
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
        try {
            viewManagerController.handleDetailButton(shuttle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadTableContent() {
        try {
            //Shuttle
            ArrayList<Shuttle> shuttles = Util.getShuttles(clientRequests, user);
            shuttleTableView.setItems(FXCollections.observableArrayList(shuttles));

            //Details button
            setupDetailsButtonColumn();

            // Task
            ArrayList<Task> tasks = Util.getActiveTasks(clientRequests, user);
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

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
    }

    public void onStatistikenBoxClicked(MouseEvent mouseEvent) {
        viewManagerController.handleStatistikenVBox();
    }
}
