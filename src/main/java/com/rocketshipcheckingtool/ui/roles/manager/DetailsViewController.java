package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.datamodel.QuestionnaireRating;
import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.QuestionnaireUtil;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
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

public class DetailsViewController extends DetailsViewControllerMaster {
    public TableView<QuestionnaireRating> questionnaireTableView;
    public TableColumn<QuestionnaireRating, String> questionedPointsColumn;
    public TableColumn<QuestionnaireRating, String> ratingColumn;

    private static final Logger logger = LoggerFactory.getLogger(DetailsViewController.class);

    @FXML
    public void initialize() {
        super.initialize();
        logger.info("Manager DetailsViewController initialized");
    }

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

    public void onFreigebenButtonClicked(ActionEvent actionEvent) {
        logger.info("Release button clicked");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bitte bestätigen Sie die Freigabe");
        alert.setHeaderText(null);
        alert.setTitle("Bestätigung erforderlich");
        alert.show();
    }

    public void selectShuttle(Shuttle shuttle) {
        logger.info("Shuttle '{}' selected in manager details view", shuttle != null ? shuttle.getShuttleName() : "null");
        loadShuttleContent(shuttle.getShuttleName());
    }

    private void setUpTableColumns() {
        questionedPointsColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));
        ratingColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getRating() + "/10"));
        questionnaireTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        logger.debug("Survey table columns set up");
    }

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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    @Override
    protected void reload() {
        setUpTableColumns();
        loadData();
    }

}
