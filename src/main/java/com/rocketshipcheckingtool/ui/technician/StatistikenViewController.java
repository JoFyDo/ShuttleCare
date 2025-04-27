package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.ui.ViewManagerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;

public class StatistikenViewController {
    public StackPane rocketContainer;
    public Group rocketSVG;
    public PieChart pieChart;
    public LineChart<Number, Number> lineChart;
    public NumberAxis xAxisLC;
    public NumberAxis yAxisLC;
    public StackedBarChart<Number, String> stackedBarChart;
    public NumberAxis xAxisSBC;
    public CategoryAxis yAxisSBC;
    public BarChart<String, Number> barChart;
    public CategoryAxis xAxisBC;
    public NumberAxis yAxisBC;
    private ClientRequests clientRequests;
    private ViewManagerController viewManagerController;

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }

    @FXML
    public void initialize() {
        rocketSVG.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            rocketContainer.widthProperty().addListener((wObs, oldW, newW) -> scaleSvg(newBounds));
            rocketContainer.heightProperty().addListener((hObs, oldH, newH) -> scaleSvg(newBounds));
        });

        // Pie Chart
        ObservableList<PieChart.Data> dataPC = FXCollections.observableArrayList(
                new PieChart.Data("new", 25),
                new PieChart.Data("in use", 35),
                new PieChart.Data("worn", 15),
                new PieChart.Data("critical", 25));
        pieChart.setData(dataPC);

        // Line Chart
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

        // Stacked Bar Chart
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

        // Bar Chart
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.getData().add(new XYChart.Data<>("Triebwerk A", 50));
        series2.getData().add(new XYChart.Data<>("Triebwerk B", 70));
        series2.getData().add(new XYChart.Data<>("Reaktor", 30));
        series2.getData().add(new XYChart.Data<>("Kabine", 90));

        barChart.setLegendVisible(false);
        barChart.getData().add(series2);
    }


    private void scaleSvg(Bounds newBounds) {
        double scaleX = rocketContainer.getWidth() / newBounds.getWidth();
        double scaleY = rocketContainer.getHeight() / newBounds.getHeight();
        double scale = Math.min(scaleX, scaleY);

        rocketSVG.setScaleX(scale);
        rocketSVG.setScaleY(scale);

        rocketSVG.setLayoutX((rocketContainer.getWidth() - newBounds.getWidth() * scale) / 2);
        rocketSVG.setLayoutY((rocketContainer.getHeight() - newBounds.getHeight() * scale) / 2);
    }

    public void setViewManagerController(ViewManagerController viewManagerController) {
        this.viewManagerController = viewManagerController;
    }
}
