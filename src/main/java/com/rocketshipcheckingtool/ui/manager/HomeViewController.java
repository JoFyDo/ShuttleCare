package com.rocketshipcheckingtool.ui.manager;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.UserSession;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import com.rocketshipcheckingtool.ui.technician.Util;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class HomeViewController {
    private ClientRequests clientRequests;
    private final String user = UserSession.getRole().name().toLowerCase();
    private ViewManagerController viewManagerController;

    @FXML
    private TableView<Shuttle> shuttleTableView;
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
    public void initialize() {
        setupTableColumns();
    }

    private void setupTableColumns() {
        shuttleOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        statusOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        landungOverviewColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLandungDate() + " " + cellData.getValue().getLandungTime()));
        mechanikerOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));

        setupDetailsButtonColumn();

        detailsOverviewColumn.setResizable(false);
        detailsOverviewColumn.setPrefWidth(140);
        shuttleTableView.setSelectionModel(null);
        shuttleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void setupDetailsButtonColumn() {
        detailsOverviewColumn.setCellValueFactory(param -> null);
        detailsOverviewColumn.setCellFactory(param -> new TableCell<>() {
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
                setGraphic(empty ? null : detailsButton);
            }
        });
    }

    private void showShuttleDetails(Shuttle shuttle) {
        try {
            viewManagerController.handleDetailButton(shuttle);
        } catch (Exception e) {
            showError("Fehler beim Anzeigen der Shuttle-Details", e.getMessage());
        }
    }

    public void loadTableContent() {
        System.out.println("[ManagerHome] Loading shuttles...");

        Task<ArrayList<Shuttle>> task = new Task<>() {
            @Override
            protected ArrayList<Shuttle> call() throws Exception {
                return Util.getShuttles(clientRequests, user);
            }
        };

        task.setOnSucceeded(e -> {
            ArrayList<Shuttle> shuttles = task.getValue();
            System.out.println("[ManagerHome] Loaded shuttles: " + shuttles.size());
            shuttleTableView.setItems(FXCollections.observableArrayList(shuttles));
        });

        task.setOnFailed(e -> {
            showError("Shuttles konnten nicht geladen werden", task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void setClientRequests(ClientRequests clientRequests) {
        System.out.println("[ManagerHome] clientRequests injected: " + (clientRequests != null));
        this.clientRequests = clientRequests;
        loadTableContent();
    }

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
    }
}
