package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import com.rocketshipcheckingtool.ui.HomeViewControllerMaster;
import com.rocketshipcheckingtool.ui.Util;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class HomeViewController extends HomeViewControllerMaster {
    @FXML
    private VBox shuttleProgressContainer;
    @FXML
    private Label noTasksLabel;
    @FXML
    private TableView<Task> aufgabenTableView;
    @FXML
    private TableColumn<Task, String> aufgabeTaskColumn;
    @FXML
    private TableColumn<Task, String> shuttleTaskColumn;
    @FXML
    private TableColumn<Task, String> mechanikerTaskColumn;
    @FXML
    private TableColumn<Task, String> statusTaskColumn;

    private final static Logger logger = LoggerFactory.getLogger(HomeViewController.class);

    @FXML
    public void initialize() {
        setupTableColumns();

    }

    public void setupTableColumns() {
        super.setupTableColumns();

        aufgabeTaskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        shuttleTaskColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        mechanikerTaskColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));
        statusTaskColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus() ? "Erledigt" : "in Bearbeitung"));
        aufgabenTableView.setSelectionModel(null);
        aufgabenTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    }

    public void loadTaskTableContent() {
        try {
            ArrayList<Task> tasks = Util.getActiveTasks(clientRequests, user);
            aufgabenTableView.setItems(FXCollections.observableArrayList(tasks));
        } catch (Exception e) {
            logger.error("Error loading tasks: ", e);
        }
    }

    public void loadProgressBar() {
        // Ensure UI operations run on JavaFX application thread
        javafx.application.Platform.runLater(() -> {
            try {
                // Clear existing content
                shuttleProgressContainer.getChildren().clear();

                // Sample shuttle data with progress percentages (0.0 to 1.0)
                // In production, this should come from your data source
                Map<String, Double> shuttleProgress = getShuttleProgressData();

                if (shuttleProgress.isEmpty()) {
                    noTasksLabel.setVisible(true);
                    return;
                }

                noTasksLabel.setVisible(false);

                // Create a progress bar for each shuttle
                for (Map.Entry<String, Double> entry : shuttleProgress.entrySet()) {
                    String shuttleName = entry.getKey();
                    Double progress = entry.getValue();

                    Label shuttleLabel = new Label(shuttleName);
                    shuttleLabel.getStyleClass().add("shuttle-name");
                    shuttleLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 0");

                    ProgressBar progressBar = new ProgressBar(progress);
                    progressBar.setPrefWidth(Double.MAX_VALUE);
                    progressBar.setMinHeight(20); // Ensure visible height
                    progressBar.getStyleClass().add("progressBar");

                    // More visible percentage label
                    Label percentLabel = new Label(String.format("%.0f%%  complete", progress * 100));
                    percentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px");

                    if (progress == 0.0) {
                        percentLabel.setText("Noch nicht gestartet");
                        percentLabel.setStyle("-fx-text-fill: gray;");
                    }

//                    VBox shuttleBox = new VBox(5);
//                    shuttleBox.getChildren().addAll(shuttleLabel, progressBar);
//                    shuttleBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

                    StackPane progressStack = new StackPane();
                    progressStack.getChildren().addAll(progressBar, percentLabel);
                    progressStack.setPrefWidth(Double.MAX_VALUE);
                    StackPane.setAlignment(percentLabel, Pos.CENTER_LEFT);
                    StackPane.setMargin(percentLabel, new Insets(0, 0, 0, 5));

                    VBox shuttleBox = new VBox(5);
                    shuttleBox.getChildren().addAll(shuttleLabel, progressStack);

                    shuttleProgressContainer.getChildren().add(shuttleBox);
                }
            } catch (Exception e) {
                logger.error("Error loading progress bars: ", e);
                // Display error in UI for better debugging
                Label errorLabel = new Label("Error loading progress data: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red;");
                shuttleProgressContainer.getChildren().add(errorLabel);
            }
        });
    }

    private Map<String, Double> getShuttleProgressData() throws IOException {
        ArrayList<Shuttle> shuttles = Util.getShuttles(clientRequests, user);
        Map<String, Double> progressMap = new java.util.HashMap<>();

        for (Shuttle shuttle : shuttles) {
            ArrayList<Task> generalTasks = Util.getGeneralTasksByShuttleID(clientRequests, user, shuttle.getId());
            ArrayList<Task> tasks = Util.getActiveTasksByShuttleID(clientRequests, user, shuttle.getId());

            int completedTasks = 0;
            int totalTasks = tasks.size() + generalTasks.size();

            // Count completed tasks
            for (Task task : tasks) {
                if (task.getStatus()) {
                    completedTasks++;
                }
            }
            for (Task task : generalTasks) {
                if (task.getStatus()) {
                    completedTasks++;
                }
            }

            // Avoid division by zero
            double progress = (totalTasks > 0) ? (double) completedTasks / totalTasks : 0.0;
            progressMap.put(shuttle.getShuttleName(), progress);

            logger.info("Shuttle: {} - Completed: {}/{} tasks, Progress: {:.2f}%",
                    shuttle.getShuttleName(), completedTasks, totalTasks, progress * 100);

        }

        return progressMap;
    }


    public void onBoxClicked(MouseEvent mouseEvent) throws IOException {
        super.viewManagerController.handleDetailButton(null);
    }

//    public void load(){
//        loadTaskTableContent();
//        loadProgressBar();
//    }

    public void load() {
        loadTaskTableContent();
        loadProgressBar();
    }
}
