package com.rocketshipcheckingtool.ui.technician;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;

public class HomeView extends Scene{

    private final double minMargin = 20;

    private final String user = "technician";
    private final static Logger logger = LoggerFactory.getLogger(HomeView.class);
    private ClientRequests clientRequests;

    private double shuttleOverviewMinWidth;
    private double shuttleOverviewMinHeight;

    @FXML
    private Line tableHeaderLine;
    private Line tableHeaderLineTasks;
    private Rectangle shuttleOverviewRectangle;
    private Rectangle shuttleTasksOverviewRectangle;
    private HBox shuttleOverviewHeaderHBox;
    private VBox shuttleOverviewEntrysVBox;
    private HBox shuttleTasksHeaderHBox;
    private VBox shuttleTasksEntrysVBox;
    private StackPane shuttleOverviewStackPane;

    public HomeView(Parent parent, ClientRequests clientRequests) {
        super(parent);
        this.clientRequests = clientRequests;
        initialize();
    }

    public void initialize() {
        try {
            this.widthProperty().addListener((observable, oldValue, newValue) -> resize());
            this.heightProperty().addListener((observable, oldValue, newValue) -> resize());

            lookupContent();
            loadContent();




            Platform.runLater(() -> {
                resize();
                fitVBoxs(shuttleOverviewEntrysVBox, shuttleOverviewHeaderHBox, shuttleOverviewRectangle);
                fitVBoxs(shuttleTasksEntrysVBox, shuttleTasksHeaderHBox, shuttleTasksOverviewRectangle);
                tableHeaderLine.setEndX((shuttleOverviewRectangle.getWidth() - 34));
                tableHeaderLineTasks.setEndX((shuttleTasksOverviewRectangle.getWidth() - 34));
            });

        }catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private void lookupContent(){

        shuttleOverviewHeaderHBox = (HBox) lookup("#shuttleOverviewHeaderHBox");
        shuttleOverviewEntrysVBox = (VBox) lookup("#shuttleOverviewEntrysVBox");
        shuttleTasksHeaderHBox = (HBox) lookup("#shuttleTasksHeaderHBox");
        shuttleTasksEntrysVBox = (VBox) lookup("#shuttleTasksEntrysVBox");
        shuttleOverviewRectangle = (Rectangle) lookup("#shuttleOverviewRectangle");
        shuttleTasksOverviewRectangle = (Rectangle) lookup("#shuttleTasksOverviewRectangle");
        tableHeaderLine = (Line) lookup("#tableHeaderLine");
        tableHeaderLineTasks = (Line) lookup("#tableHeaderLineTasks");
        shuttleOverviewStackPane = (StackPane) lookup("#shuttleOverviewStackPane");
    }

    private void loadContent(){
        try {
            ArrayList<Shuttle> shuttles = Util.getShuttles(clientRequests, user);
            Util.shuttleEntryLoadVBoxes(shuttles, shuttleOverviewEntrysVBox);

            ArrayList<Task> tasks = Util.getActiveTasks(clientRequests, user);
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

    private void resize(){
        double newWidth = getWidth() - 140;
        double newHeight = getHeight()/2 - 120;
        //System.out.println(getWidth());
        SizeUtil.resizeStackPane(shuttleOverviewStackPane, newWidth, newHeight);

    }

}
