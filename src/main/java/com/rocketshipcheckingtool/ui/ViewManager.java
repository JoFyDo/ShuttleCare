package com.rocketshipcheckingtool.ui;

import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ViewManager extends Application {
    private ClientRequests clientRequests;
    private final String user = "technician";
    private final static Logger logger = LoggerFactory.getLogger(ViewManagerController.class);

    @Override
    public void start(Stage stage) throws Exception {
        try {
            connectToServer();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/technician/MainView.fxml"));
            Parent root = loader.load();

            ViewManagerController controller = loader.getController();
            controller.setClientRequests(clientRequests);
            controller.initContent();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/rocketshipcheckingtool/ui/style.css")).toExternalForm());
            stage.setTitle("Rocketship Checking Tool");
            Image i = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/rocketshipcheckingtool/ui/graphics/icon.png")));
            stage.getIcons().add(i);
            stage.setScene(scene);
            stage.show();

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

    public static void main(String[] args) {
        launch(args);
    }
}
