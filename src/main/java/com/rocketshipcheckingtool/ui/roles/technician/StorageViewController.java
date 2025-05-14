package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.datamodel.Part;
import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.PartUtil;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.helper.TableSearchHelper;
import com.rocketshipcheckingtool.ui.helper.Util;
import javafx.collections.FXCollections;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for managing the storage view in the technician role.
 * Handles the display and interaction with parts in the inventory.
 */
public class StorageViewController {
    public TextField searchField; // TextField for searching parts in the inventory.
    public Button useButton; // Button for using a selected part.
    public Button orderButton; // Button for ordering more of a selected part.

    public TableColumn<Part, Boolean> checkBoxColumn; // Column for selecting parts using checkboxes.
    public TableColumn<Part, Boolean> nameColumn; // Column for displaying part names.
    public TableColumn<Part, Boolean> nrColumn; // Column for displaying part IDs.
    public TableColumn<Part, Boolean> priceColumn; // Column for displaying part prices.
    public TableColumn<Part, Boolean> quantityColumn; // Column for displaying part quantities.
    public TableView<Part> storageTableView; // TableView for displaying parts in the inventory.

    private ClientRequests clientRequests; // ClientRequests instance for making HTTP requests.
    private final String user = UserSession.getRole().name().toLowerCase(); // Current user's role in lowercase.
    private TableSearchHelper<Part> searchHelper; // Helper for enabling search functionality in the table.
    private static final Logger logger = LoggerFactory.getLogger(StorageViewController.class); // Logger instance for logging activities.

    /**
     * Sets the ClientRequests instance and loads the table content.
     *
     * @param clientRequests The ClientRequests instance to be set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadTableContent();
    }

    /**
     * Initializes the controller and sets up the table columns and search functionality.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        searchHelper = new TableSearchHelper<>(
                storageTableView,
                searchField,
                Part::getName
        );
    }

    /**
     * Configures the columns of the storage table.
     * Sets up cell factories, value factories, and column properties.
     */
    private void setupTableColumns() {
        checkBoxColumn.setCellFactory(col -> {
            CheckBoxTableCell<Part, Boolean> cell =
                    new CheckBoxTableCell<>(index ->
                            storageTableView.getItems().get(index).selectedProperty()
                    );

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                int row = cell.getIndex();
                if (row >= 0 && row < storageTableView.getItems().size()) {
                    Part item = storageTableView.getItems().get(row);
                    for (Part part : storageTableView.getItems()) {
                        if (part != item) {
                            part.selectedProperty().set(false);
                        }
                    }
                    item.selectedProperty().set(!item.selectedProperty().get());
                    logger.info("Row selected: name='{}', id={}, price={}, quantity={}", item.getName(), item.getId(), item.getPrice(), item.getQuantity());
                }
                evt.consume();
            });

            return cell;
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        checkBoxColumn.setResizable(false);
        checkBoxColumn.setPrefWidth(50);

        storageTableView.getSelectionModel().setCellSelectionEnabled(false);
        storageTableView.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (!event.getTarget().toString().contains("CheckBox")) {
                    storageTableView.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
        storageTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    /**
     * Loads the content of the storage table.
     * Fetches parts from the server and populates the table.
     */
    private void loadTableContent() {
        try {
            logger.debug("Loading parts for user '{}'", user);
            List<Part> parts = PartUtil.getParts(clientRequests, user);
            storageTableView.setItems(FXCollections.observableArrayList(parts));
            if (searchHelper != null) {
                searchHelper.setItems(parts);
            }
            logger.info("Loaded {} parts into table", parts.size());
        } catch (Exception e) {
            logger.error("Failed to load parts", e);
            Util.showErrorDialog("Failed to load parts: " + e.getMessage());
            storageTableView.setDisable(true);
            useButton.setDisable(true);
            orderButton.setDisable(true);
        }
    }

    /**
     * Handles the action when the "Use" button is clicked.
     * Opens a popup dialog for using a selected part.
     *
     * @param actionEvent The action event triggered by the button click.
     */
    public void onVerwendenButtonClick(ActionEvent actionEvent) {
        try {
            logger.info("Use button clicked in StorageViewController");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/roles/technician/VerwendenPopupView.fxml"));
            Parent popupRoot = loader.load();
            UsePartPopupController usePartPopupController = loader.getController();

            Stage popupStage = new Stage();
            try {
                Part part = storageTableView.getItems().stream()
                        .filter(Part::isSelected)
                        .findFirst()
                        .orElse(null);
                if (part == null) {
                    logger.warn("No part selected for use");
                    alertDidntSelect();
                    return;
                }
                popupStage.setTitle(part.getName() + " - Use from inventory");
                usePartPopupController.setPart(part.getName());
                usePartPopupController.setPrice(part.getPrice());
                usePartPopupController.setMaxQuantity(part.getQuantity());
                popupStage.setScene(new Scene(popupRoot));
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.showAndWait();
                if (usePartPopupController.getIsVerwendenButton()) {
                    logger.info("Confirmed use of part '{}', quantity={}", part.getName(), usePartPopupController.getQuantity());
                    PartUtil.usePart(clientRequests, user, part.getId(), usePartPopupController.getQuantity());
                    loadTableContent();
                } else {
                    logger.debug("Use dialog closed without confirmation");
                }
            } catch (NullPointerException e) {
                logger.error("NullPointerException in use dialog", e);
                alertDidntSelect();
            }
        } catch (IOException e) {
            logger.error("IOException in onVerwendenButtonClick", e);
            Util.showErrorDialog("Error opening use dialog: " + e.getMessage());
        }
    }

    /**
     * Handles the action when the "Order" button is clicked.
     * Opens a popup dialog for ordering more of a selected part.
     *
     * @param actionEvent The action event triggered by the button click.
     */
    public void onBestellenButtonClick(ActionEvent actionEvent) {
        try {
            logger.info("Order button clicked in StorageViewController");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/roles/technician/OrderPopupView.fxml"));
            Parent popupRoot = loader.load();
            OrderPopupController orderPopupController = loader.getController();
            orderPopupController.setClientRequests(clientRequests);
            Part part = storageTableView.getItems().stream()
                    .filter(Part::isSelected)
                    .findFirst()
                    .orElse(null);

            Stage popupStage = new Stage();
            try {
                if (part == null) {
                    logger.warn("No part selected for order");
                    alertDidntSelect();
                    return;
                }
                popupStage.setTitle(part.getName() + " - Order more");
                orderPopupController.setPart(part.getName());
                orderPopupController.setPrice(part.getPrice());
                popupStage.setScene(new Scene(popupRoot));
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setResizable(false);
                popupStage.showAndWait();
                if (orderPopupController.getIsBestellenButton()) {
                    Shuttle shuttle = orderPopupController.getSelectedShuttle();
                    logger.info("Confirmed order of part '{}', quantity={}, shuttle={}", part.getName(), orderPopupController.getQuantity(), shuttle != null ? shuttle.getShuttleName() : "None");
                    if (shuttle != null) {
                        PartUtil.orderPart(clientRequests, user, part.getId(), orderPopupController.getQuantity(), shuttle.getId());
                    } else {
                        PartUtil.orderPart(clientRequests, user, part.getId(), orderPopupController.getQuantity(), null);
                    }
                    loadTableContent();
                } else {
                    logger.debug("Order dialog closed without confirmation");
                }
            } catch (NullPointerException e) {
                logger.error("NullPointerException in order dialog", e);
                alertDidntSelect();
            }
        } catch (IOException e) {
            logger.error("IOException in onBestellenButtonClick", e);
            Util.showErrorDialog("Error opening order dialog: " + e.getMessage());
        }
    }

    /**
     * Displays an alert when no row is selected in the inventory table.
     */
    public void alertDidntSelect() {
        logger.info("No row selected in inventory table when attempting action");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wähle bitte eine Zeile aus");
        alert.setHeaderText(null);
        alert.setContentText("Bitte wähle eine Zeile aus, um mit dem Artikel zu interagieren.");
        alert.showAndWait();
    }
}