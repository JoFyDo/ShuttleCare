package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.SidebarControllerInterface;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.helper.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for managing the sidebar in the technician role.
 * Handles navigation between different views and logout functionality.
 */
public class TechnicianSidebarController implements SidebarControllerInterface {
    public ToggleGroup toggleGroup; // Group for managing toggle buttons in the sidebar.
    private ViewManagerController mainController; // Reference to the main view manager controller.
    private static final Logger logger = LoggerFactory.getLogger(TechnicianSidebarController.class); // Logger instance for logging activities.

    @FXML
    private ToggleButton btnHome; // Toggle button for navigating to the home view.
    @FXML private ToggleButton btnDetails; // Toggle button for navigating to the details view.
    @FXML private ToggleButton btnStats; // Toggle button for navigating to the statistics view.
    @FXML private ToggleButton btnStorage; // Toggle button for navigating to the storage view.
    @FXML private ToggleButton btnNotification; // Toggle button for navigating to the notifications view.
    @FXML private ToggleButton btnLogout; // Toggle button for logging out.

    /**
     * Sets the main controller for managing views.
     *
     * @param controller The main view manager controller to be set.
     */
    @Override
    public void setMainController(ViewManagerController controller) {
        this.mainController = controller;
        logger.debug("Main controller set in TechnicianSidebarController");
    }

    /**
     * Handles sidebar button click events.
     * Navigates to the corresponding view based on the button clicked.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleSidebarClick(ActionEvent event) {
        try {
            ToggleButton source = (ToggleButton) event.getSource();
            if (mainController != null && source != null) {
                logger.info("Sidebar navigation: {}", source.getId());
                mainController.handleSidebarNavigation(source.getId());
            } else {
                logger.warn("Sidebar click event with null mainController or source");
            }
        } catch (Exception e) {
            logger.error("Error navigating the sidebar: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Navigieren in der Sidebar: " + e.getMessage());
        }
    }

    /**
     * Handles the logout button click event.
     * Initiates the logout process through the main controller.
     *
     * @param event The action event triggered by the logout button click.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            logger.info("Logout button clicked");
            if (mainController != null) {
                mainController.handleLogout(event);
            } else {
                logger.warn("Logout attempted with null mainController");
            }
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Logout: " + e.getMessage());
        }
    }

    /**
     * Selects the sidebar button corresponding to the given button ID.
     * Highlights the selected button to indicate the active view.
     *
     * @param buttonId The ID of the button to be selected.
     */
    @Override
    public void selectButton(String buttonId) {
        logger.debug("Selecting sidebar button: {}", buttonId);
        switch (buttonId) {
            case "btnHome" -> btnHome.setSelected(true);
            case "btnDetails" -> btnDetails.setSelected(true);
            case "btnStatistiken" -> btnStats.setSelected(true);
            case "btnLager" -> btnStorage.setSelected(true);
            case "btnNachrichten" -> btnNotification.setSelected(true);
        }
    }
}