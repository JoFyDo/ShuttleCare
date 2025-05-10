package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Notification;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.NotificationViewControllerMaster;
import com.rocketshipcheckingtool.ui.Util;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.SVGPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                        Util.newTaskForShuttle(clientRequests, user, Util.getShuttle(clientRequests, user, item.getShuttleID()), item.getMessage() + ": " + item.getComment());
                        Util.updateNotification(clientRequests, user, item.getId(), "false");
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
