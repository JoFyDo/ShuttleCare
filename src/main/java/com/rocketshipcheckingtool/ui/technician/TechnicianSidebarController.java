package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.SidebarControllerInterface;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

public class TechnicianSidebarController implements SidebarControllerInterface {
    private ViewManagerController mainController;

    @FXML
    private ToggleButton btnHome;
    @FXML private ToggleButton btnDetails;
    @FXML private ToggleButton btnStatistiken;
    @FXML private ToggleButton btnLager;
    @FXML private ToggleButton btnNachrichten;
    @FXML private ToggleButton btnLogout;

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
