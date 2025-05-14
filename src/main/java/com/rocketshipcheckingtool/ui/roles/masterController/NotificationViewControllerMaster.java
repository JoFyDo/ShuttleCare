package com.rocketshipcheckingtool.ui.roles.masterController;

import com.rocketshipcheckingtool.ui.datamodel.Notification;
import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.NotificationUtil;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.helper.Util;
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

/**
 * Abstract base controller class for managing the notification view.
 * Provides common functionality for handling notifications and user interactions.
 */
public abstract class NotificationViewControllerMaster {
    @FXML
    protected ComboBox<String> shuttleComboBox; // ComboBox for selecting a shuttle.
    @FXML
    protected TableView<Notification> noticationTableView; // TableView for displaying notifications.
    @FXML
    protected TableColumn<Notification, String> notificationColumn; // Column for notification messages.
    @FXML
    protected TableColumn<Notification, String> commentColumn; // Column for notification comments.
    @FXML
    protected TableColumn<Notification, String> shuttleColumn; // Column for shuttle names.
    @FXML
    protected TableColumn<Notification, String> senderColumn; // Column for notification senders.
    @FXML
    protected TableColumn<Notification, Void> deleteColumn; // Column for delete buttons.

    protected Shuttle shuttleSelected; // The currently selected shuttle.

    protected ClientRequests clientRequests; // ClientRequests instance for server communication.
    protected final String user = UserSession.getRole().name().toLowerCase(); // The current user's role.

    protected List<Shuttle> shuttles; // List of all available shuttles.
    protected final static Logger logger = LoggerFactory.getLogger(NotificationViewControllerMaster.class); // Logger instance for logging activities.

    protected ViewManagerController viewManagerController; // Reference to the main view manager controller.

    /**
     * Initializes the controller and sets up the ComboBox and TableView.
     * Logs the initialization process and handles shuttle selection changes.
     */
    public void initialize() {
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

        // TableView
        setupTableColumns();
        logger.debug("NotificationViewControllerMaster initialized and ComboBox listener set");
    }

    /**
     * Loads the content of the notification table.
     * Fetches notifications based on the selected shuttle and populates the table.
     */
    public void loadTableContent() {
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

            // Buttons
            setupLoeschenButtonColumn();
            load();

        } catch (Exception e) {
            logger.error("Error loading notifications: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Laden der Benachrichtigungen: " + e.getMessage());
        }
    }

    /**
     * Abstract method to load additional data or content.
     * Must be implemented by subclasses to provide specific loading logic.
     */
    protected abstract void load();

    /**
     * Sets up the columns of the notification table.
     * Configures cell value factories and adjusts column properties.
     */
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

    /**
     * Abstract method to set up additional table content.
     * Must be implemented by subclasses to provide specific logic for populating the table.
     */
    protected abstract void setupTableErstellenContent();

    /**
     * Loads an SVG icon from the specified file.
     *
     * @param filename The name of the SVG file to load.
     * @return The loaded SVGPath object.
     */
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

    /**
     * Sets up the delete button column in the table.
     * Adds a button to each row for deleting notifications.
     */
    private void setupLoeschenButtonColumn() {
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = createIconButton("trash.fxml");
            {
                button.setOnAction(event -> {
                    Notification item = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Benachrichtigung löschen");
                    alert.setHeaderText("Möchten Sie diese Benachrichtigung wirklich löschen?");
                    alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden.");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                NotificationUtil.updateNotification(clientRequests, user, item.getId(), "false");
                                logger.info("Notification with ID {} deleted", item.getId());
                                loadTableContent();
                            } catch (Exception e) {
                                logger.error("Error deleting notification: {}", e.getMessage(), e);
                                Util.showErrorDialog("Fehler beim Löschen der Benachrichtigung: " + e.getMessage());
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

    /**
     * Creates a button with an SVG icon.
     *
     * @param iconFile The name of the SVG file to use as the button icon.
     * @return The created Button object.
     */
    protected Button createIconButton(String iconFile) {
        Button button = new Button();
        button.getStyleClass().add("iconButton");
        button.setGraphic(loadSvgIcon(iconFile));
        return button;
    }

    /**
     * Loads the content of the shuttle ComboBox.
     * Fetches the list of shuttles and populates the ComboBox with their names.
     */
    private void loadShuttleContent() {
        try {
            shuttles = ShuttleUtil.getShuttles(clientRequests, user);
            shuttleComboBox.getItems().clear();
            shuttleComboBox.getItems().add("Alle Shuttles");

            shuttleComboBox.getItems().addAll(shuttles.stream()
                    .map(Shuttle::getShuttleName)
                    .toList());
            shuttleSelected = null;
            logger.info("Loaded {} shuttles into notification ComboBox", shuttles != null ? shuttles.size() : 0);

        } catch (Exception e) {
            logger.error("Error loading shuttles for notifications: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Laden der Shuttles: " + e.getMessage());
        }
    }

    /**
     * Sets the ViewManagerController instance for managing the view.
     *
     * @param viewManagerController The ViewManagerController instance to set.
     */
    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
        logger.debug("ViewManagerController set in NotificationViewControllerMaster");
    }

    /**
     * Sets the ClientRequests instance for server communication.
     * Loads shuttle and notification data and refreshes the view.
     *
     * @param clientRequests The ClientRequests instance to set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleContent();
        loadTableContent();
        logger.debug("ClientRequests set and content loaded in NotificationViewControllerMaster");
    }
}