package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.datamodel.Comment;
import com.rocketshipcheckingtool.ui.helper.NotificationUtil;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.helper.Util;
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

/**
 * Controller class for managing the forward popup view in the manager role.
 * Handles the forwarding of comments to a specific department with optional additional comments.
 */
public class ForwardPopupViewController {
    public Label shuttleName; // Label displaying the name of the shuttle.
    public TextArea commentArea; // TextArea for displaying and editing comments.
    public TextField ownCommandField; // TextField for entering an additional comment.
    public ComboBox<String> departmentComboBox; // ComboBox for selecting the department to forward comments to.
    public Button weiterleitenButton; // Button for triggering the forwarding action.

    private ClientRequests clientRequests; // ClientRequests instance for server communication.
    private ArrayList<Comment> comments; // List of comments to be forwarded.
    private final String user = UserSession.getRole().name().toLowerCase(); // The current user's role.
    private boolean successfull = false; // Indicates whether the forwarding was successful.
    private Stage stage; // The stage for the popup window.

    private static final Logger logger = LoggerFactory.getLogger(ForwardPopupViewController.class); // Logger instance.

    /**
     * Initializes the controller and sets up the UI components.
     */
    @FXML
    public void initialize() {
        ownCommandField.setPromptText("Additional comment");
        logger.debug("ForwardPopupViewController initialized");
    }

    /**
     * Handles the action of the forward button click.
     * Validates input, confirms the action if no additional comment is provided, and forwards the comments.
     *
     * @param actionEvent The ActionEvent triggered by the button click.
     * @throws IOException If an error occurs during the forwarding process.
     */
    public void onWeiterleitenButtonClick(ActionEvent actionEvent) throws IOException {
        String comment = commentArea.getText();
        String ownCommand = ownCommandField.getText();
        String department = departmentComboBox.getValue();

        if (comment.isEmpty() || department == null) {
            logger.warn("Forwarding failed: department not selected or comment empty");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Abteilung muss zugewiesen werden");
            alert.showAndWait();
            return;
        }
        AtomicBoolean send = new AtomicBoolean(true);

        if (ownCommand.isEmpty()) {
            logger.info("No additional comment provided, asking for confirmation");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ohne ergänzenden Kommentar fortfahren?");
            alert.setHeaderText("Es wird kein eigener Kommentar gesendet");
            alert.setContentText("Möchten Sie trotzdem fortfahren?");
            alert.showAndWait().ifPresent(response -> {
                if (response != ButtonType.OK) {
                    send.set(false);
                }
            });
        }
        if (send.get()) {
            int forwarded = 0;
            for (Comment comment1 : comments) {
                try {
                    if (department.equals("Technik")) {
                        successfull = true;
                        NotificationUtil.createNotification(clientRequests, user, comment1.getShuttleId(), comment1.getComment(), user, ownCommand);
                        forwarded++;
                        logger.info("Forwarded comment with ID {} to department '{}'", comment1.getId(), department);
                    }
                } catch (Exception e) {
                    logger.error("Error forwarding the Comment with the ID {}: {}", comment1.getId(), e.getMessage(), e);
                    Util.showErrorDialog("Fehler beim Weiterleiten des Kommentars: " + e.getMessage());
                }
            }
            logger.info("Total comments forwarded: {}", forwarded);
            stage.close();
        }
    }

    /**
     * Sets the stage for the popup window.
     *
     * @param popupStage The Stage to set.
     */
    public void setStage(Stage popupStage) {
        this.stage = popupStage;
    }

    /**
     * Sets the list of comments to be forwarded and displays them in the comment area.
     *
     * @param comments The list of comments to set.
     */
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
        StringBuilder sb = new StringBuilder();
        for (Comment comment : comments) {
            sb.append(comment.getComment()).append("\n");
        }
        commentArea.setText(sb.toString());
        logger.debug("Comments set in ForwardPopupViewController: {} comment(s)", comments.size());
    }

    /**
     * Sets the ClientRequests instance for server communication.
     *
     * @param clientRequests The ClientRequests instance to set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        logger.debug("ClientRequests set in ForwardPopupViewController");
    }

    /**
     * Checks whether the forwarding process was successful.
     *
     * @return True if the forwarding was successful, false otherwise.
     */
    public boolean isSuccessfull() {
        return successfull;
    }
}