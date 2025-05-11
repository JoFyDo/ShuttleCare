package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.SidebarControllerInterface;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TechnicianSidebarController implements SidebarControllerInterface {
    public ToggleGroup toggleGroup;
    private ViewManagerController mainController;
    private static final Logger logger = LoggerFactory.getLogger(TechnicianSidebarController.class);

    @FXML
    private ToggleButton btnHome;
    @FXML private ToggleButton btnDetails;
    @FXML private ToggleButton btnStats;
    @FXML private ToggleButton btnStorage;
    @FXML private ToggleButton btnNotification;
    @FXML private ToggleButton btnLogout;

    @Override
    public void setMainController(ViewManagerController controller) {
        this.mainController = controller;
        logger.debug("Main controller set in TechnicianSidebarController");
    }

    @FXML
    private void handleSidebarClick(ActionEvent event) {
        ToggleButton source = (ToggleButton) event.getSource();
        if (mainController != null && source != null) {
            logger.info("Sidebar navigation: {}", source.getId());
            mainController.handleSidebarNavigation(source.getId());
        } else {
            logger.warn("Sidebar click event with null mainController or source");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        logger.info("Logout button clicked");
        if (mainController != null) {
            mainController.handleLogout(event);
        } else {
            logger.warn("Logout attempted with null mainController");
        }
    }

    @Override
    public void selectButton(String buttonId) {
        logger.debug("Selecting sidebar button: {}", buttonId);
        switch (buttonId) {
            case "btnHome" -> btnHome.setSelected(true);
            case "btnDetails" -> btnDetails.setSelected(true);
            case "btnStatistiken" -> btnStats.setSelected(true);
            case "btnLager" -> btnStorage.setSelected(true);
            case "btnNachrichten" -> btnNotification.setSelected(true);
        }
    }
}
