package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.datamodel.Notification;
import com.rocketshipcheckingtool.ui.helper.NotificationUtil;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.roles.masterController.NotificationViewControllerMaster;
import com.rocketshipcheckingtool.ui.helper.Util;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NotificationViewController extends NotificationViewControllerMaster {

    public TableColumn<Notification, Void> erstellenColumn;

    @FXML
    public void initialize() {
        super.initialize();
    }

    protected void load() {
        setupErstellenButtonColumn();
    }

    protected void setupTableErstellenContent() {
        erstellenColumn.setResizable(false);
        erstellenColumn.setPrefWidth(50);
    }

    private void setupErstellenButtonColumn() {
        erstellenColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = createIconButton("add.fxml");
                {
                    button.setOnAction(event -> {
                    Notification item = getTableView().getItems().get(getIndex());
                    try {
                        Util.newTaskForShuttle(clientRequests, user, ShuttleUtil.getShuttle(clientRequests, user, item.getShuttleID()), item.getMessage() + ": " + item.getComment());
                        NotificationUtil.updateNotification(clientRequests, user, item.getId(), "false");
                        loadTableContent();
                    }catch (Exception e) {
                        System.out.println("[Nachrichten] Error creating task: " + e.getMessage());
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
