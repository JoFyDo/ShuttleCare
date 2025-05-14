package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.helper.Util;
import com.rocketshipcheckingtool.ui.roles.masterController.HomeViewControllerMaster;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for managing the home view in the manager role.
 * Handles the initialization and loading of the home view.
 */
public class HomeViewController extends HomeViewControllerMaster {
    private final String user = UserSession.getRole().name().toLowerCase(); // The current user's role.

    private static final Logger logger = LoggerFactory.getLogger(HomeViewController.class); // Logger instance for logging activities.

    /**
     * Initializes the controller and sets up the table columns.
     * Logs the initialization process and handles any errors that occur.
     */
    @FXML
    public void initialize() {
        try {
            setupTableColumns();
            logger.info("Manager HomeViewController initialized for user '{}'", user);
        } catch (Exception e) {
            logger.error("Error initializing HomeViewControllers: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler bei der Initialisierung: " + e.getMessage());
        }
    }

    /**
     * Loads the data or content for the home view.
     * This method is intended to be overridden with specific loading logic.
     */
    @Override
    public void load() {

    }
}