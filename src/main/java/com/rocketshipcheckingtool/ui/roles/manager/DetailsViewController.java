package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.datamodel.QuestionnaireRating;
import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.QuestionnaireUtil;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.helper.Util;
import com.rocketshipcheckingtool.ui.roles.masterController.DetailsViewControllerMaster;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing the details view in the manager role.
 * Handles the display and interaction with shuttles and their associated data.
 */
public class DetailsViewController extends DetailsViewControllerMaster {
    public TableView<QuestionnaireRating> questionnaireTableView; // TableView for displaying questionnaire ratings.
    public TableColumn<QuestionnaireRating, String> questionedPointsColumn; // Column for displaying questioned points.
    public TableColumn<QuestionnaireRating, String> ratingColumn; // Column for displaying ratings.

    private static final Logger logger = LoggerFactory.getLogger(DetailsViewController.class); // Logger instance for logging activities.

    /**
     * Initializes the controller and sets up the necessary configurations.
     */
    @FXML
    public void initialize() {
        super.initialize();
        logger.info("Manager DetailsViewController initialized");
    }

    /**
     * Retrieves the list of shuttles filtered by specific statuses.
     *
     * @return A filtered list of shuttles.
     * @throws IOException If an error occurs while retrieving the shuttle list.
     */
    @Override
    protected ArrayList<Shuttle> getShuttleList() throws IOException {
        ArrayList<Shuttle> shuttles = ShuttleUtil.getShuttles(clientRequests, user);
        ArrayList<Shuttle> filteredShuttles = new ArrayList<>();
        for (Shuttle shuttle : shuttles) {
            if (shuttle.getStatus().equals("Gelandet") || shuttle.getStatus().equals("Inspektion 1") || shuttle.getStatus().equals("Inspektion 2") || shuttle.getStatus().equals("In Wartung")) {
                filteredShuttles.add(shuttle);
            }
        }
        return filteredShuttles;
    }

    /**
     * Handles the action of the comment button click.
     * Opens the comment view for the selected shuttle.
     *
     * @param actionEvent The ActionEvent triggered by the button click.
     * @throws IOException If an error occurs while loading the comment view.
     */
    public void onKommentarButtonClicked(ActionEvent actionEvent) throws IOException {
        logger.info("Comment button clicked");
        if (shuttleSelected == null) {
            logger.warn("No shuttle selected when trying to open comment dialog");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte wählen Sie ein Shuttle aus.");
            alert.showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/roles/manager/CommentView.fxml"));
        Parent kommentarPage = loader.load();

        CommentViewController commentViewController = loader.getController();
        commentViewController.setShuttle(shuttleSelected);
        commentViewController.setClientRequests(clientRequests);
        commentViewController.initialize();

        if (viewManagerController != null) {
            viewManagerController.setContent(kommentarPage);
            logger.debug("Comment view loaded for shuttle '{}'", shuttleSelected.getShuttleName());
        } else {
            logger.error("viewManagerController is not set");
        }
    }

    /**
     * Handles the action of the release button click.
     * Displays a confirmation dialog for releasing the shuttle.
     *
     * @param actionEvent The ActionEvent triggered by the button click.
     */
    public void onFreigebenButtonClicked(ActionEvent actionEvent) {
        logger.info("Release button clicked");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bitte bestätigen Sie die Freigabe");
        alert.setHeaderText(null);
        alert.setTitle("Bestätigung erforderlich");
        alert.show();
    }

    /**
     * Selects a shuttle and loads its content into the view.
     *
     * @param shuttle The shuttle to select.
     */
    public void selectShuttle(Shuttle shuttle) {
        logger.info("Shuttle '{}' selected in manager details view", shuttle != null ? shuttle.getShuttleName() : "null");
        loadShuttleContent(shuttle.getShuttleName());
    }

    /**
     * Sets up the columns for the questionnaire TableView.
     */
    private void setUpTableColumns() {
        questionedPointsColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));
        ratingColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getRating() + "/10"));
        questionnaireTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        logger.debug("Survey table columns set up");
    }

    /**
     * Loads the questionnaire data for the selected shuttle into the TableView.
     */
    private void loadData() {
        if (shuttleSelected == null) {
            logger.warn("No shuttle selected when loading survey data");
            return;
        }
        try {
            List<QuestionnaireRating> questionnaire = QuestionnaireUtil.getQuestionnaireForShuttle(clientRequests, user, shuttleSelected.getId());
            ObservableList<QuestionnaireRating> observableList = FXCollections.observableArrayList(questionnaire);
            questionnaireTableView.setItems(observableList);
            logger.info("Loaded {} questionnaire ratings for shuttle '{}'", observableList.size(), shuttleSelected.getShuttleName());
        } catch (Exception e) {
            logger.error("Error loading questionnaire data: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Laden der Fragebogen-Antworten: " + e.getMessage());
        }
    }

    /**
     * Reloads the view by setting up the table columns and loading data.
     */
    @Override
    protected void reload() {
        setUpTableColumns();
        loadData();
    }
}