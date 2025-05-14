package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.SidebarControllerInterface;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.helper.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for managing the sidebar in the manager role.
 * Handles navigation between different views and logout functionality.
 */
public class ManagerSidebarController implements SidebarControllerInterface {
    public ToggleButton btnHome; // ToggleButton for navigating to the home view.
    public ToggleGroup toggleGroup; // ToggleGroup for grouping sidebar buttons.
    public ToggleButton btnDetails; // ToggleButton for navigating to the details view.
    public ToggleButton btnNotification; // ToggleButton for navigating to the notifications view.
    public Pane spacer; // Spacer element for layout purposes.
    public ToggleButton btnLogout; // ToggleButton for triggering the logout action.
    public VBox managerSidebar; // VBox container for the sidebar layout.

    private ViewManagerController mainController; // Reference to the main controller for handling navigation.
    private static final Logger logger = LoggerFactory.getLogger(ManagerSidebarController.class); // Logger instance for logging activities.

    /**
     * Sets the main controller for handling navigation and other actions.
     *
     * @param controller The ViewManagerController instance to set.
     */
    @Override
    public void setMainController(ViewManagerController controller) {
        this.mainController = controller;
        logger.debug("Main controller set in ManagerSidebarController");
    }

    /**
     * Handles click events on the sidebar buttons.
     * Navigates to the corresponding view based on the button clicked.
     *
     * @param event The ActionEvent triggered by the button click.
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
            logger.error("Error during Sidebar-Navigation: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler bei der Sidebar-Navigation: " + e.getMessage());
        }
    }

    /**
     * Handles the logout action when the logout button is clicked.
     * Delegates the logout process to the main controller.
     *
     * @param event The ActionEvent triggered by the logout button click.
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
            logger.error("Error during Logout: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Logout: " + e.getMessage());
        }
    }

    /**
     * Selects a sidebar button based on the provided button ID.
     * Highlights the corresponding button to indicate the current view.
     *
     * @param buttonId The ID of the button to select.
     */
    @Override
    public void selectButton(String buttonId) {
        logger.debug("Selecting sidebar button: {}", buttonId);
        switch (buttonId) {
            case "btnHome" -> btnHome.setSelected(true);
            case "btnDetails" -> btnDetails.setSelected(true);
            case "btnNotification" -> btnNotification.setSelected(true);
        }
    }
}