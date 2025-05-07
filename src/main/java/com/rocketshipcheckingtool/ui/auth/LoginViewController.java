package com.rocketshipcheckingtool.ui.auth;

import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginViewController {

    public VBox focusTrap;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private ClientRequests clientRequests;


    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.equals("technician") && pass.equals("technician")) {
            UserSession.setRole(UserRole.TECHNICIAN);
        } else if (user.equals("manager") && pass.equals("manager")) {
            UserSession.setRole(UserRole.MANAGER);
        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid credentials").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/MainView.fxml"));
            Parent mainRoot = loader.load();

            ViewManagerController controller = loader.getController();
            controller.setClientRequests(clientRequests);
            controller.initAfterLogin();

            Stage newStage = new Stage();
            newStage.setTitle("ShuttleCare");
            newStage.getIcons().add(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/com/rocketshipcheckingtool/ui/graphics/icon.png"))));

            Scene scene = new Scene(mainRoot);
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/com/rocketshipcheckingtool/ui/style.css")).toExternalForm());

            newStage.setScene(scene);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            newStage.setX(bounds.getMinX());
            newStage.setY(bounds.getMinY());
            newStage.setWidth(bounds.getWidth());
            newStage.setHeight(bounds.getHeight());
            newStage.show();
            newStage.setOnCloseRequest(e -> {System.exit(1);});

            Stage oldStage = (Stage) usernameField.getScene().getWindow();
            oldStage.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load main view", e);
        }
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }

    public void initialize() {
        Platform.runLater(() -> {
            focusTrap.requestFocus();

            Scene scene = focusTrap.getScene();
            if (scene != null) {
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    switch (event.getCode()) {
                        case DIGIT1 -> {
                            usernameField.setText("technician");
                            passwordField.setText("technician");
                            handleLogin();
                            event.consume();
                        }
                        case DIGIT2 -> {
                            usernameField.setText("manager");
                            passwordField.setText("manager");
                            handleLogin();
                            event.consume();
                        }
                    }
                });
            }
        });
    }

}
