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

public class NotificationViewController extends NotificationViewControllerMaster {

    private static final Logger logger = LoggerFactory.getLogger(NotificationViewController.class);

    public TableColumn<Notification, Void> createColumn;

    @FXML
    public void initialize() {
        super.initialize();
    }

    protected void load() {
        setupErstellenButtonColumn();
    }

    protected void setupTableErstellenContent() {
        createColumn.setResizable(false);
        createColumn.setPrefWidth(50);
    }

    private void setupErstellenButtonColumn() {
        createColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = createIconButton("add.fxml");
                {
                    button.setOnAction(event -> {
                    Notification item = getTableView().getItems().get(getIndex());
                    try {
                        logger.info("Creating new task for shuttleID={}, message='{}'", item.getShuttleID(), item.getMessage());
                        Util.newTaskForShuttle(clientRequests, user, ShuttleUtil.getShuttle(clientRequests, user, item.getShuttleID()), item.getMessage() + ": " + item.getComment());
                        NotificationUtil.updateNotification(clientRequests, user, item.getId(), "false");
                        loadTableContent();
                        logger.debug("Task created and notification updated for notificationID={}", item.getId());
                    }catch (Exception e) {
                        logger.error("Error creating task for notificationID={}: {}", item.getId(), e.getMessage(), e);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }
}
