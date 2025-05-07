package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Notification;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class NachrichtenViewController {
    public ComboBox<String> shuttleComboBox;

    // ────────────────────────────────────────────────────────────────────────────
    // EXAMPLE
    // ────────────────────────────────────────────────────────────────────────────
    public TableView<Notification> nachrichtenTableView;
    public TableColumn<Notification, String> nachrichtColumn;
    public TableColumn<Notification, String> kommentarColumn;
    public TableColumn<Notification, String> ShuttleColumn;
    public TableColumn<Notification, String> vonColumn;
    public TableColumn<Notification, Void> loeschenColumn;
    public TableColumn<Notification, Void> erstellenColumn;

    private ClientRequests clientRequests;
    public List<Shuttle> shuttles;
    public Shuttle shuttleSelected;
    private final String user = "technician";

    private final static Logger logger = LoggerFactory.getLogger(NachrichtenViewController.class);


    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleContent();
        loadTableContent();
    }

    @FXML
    public void initialize() {
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

    private void loadTableContent() {
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
            setupErstellenButtonColumn();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    private void setupTableColumns() {
        nachrichtColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        kommentarColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        vonColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        ShuttleColumn.setCellValueFactory(cellData -> {
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
        erstellenColumn.setResizable(false);
        erstellenColumn.setPrefWidth(50);
        nachrichtenTableView.setSelectionModel(null);
        nachrichtenTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

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
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }

    private void setupErstellenButtonColumn() {
        erstellenColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = createIconButton("add.fxml");

            {
                button.setOnAction(event -> {
                    Notification item = getTableView().getItems().get(getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }

    private Button createIconButton(String iconFile) {
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
}
