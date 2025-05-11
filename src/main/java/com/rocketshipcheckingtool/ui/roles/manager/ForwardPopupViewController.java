package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.datamodel.Comment;
import com.rocketshipcheckingtool.ui.helper.NotificationUtil;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ForwardPopupViewController {
    public Label shuttleName;
    public TextArea commentArea;
    public TextField ownCommandField;
    public ComboBox<String> departmentComboBox;
    public Button weiterleitenButton;

    private ClientRequests clientRequests;
    private ArrayList<Comment> comments;
    private final String user = UserSession.getRole().name().toLowerCase();
    private boolean successfull = false;
    private Stage stage;

    private static final Logger logger = LoggerFactory.getLogger(ForwardPopupViewController.class);

    @FXML
    public void initialize() {
        ownCommandField.setPromptText("Additional comment");
        logger.debug("ForwardPopupViewController initialized");
    }

    public void onWeiterleitenButtonClick(ActionEvent actionEvent) throws IOException {
        String comment = commentArea.getText();
        String ownCommand = ownCommandField.getText();
        String department = departmentComboBox.getValue();

        if (comment.isEmpty() || department == null) {
            logger.warn("Forwarding failed: department not selected or comment empty");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Department must be assigned");
            alert.showAndWait();
            return;
        }
        AtomicBoolean send = new AtomicBoolean(true);

        if (ownCommand.isEmpty()) {
            logger.info("No additional comment provided, asking for confirmation");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Proceed without additional comment?");
            alert.setHeaderText("No additional comment will be sent");
            alert.setContentText("Do you want to continue?");
            alert.showAndWait().ifPresent(response -> {
                if (response != ButtonType.OK) {
                    send.set(false);
                }
            });
        }
        if (send.get()) {
            int forwarded = 0;
            for (Comment comment1 : comments) {
                if (department.equals("Technik")) {
                    successfull = true;
                    NotificationUtil.createNotification(clientRequests, user, comment1.getShuttleId(), comment1.getComment(), user, ownCommand);
                    forwarded++;
                    logger.info("Forwarded comment with ID {} to department '{}'", comment1.getId(), department);
                }
            }
            logger.info("Total comments forwarded: {}", forwarded);
            stage.close();
        }
    }

    public void setStage(Stage popupStage) {
        this.stage = popupStage;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
        StringBuilder sb = new StringBuilder();
        for (Comment comment : comments) {
            sb.append(comment.getComment()).append("\n");
        }
        commentArea.setText(sb.toString());
        logger.debug("Comments set in ForwardPopupViewController: {} comment(s)", comments.size());
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        logger.debug("ClientRequests set in ForwardPopupViewController");
    }

    public boolean isSuccessfull() {
        return successfull;
    }
}
