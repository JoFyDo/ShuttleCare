package com.rocketshipcheckingtool.ui.manager;

import com.rocketshipcheckingtool.domain.Comment;
import com.rocketshipcheckingtool.domain.Part;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.ui.TableSearchHelper;
import com.rocketshipcheckingtool.ui.Util;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
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
import java.util.ArrayList;

public class KommentarViewController {
    public TextField searchField;
    public Button weiterleitenButton;
    public TableView<Comment> kommentarTableView;
    public TableColumn<Comment, Boolean> checkBoxColumn;
    public TableColumn<Comment, String> kommentarColumn;

    private ClientRequests clientRequests;
    private Shuttle shuttleSelected;
    private final String user = UserSession.getRole().name().toLowerCase();
    private TableSearchHelper<Comment> searchHelper;

    
    public void initialize() throws IOException {
        setupTableColumns();
        searchHelper = new TableSearchHelper<>(
                kommentarTableView,
                searchField,
                Comment::getComment
        );
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
                }
                evt.consume();
            });

            return cell;
        });

        kommentarColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());

        checkBoxColumn.setResizable(false);
        checkBoxColumn.setPrefWidth(50);

        kommentarTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    public void onWeiterleitenButtonClick(ActionEvent actionEvent) throws IOException {
        System.out.println("[Kommentar] Weiterleiten Button Clicked");
        boolean hasSelectedComment = false;
        for (Comment comment : kommentarTableView.getItems()) {
            if (comment.selectedProperty().get()) {
                hasSelectedComment = true;
                break;
            }
        }

        if (hasSelectedComment) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/manager/WeiterleitenPopupView.fxml"));
            Parent popupRoot = loader.load();

            WeiterleitenPopupViewController popupController = loader.getController();

            ArrayList<Comment> selectedComments = new ArrayList<>();
            for (Comment comment : kommentarTableView.getItems()) {
                if (comment.selectedProperty().get()) {
                    selectedComments.add(comment);
                }
            }

            popupController.setClientRequests(clientRequests);
            popupController.setComments(selectedComments);

            // New Stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Weiterleiten");
            popupStage.setScene(new Scene(popupRoot));
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupController.setStage(popupStage);
            popupController.initialize();
            popupController.setStage(popupStage);
            popupStage.showAndWait();

            if (popupController.isSuccessfull()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erfolg");
                alert.setHeaderText(null);
                alert.setContentText("Kommentare wurden erfolgreich weitergeleitet.");
                for (Comment comment : selectedComments ) {
                    Util.updateComment(clientRequests, user, comment.getId(), "false");
                }
                popupStage.close();
                alert.showAndWait();
                loadData();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Fehler beim Weiterleiten der Kommentare.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Kein Kommentar ausgewählt");
            alert.setHeaderText(null);
            alert.setContentText("Bitte wählen Sie mindestens einen Kommentar aus, um ihn weiterzuleiten.");
            alert.showAndWait();
        }

    }

    public void loadData() {
        if (shuttleSelected == null) {
            return;
        }
        try {
            kommentarTableView.setItems(FXCollections.observableArrayList(Util.getCommentsForShuttle(clientRequests, user, shuttleSelected.getId())));
            if (searchHelper != null) {
                searchHelper.setItems(kommentarTableView.getItems());
            }
        } catch (Exception e) {

        }
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadData();
    }

    public void setShuttle(Shuttle shuttleSelected) {
        this.shuttleSelected = shuttleSelected;
    }



}
