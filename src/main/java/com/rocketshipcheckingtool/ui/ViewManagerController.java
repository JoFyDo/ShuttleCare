package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.technician.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

public class ViewManagerController {

    private ClientRequests clientRequests;
    public ToggleGroup toggleGroup;
    public ToggleButton btnHome, btnDetails, btnStatistiken, btnLager, btnNachrichten;
    public StackPane contentArea;
    private Node homePage, detailsPage, statistikenPage, lagerPage, nachrichtenPage;
    private DetailsViewController detailsController;
    private HomeViewController homeController;


    @FXML
    public void initContent() throws IOException {
        homePage = loadPage("/com/rocketshipcheckingtool/ui/technician/HomeView.fxml");
        detailsPage = loadPage("/com/rocketshipcheckingtool/ui/technician/DetailsView.fxml");
        statistikenPage = loadPage("/com/rocketshipcheckingtool/ui/technician/StatistikenView.fxml");
        lagerPage = loadPage("/com/rocketshipcheckingtool/ui/technician/LagerView.fxml");
        nachrichtenPage = loadPage("/com/rocketshipcheckingtool/ui/technician/NachrichtenView.fxml");

        contentArea.getChildren().addAll(homePage, detailsPage, statistikenPage, lagerPage, nachrichtenPage);
        contentArea.getChildren().forEach(node -> node.setVisible(false));

        homePage.setVisible(true);
        btnHome.setSelected(true);
    }

    @FXML
    private void handleSidebarClick(ActionEvent event) {
        if (event.getSource() == btnHome) {
            homeController.initialize();
            homeController.loadTableContent();
            showPage(homePage);
        } else if (event.getSource() == btnDetails) {
            showPage(detailsPage);
        } else if (event.getSource() == btnStatistiken) {
            showPage(statistikenPage);
        } else if (event.getSource() == btnLager) {
            showPage(lagerPage);
        } else if (event.getSource() == btnNachrichten) {
            showPage(nachrichtenPage);
        }
    }

    private Node loadPage(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxml)));
        Node page = loader.load();

        if (fxml.endsWith("HomeView.fxml")) {
            homeController = loader.getController();
            homeController.setClientRequests(this.clientRequests);
            homeController.setViewManagerController(this);
        } else if (fxml.endsWith("DetailsView.fxml")) {
            detailsController = loader.getController();
            detailsController.setClientRequests(this.clientRequests);
            detailsController.setViewManagerController(this);
        } else if (fxml.endsWith("StatistikenView.fxml")) {
            StatistikenViewController statistikenController = loader.getController();
            statistikenController.setClientRequests(this.clientRequests);
            statistikenController.setViewManagerController(this);
        } else if (fxml.endsWith("LagerView.fxml")) {
            LagerViewController lagerController = loader.getController();
            lagerController.setClientRequests(this.clientRequests);
        } else if (fxml.endsWith("NachrichtenView.fxml")) {
            NachrichtenViewController nachrichtenController = loader.getController();
            nachrichtenController.setClientRequests(this.clientRequests);
        }
        return page;
    }

    private void showPage(Node page) {
        contentArea.getChildren().forEach(node -> node.setVisible(false));
        page.setVisible(true);
    }

    public void handleDetailButton(Shuttle shuttle) throws IOException {
        detailsController.selectShuttle(shuttle);
        showPage(detailsPage);
        btnDetails.setSelected(true);
    }

    public void handleBestellenButton() {
        showPage(lagerPage);
        btnLager.setSelected(true);
    }

    public void handleStatistikenVBox() {
        showPage(statistikenPage);
        btnStatistiken.setSelected(true);
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }
}
