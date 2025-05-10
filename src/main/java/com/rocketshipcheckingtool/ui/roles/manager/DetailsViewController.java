package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.datamodel.QuestionnaireRating;
import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.QuestionnaireUtil;
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

import java.io.IOException;
import java.util.List;

public class DetailsViewController extends DetailsViewControllerMaster {
    public TableView<QuestionnaireRating> umfrageTableView;
    public TableColumn<QuestionnaireRating, String> befragtePunkteColumn;
    public TableColumn<QuestionnaireRating, String> bewertungColumn;

    @FXML
    public void initialize() {
        super.initialize();
    }

    public void onKommentarButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[Manager Details] Kommentar Button Clicked");
        if (shuttleSelected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Bitte wählen Sie ein Shuttle aus.");
            alert.showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/manager/KommentarView.fxml"));
        Parent kommentarPage = loader.load();

        KommentarViewController kommentarViewController = loader.getController();
        kommentarViewController.setShuttle(shuttleSelected);
        kommentarViewController.setClientRequests(clientRequests);
        kommentarViewController.initialize();

        if (viewManagerController != null) {
            viewManagerController.setContent(kommentarPage);
        } else {
            System.err.println("viewManagerController is not set");
        }
    }

    public void onFreigebenButtonClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bitte bestätigen Sie die Freigabe");
        alert.setHeaderText(null);
        alert.setTitle("Bestätigung erforderlich");
        alert.show();
    }

    public void selectShuttle(Shuttle shuttle) {
        loadShuttleContent(shuttle.getShuttleName());
    }

    private void setUpTableColumns() {
        befragtePunkteColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));
        bewertungColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getRating() + "/10"));
        umfrageTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void loadData() {
        if (shuttleSelected == null) {
            return;
        }
        try {
            List<QuestionnaireRating> questionnaire = QuestionnaireUtil.getQuestionnaireForShuttle(clientRequests, user, shuttleSelected.getId());
            ObservableList<QuestionnaireRating> observableList = FXCollections.observableArrayList(questionnaire);
            umfrageTableView.setItems(observableList);
        } catch (Exception e) {
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
