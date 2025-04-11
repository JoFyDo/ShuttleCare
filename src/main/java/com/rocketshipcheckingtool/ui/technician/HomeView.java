package com.rocketshipcheckingtool.ui.technician;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import com.rocketshipcheckingtool.server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Objects;

public class HomeView extends Application {

    private ClientRequests clientRequests;
    private final String user = "technician";
    private final static Logger logger = LoggerFactory.getLogger(HomeView.class);

    @FXML
    private Line tableHeaderLine;
    private Line tableHeaderLineTasks;
    private Rectangle shuttleOverviewRectangle;
    private Rectangle shuttleTasksOverviewRectangle;
    private HBox shuttleOverviewHeaderHBox;
    private VBox shuttleOverviewEntrysVBox;
    private HBox shuttleTasksHeaderHBox;
    private VBox shuttleTasksEntrysVBox;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            connectToServer();
            Parent root = FXMLLoader.load(getClass().getResource("HomeView.fxml"));
            Scene home = fitContent(root);
            loadContent();

            for (Node n : shuttleOverviewEntrysVBox.getChildren()){
                VBox vbox = (VBox) n;
                for (Node f: vbox.getChildren()){
                    if (f instanceof HBox hbox){
                        for (Node n2 : hbox.getChildren()){
                                System.out.println(HBox.getMargin(n2));
                            }
                    }
                }
            }

            Platform.runLater(() -> {
                fitVBoxs(shuttleOverviewEntrysVBox, shuttleOverviewHeaderHBox, shuttleOverviewRectangle);
                fitVBoxs(shuttleTasksEntrysVBox, shuttleTasksHeaderHBox, shuttleTasksOverviewRectangle);
            });

            stage.setTitle("Rocketship Checking Tool");
            Image i = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png")));
            stage.getIcons().add(i);
            stage.setScene(home);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Scene fitContent(Parent root){
        Scene scene = new Scene(root);

        shuttleOverviewHeaderHBox = (HBox) scene.lookup("#shuttleOverviewHeaderHBox");
        shuttleOverviewEntrysVBox = (VBox) scene.lookup("#shuttleOverviewEntrysVBox");

        shuttleTasksHeaderHBox = (HBox) scene.lookup("#shuttleTasksHeaderHBox");
        shuttleTasksEntrysVBox = (VBox) scene.lookup("#shuttleTasksEntrysVBox");

        shuttleOverviewRectangle = (Rectangle) root.lookup("#shuttleOverviewRectangle");
        shuttleTasksOverviewRectangle = (Rectangle) root.lookup("#shuttleTasksOverviewRectangle");

        tableHeaderLine = (Line) scene.lookup("#tableHeaderLine");
        tableHeaderLine.setEndX((shuttleOverviewRectangle.getWidth() - 34));

        tableHeaderLineTasks = (Line) scene.lookup("#tableHeaderLineTasks");
        tableHeaderLineTasks.setEndX((shuttleTasksOverviewRectangle.getWidth() - 34));

        return scene;
    }

    private void loadContent(){
        try {
            ArrayList<Shuttle> shuttles = getShuttleEntrysForOverview();
            Util.shuttleEntryLoadVBoxes(shuttles, shuttleOverviewEntrysVBox);

            ArrayList<Task> tasks = getShutlleEntrysForTasks();
            Util.shuttleEntryLoadVBoxes(tasks, shuttleTasksEntrysVBox);

        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    private void fitVBoxs(VBox entrysVBox, HBox headerHBox, Rectangle rectangle){
        for (Node node : entrysVBox.getChildren()) {
            VBox vbox = (VBox) node;
            for (Node n : vbox.getChildren()) {
                if (n.getClass() == HBox.class) {
                    Util.calculateEntryHBoxMargin(headerHBox, (HBox) n);
                } else if (n.getClass() == Line.class) {
                    ((Line) n).setEndX(rectangle.getWidth() - 34);
                }
            }
        }
    }


    private ArrayList<Shuttle> getShuttleEntrysForOverview() throws IOException {
        try {
            String shuttles = clientRequests.request("/requestShuttleOverview", user);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (jsonElement, type, context) ->
                            Date.valueOf(jsonElement.getAsString()))
                    .registerTypeAdapter(Time.class, (JsonDeserializer<Time>) (jsonElement, type, context) ->
                            Time.valueOf(jsonElement.getAsString()))
                    .create();
            Type shuttleListType = new TypeToken<ArrayList<Shuttle>>() {}.getType();
            return gson.fromJson(shuttles, shuttleListType);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    private ArrayList<Task> getShutlleEntrysForTasks() throws IOException {
        try {
            String tasks = clientRequests.request("/requestShuttleTasks", user);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            System.out.println(tasks);
            return gson.fromJson(tasks, shuttleListType);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
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
