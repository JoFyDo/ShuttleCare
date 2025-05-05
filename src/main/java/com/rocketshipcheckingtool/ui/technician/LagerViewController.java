package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Part;
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


    public TableColumn<Part, Boolean> checkBoxColumn;
    public TableColumn<Part, Boolean> nameColumn;
    public TableColumn<Part, Boolean> nrColumn;
    public TableColumn<Part, Boolean> preisColumn;
    public TableColumn<Part, Boolean> bestandColumn;
    public TableView<Part> lagerTableView;
    private ClientRequests clientRequests;
    private final String user = "technician";


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
            CheckBoxTableCell<Part, Boolean> cell =
                    new CheckBoxTableCell<Part, Boolean>(index ->
                            lagerTableView.getItems().get(index).selectedProperty()
                    );

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                int row = cell.getIndex();
                if (row >= 0 && row < lagerTableView.getItems().size()) {
                    Part item = lagerTableView.getItems().get(row);
                    for (Part part : lagerTableView.getItems()) {
                        if (part != item) {
                            part.selectedProperty().set(false);
                        }
                    }
                    item.selectedProperty().set(!item.selectedProperty().get());
                    System.out.println("[Lager] Clicked row: " + item.getName() + ", " + item.getId()
                            + ", " + item.getPrice() + ", " + item.getQuantity());
                }
                evt.consume();
            });

            return cell;
        });

        // ────────────────────────────────────────────────────────────────────────────
        // EXAMPLE
        // ────────────────────────────────────────────────────────────────────────────
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        preisColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        bestandColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        checkBoxColumn.setResizable(false);
        checkBoxColumn.setPrefWidth(50);

        lagerTableView.setSelectionModel(null);
        lagerTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }


    private void loadTableContent() {
        try {
            lagerTableView.setItems(FXCollections.observableArrayList(Util.getParts(clientRequests, user)));

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
            VerwendenPopupController verwendenPopupController = loader.getController();


            Stage popupStage = new Stage();
            Part part = lagerTableView.getItems().stream()
                    .filter(Part::isSelected)
                    .findFirst()
                    .orElse(null);
            popupStage.setTitle(part.getName() + " aus dem Lager entnehmen");
            verwendenPopupController.setTeil(part.getName());
            verwendenPopupController.setPreis(part.getPrice());
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
}
