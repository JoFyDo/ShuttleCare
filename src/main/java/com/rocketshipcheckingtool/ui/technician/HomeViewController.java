package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import com.rocketshipcheckingtool.ui.HomeViewControllerMaster;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.ViewManagerController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class HomeViewController extends HomeViewControllerMaster {

    public PieChart pieChart;
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

    public void setupTableColumns(){
        super.setupTableColumns();

        aufgabeTaskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        shuttleTaskColumn.setCellValueFactory(new PropertyValueFactory<>("shuttleName"));
        mechanikerTaskColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));
        statusTaskColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        aufgabenTableView.setSelectionModel(null);
        aufgabenTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    }

    public void loadTaskTableContent() {
        try {
            ArrayList<Task> tasks = Util.getActiveTasks(super.clientRequests, super.user);
            aufgabenTableView.setItems(FXCollections.observableArrayList(tasks));
        }catch (Exception e) {
            logger.error("Error loading tasks: ", e);
        }
    }

    public void onAufgabenBoxClicked(MouseEvent mouseEvent) {}

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadShuttleTableContent();
        loadTaskTableContent();
    }

    public void onBoxClicked(MouseEvent mouseEvent) throws IOException {
        super.viewManagerController.handleDetailButton(null);
    }
}
