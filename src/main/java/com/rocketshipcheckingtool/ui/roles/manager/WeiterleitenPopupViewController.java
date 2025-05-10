package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.datamodel.Comment;
import com.rocketshipcheckingtool.ui.helper.NotificationUtil;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class WeiterleitenPopupViewController {
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

    @FXML
    public void initialize() {
        ownCommandField.setPromptText("Ergänzender Kommentar");
    }

    public void onWeiterleitenButtonClick(ActionEvent actionEvent) throws IOException {
        String comment = commentArea.getText();
        String ownCommand = ownCommandField.getText();
        String department = departmentComboBox.getValue();

        if (comment.isEmpty() || department == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Abteilung muss zugewiesen werden");
            alert.showAndWait();
            return;
        }
        AtomicBoolean send = new AtomicBoolean(true);

        if (ownCommand.isEmpty()) {
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
            for (Comment comment1 : comments) {
                if (department.equals("Technik")) {
                    successfull = true;
                    NotificationUtil.createNotification(clientRequests, user, comment1.getShuttleId(), comment1.getComment(), user, ownCommand);
                }
            }
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
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }

    public boolean isSuccessfull() {
        return successfull;
    }
}

