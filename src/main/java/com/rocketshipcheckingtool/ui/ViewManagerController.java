package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.SidebarControllerInterface;
import com.rocketshipcheckingtool.domain.UserRole;
import com.rocketshipcheckingtool.domain.UserSession;
import com.rocketshipcheckingtool.ui.technician.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ViewManagerController {

    public VBox managerSidebar;
    public ToggleButton btnLogout;
    public Pane spacer;
    public VBox technicianSidebar;
    public VBox sidebarPlaceholder;
    private ClientRequests clientRequests;
    public ToggleGroup toggleGroup;
    public ToggleButton btnHome, btnDetails, btnStatistiken, btnLager, btnNachrichten;
    public StackPane contentArea;
    private Node homePage, detailsPage, statistikenPage, lagerPage, nachrichtenPage;
    private DetailsViewController detailsController;
    private HomeViewController homeController;
    private com.rocketshipcheckingtool.ui.manager.HomeViewController managerHomeController;



    public void initAfterLogin() throws IOException {
        loadSidebar();
        loadContentPages();
    }

    private void loadSidebar() throws IOException {
        String sidebarPath = String.format("/com/rocketshipcheckingtool/ui/sidebar/%sSidebar.fxml",
                UserSession.getRole().name().toLowerCase());

        FXMLLoader loader = new FXMLLoader(getClass().getResource(sidebarPath));
        Node sidebar = loader.load();

        Object sidebarController = loader.getController();
        if (sidebarController instanceof SidebarControllerInterface controller) {
            controller.setMainController(this);
        }

        BorderPane root = (BorderPane) contentArea.getParent();
        root.setLeft(sidebar);
    }

    private void loadContentPages() throws IOException {
        switch (UserSession.getRole()) {
            case TECHNICIAN -> {
                homePage = loadPage("/com/rocketshipcheckingtool/ui/technician/HomeView.fxml");
                detailsPage = loadPage("/com/rocketshipcheckingtool/ui/technician/DetailsView.fxml");
                statistikenPage = loadPage("/com/rocketshipcheckingtool/ui/technician/StatistikenView.fxml");
                lagerPage = loadPage("/com/rocketshipcheckingtool/ui/technician/LagerView.fxml");
                nachrichtenPage = loadPage("/com/rocketshipcheckingtool/ui/technician/NachrichtenView.fxml");

                contentArea.getChildren().addAll(homePage, detailsPage, statistikenPage, lagerPage, nachrichtenPage);
            }
            case MANAGER -> {
                homePage = loadPage("/com/rocketshipcheckingtool/ui/manager/HomeView.fxml");
                detailsPage = loadPage("/com/rocketshipcheckingtool/ui/manager/DetailsView.fxml");

                contentArea.getChildren().addAll(homePage, detailsPage);
            }
        }

        contentArea.getChildren().forEach(n -> n.setVisible(false));
        homePage.setVisible(true);
    }

    private Region loadPage(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxml)));
        Region page = loader.load();
        //page.prefWidthProperty().bind(contentArea.widthProperty());
        //page.prefHeightProperty().bind(contentArea.heightProperty());

        Object controller = loader.getController();

        if (controller instanceof com.rocketshipcheckingtool.ui.technician.HomeViewController hvc) {
            homeController = hvc;
            hvc.setClientRequests(clientRequests);
            hvc.setViewManagerController(this);

        } else if (controller instanceof com.rocketshipcheckingtool.ui.manager.HomeViewController mhvc) {
            managerHomeController = mhvc;
            mhvc.setClientRequests(clientRequests);
            mhvc.setViewManagerController(this);

        } else if (controller instanceof DetailsViewController dvc) {
            detailsController = dvc;
            dvc.setClientRequests(clientRequests);
            dvc.setViewManagerController(this);

        } else if (controller instanceof StatistikenViewController svc) {
            svc.setClientRequests(clientRequests);
            svc.setViewManagerController(this);

        } else if (controller instanceof LagerViewController lvc) {
            lvc.setClientRequests(clientRequests);

        } else if (controller instanceof NachrichtenViewController nvc) {
            nvc.setClientRequests(clientRequests);
        }

        return page;
    }

    private void showPage(Node page) {
        contentArea.getChildren().forEach(n -> n.setVisible(false));
        page.setVisible(true);
    }

    public void handleDetailButton(Shuttle shuttle) throws IOException {
        detailsController.selectShuttle(shuttle);
        showPage(detailsPage);
        if (btnDetails != null) {
            btnDetails.setSelected(true);
        }
    }

    public void handleBestellenButton() {
        showPage(lagerPage);
        btnLager.setSelected(true);
    }

    public void handleStatistikenVBox() {
        showPage(statistikenPage);
        btnStatistiken.setSelected(true);
    }

    public void showHome() {
        if (UserSession.getRole() == UserRole.TECHNICIAN && homeController != null) {
            homeController.initialize();
            homeController.loadTableContent();
            showPage(homePage);
        } else if (UserSession.getRole() == UserRole.MANAGER && managerHomeController != null) {
            managerHomeController.loadTableContent(); // No need to re-init columns
            showPage(homePage);
        }
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }

    public void handleLogout(ActionEvent event) {
        try {
            UserSession.setRole(null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/LoginView.fxml"));
            Parent loginRoot = loader.load();

            LoginViewController loginController = loader.getController();
            loginController.setClientRequests(clientRequests);

            Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            oldStage.close();

            Stage newStage = new Stage();
            newStage.setTitle("Rocketship Checking Tool");

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/rocketshipcheckingtool/ui/graphics/icon.png")));
            newStage.getIcons().add(icon);

            Scene loginScene = new Scene(loginRoot);
            loginScene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/rocketshipcheckingtool/ui/style.css")).toExternalForm()
            );

            newStage.setScene(loginScene);
            newStage.setMaximized(true);
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSidebarNavigation(String buttonId) {
        switch (buttonId) {
            case "btnHome" -> showHome();
            case "btnDetails" -> showPage(detailsPage);
            case "btnStatistiken" -> showPage(statistikenPage);
            case "btnLager" -> showPage(lagerPage);
            case "btnNachrichten" -> showPage(nachrichtenPage);
            default -> System.err.println("Unknown buttonId: " + buttonId);
        }
    }
}

