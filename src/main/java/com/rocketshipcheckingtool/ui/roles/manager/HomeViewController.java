package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.roles.masterController.HomeViewControllerMaster;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeViewController extends HomeViewControllerMaster {
    private final String user = UserSession.getRole().name().toLowerCase();

    private static final Logger logger = LoggerFactory.getLogger(HomeViewController.class);

    @FXML
    public void initialize() {
        setupTableColumns();
        logger.info("Manager HomeViewController initialized for user '{}'", user);
    }

    @Override
    public void load() {

    }
}

