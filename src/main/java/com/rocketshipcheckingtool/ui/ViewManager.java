package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.ui.auth.LoginViewController;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Main application class for managing the view transitions and startup logic.
 * Handles the initialization of the application, connection to the server, and navigation between views.
 */
public class ViewManager extends Application {
    private static ClientRequests clientRequests; // Instance for handling client requests.
    private final static Logger logger = LoggerFactory.getLogger(ViewManagerController.class); // Logger for logging application events.

    /**
     * Entry point for the JavaFX application.
     * Initializes the application and displays the startup screen.
     *
     * @param stage The primary stage for the application.
     * @throws Exception If an error occurs during startup.
     */
    @Override
    public void start(Stage stage) throws Exception {
        connectToServer();
        try {
            showStartupScreen(stage);
        } catch (Exception e) {
            logger.error("Error starting MainView", e);
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection to the server by initializing the ClientRequests instance.
     * Logs any errors that occur during the connection process.
     */
    private void connectToServer() {
        try {
            clientRequests = new ClientRequests();
        } catch (Exception e) {
            logger.error("Error connecting to server", e);
            e.printStackTrace();
        }
    }

    /**
     * Displays the startup screen with a video.
     * After the video ends, transitions to the login view.
     *
     * @param mainStage The primary stage for the application.
     */
    public static void showStartupScreen(Stage mainStage) {
        try {
            // Create a new stage for the video
            Stage videoStage = new Stage();
            videoStage.setTitle("Welcome to ShuttleCare");

            // Load the video file
            String videoPath = Objects.requireNonNull(ViewManager.class.getResource("/com/rocketshipcheckingtool/ui/graphics/StartupScreen.mp4")).toExternalForm();
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            // Set up the container and scene
            StackPane root = new StackPane(mediaView);
            Scene scene = new Scene(root, 800, 600);

            // Configure the media view to resize with the stage
            mediaView.fitWidthProperty().bind(root.widthProperty());
            mediaView.fitHeightProperty().bind(root.heightProperty());

            // Set the scene and show the video stage
            videoStage.setScene(scene);
            videoStage.show();

            // When video ends, close the video stage and show login
            mediaPlayer.setOnEndOfMedia(() -> {
                videoStage.close();
                try {
                    showLoginView(mainStage);
                } catch (Exception e) {
                    logger.error("Error showing login screen after video: {}", e.getMessage(), e);
                }
            });

            // Start playing the video
            mediaPlayer.play();

        } catch (Exception e) {
            logger.error("Error playing startup video: {}", e.getMessage(), e);
            try {
                showLoginView(mainStage);
            } catch (Exception ex) {
                logger.error("Error showing login screen after video failure: {}", ex.getMessage(), ex);
            }
        }
    }

    /**
     * Displays the login view.
     * Configures the stage with the login scene and sets up the application icon.
     *
     * @param stage The primary stage for the application.
     * @throws Exception If an error occurs while loading the login view.
     */
    public static void showLoginView(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("/com/rocketshipcheckingtool/ui/LoginView.fxml"));
        Parent root = loader.load();

        LoginViewController controller = loader.getController();
        controller.setClientRequests(clientRequests);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(ViewManager.class.getResource("/com/rocketshipcheckingtool/ui/style.css")).toExternalForm());
        stage.setTitle("ShuttleCare");
        Image i = new Image(Objects.requireNonNull(ViewManager.class.getResourceAsStream("/com/rocketshipcheckingtool/ui/graphics/icon.png")));

        stage.getIcons().add(i);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> System.exit(1));
    }

    /**
     * Main method for launching the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}