package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class DetailsViewController {
    public ComboBox<String> shuttleComboBox;
    public String shuttleSelected;
    public List<String> shuttleList;
    public List<Shuttle> shuttles;
    public Button gelandetButton;
    public Button inspektion1Button;
    public Button inWartungButton;
    public Button inspektion2Button;
    public Button freigegebenButton;
    public HBox progressBar;
    public VBox aufgabenBox;
    public VBox zusaetzlichAufgabenBox;

    private ClientRequests clientRequests;
    private final String user = "technician";
    private final static Logger logger = LoggerFactory.getLogger(DetailsViewController.class);


    @FXML
    public void initialize() {
        shuttleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadAufgaben(newVal);
                loadZusaetzlicheAufgaben(newVal);
                loadLoadingBar(newVal);
                //loadCrewFeedback(newVal);
                System.out.println("[Details] selected Shuttle: " + newVal);
            }
        });

        shuttleComboBox.getStyleClass().add("comboBox");
    }


    private void loadZusaetzlicheAufgaben(String shuttleName) {
        zusaetzlichAufgabenBox.getChildren().clear();

        if (shuttleName == null) return;

        Shuttle shuttle = shuttles.stream().filter(sh -> sh.getShuttleName().equals(shuttleName)).findFirst().get();
        List<Task> tasksForShuttle = null;
        try {
            tasksForShuttle = Util.getActiveTasksByShuttleID(clientRequests, user, shuttle.getId());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        for (Task task : tasksForShuttle) {
            HBox taskItem = new HBox();
            taskItem.setSpacing(10);
            taskItem.setMaxWidth(Double.MAX_VALUE);
            taskItem.setStyle("-fx-alignment: CENTER_LEFT;");

            Label taskLabel = new Label(task.getTask());
            taskLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(taskLabel, Priority.ALWAYS);

            CheckBox checkBox = new CheckBox();

            taskItem.getChildren().addAll(taskLabel, checkBox);
            zusaetzlichAufgabenBox.getChildren().add(taskItem);
        }
    }

    private void loadAufgaben(String shuttleName) {
    }

    private void loadLoadingBar(String shuttleName) {
        Shuttle shuttle = shuttles.stream().filter(sh -> sh.getShuttleName().equals(shuttleName)).findFirst().get();

        Color[] colors = switch (shuttle.getStatus()) {
            case "Gelandet" -> new Color[]{Color.GREEN, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
            case "Inspektion 1" -> new Color[]{Color.GREEN, Color.ORANGE, Color.WHITE, Color.WHITE, Color.WHITE};
            case "In Wartung" -> new Color[]{Color.GREEN, Color.GREEN, Color.ORANGE, Color.WHITE, Color.WHITE};
            case "Inspektion 2" -> new Color[]{Color.GREEN, Color.GREEN, Color.GREEN, Color.ORANGE, Color.WHITE};
            case "Erledigt - Warte auf Freigabe" ->
                    new Color[]{Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.ORANGE};
            case "Freigegeben" -> new Color[]{Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN};
            case "Unterwegs" -> new Color[]{Color.ORANGE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
            default -> null;
        };

        assert colors != null;
        Background gelandetButtonBackground = gelandetButton.getBackground();
        Background inspektion1ButtonBackground = inspektion1Button.getBackground();
        Background inWartungButtonBackground = inWartungButton.getBackground();
        Background inspektion2ButtonBackground = inspektion2Button.getBackground();
        Background freigegebenButtonBackground = freigegebenButton.getBackground();
        gelandetButton.setBackground(new Background(new BackgroundFill(colors[0], gelandetButtonBackground.getFills().get(0).getRadii(), gelandetButtonBackground.getFills().get(0).getInsets())));
        inspektion1Button.setBackground(new Background(new BackgroundFill(colors[1], inspektion1ButtonBackground.getFills().get(0).getRadii(), inspektion1ButtonBackground.getFills().get(0).getInsets())));
        inWartungButton.setBackground(new Background(new BackgroundFill(colors[2], inWartungButtonBackground.getFills().get(0).getRadii(), inWartungButtonBackground.getFills().get(0).getInsets())));
        inspektion2Button.setBackground(new Background(new BackgroundFill(colors[3], inspektion2ButtonBackground.getFills().get(0).getRadii(), inspektion2ButtonBackground.getFills().get(0).getInsets())));
        freigegebenButton.setBackground(new Background(new BackgroundFill(colors[4], freigegebenButtonBackground.getFills().get(0).getRadii(), freigegebenButtonBackground.getFills().get(0).getInsets())));


    }


    private static String getString(Color[] colors) {
        return colors[0].toString();
    }

    //no preselected shuttle
    private void loadShuttleContent() {
        loadShuttleContent(null);
    }

    //preselected shuttle
    private void loadShuttleContent(String preSelectedShuttle) {
        try {
            //Shuttle
            shuttles = Util.getShuttles(clientRequests, user);

            shuttleList = shuttles.stream()
                    .map(Shuttle::getShuttleName)
                    .toList();

            shuttleComboBox.getItems().clear();
            shuttleComboBox.getItems().addAll(shuttleList);

            shuttleSelected = shuttleComboBox.getValue();


            if (preSelectedShuttle != null && shuttleList.contains(preSelectedShuttle)) {
                shuttleComboBox.setValue(preSelectedShuttle);
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleContent();
    }

    public void selectShuttle(Shuttle shuttle) {
        loadShuttleContent(shuttle.getShuttleName());
    }

}
