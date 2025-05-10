package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;

public abstract class HomeViewControllerMaster {

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
    public TableView<Shuttle> shuttleTableView;

    protected ClientRequests clientRequests;
    protected final String user = UserSession.getRole().name().toLowerCase();

    protected ViewManagerController viewManagerController;

    protected void setupTableColumns() {
        shuttleOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        statusOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        landungOverviewColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLandingTimeString()));
        mechanikerOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));

        // resize column width
        detailsOverviewColumn.setResizable(false);
        detailsOverviewColumn.setPrefWidth(140);
        shuttleTableView.setSelectionModel(null);
        shuttleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
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

    public void loadShuttleTableContent() {
        try {
            //Shuttle
            ArrayList<Shuttle> shuttles = Util.getShuttles(clientRequests, user);
            shuttleTableView.setItems(FXCollections.observableArrayList(shuttles));

            //Details button
            setupDetailsButtonColumn();



        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    private void showShuttleDetails(Shuttle shuttle) {
        try {
            viewManagerController.handleDetailButton(shuttle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleTableContent();
        load();
    }

    public abstract void load();
}
