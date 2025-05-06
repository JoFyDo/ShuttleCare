package com.rocketshipcheckingtool.ui.manager;

import com.rocketshipcheckingtool.ui.technician.LagerViewController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class KommentarViewController {
    public TextField searchField;
    public Button weiterleitenButton;
    public TableView<Comment> kommentarTableView;
    public TableColumn<Comment, Boolean> checkBoxColumn;
    public TableColumn<Comment, String> kommentarColumn;

    
    public void initialize() {
        setupTableColumns();
        kommentarTableView.setItems(commentData);
    }

    private void setupTableColumns() {
        checkBoxColumn.setCellFactory(col -> {
            CheckBoxTableCell<Comment, Boolean> cell =
                    new CheckBoxTableCell<>(index ->
                            kommentarTableView.getItems().get(index).selectedProperty()
                    );

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                int row = cell.getIndex();
                if (row >= 0 && row < kommentarTableView.getItems().size()) {
                    Comment item = kommentarTableView.getItems().get(row);
                    item.selectedProperty().set(!item.selectedProperty().get());
                    System.out.println("[Kommentar] Clicked row: " + item.getText());
                }
                evt.consume();
            });

            return cell;
        });

        kommentarColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());

        checkBoxColumn.setResizable(false);
        checkBoxColumn.setPrefWidth(50);

        kommentarTableView.setSelectionModel(null);
        kommentarTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    public void onWeiterleitenButtonClick(ActionEvent actionEvent) throws IOException {
        System.out.println("[Kommentar] Weiterleiten Button Clicked");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/manager/WeiterleitenPopupView.fxml"));
        Parent popupRoot = loader.load();

        // New Stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Weiterleiten");
        popupStage.setScene(new Scene(popupRoot));
        popupStage.initModality(Modality.APPLICATION_MODAL);

        WeiterleitenPopupViewController popupController = loader.getController();
        popupController.setStage(popupStage);
        popupController.initialize();
        popupStage.showAndWait();
    }

    // Testiees
    private final ObservableList<Comment> commentData = FXCollections.observableArrayList(
            new Comment("Kommis", false),
            new Comment("Kommis2", true),
            new Comment("Kommis3", false)
    );

    public static class Comment {
        private final SimpleStringProperty text;
        private final SimpleBooleanProperty selected;

        public Comment(String text, boolean selected) {
            this.text = new SimpleStringProperty(text);
            this.selected = new SimpleBooleanProperty(selected);
        }

        public String getText() {
            return text.get();
        }

        public void setText(String text) {
            this.text.set(text);
        }

        public boolean isSelected() {
            return selected.get();
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        public SimpleBooleanProperty selectedProperty() {
            return selected;
        }
    }

}
