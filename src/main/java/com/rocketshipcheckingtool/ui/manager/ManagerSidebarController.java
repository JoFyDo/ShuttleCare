package com.rocketshipcheckingtool.ui.manager;

import com.rocketshipcheckingtool.domain.SidebarControllerInterface;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ManagerSidebarController implements SidebarControllerInterface {
    public ToggleButton btnHome;
    public ToggleGroup toggleGroup;
    public ToggleButton btnDetails;
    public Pane spacer;
    public ToggleButton btnLogout;
    public VBox managerSidebar;
    private ViewManagerController mainController;

    @Override
    public void setMainController(ViewManagerController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleSidebarClick(ActionEvent event) {
        ToggleButton source = (ToggleButton) event.getSource();
        if (mainController != null && source != null) {
            mainController.handleSidebarNavigation(source.getId());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        if (mainController != null) {
            mainController.handleLogout(event);
        }
    }
}
