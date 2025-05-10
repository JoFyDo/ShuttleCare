package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.domain.Notification;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import com.rocketshipcheckingtool.ui.technician.NotificationViewController;
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
    protected TableView<Notification> nachrichtenTableView;
    @FXML
    protected TableColumn<Notification, String> nachrichtColumn;
    @FXML
    protected TableColumn<Notification, String> kommentarColumn;
    @FXML
    protected TableColumn<Notification, String> shuttleColumn;
    @FXML
    protected TableColumn<Notification, String> vonColumn;
    @FXML
    protected TableColumn<Notification, Void> loeschenColumn;

    protected Shuttle shuttleSelected;

    protected ClientRequests clientRequests;
    protected final String user = UserSession.getRole().name().toLowerCase();

    protected List<Shuttle> shuttles;
    protected final static Logger logger = LoggerFactory.getLogger(NotificationViewController.class);

    protected ViewManagerController viewManagerController;

    public void initialize(){
        shuttleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                shuttleSelected = shuttles.stream()
                        .filter(sh -> sh.getShuttleName().equals(newVal))
                        .findFirst()
                        .orElse(null);
                loadTableContent();
            }
        });
        shuttleComboBox.getStyleClass().add("comboBox");

        //TableView
        setupTableColumns();
    }

    public void loadTableContent(){
        try {
            ArrayList<Notification> notifications = null;
            if (shuttleSelected != null) {
                notifications = Util.requestNotificationsByShuttle(clientRequests, user, shuttleSelected.getId());
            } else {
                notifications = Util.requestNotifications(clientRequests, user);
            }
            nachrichtenTableView.setItems(FXCollections.observableArrayList(notifications));

            //Buttons
            setupLoeschenButtonColumn();
            load();

        } catch (Exception e) {
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
        nachrichtColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        kommentarColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        vonColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        shuttleColumn.setCellValueFactory(cellData -> {
            int shuttleID = cellData.getValue().getShuttleID();
            Shuttle matchingShuttle = shuttles.stream()
                    .filter(shuttle -> shuttle.getId() == shuttleID)
                    .findFirst()
                    .orElse(null);
            return new SimpleStringProperty(matchingShuttle != null ? matchingShuttle.getShuttleName() : "Unknown");
        });
        vonColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        loeschenColumn.setResizable(false);
        loeschenColumn.setPrefWidth(50);
        nachrichtenTableView.setSelectionModel(null);
        nachrichtenTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
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
        loeschenColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = createIconButton("trash.fxml");
            {
                button.setOnAction(event -> {
                    Notification item = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Benachrichtigung löschen");
                    alert.setHeaderText("Willst du diese Benachrichtigung wirklich löschen?");
                    alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden.");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                Util.updateNotification(clientRequests, user, item.getId(), "false");
                                loadTableContent();
                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Error");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText(e.getMessage());
                                errorAlert.showAndWait();
                            }
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
            shuttles = Util.getShuttles(clientRequests, user);
            shuttleComboBox.getItems().clear();
            shuttleComboBox.getItems().add("Alle Shuttles");

            shuttleComboBox.getItems().addAll(shuttles.stream()
                    .map(Shuttle::getShuttleName)
                    .toList());
            shuttleSelected = null;

        } catch (Exception e) {
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
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleContent();
        loadTableContent();
    }



}
