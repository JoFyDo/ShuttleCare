package com.rocketshipcheckingtool.ui.auth;

import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller class for the login view.
 * Handles user authentication and navigation to the main application view.
 */
public class LoginViewController {

    public VBox focusTrap; // A VBox element used to trap focus on the login screen.
    @FXML
    private TextField usernameField; // TextField for entering the username.
    @FXML
    private PasswordField passwordField; // PasswordField for entering the password.

    private ClientRequests clientRequests; // ClientRequests instance for handling server communication.

    private static final Logger logger = LoggerFactory.getLogger(LoginViewController.class); // Logger instance.

    /**
     * Handles the login process when the login button is clicked.
     * Authenticates the user and navigates to the main view if successful.
     */
    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim(); // Retrieve the entered username.
        String pass = passwordField.getText().trim(); // Retrieve the entered password.

        logger.info("Login attempt for user '{}'", user);

        // Check credentials and set the user role.
        if (user.equals("technician") && pass.equals("technician")) {
            UserSession.setRole(UserRole.TECHNICIAN);
            logger.info("Technician login successful");
        } else if (user.equals("manager") && pass.equals("manager")) {
            UserSession.setRole(UserRole.MANAGER);
            logger.info("Manager login successful");
        } else {
            logger.warn("Login failed for user '{}': Invalid credentials", user);
            new Alert(Alert.AlertType.ERROR, "Invalid credentials").show();
            return;
        }

        try {
            // Load the main view after successful login.
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

            // Set the stage to fullscreen and display it.
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            newStage.setX(bounds.getMinX());
            newStage.setY(bounds.getMinY());
            newStage.setWidth(bounds.getWidth());
            newStage.setHeight(bounds.getHeight());
            newStage.setScene(scene);
            newStage.show();
            newStage.setOnCloseRequest(e -> {
                logger.info("Application closed by user");
                System.exit(1);
            });

            // Close the login window.
            Stage oldStage = (Stage) usernameField.getScene().getWindow();
            oldStage.close();
            logger.debug("Main view loaded and login window closed");
        } catch (IOException e) {
            logger.error("Failed to load main view after login: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load main view", e);
        }
    }

    /**
     * Sets the ClientRequests instance for server communication.
     *
     * @param clientRequests The ClientRequests instance to set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        logger.debug("ClientRequests set in LoginViewController");
    }

    /**
     * Initializes the login view.
     * Sets up focus trapping and keyboard shortcuts for quick login.
     */
    public void initialize() {
        Platform.runLater(() -> {
            focusTrap.requestFocus(); // Set initial focus to the focus trap.

            Scene scene = focusTrap.getScene();
            if (scene != null) {
                // Add keyboard shortcuts for quick login.
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    switch (event.getCode()) {
                        case DIGIT1 -> {
                            logger.debug("Shortcut: technician login triggered");
                            usernameField.setText("technician");
                            passwordField.setText("technician");
                            handleLogin();
                            event.consume();
                        }
                        case DIGIT2 -> {
                            logger.debug("Shortcut: manager login triggered");
                            usernameField.setText("manager");
                            passwordField.setText("manager");
                            handleLogin();
                            event.consume();
                        }
                    }
                });
            }
            logger.info("LoginViewController initialized");
        });
    }
}