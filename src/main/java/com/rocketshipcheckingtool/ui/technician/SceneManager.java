package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Shuttle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class SceneManager extends Application {

    private ClientRequests clientRequests;

    @Override
    public void start(Stage stage) throws Exception {
        connectToServer();

        Parent homeViewFxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("HomeView.fxml")));
        Parent detailsViewFxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("DetailsView.fxml")));

        HomeView homeView = new HomeView(homeViewFxml, clientRequests);
        DetailsView detailsView = new DetailsView(detailsViewFxml, Util.getShuttle(clientRequests, "technician", 3));


        stage.setTitle("Rocketship Checking Tool");
        Image i = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png")));
        stage.getIcons().add(i);

        if (clientRequests != null) {
            stage.setScene(homeView);
            //stage.setScene(detailsView);

            stage.setMinHeight(600);
            stage.setMinWidth(1000);
            stage.setMaximized(true);

            stage.show();
        }

    }

    private void connectToServer() {
        try {
            clientRequests = new ClientRequests();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
