package com.rocketshipcheckingtool.ui.technician;

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
import java.util.List;

public class NachrichtenViewController {
    public ComboBox<String> shuttleComboBox;

    // ────────────────────────────────────────────────────────────────────────────
    // EXAMPLE
    // ────────────────────────────────────────────────────────────────────────────
    public TableView<DemoNachrichtItem> nachrichtenTableView;
    public TableColumn<DemoNachrichtItem, String> nachrichtColumn;
    public TableColumn<DemoNachrichtItem, String> kommentarColumn;
    public TableColumn<DemoNachrichtItem, String> ShuttleColumn;
    public TableColumn<DemoNachrichtItem, String> vonColumn;
    public TableColumn<DemoNachrichtItem, String> datumColumn;
    public TableColumn<DemoNachrichtItem, Void> loeschenColumn;
    public TableColumn<DemoNachrichtItem, Void> erstellenColumn;

    private ClientRequests clientRequests;
    public List<Shuttle> shuttles;
    public String shuttleSelected;
    private final String user = UserSession.getRole().name().toLowerCase();
    private final static Logger logger = LoggerFactory.getLogger(NachrichtenViewController.class);


    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleContent();
        loadTableContent();
    }

    @FXML
    public void initialize() {
        // ComboBox
        shuttleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                shuttleSelected = newVal;
                if (newVal.equals("Alle Shuttles")) {
                    System.out.println("[Nachrichten] selected ALLE shuttles");
                } else {
                    System.out.println("[Nachrichten] selected Shuttle: " + newVal);
                }
            }
        });
        shuttleComboBox.getStyleClass().add("comboBox");

        //TableView
        setupTableColumns();
    }

    private void loadTableContent() {
        try {
            //Content

            // ────────────────────────────────────────────────────────────────────────────
            // EXAMPLE
            // ────────────────────────────────────────────────────────────────────────────
            ObservableList<NachrichtenViewController.DemoNachrichtItem> items = FXCollections.observableArrayList(
                    new NachrichtenViewController.DemoNachrichtItem(
                            "Alles Doof",
                            "Leider funktioniert nichts",
                            "Shuttle Ö",
                            "LG Reiner",
                            "Heute")
            );
            nachrichtenTableView.setItems(items);

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
        // ────────────────────────────────────────────────────────────────────────────
        // EXAMPLE
        // ────────────────────────────────────────────────────────────────────────────
        nachrichtColumn.setCellValueFactory(new PropertyValueFactory<>("nachricht"));
        kommentarColumn.setCellValueFactory(new PropertyValueFactory<>("kommentar"));
        ShuttleColumn.setCellValueFactory(new PropertyValueFactory<>("shuttle"));
        vonColumn.setCellValueFactory(new PropertyValueFactory<>("von"));
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("datum"));

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
                    DemoNachrichtItem item = getTableView().getItems().get(getIndex());
                    System.out.println("[Nachrichten] Loeschen button clicked: " + item.nachrichtProperty().get());
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
                    DemoNachrichtItem item = getTableView().getItems().get(getIndex());
                    System.out.println("[Nachrichten] Erstellen button clicked: " + item.nachrichtProperty().get());
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

            shuttleComboBox.setValue("Alle Shuttles");
            shuttleSelected = shuttleComboBox.getValue();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // EXAMPLE
    // ────────────────────────────────────────────────────────────────────────────
    public static class DemoNachrichtItem {
        private final StringProperty nachricht = new SimpleStringProperty();
        private final StringProperty kommentar = new SimpleStringProperty();
        private final StringProperty shuttle = new SimpleStringProperty();
        private final StringProperty von = new SimpleStringProperty();
        private final StringProperty datum = new SimpleStringProperty();


        public DemoNachrichtItem(String n, String k, String s, String v, String d) {
            nachricht.set(n);
            kommentar.set(k);
            shuttle.set(s);
            von.set(v);
            datum.set(d);
        }

        public StringProperty nachrichtProperty() {
            return nachricht;
        }

        public StringProperty kommentarProperty() {
            return kommentar;
        }

        public StringProperty shuttleProperty() {
            return shuttle;
        }

        public StringProperty vonProperty() {
            return von;
        }

        public StringProperty datumProperty() {
            return datum;
        }
    }
}
