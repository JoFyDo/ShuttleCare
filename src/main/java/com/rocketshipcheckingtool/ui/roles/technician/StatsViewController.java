package com.rocketshipcheckingtool.ui.roles.technician;

import com.rocketshipcheckingtool.ui.ViewManagerController;
import com.rocketshipcheckingtool.ui.helper.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for managing the statistics view in the technician role.
 * Handles the initialization and configuration of various charts and the scaling of SVG graphics.
 */
public class StatsViewController {
    public StackPane rocketContainer; // Container for the rocket SVG graphic.
    public Group rocketSVG; // Group containing the rocket SVG graphic.
    public PieChart pieChart; // Pie chart for displaying part statuses.
    public LineChart<Number, Number> lineChart; // Line chart for displaying O₂ pressure over time.
    public NumberAxis xAxisLC; // X-axis for the line chart.
    public NumberAxis yAxisLC; // Y-axis for the line chart.
    public StackedBarChart<Number, String> stackedBarChart; // Stacked bar chart for displaying load percentages.
    public NumberAxis xAxisSBC; // X-axis for the stacked bar chart.
    public CategoryAxis yAxisSBC; // Y-axis for the stacked bar chart.
    public BarChart<String, Number> barChart; // Bar chart for displaying component statuses.
    public CategoryAxis xAxisBC; // X-axis for the bar chart.
    public NumberAxis yAxisBC; // Y-axis for the bar chart.
    private ClientRequests clientRequests; // ClientRequests instance for making HTTP requests.
    private ViewManagerController viewManagerController; // Controller for managing views.
    private static final Logger logger = LoggerFactory.getLogger(StatsViewController.class); // Logger instance for logging activities.

    /**
     * Sets the ClientRequests instance for making HTTP requests.
     *
     * @param clientRequests The ClientRequests instance to be set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }

    /**
     * Initializes the statistics view controller.
     * Configures the charts with sample data and sets up the scaling for the rocket SVG graphic.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing StatsViewController");
        try {
            // Set up scaling for the rocket SVG graphic.
            rocketSVG.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                rocketContainer.widthProperty().addListener((wObs, oldW, newW) -> scaleSvg(newBounds));
                rocketContainer.heightProperty().addListener((hObs, oldH, newH) -> scaleSvg(newBounds));
            });

            // Configure the pie chart with sample data.
            ObservableList<PieChart.Data> dataPC = FXCollections.observableArrayList(
                    new PieChart.Data("new", 25),
                    new PieChart.Data("in use", 35),
                    new PieChart.Data("worn", 15),
                    new PieChart.Data("critical", 25));
            pieChart.setData(dataPC);

            // Configure the line chart with sample data.
            xAxisLC.setLabel("Time (min)");
            yAxisLC.setLabel("O₂ Pressure (kPa)");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(0, 21.0));
            series.getData().add(new XYChart.Data<>(5, 21.1));
            series.getData().add(new XYChart.Data<>(10, 20.9));
            series.getData().add(new XYChart.Data<>(15, 20.8));
            series.getData().add(new XYChart.Data<>(20, 20.6));
            series.getData().add(new XYChart.Data<>(25, 20.5));
            series.getData().add(new XYChart.Data<>(30, 20.7));
            lineChart.setLegendVisible(false);
            lineChart.getData().add(series);

            // Configure the stacked bar chart with sample data.
            xAxisSBC.setLabel("Belastung (%)");
            yAxisSBC.setLabel("Zeit (min)");
            yAxisSBC.getCategories().addAll("0", "5", "10", "15", "20");

            XYChart.Series<Number, String> triebwerkA = new XYChart.Series<>();
            triebwerkA.setName("Triebwerk A");
            triebwerkA.getData().add(new XYChart.Data<>(30, "0"));
            triebwerkA.getData().add(new XYChart.Data<>(40, "5"));
            triebwerkA.getData().add(new XYChart.Data<>(50, "10"));
            triebwerkA.getData().add(new XYChart.Data<>(60, "15"));
            triebwerkA.getData().add(new XYChart.Data<>(55, "20"));

            XYChart.Series<Number, String> triebwerkB = new XYChart.Series<>();
            triebwerkB.setName("Triebwerk B");
            triebwerkB.getData().add(new XYChart.Data<>(25, "0"));
            triebwerkB.getData().add(new XYChart.Data<>(35, "5"));
            triebwerkB.getData().add(new XYChart.Data<>(40, "10"));
            triebwerkB.getData().add(new XYChart.Data<>(38, "15"));
            triebwerkB.getData().add(new XYChart.Data<>(45, "20"));

            XYChart.Series<Number, String> reaktor = new XYChart.Series<>();
            reaktor.setName("Reaktor");
            reaktor.getData().add(new XYChart.Data<>(20, "0"));
            reaktor.getData().add(new XYChart.Data<>(30, "5"));
            reaktor.getData().add(new XYChart.Data<>(35, "10"));
            reaktor.getData().add(new XYChart.Data<>(40, "15"));
            reaktor.getData().add(new XYChart.Data<>(42, "20"));

            XYChart.Series<Number, String> kabine = new XYChart.Series<>();
            kabine.setName("Kabine");
            kabine.getData().add(new XYChart.Data<>(10, "0"));
            kabine.getData().add(new XYChart.Data<>(12, "5"));
            kabine.getData().add(new XYChart.Data<>(15, "10"));
            kabine.getData().add(new XYChart.Data<>(18, "15"));
            kabine.getData().add(new XYChart.Data<>(20, "20"));

            stackedBarChart.getData().addAll(triebwerkA, triebwerkB, reaktor, kabine);

            // Configure the bar chart with sample data.
            XYChart.Series<String, Number> series2 = new XYChart.Series<>();
            series2.getData().add(new XYChart.Data<>("Triebwerk A", 50));
            series2.getData().add(new XYChart.Data<>("Triebwerk B", 70));
            series2.getData().add(new XYChart.Data<>("Reaktor", 30));
            series2.getData().add(new XYChart.Data<>("Kabine", 90));

            barChart.setLegendVisible(false);
            barChart.getData().add(series2);

            logger.debug("Charts initialized with sample data");
        } catch (Exception e) {
            logger.error("Error initializing the Stats: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Initialisieren der Statistiken: " + e.getMessage());
        }
    }

    /**
     * Scales the rocket SVG graphic to fit within the container.
     *
     * @param newBounds The new bounds of the SVG graphic.
     */
    private void scaleSvg(Bounds newBounds) {
        double scaleX = rocketContainer.getWidth() / newBounds.getWidth();
        double scaleY = rocketContainer.getHeight() / newBounds.getHeight();
        double scale = Math.min(scaleX, scaleY);

        rocketSVG.setScaleX(scale);
        rocketSVG.setScaleY(scale);

        rocketSVG.setLayoutX((rocketContainer.getWidth() - newBounds.getWidth() * scale) / 2);
        rocketSVG.setLayoutY((rocketContainer.getHeight() - newBounds.getHeight() * scale) / 2);

        logger.debug("Scaled SVG to scale={}, layoutX={}, layoutY={}", scale, rocketSVG.getLayoutX(), rocketSVG.getLayoutY());
    }

    /**
     * Sets the ViewManagerController instance for managing views.
     *
     * @param viewManagerController The ViewManagerController instance to be set.
     */
    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
    }
}