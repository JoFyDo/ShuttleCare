package com.rocketshipcheckingtool.ui.roles.masterController;

import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.helper.Util;
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

/**
 * Abstract base controller class for managing the home view.
 * Provides common functionality for setting up and loading shuttle data into a table view.
 */
public abstract class HomeViewControllerMaster {

    @FXML
    private TableColumn<Shuttle, String> shuttleOverviewColumn; // Column for displaying shuttle names.
    @FXML
    private TableColumn<Shuttle, String> statusOverviewColumn; // Column for displaying shuttle statuses.
    @FXML
    private TableColumn<Shuttle, String> landingOverviewColumn; // Column for displaying shuttle landing times.
    @FXML
    private TableColumn<Shuttle, String> mechanicOverviewColumn; // Column for displaying assigned mechanics.
    @FXML
    private TableColumn<Shuttle, Void> detailsOverviewColumn; // Column for displaying a "Details" button.
    @FXML
    public TableView<Shuttle> shuttleTableView; // TableView for displaying shuttle data.

    protected ClientRequests clientRequests; // ClientRequests instance for server communication.
    protected final String user = UserSession.getRole().name().toLowerCase(); // The current user's role.

    protected ViewManagerController viewManagerController; // Reference to the main view manager controller.

    private static final Logger logger = LoggerFactory.getLogger(HomeViewControllerMaster.class); // Logger instance for logging activities.

    /**
     * Sets up the columns of the shuttle overview table.
     * Configures cell value factories and adjusts column properties.
     */
    protected void setupTableColumns() {
        shuttleOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        statusOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        landingOverviewColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLandingTimeString()));
        mechanicOverviewColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));

        // Resize column width
        detailsOverviewColumn.setResizable(false);
        detailsOverviewColumn.setPrefWidth(140);
        shuttleTableView.setSelectionModel(null);
        shuttleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        logger.debug("Shuttle overview table columns set up");
    }

    /**
     * Sets up the "Details" button column in the table.
     * Adds a button to each row for viewing shuttle details.
     */
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

    /**
     * Loads shuttle data into the table view.
     * Fetches shuttle data, updates their statuses, and populates the table.
     */
    public void loadShuttleTableContent() {
        try {
            // Fetch shuttle data
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

            // Set up the "Details" button column
            setupDetailsButtonColumn();

        } catch (Exception e) {
            logger.error("Error loading shuttles: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ladefehler");
            alert.setHeaderText(null);
            alert.setContentText("Fehler beim Laden der Shuttles: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Displays the details of a selected shuttle.
     * Delegates the action to the view manager controller.
     *
     * @param shuttle The selected shuttle.
     */
    private void showShuttleDetails(Shuttle shuttle) {
        try {
            viewManagerController.handleDetailButton(shuttle);
        } catch (IOException e) {
            logger.error("Error opening shuttle details: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Öffnen der Shuttle-Details: " + e.getMessage());
        }
    }

    /**
     * Sets the ViewManagerController instance for managing the view.
     *
     * @param viewManagerController The ViewManagerController instance to set.
     */
    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
        logger.debug("ViewManagerController set in HomeViewControllerMaster");
    }

    /**
     * Sets the ClientRequests instance for server communication.
     * Loads shuttle data and refreshes the view.
     *
     * @param clientRequests The ClientRequests instance to set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        logger.debug("ClientRequests set in HomeViewControllerMaster");
        loadShuttleTableContent();
        load();
    }

    /**
     * Abstract method to load additional data or content.
     * Must be implemented by subclasses to provide specific loading logic.
     */
    public abstract void load();
}