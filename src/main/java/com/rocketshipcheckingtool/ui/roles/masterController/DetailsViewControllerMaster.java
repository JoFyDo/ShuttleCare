package com.rocketshipcheckingtool.ui.roles.masterController;

import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base controller class for managing the details view.
 * Provides common functionality for handling shuttle data and user interactions.
 */
public abstract class DetailsViewControllerMaster {

    @FXML
    protected ComboBox<String> shuttleComboBox; // ComboBox for selecting a shuttle.
    protected Shuttle shuttleSelected; // The currently selected shuttle.
    protected List<Shuttle> shuttles; // List of all available shuttles.
    protected List<String> shuttleList; // List of shuttle names for the ComboBox.
    protected ClientRequests clientRequests; // ClientRequests instance for server communication.
    protected final String user = UserSession.getRole().name().toLowerCase(); // The current user's role.
    protected ViewManagerController viewManagerController; // Reference to the main view manager controller.
    private static final Logger logger = LoggerFactory.getLogger(DetailsViewControllerMaster.class); // Logger instance for logging activities.

    /**
     * Initializes the controller and sets up the ComboBox listener.
     * Logs the initialization process and handles shuttle selection changes.
     */
    public void initialize() {
        shuttleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                shuttleSelected = shuttles.stream()
                        .filter(sh -> sh.getShuttleName().equals(newVal))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Shuttle not found"));
                reload();
                logger.info("Shuttle selected: '{}'", newVal);
            }
        });

        shuttleComboBox.getStyleClass().add("comboBox");
        logger.debug("DetailsViewControllerMaster initialized and ComboBox listener set");
    }

    /**
     * Loads the shuttle content without a preselected shuttle.
     * Calls the overloaded method with a null parameter.
     */
    protected void loadShuttleContent() {
        loadShuttleContent(null);
    }

    /**
     * Abstract method to retrieve the list of shuttles.
     * Must be implemented by subclasses to provide specific logic for fetching shuttles.
     *
     * @return A list of shuttles.
     * @throws IOException If an error occurs while retrieving the shuttle list.
     */
    protected abstract ArrayList<Shuttle> getShuttleList() throws IOException;

    /**
     * Loads the shuttle content with an optional preselected shuttle.
     * Populates the ComboBox with shuttle names and sets the preselected shuttle if provided.
     *
     * @param preSelectedShuttle The name of the preselected shuttle, or null if none.
     */
    protected void loadShuttleContent(String preSelectedShuttle) {
        try {
            // Retrieve the list of shuttles
            shuttles = getShuttleList();

            // Extract shuttle names for the ComboBox
            shuttleList = shuttles.stream()
                    .map(Shuttle::getShuttleName)
                    .toList();

            // Populate the ComboBox with shuttle names
            shuttleComboBox.getItems().clear();
            shuttleComboBox.getItems().addAll(shuttleList);

            // Set the preselected shuttle if provided
            shuttleSelected = shuttles.stream()
                    .filter(sh -> sh.getShuttleName().equals(preSelectedShuttle))
                    .findFirst()
                    .orElse(null);

            if (preSelectedShuttle != null && shuttleList.contains(preSelectedShuttle)) {
                shuttleComboBox.setValue(preSelectedShuttle);
                logger.info("Preselected shuttle set: '{}'", preSelectedShuttle);
            } else {
                logger.debug("No preselected shuttle or shuttle not found in list");
            }

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
     * Abstract method to reload the view.
     * Must be implemented by subclasses to provide specific logic for reloading the view.
     */
    protected abstract void reload();

    /**
     * Sets the ViewManagerController instance for managing the view.
     *
     * @param viewManagerController The ViewManagerController instance to set.
     */
    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
        logger.debug("ViewManagerController set in DetailsViewControllerMaster");
    }

    /**
     * Sets the ClientRequests instance for server communication.
     * Loads the shuttle content and reloads the view.
     *
     * @param clientRequests The ClientRequests instance to set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleContent();
        reload();
        logger.debug("ClientRequests set and content loaded in DetailsViewControllerMaster");
    }
}