package com.rocketshipcheckingtool.ui.technician;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LagerViewController {
    public TextField searchField;
    public Button verwendenButton;
    public Button bestellenButton;
    public TableColumn checkBoxColumn;
    public TableColumn nameColumn;
    public TableColumn nrColumn;
    public TableColumn preisColumn;
    public TableColumn bestandColumn;
    public TableView lagerTableView;
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
        checkBoxColumn.setResizable(false);
        checkBoxColumn.setPrefWidth(50);
        lagerTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void setupCheckBoxColumn() {

    }

    private void loadTableContent() {
        try {

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
