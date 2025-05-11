package com.rocketshipcheckingtool.ui.roles.masterController;

import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public abstract class HomeViewControllerMaster {

    @FXML
    private TableColumn<Shuttle, String> shuttleOverviewColumn;
    @FXML
    private TableColumn<Shuttle, String> statusOverviewColumn;
    @FXML
    private TableColumn<Shuttle, String> landingOverviewColumn;
    @FXML
    private TableColumn<Shuttle, String> mechanicOverviewColumn;
    @FXML
    private TableColumn<Shuttle, Void> detailsOverviewColumn;
    @FXML
    public TableView<Shuttle> shuttleTableView;

    protected ClientRequests clientRequests;
    protected final String user = UserSession.getRole().name().toLowerCase();

    protected ViewManagerController viewManagerController;

    private static final Logger logger = LoggerFactory.getLogger(HomeViewControllerMaster.class);

    protected void setupTableColumns() {
        shuttleOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        statusOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        landingOverviewColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLandingTimeString()));
        mechanicOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));

        // resize column width
        detailsOverviewColumn.setResizable(false);
        detailsOverviewColumn.setPrefWidth(140);
        shuttleTableView.setSelectionModel(null);
        shuttleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        logger.debug("Shuttle overview table columns set up");
    }

    private void setupDetailsButtonColumn() {
        detailsOverviewColumn.setCellValueFactory(param -> null);
        detailsOverviewColumn.setCellFactory(param -> new TableCell<Shuttle, Void>() {
            private final Button detailsButton = new Button("Details");

            {
                detailsButton.getStyleClass().add("details-button");
                detailsButton.setOnAction(event -> {
                    Shuttle shuttle = getTableView().getItems().get(getIndex());
                    logger.info("Details button clicked for shuttle '{}'", shuttle.getShuttleName());
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
            ArrayList<Shuttle> shuttles = ShuttleUtil.getShuttles(clientRequests, user);
            for (Shuttle shuttle : shuttles) {
                switch (shuttle.getStatus()) {
                    case "Gelandet" -> shuttle.setStatus("Inspektion 1");
                    case "Inspektion 1" -> shuttle.setStatus("In Wartung");
                    case "In Wartung" -> shuttle.setStatus("Reinspektion");
                    case "Inspektion 2" -> shuttle.setStatus("Erledigt");
                }
            }
            shuttleTableView.setItems(FXCollections.observableArrayList(shuttles));
            logger.info("Loaded {} shuttles into overview table", shuttles.size());

            //Details button
            setupDetailsButtonColumn();

        } catch (Exception e) {
            logger.error("Error loading shuttles: {}", e.getMessage(), e);
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
            logger.error("Error opening shuttle details: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
        logger.debug("ViewManagerController set in HomeViewControllerMaster");
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        logger.debug("ClientRequests set in HomeViewControllerMaster");
        loadShuttleTableContent();
        load();
    }

    public abstract void load();
}
