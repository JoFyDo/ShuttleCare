package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.roles.masterController.HomeViewControllerMaster;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import javafx.fxml.FXML;

public class HomeViewController extends HomeViewControllerMaster {
    private final String user = UserSession.getRole().name().toLowerCase();

    @FXML
    public void initialize() {
        setupTableColumns();
        System.out.println(user);
    }

    @Override
    public void load() {

    }
}
