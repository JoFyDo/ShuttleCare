package com.rocketshipcheckingtool.ui.roles.masterController;

import com.rocketshipcheckingtool.ui.datamodel.Notification;
import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.NotificationUtil;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
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

public abstract class NotificationViewControllerMaster {
    @FXML
    protected ComboBox<String> shuttleComboBox;
    @FXML
    protected TableView<Notification> noticationTableView;
    @FXML
    protected TableColumn<Notification, String> notificationColumn;
    @FXML
    protected TableColumn<Notification, String> commentColumn;
    @FXML
    protected TableColumn<Notification, String> shuttleColumn;
    @FXML
    protected TableColumn<Notification, String> senderColumn;
    @FXML
    protected TableColumn<Notification, Void> deleteColumn;

    protected Shuttle shuttleSelected;

    protected ClientRequests clientRequests;
    protected final String user = UserSession.getRole().name().toLowerCase();

    protected List<Shuttle> shuttles;
    protected final static Logger logger = LoggerFactory.getLogger(NotificationViewControllerMaster.class);

    protected ViewManagerController viewManagerController;

    public void initialize(){
        shuttleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                shuttleSelected = shuttles.stream()
                        .filter(sh -> sh.getShuttleName().equals(newVal))
                        .findFirst()
                        .orElse(null);
                logger.info("Shuttle selected in notifications: '{}'", newVal);
                loadTableContent();
            }
        });
        shuttleComboBox.getStyleClass().add("comboBox");

        //TableView
        setupTableColumns();
        logger.debug("NotificationViewControllerMaster initialized and ComboBox listener set");
    }

    public void loadTableContent(){
        try {
            ArrayList<Notification> notifications = null;
            if (shuttleSelected != null) {
                notifications = NotificationUtil.requestNotificationsByShuttle(clientRequests, user, shuttleSelected.getId());
                logger.info("Loaded {} notifications for shuttle '{}'", notifications != null ? notifications.size() : 0, shuttleSelected.getShuttleName());
            } else {
                notifications = NotificationUtil.requestNotifications(clientRequests, user);
                logger.info("Loaded {} notifications for all shuttles", notifications != null ? notifications.size() : 0);
            }
            noticationTableView.setItems(FXCollections.observableArrayList(notifications));

            //Buttons
            setupLoeschenButtonColumn();
            load();

        } catch (Exception e) {
            logger.error("Error loading notifications: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    protected abstract void load();

    protected void setupTableColumns() {
        notificationColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        shuttleColumn.setCellValueFactory(cellData -> {
            int shuttleID = cellData.getValue().getShuttleID();
            Shuttle matchingShuttle = shuttles.stream()
                    .filter(shuttle -> shuttle.getId() == shuttleID)
                    .findFirst()
                    .orElse(null);
            return new SimpleStringProperty(matchingShuttle != null ? matchingShuttle.getShuttleName() : "Unknown");
        });
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        deleteColumn.setResizable(false);
        deleteColumn.setPrefWidth(50);
        noticationTableView.setSelectionModel(null);
        noticationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        setupTableErstellenContent();
    }

    protected abstract void setupTableErstellenContent();

    private SVGPath loadSvgIcon(String filename) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/graphics/" + filename));
            SVGPath svgIcon = loader.load();
            svgIcon.getStyleClass().add("svgIcon");
            return svgIcon;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SVG icon: " + filename, e);
        }
    }

    private void setupLoeschenButtonColumn() {
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = createIconButton("trash.fxml");
            {
                button.setOnAction(event -> {
                    Notification item = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete notification");
                    alert.setHeaderText("Do you really want to delete this notification?");
                    alert.setContentText("This action cannot be undone.");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                NotificationUtil.updateNotification(clientRequests, user, item.getId(), "false");
                                logger.info("Notification with ID {} deleted", item.getId());
                                loadTableContent();
                            } catch (Exception e) {
                                logger.error("Error deleting notification: {}", e.getMessage(), e);
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Error");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText(e.getMessage());
                                errorAlert.showAndWait();
                            }
                        } else {
                            logger.debug("Notification deletion cancelled by user");
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }

    protected Button createIconButton(String iconFile) {
        Button button = new Button();
        button.getStyleClass().add("iconButton");
        button.setGraphic(loadSvgIcon(iconFile));
        return button;
    }

    // Combobox
    private void loadShuttleContent() {
        try {
            shuttles = ShuttleUtil.getShuttles(clientRequests, user);
            shuttleComboBox.getItems().clear();
            shuttleComboBox.getItems().add("All shuttles");

            shuttleComboBox.getItems().addAll(shuttles.stream()
                    .map(Shuttle::getShuttleName)
                    .toList());
            shuttleSelected = null;
            logger.info("Loaded {} shuttles into notification ComboBox", shuttles != null ? shuttles.size() : 0);

        } catch (Exception e) {
            logger.error("Error loading shuttles for notifications: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
        logger.debug("ViewManagerController set in NotificationViewControllerMaster");
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleContent();
        loadTableContent();
        logger.debug("ClientRequests set and content loaded in NotificationViewControllerMaster");
    }
}
