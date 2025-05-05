package com.rocketshipcheckingtool.ui.manager;

import com.rocketshipcheckingtool.ui.technician.NeueAufgabePopupController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DetailsViewController {
    public TableView umfrageTableView;
    public TableColumn befragtePunkteColumn;
    public TableColumn bewertungColumn;

    public void onKommentarButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[Manager Details] Kommentar Button Clicked");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/manager/KommentarView.fxml"));
        Parent popupRoot = loader.load();

        // New Stage for the Kommentar scene
        Stage stage = new Stage();
        stage.setTitle("Neue Aufgabe");
        stage.setScene(new Scene(popupRoot));

        KommentarViewController kommentarViewController = loader.getController();
        kommentarViewController.setStage(stage);
        kommentarViewController.initialize();
        stage.show();
    }

    public void onFreigebenButtonClicked(ActionEvent actionEvent) {


    }
}
