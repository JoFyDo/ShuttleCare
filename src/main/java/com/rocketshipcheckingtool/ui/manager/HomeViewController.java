package com.rocketshipcheckingtool.ui.manager;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

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
        System.out.println("[ManagerHome] initialize");
        setupTableColumns();
    }

    private void setupTableColumns() {
        shuttleOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        statusOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        landungOverviewColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getLandungDate() + " " + data.getValue().getLandungTime()));
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
                    System.out.println("[Manager Home] details for shuttle clicked");
                    //Disabled because does not work :(
                    //showShuttleDetails(shuttle);
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
            e.printStackTrace();
            throw new RuntimeException("Error opening shuttle details", e);
        }
    }

    public void loadTableContent() {
        System.out.println("[ManagerHome] try loading shuttles");
        try {
            List<Shuttle> shuttles;

            // Test Data
            shuttles = List.of(
                    new Shuttle(1, "Shuttle F", "Gelandet", Date.valueOf("2025-05-01"), Time.valueOf("12:00:00"), "Mr. Dornige Chancen"),
                    new Shuttle(2, "Shuttle S", "Gelandet", Date.valueOf("2025-05-02"), Time.valueOf("13:30:00"), "Nestlee Glöckner")
            );

            // Replace test data:
            // ArrayList<Shuttle> shuttles = Util.getShuttles(clientRequests, user);

            System.out.println("[ManagerHome] shuttles found: " + shuttles.size());

            shuttleTableView.setItems(FXCollections.observableArrayList(shuttles));
            shuttleTableView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void setClientRequests(ClientRequests clientRequests) {
        System.out.println("[ManagerHome] clientRequests: " + (clientRequests != null));
        this.clientRequests = clientRequests;
        loadTableContent();
    }

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
    }
}
