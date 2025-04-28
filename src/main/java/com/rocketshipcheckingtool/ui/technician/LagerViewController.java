package com.rocketshipcheckingtool.ui.technician;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LagerViewController {
    public TextField searchField;
    public Button verwendenButton;
    public Button bestellenButton;

    // ────────────────────────────────────────────────────────────────────────────
    // EXAMPLE
    // ────────────────────────────────────────────────────────────────────────────
    public TableColumn<DemoLagerItem, Boolean> checkBoxColumn;
    public TableColumn<DemoLagerItem, Boolean> nameColumn;
    public TableColumn<DemoLagerItem, Boolean> nrColumn;
    public TableColumn<DemoLagerItem, Boolean> preisColumn;
    public TableColumn<DemoLagerItem, Boolean> bestandColumn;
    public TableView<DemoLagerItem> lagerTableView;
    private ClientRequests clientRequests;

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadTableContent();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
    }

    private void setupTableColumns() {

        checkBoxColumn.setCellFactory(col -> {
            CheckBoxTableCell<DemoLagerItem, Boolean> cell =
                    new CheckBoxTableCell<>(index ->
                            lagerTableView.getItems().get(index).selectedProperty()
                    );

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                int row = cell.getIndex();
                if (row >= 0 && row < lagerTableView.getItems().size()) {
                    DemoLagerItem item = lagerTableView.getItems().get(row);
                    item.selectedProperty().set(!item.selectedProperty().get());
                    System.out.println("[Lager] Clicked row: " + item.nameProperty().get() + ", " + item.nummerProperty().get()
                            + ", " + item.preisProperty().get() + ", " + item.bestandProperty().get());
                }
                evt.consume();
            });

            return cell;
        });

        // ────────────────────────────────────────────────────────────────────────────
        // EXAMPLE
        // ────────────────────────────────────────────────────────────────────────────
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nummer"));
        preisColumn.setCellValueFactory(new PropertyValueFactory<>("preis"));
        bestandColumn.setCellValueFactory(new PropertyValueFactory<>("bestand"));

        checkBoxColumn.setResizable(false);
        checkBoxColumn.setPrefWidth(50);

        lagerTableView.setSelectionModel(null);
        lagerTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }


    private void loadTableContent() {
        try {
            // ────────────────────────────────────────────────────────────────────────────
            // DEMO - delete later!!!
            // ────────────────────────────────────────────────────────────────────────────
            ObservableList<DemoLagerItem> items = FXCollections.observableArrayList(
                    new DemoLagerItem(false, "Schraube M5", "12345", 0.12, 150)
            );
            lagerTableView.setItems(items);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    public void onVerwendenButtonClick(ActionEvent actionEvent) {
        try {
            System.out.println("[Lager] Verwenden Button Clicked");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/technician/VerwendenPopupView.fxml"));
            Parent popupRoot = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Verwenden");
            popupStage.setScene(new Scene(popupRoot));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onBestellenButtonClick(ActionEvent actionEvent) {
        try {
            System.out.println("[Lager] Bestellen Button Clicked");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/technician/BestellenPopupView.fxml"));
            Parent popupRoot = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Bestellen");
            popupStage.setScene(new Scene(popupRoot));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setResizable(false);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // EXAMPLE
    // ────────────────────────────────────────────────────────────────────────────
    public static class DemoLagerItem {
        private final BooleanProperty selected = new SimpleBooleanProperty();
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty nummer = new SimpleStringProperty();
        private final DoubleProperty preis = new SimpleDoubleProperty();
        private final IntegerProperty bestand = new SimpleIntegerProperty();

        public DemoLagerItem(boolean sel, String nm, String nr, double pr, int bs) {
            selected.set(sel);
            name.set(nm);
            nummer.set(nr);
            preis.set(pr);
            bestand.set(bs);
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public StringProperty nameProperty() {
            return name;
        }

        public StringProperty nummerProperty() {
            return nummer;
        }

        public DoubleProperty preisProperty() {
            return preis;
        }

        public IntegerProperty bestandProperty() {
            return bestand;
        }
    }
}
