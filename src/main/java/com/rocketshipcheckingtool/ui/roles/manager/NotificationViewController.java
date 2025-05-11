package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.roles.masterController.NotificationViewControllerMaster;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationViewController extends NotificationViewControllerMaster {

    private static final Logger logger = LoggerFactory.getLogger(NotificationViewController.class);

    @FXML
    public void initialize() {
        super.initialize();
        logger.info("Manager NotificationViewController initialized");
    }

    @Override
    protected void load() {
        shuttleColumn.setVisible(false);
    }

    @Override
    protected void setupTableErstellenContent() {

    }

}
