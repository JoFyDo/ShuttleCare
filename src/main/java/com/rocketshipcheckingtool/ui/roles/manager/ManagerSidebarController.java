package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.SidebarControllerInterface;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerSidebarController implements SidebarControllerInterface {
    public ToggleButton btnHome;
    public ToggleGroup toggleGroup;
    public ToggleButton btnDetails;
    public ToggleButton btnNotification;
    public Pane spacer;
    public ToggleButton btnLogout;
    public VBox managerSidebar;
    private ViewManagerController mainController;
    private static final Logger logger = LoggerFactory.getLogger(ManagerSidebarController.class);

    @Override
    public void setMainController(ViewManagerController controller) {
        this.mainController = controller;
        logger.debug("Main controller set in ManagerSidebarController");
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
            case "btnNotification" -> btnNotification.setSelected(true);
        }
    }
}
