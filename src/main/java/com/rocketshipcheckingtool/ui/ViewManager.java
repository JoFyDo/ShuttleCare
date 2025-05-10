package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.ui.auth.LoginViewController;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ViewManager extends Application {
    private static ClientRequests clientRequests;
    private final static Logger logger = LoggerFactory.getLogger(ViewManagerController.class);

    @Override
    public void start(Stage stage) throws Exception {
        connectToServer();
        try {
            showLoginView(stage);
        } catch (Exception e) {
            logger.error("Error starting MainView", e);
            e.printStackTrace();
        }
    }

    private void connectToServer() {
        try {
            clientRequests = new ClientRequests();
        } catch (Exception e) {
            logger.error("Error connecting to server", e);
            e.printStackTrace();
        }
    }

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
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.show();
        stage.setOnCloseRequest(e -> {System.exit(1);});
    }

    public static void main(String[] args) {
        launch(args);
    }
}
