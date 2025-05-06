package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.auth.LoginViewController;
import com.rocketshipcheckingtool.ui.auth.UserRole;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import com.rocketshipcheckingtool.ui.technician.LagerViewController;
import com.rocketshipcheckingtool.ui.technician.NachrichtenViewController;
import com.rocketshipcheckingtool.ui.technician.StatistikenViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Screen;
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
    private Node technicianHomePage, technicianDetailsPage, statistikenPage, lagerPage, nachrichtenPage;
    private Node managerHomePage, managerDetailsPage;
    private com.rocketshipcheckingtool.ui.technician.DetailsViewController technicianDetailsController;
    private com.rocketshipcheckingtool.ui.technician.HomeViewController technicianHomeController;
    private com.rocketshipcheckingtool.ui.manager.HomeViewController managerHomeController;
    private com.rocketshipcheckingtool.ui.manager.DetailsViewController managerDetailsController;
    private SidebarControllerInterface sidebarController;


    public void initAfterLogin() throws IOException {
        loadSidebar();
        loadContentPages();
    }

    private void loadSidebar() throws IOException {
        String sidebarPath = String.format("/com/rocketshipcheckingtool/ui/sidebar/%sSidebar.fxml",
                UserSession.getRole().name().toLowerCase());

        FXMLLoader loader = new FXMLLoader(getClass().getResource(sidebarPath));
        Node sidebar = loader.load();

        Object controller = loader.getController();
        if (controller instanceof SidebarControllerInterface sc) {
            sc.setMainController(this);
            this.sidebarController = sc;
        }

        BorderPane root = (BorderPane) contentArea.getParent();
        root.setLeft(sidebar);
    }

    private void loadContentPages() throws IOException {
        switch (UserSession.getRole()) {
            case TECHNICIAN -> {
                technicianHomePage = loadPage("/com/rocketshipcheckingtool/ui/technician/HomeView.fxml");
                technicianDetailsPage = loadPage("/com/rocketshipcheckingtool/ui/technician/DetailsView.fxml");
                statistikenPage = loadPage("/com/rocketshipcheckingtool/ui/technician/StatistikenView.fxml");
                lagerPage = loadPage("/com/rocketshipcheckingtool/ui/technician/LagerView.fxml");
                nachrichtenPage = loadPage("/com/rocketshipcheckingtool/ui/technician/NachrichtenView.fxml");

                contentArea.getChildren().addAll(technicianHomePage, technicianDetailsPage, statistikenPage, lagerPage, nachrichtenPage);
            }
            case MANAGER -> {
                managerHomePage = loadPage("/com/rocketshipcheckingtool/ui/manager/HomeView.fxml");
                managerDetailsPage = loadPage("/com/rocketshipcheckingtool/ui/manager/DetailsView.fxml");

                contentArea.getChildren().addAll(managerHomePage, managerDetailsPage);
            }
        }

        contentArea.getChildren().forEach(n -> n.setVisible(false));
        showHome();
    }

    private Region loadPage(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxml)));
        Region page = loader.load();

        Object controller = loader.getController();

        if (controller instanceof com.rocketshipcheckingtool.ui.technician.HomeViewController hvc) {
            technicianHomeController = hvc;
            hvc.setClientRequests(clientRequests);
            hvc.setViewManagerController(this);
        } else if (controller instanceof com.rocketshipcheckingtool.ui.manager.HomeViewController mhvc) {
            managerHomeController = mhvc;
            mhvc.setClientRequests(clientRequests);
            mhvc.setViewManagerController(this);
        } else if (controller instanceof com.rocketshipcheckingtool.ui.technician.DetailsViewController dvc) {
            technicianDetailsController = dvc;
            dvc.setClientRequests(clientRequests);
            dvc.setViewManagerController(this);
        } else if (controller instanceof com.rocketshipcheckingtool.ui.manager.DetailsViewController mdvc) {
            managerDetailsController = mdvc;
            mdvc.setClientRequests(clientRequests);
            mdvc.setViewManagerController(this);
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
        if (UserSession.getRole() == UserRole.TECHNICIAN && technicianDetailsController != null) {
            technicianDetailsController.selectShuttle(shuttle);
            showPage(technicianDetailsPage);
            sidebarController.selectButton("btnDetails");
        } else if (UserSession.getRole() == UserRole.MANAGER && managerDetailsController != null) {
            managerDetailsController.selectShuttle(shuttle);
            showPage(managerDetailsPage);
            sidebarController.selectButton("btnDetails");
        } else {
            System.err.println("[ViewManagerController] No valid details controller set!");
        }
    }

    public void handleBestellenButton() {
        showPage(lagerPage);
        sidebarController.selectButton("btnLager");
    }

    public void handleStatistikenVBox() {
        showPage(statistikenPage);
        sidebarController.selectButton("btnStatistiken");
    }

    public void showHome() {
        if (UserSession.getRole() == UserRole.TECHNICIAN && technicianHomeController != null) {
            technicianHomeController.initialize();
            technicianHomeController.loadTableContent();
            showPage(technicianHomePage);
        } else if (UserSession.getRole() == UserRole.MANAGER && managerHomeController != null) {
            managerHomeController.loadTableContent();
            showPage(managerHomePage);
        }
        sidebarController.selectButton("btnHome");
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

            Scene loginScene = new Scene(loginRoot);
            loginScene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/rocketshipcheckingtool/ui/style.css")).toExternalForm()
            );

            Stage newStage = new Stage();
            newStage.setTitle("Rocketship Checking Tool");
            newStage.setScene(loginScene);
            newStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/rocketshipcheckingtool/ui/graphics/icon.png"))));

            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            newStage.setX(bounds.getMinX());
            newStage.setY(bounds.getMinY());
            newStage.setWidth(bounds.getWidth());
            newStage.setHeight(bounds.getHeight());

            newStage.show();

            Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            oldStage.close();

//            newStage.setScene(loginScene);
//            newStage.setOnShown(e -> newStage.setMaximized(true));
//            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSidebarNavigation(String buttonId) {
        switch (buttonId) {
            case "btnHome" -> showHome();
            case "btnDetails" -> {
                if (UserSession.getRole() == UserRole.TECHNICIAN) {
                    showPage(technicianDetailsPage);
                } else if (UserSession.getRole() == UserRole.MANAGER) {
                    showPage(managerDetailsPage);
                }
                sidebarController.selectButton("btnDetails");
            }
            case "btnStatistiken" -> {
                if (UserSession.getRole() == UserRole.TECHNICIAN) {
                    showPage(statistikenPage);
                    sidebarController.selectButton("btnStatistiken");
                }
            }
            case "btnLager" -> {
                if (UserSession.getRole() == UserRole.TECHNICIAN) {
                    showPage(lagerPage);
                    sidebarController.selectButton("btnLager");
                }
            }
            case "btnNachrichten" -> {
                if (UserSession.getRole() == UserRole.TECHNICIAN) {
                    showPage(nachrichtenPage);
                    sidebarController.selectButton("btnNachrichten");
                }
            }
            default -> System.err.println("Unknown buttonId: " + buttonId);
        }
    }


    public void setContent(Parent kommentarPage) {
        if (kommentarPage == null) {
            System.err.println("[ViewManager] Tried to set null content.");
            return;
        }
        contentArea.getChildren().forEach(n -> n.setVisible(false));

        if (!contentArea.getChildren().contains(kommentarPage)) {
            contentArea.getChildren().add(kommentarPage);
        }
        kommentarPage.setVisible(true);
    }
}

