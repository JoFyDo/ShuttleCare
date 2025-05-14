package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.helper.Util;
import com.rocketshipcheckingtool.ui.roles.masterController.NotificationViewControllerMaster;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for managing the notification view in the manager role.
 * Extends the NotificationViewControllerMaster to provide specific functionality for the manager role.
 */
public class NotificationViewController extends NotificationViewControllerMaster {

    private static final Logger logger = LoggerFactory.getLogger(NotificationViewController.class); // Logger instance for logging activities.

    /**
     * Initializes the controller and sets up the necessary configurations.
     * Logs the initialization process and handles any errors that occur.
     */
    @FXML
    public void initialize() {
        try {
            super.initialize();
            logger.info("Manager NotificationViewController initialized");
        } catch (Exception e) {
            logger.error("Error during initialization of NotificationViewController: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler bei der Initialisierung: " + e.getMessage());
        }
    }

    /**
     * Loads the notification view content.
     * Hides the shuttle column in the table for the manager role.
     */
    @Override
    protected void load() {
        shuttleColumn.setVisible(false);
    }

    /**
     * Sets up the content for the "Erstellen" table.
     * This method is intended to be overridden with specific logic for populating the table.
     */
    @Override
    protected void setupTableErstellenContent() {

    }
}