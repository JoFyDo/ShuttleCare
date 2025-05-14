package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.datamodel.Task;
import com.rocketshipcheckingtool.ui.helper.GeneralTaskUtil;
import com.rocketshipcheckingtool.ui.helper.ShuttleUtil;
import com.rocketshipcheckingtool.ui.helper.TaskUtil;
import com.rocketshipcheckingtool.ui.helper.Util;
import com.rocketshipcheckingtool.ui.roles.masterController.HomeViewControllerMaster;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
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

/**
 * Controller class for managing the home view in the technician role.
 * Extends the HomeViewControllerMaster to provide specific functionality for technicians.
 */
public class HomeViewController extends HomeViewControllerMaster {
    @FXML
    private VBox shuttleProgressContainer; // Container for displaying shuttle progress bars.
    @FXML
    private Label noTasksLabel; // Label displayed when no tasks are available.
    @FXML
    private TableView<Task> taskTableView; // TableView for displaying tasks.
    @FXML
    private TableColumn<Task, String> taskTaskColumn; // Column for task descriptions.
    @FXML
    private TableColumn<Task, String> shuttleTaskColumn; // Column for shuttle names.
    @FXML
    private TableColumn<Task, String> mechanicTaskColumn; // Column for mechanic names.
    @FXML
    private TableColumn<Task, String> statusTaskColumn; // Column for task statuses.

    private final static Logger logger = LoggerFactory.getLogger(HomeViewController.class); // Logger instance for logging activities.

    /**
     * Initializes the controller and sets up the table columns.
     * Logs the initialization process.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing HomeViewController");
        setupTableColumns();
        logger.debug("Table columns set up");
    }

    /**
     * Configures the columns of the task table.
     * Sets cell value factories and adjusts column properties.
     */
    public void setupTableColumns() {
        super.setupTableColumns();

        taskTaskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        shuttleTaskColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        mechanicTaskColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));
        statusTaskColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus() ? "Completed" : "In Progress"));
        taskTableView.setSelectionModel(null);
        taskTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        logger.debug("Task table columns configured");
    }

    /**
     * Loads the content of the task table.
     * Fetches active tasks and populates the table.
     */
    public void loadTaskTableContent() {
        try {
            logger.info("Loading active tasks for user '{}'", user);
            ArrayList<Task> tasks = TaskUtil.getActiveTasks(clientRequests, user);
            taskTableView.setItems(FXCollections.observableArrayList(tasks));
            logger.info("Loaded {} active tasks", tasks.size());
        } catch (Exception e) {
            logger.error("Error loading tasks", e);
            Util.showErrorDialog("Error loading tasks: " + e.getMessage());
        }
    }

    /**
     * Loads the progress bars for shuttles.
     * Fetches progress data and updates the UI.
     */
    public void loadProgressBar() {
        javafx.application.Platform.runLater(() -> {
            try {
                logger.info("Loading shuttle progress bars");
                shuttleProgressContainer.getChildren().clear();

                Map<String, Double> shuttleProgress = getShuttleProgressData();

                if (shuttleProgress.isEmpty()) {
                    logger.info("No shuttle progress data available");
                    noTasksLabel.setVisible(true);
                    return;
                }

                noTasksLabel.setVisible(false);

                for (Map.Entry<String, Double> entry : shuttleProgress.entrySet()) {
                    String shuttleName = entry.getKey();
                    Double progress = entry.getValue();

                    Label shuttleLabel = new Label(shuttleName);
                    shuttleLabel.getStyleClass().add("shuttle-name");
                    shuttleLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 0");

                    ProgressBar progressBar = new ProgressBar(progress);
                    progressBar.setPrefWidth(Double.MAX_VALUE);
                    progressBar.setMinHeight(20);
                    progressBar.getStyleClass().add("progressBar");

                    Label percentLabel = new Label(String.format("%.0f%%  complete", progress * 100));
                    percentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px");

                    if (progress == 0.0) {
                        percentLabel.setText("Not started yet");
                        percentLabel.setStyle("-fx-text-fill: gray;");
                    }

                    StackPane progressStack = new StackPane();
                    progressStack.getChildren().addAll(progressBar, percentLabel);
                    progressStack.setPrefWidth(Double.MAX_VALUE);
                    StackPane.setAlignment(percentLabel, Pos.CENTER_LEFT);
                    StackPane.setMargin(percentLabel, new Insets(0, 0, 0, 5));

                    VBox shuttleBox = new VBox(5);
                    shuttleBox.getChildren().addAll(shuttleLabel, progressStack);

                    shuttleProgressContainer.getChildren().add(shuttleBox);

                    logger.debug("Progress bar for shuttle '{}' set to {:.2f}%%", shuttleName, progress * 100);
                }
            } catch (Exception e) {
                logger.error("Error loading progress bars", e);
                Label errorLabel = new Label("Error loading progress data: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red;");
                shuttleProgressContainer.getChildren().add(errorLabel);
                Util.showErrorDialog("Error loading progress bars: " + e.getMessage());
            }
        });
    }

    /**
     * Fetches progress data for all shuttles.
     *
     * @return A map of shuttle names to their progress percentages.
     * @throws IOException If an error occurs while fetching the data.
     */
    private Map<String, Double> getShuttleProgressData() throws IOException {
        logger.debug("Fetching shuttle progress data");
        ArrayList<Shuttle> shuttles = ShuttleUtil.getShuttles(clientRequests, user);
        Map<String, Double> progressMap = new java.util.HashMap<>();

        for (Shuttle shuttle : shuttles) {
            ArrayList<Task> generalTasks = GeneralTaskUtil.getGeneralTasksByShuttleID(clientRequests, user, shuttle.getId());
            ArrayList<Task> tasks = TaskUtil.getActiveTasksByShuttleID(clientRequests, user, shuttle.getId());

            int completedTasks = 0;
            int totalTasks = tasks.size() + generalTasks.size();
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

            double progress = (totalTasks > 0) ? (double) completedTasks / totalTasks : 0.0;
            progressMap.put(shuttle.getShuttleName(), progress);

            logger.info("Shuttle '{}' - Completed: {}/{} tasks, Progress: {:.2f}%%",
                    shuttle.getShuttleName(), completedTasks, totalTasks, progress * 100);
        }

        return progressMap;
    }

    /**
     * Handles the event when a shuttle box is clicked.
     * Navigates to the details view.
     *
     * @param mouseEvent The mouse event triggered by the click.
     * @throws IOException If an error occurs during navigation.
     */
    public void onBoxClicked(MouseEvent mouseEvent) throws IOException {
        logger.info("Shuttle box clicked, navigating to details view");
        super.viewManagerController.handleDetailButton(null);
    }

    /**
     * Loads the content of the home view.
     * Populates the task table and progress bars.
     */
    public void load() {
        logger.info("Loading HomeViewController content");
        loadTaskTableContent();
        loadProgressBar();
    }
}