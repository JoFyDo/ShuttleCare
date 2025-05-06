package com.rocketshipcheckingtool.ui.manager;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import com.rocketshipcheckingtool.ui.technician.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class DetailsViewController {
    public TableView<UmfrageResult> umfrageTableView;
    public TableColumn<UmfrageResult, String> befragtePunkteColumn;
    public TableColumn<UmfrageResult, String> bewertungColumn;
    public ViewManagerController viewManagerController;
    public ComboBox<String> shuttleComboBox;
    public Shuttle shuttleSelected;
    public List<String> shuttleList;
    public List<Shuttle> shuttles;
    private ClientRequests clientRequests;
    private final String user = UserSession.getRole().name().toLowerCase();

    @FXML
    public void initialize() {
        setUpTableColumns();
        // Only to display example:
        loadData();

        shuttleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                shuttleSelected = shuttles.stream()
                        .filter(sh -> sh.getShuttleName().equals(newVal))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Shuttle not found"));
                System.out.println("[Manager Details] selected Shuttle: " + newVal);
                //loadData();
            }
        });
        shuttleComboBox.getStyleClass().add("comboBox");

    }

    public void onKommentarButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[Manager Details] Kommentar Button Clicked");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/manager/KommentarView.fxml"));
        Parent kommentarPage = loader.load();

        KommentarViewController kommentarViewController = loader.getController();
        kommentarViewController.initialize();

        if (viewManagerController != null) {
            viewManagerController.setContent(kommentarPage);
        } else {
            System.err.println("viewManagerController is not set");
        }
    }

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
    }

    public void onFreigebenButtonClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bitte bestätigen Sie die Freigabe");
        alert.setHeaderText(null);
        alert.setTitle("Bestätigung erforderlich");
        alert.show();
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        // loadShuttleContent();
    }

    public void selectShuttle(Shuttle shuttle) {
        loadShuttleContent(shuttle.getShuttleName());
    }


    private void setUpTableColumns() {
        befragtePunkteColumn.setCellValueFactory(new PropertyValueFactory<>("punkt"));
        bewertungColumn.setCellValueFactory(new PropertyValueFactory<>("bewertung"));
        umfrageTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void loadData() {
        ObservableList<UmfrageResult> mockData = FXCollections.observableArrayList(
                new UmfrageResult("Sicherheit", "2"),
                new UmfrageResult("Zuverlässigkeit", "1"),
                new UmfrageResult("Effizienz", "2")
        );
        umfrageTableView.setItems(mockData);
    }

    // Combobox

    //no preselected shuttle
    private void loadShuttleContent() {
        loadShuttleContent(null);
    }

    //preselected shuttle
    private void loadShuttleContent(String preSelectedShuttle) {
        try {
            //Shuttle
            shuttles = Util.getShuttles(clientRequests, user);

            shuttleList = shuttles.stream()
                    .map(Shuttle::getShuttleName)
                    .toList();

            shuttleComboBox.getItems().clear();
            shuttleComboBox.getItems().addAll(shuttleList);

            shuttleSelected = shuttles.stream()
                    .filter(sh -> sh.getShuttleName().equals(preSelectedShuttle))
                    .findFirst()
                    .orElse(null);

            if (preSelectedShuttle != null && shuttleList.contains(preSelectedShuttle)) {
                shuttleComboBox.setValue(preSelectedShuttle);
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    // Testiessss
    public static class UmfrageResult {
        private final String punkt;
        private final String bewertung;

        public UmfrageResult(String punkt, String bewertung) {
            this.punkt = punkt;
            this.bewertung = bewertung;
        }

        public String getPunkt() {
            return punkt;
        }

        public String getBewertung() {
            return bewertung;
        }
    }
}
