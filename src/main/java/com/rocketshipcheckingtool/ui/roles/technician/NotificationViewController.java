package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.datamodel.Notification;
import com.rocketshipcheckingtool.ui.helper.NotificationUtil;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.roles.masterController.NotificationViewControllerMaster;
import com.rocketshipcheckingtool.ui.helper.Util;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for managing the notification view in the technician role.
 * Extends the NotificationViewControllerMaster to provide specific functionality for technicians.
 */
public class NotificationViewController extends NotificationViewControllerMaster {

    private static final Logger logger = LoggerFactory.getLogger(NotificationViewController.class); // Logger instance for logging activities.

    public TableColumn<Notification, Void> createColumn; // Table column for adding a button to create tasks.

    /**
     * Initializes the controller and sets up the base configurations.
     * Calls the parent class's initialize method.
     */
    @FXML
    public void initialize() {
        super.initialize();
    }

    /**
     * Loads the notification view content.
     * Sets up the "Erstellen" button column.
     */
    protected void load() {
        setupErstellenButtonColumn();
    }

    /**
     * Configures the "Erstellen" button column in the table.
     * Sets the column to be non-resizable and defines its preferred width.
     */
    protected void setupTableErstellenContent() {
        createColumn.setResizable(false);
        createColumn.setPrefWidth(50);
    }

    /**
     * Sets up the "Erstellen" button column with a custom cell factory.
     * Adds a button to each row for creating a new task based on the notification.
     */
    private void setupErstellenButtonColumn() {
        createColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = createIconButton("add.fxml"); // Button for creating a new task.

            {
                // Sets the action for the button to create a task and update the notification.
                button.setOnAction(event -> {
                    Notification item = getTableView().getItems().get(getIndex());
                    try {
                        logger.info("Creating new task for shuttleID={}, message='{}'", item.getShuttleID(), item.getMessage());
                        Util.newTaskForShuttle(clientRequests, user, ShuttleUtil.getShuttle(clientRequests, user, item.getShuttleID()), item.getMessage() + ": " + item.getComment());
                        NotificationUtil.updateNotification(clientRequests, user, item.getId(), "false");
                        loadTableContent();
                        logger.debug("Task created and notification updated for notificationID={}", item.getId());
                    } catch (Exception e) {
                        logger.error("Error creating task for notificationID={}: {}", item.getId(), e.getMessage(), e);
                        Util.showErrorDialog("Fehler beim Erstellen der Aufgabe: " + e.getMessage());
                    }
                });
            }

            /**
             * Updates the cell's content with the button or clears it if the row is empty.
             *
             * @param item  The item associated with the cell.
             * @param empty Whether the cell is empty.
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }
}