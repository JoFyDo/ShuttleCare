package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.auth.UserRole;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.helper.Util;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import com.rocketshipcheckingtool.ui.roles.technician.StorageViewController;
import com.rocketshipcheckingtool.ui.roles.technician.StatsViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller class for managing the main view and navigation logic.
 * Handles loading sidebars, content pages, and user interactions.
 */
public class ViewManagerController {

    public VBox managerSidebar; // Sidebar for the manager role.
    public ToggleButton btnLogout; // Button for logging out.
    public Pane spacer; // Spacer element in the layout.
    public VBox technicianSidebar; // Sidebar for the technician role.
    public VBox sidebarPlaceholder; // Placeholder for dynamically loaded sidebars.
    private ClientRequests clientRequests; // Instance for handling client requests.
    public ToggleGroup toggleGroup; // Group for managing toggle buttons.
    public ToggleButton btnHome, btnDetails, btnStatistiken, btnLager, btnNachrichten; // Sidebar buttons.
    public StackPane contentArea; // Container for displaying content pages.
    private Node technicianHomePage, technicianDetailsPage, statistikenPage, lagerPage, technicianNotificationPage; // Technician pages.
    private Node managerHomePage, managerDetailsPage, managerNotificationPage; // Manager pages.
    private com.rocketshipcheckingtool.ui.roles.technician.DetailsViewController technicianDetailsController; // Technician details controller.
    private com.rocketshipcheckingtool.ui.roles.technician.HomeViewController technicianHomeController; // Technician home controller.
    private com.rocketshipcheckingtool.ui.roles.manager.HomeViewController managerHomeController; // Manager home controller.
    private com.rocketshipcheckingtool.ui.roles.manager.DetailsViewController managerDetailsController; // Manager details controller.
    private SidebarControllerInterface sidebarController; // Interface for managing sidebars.
    private static final Logger logger = LoggerFactory.getLogger(ViewManagerController.class); // Logger for logging activities.

    /**
     * Initializes the view manager after login.
     * Loads the sidebar and content pages based on the user's role.
     *
     * @throws IOException If an error occurs while loading resources.
     */
    public void initAfterLogin() throws IOException {
        loadSidebar();
        loadContentPages();
    }

    /**
     * Loads the sidebar based on the user's role.
     * Dynamically sets the sidebar controller.
     *
     * @throws IOException If an error occurs while loading the sidebar.
     */
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

    /**
     * Loads the content pages for the current user's role.
     * Adds the pages to the content area and sets the default view.
     *
     * @throws IOException If an error occurs while loading the pages.
     */
    private void loadContentPages() throws IOException {
        switch (UserSession.getRole()) {
            case TECHNICIAN -> {
                technicianHomePage = loadPage("/com/rocketshipcheckingtool/ui/roles/technician/HomeView.fxml");
                technicianDetailsPage = loadPage("/com/rocketshipcheckingtool/ui/roles/technician/DetailsView.fxml");
                statistikenPage = loadPage("/com/rocketshipcheckingtool/ui/roles/technician/StatsView.fxml");
                lagerPage = loadPage("/com/rocketshipcheckingtool/ui/roles/technician/StorageView.fxml");
                technicianNotificationPage = loadPage("/com/rocketshipcheckingtool/ui/roles/technician/NotifictionView.fxml");

                contentArea.getChildren().addAll(technicianHomePage, technicianDetailsPage, statistikenPage, lagerPage, technicianNotificationPage);
            }
            case MANAGER -> {
                managerHomePage = loadPage("/com/rocketshipcheckingtool/ui/roles/manager/HomeView.fxml");
                managerDetailsPage = loadPage("/com/rocketshipcheckingtool/ui/roles/manager/DetailsView.fxml");
                managerNotificationPage = loadPage("/com/rocketshipcheckingtool/ui/roles/manager/NotificationView.fxml");
                contentArea.getChildren().addAll(managerHomePage, managerDetailsPage, managerNotificationPage);
            }
        }

        contentArea.getChildren().forEach(n -> n.setVisible(false));
        showHome();
    }

    /**
     * Loads a specific page from an FXML file.
     * Sets up the controller for the loaded page.
     *
     * @param fxml The path to the FXML file.
     * @return The loaded page as a `Region`.
     * @throws IOException If an error occurs while loading the page.
     */
    private Region loadPage(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxml)));
        Region page = loader.load();

        Object controller = loader.getController();

        if (controller instanceof com.rocketshipcheckingtool.ui.roles.technician.HomeViewController hvc) {
            technicianHomeController = hvc;
            hvc.setClientRequests(clientRequests);
            hvc.setViewManagerController(this);
        } else if (controller instanceof com.rocketshipcheckingtool.ui.roles.manager.HomeViewController mhvc) {
            managerHomeController = mhvc;
            mhvc.setClientRequests(clientRequests);
            mhvc.setViewManagerController(this);
        } else if (controller instanceof com.rocketshipcheckingtool.ui.roles.technician.DetailsViewController dvc) {
            technicianDetailsController = dvc;
            dvc.setClientRequests(clientRequests);
            dvc.setViewManagerController(this);
        } else if (controller instanceof com.rocketshipcheckingtool.ui.roles.manager.DetailsViewController mdvc) {
            managerDetailsController = mdvc;
            mdvc.setClientRequests(clientRequests);
            mdvc.setViewManagerController(this);
        } else if (controller instanceof StatsViewController svc) {
            svc.setClientRequests(clientRequests);
            svc.setViewManagerController(this);
        } else if (controller instanceof StorageViewController lvc) {
            lvc.setClientRequests(clientRequests);
        } else if (controller instanceof com.rocketshipcheckingtool.ui.roles.technician.NotificationViewController nvc) {
            nvc.setClientRequests(clientRequests);
        } else if (controller instanceof com.rocketshipcheckingtool.ui.roles.manager.NotificationViewController nvc) {
            nvc.setClientRequests(clientRequests);
        }

        return page;
    }

    /**
     * Displays the specified page in the content area.
     * Hides all other pages.
     *
     * @param page The page to be displayed.
     */
    private void showPage(Node page) {
        contentArea.getChildren().forEach(n -> n.setVisible(false));
        page.setVisible(true);
    }

    /**
     * Handles the action of showing the details page for a specific shuttle.
     * Updates the details controller with the selected shuttle.
     *
     * @param shuttle The shuttle to be displayed in the details view.
     * @throws IOException If an error occurs while loading the details page.
     */
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
            logger.error("[ViewManagerController] No valid details controller set!");
            Util.showErrorDialog("Fehler beim Anzeigen der Details: Kein gültiger Details-Controller gesetzt!");
        }
    }

    /**
     * Handles the action of showing the storage page.
     * Updates the sidebar selection.
     */
    public void handleBestellenButton() {
        showPage(lagerPage);
        sidebarController.selectButton("btnLager");
    }

    /**
     * Displays the home page based on the user's role.
     * Initializes and loads the home page content.
     */
    public void showHome() {
        if (UserSession.getRole() == UserRole.TECHNICIAN && technicianHomeController != null) {
            technicianHomeController.initialize();
            technicianHomeController.loadShuttleTableContent();
            technicianHomeController.load();
            showPage(technicianHomePage);
        } else if (UserSession.getRole() == UserRole.MANAGER && managerHomeController != null) {
            managerHomeController.loadShuttleTableContent();
            showPage(managerHomePage);
        }
        sidebarController.selectButton("btnHome");
    }

    /**
     * Sets the `ClientRequests` instance for handling server requests.
     *
     * @param clientRequests The `ClientRequests` instance to be set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }

    /**
     * Handles the logout action.
     * Closes the current stage and displays the login view.
     *
     * @param event The action event triggered by the logout button.
     */
    public void handleLogout(ActionEvent event) {
        UserSession.setRole(null);
        Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        oldStage.close();
        Stage stage = new Stage();
        try {
            ViewManager.showLoginView(stage);
        } catch (Exception e) {
            logger.error("[ViewManagerController] Error while logging out: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Ausloggen: " + e.getMessage());
        }
    }

    /**
     * Handles navigation based on the sidebar button clicked.
     * Displays the corresponding page and updates the sidebar selection.
     *
     * @param buttonId The ID of the button clicked.
     */
    public void handleSidebarNavigation(String buttonId) {
        try {
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
                case "btnStats" -> {
                    if (UserSession.getRole() == UserRole.TECHNICIAN) {
                        showPage(statistikenPage);
                        sidebarController.selectButton("btnStats");
                    }
                }
                case "btnStorage" -> {
                    if (UserSession.getRole() == UserRole.TECHNICIAN) {
                        showPage(lagerPage);
                        sidebarController.selectButton("btnStorage");
                    }
                }
                case "btnNotification" -> {
                    if (UserSession.getRole() == UserRole.TECHNICIAN) {
                        showPage(technicianNotificationPage);
                    } else if (UserSession.getRole() == UserRole.MANAGER) {
                        showPage(managerNotificationPage);
                    }
                    sidebarController.selectButton("btnNotification");
                }
                default -> {
                    logger.error("Unknown buttonId: {}", buttonId);
                    Util.showErrorDialog("Unbekannter Button: " + buttonId);
                }
            }
        } catch (Exception e) {
            logger.error("Fehler bei der Navigation: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler bei der Navigation: " + e.getMessage());
        }
    }

    /**
     * Sets the content of the content area to the specified page.
     * Ensures the page is visible and added to the content area if not already present.
     *
     * @param kommentarPage The page to be displayed.
     */
    public void setContent(Parent kommentarPage) {
        if (kommentarPage == null) {
            logger.error("[ViewManager] Tried to set null content.");
            Util.showErrorDialog("Fehler: Es wurde versucht, einen leeren Inhalt zu setzen.");
            return;
        }
        contentArea.getChildren().forEach(n -> n.setVisible(false));

        if (!contentArea.getChildren().contains(kommentarPage)) {
            contentArea.getChildren().add(kommentarPage);
        }
        kommentarPage.setVisible(true);
    }
}