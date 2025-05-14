package com.rocketshipcheckingtool.ui.roles.manager;

import com.rocketshipcheckingtool.ui.datamodel.Comment;
import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.helper.CommentUtil;
import com.rocketshipcheckingtool.ui.helper.TableSearchHelper;
import com.rocketshipcheckingtool.ui.auth.UserSession;
import com.rocketshipcheckingtool.ui.helper.Util;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import javafx.collections.FXCollections;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller class for managing the comment view in the manager role.
 * Handles the display, selection, and forwarding of comments related to a specific shuttle.
 */
public class CommentViewController {
    public TextField searchField; // TextField for searching comments.
    public Button forwardButton; // Button for forwarding selected comments.
    public TableView<Comment> commentTableView; // TableView for displaying comments.
    public TableColumn<Comment, Boolean> checkBoxColumn; // TableColumn for checkboxes to select comments.
    public TableColumn<Comment, String> commentColumn; // TableColumn for displaying comment text.

    private ClientRequests clientRequests; // ClientRequests instance for server communication.
    private Shuttle shuttleSelected; // The currently selected shuttle.
    private final String user = UserSession.getRole().name().toLowerCase(); // The current user's role.
    private TableSearchHelper<Comment> searchHelper; // Helper for adding search functionality to the TableView.

    private static final Logger logger = LoggerFactory.getLogger(CommentViewController.class); // Logger instance.

    /**
     * Initializes the controller, setting up the table columns and search functionality.
     *
     * @throws IOException If an error occurs during initialization.
     */
    public void initialize() throws IOException {
        setupTableColumns();
        searchHelper = new TableSearchHelper<>(
                commentTableView,
                searchField,
                Comment::getComment
        );
        logger.info("CommentViewController initialized");
    }

    /**
     * Configures the table columns, including the checkbox and comment columns.
     */
    private void setupTableColumns() {
        checkBoxColumn.setCellFactory(col -> {
            CheckBoxTableCell<Comment, Boolean> cell =
                    new CheckBoxTableCell<>(index ->
                            commentTableView.getItems().get(index).selectedProperty()
                    );

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                int row = cell.getIndex();
                if (row >= 0 && row < commentTableView.getItems().size()) {
                    Comment item = commentTableView.getItems().get(row);
                    item.selectedProperty().set(!item.selectedProperty().get());
                    logger.debug("Comment row {} selection toggled to {}", row, item.selectedProperty().get());
                }
                evt.consume();
            });

            return cell;
        });

        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());

        checkBoxColumn.setResizable(false);
        checkBoxColumn.setPrefWidth(50);

        commentTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        logger.debug("Table columns set up in KommentarViewController");
    }

    /**
     * Handles the action of the forward button, forwarding selected comments.
     *
     * @param actionEvent The ActionEvent triggered by the button click.
     * @throws IOException If an error occurs while loading the popup view.
     */
    public void onWeiterleitenButtonClick(ActionEvent actionEvent) throws IOException {
        logger.info("Forward button clicked in KommentarViewController");
        boolean hasSelectedComment = false;
        for (Comment comment : commentTableView.getItems()) {
            if (comment.selectedProperty().get()) {
                hasSelectedComment = true;
                break;
            }
        }

        if (hasSelectedComment) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rocketshipcheckingtool/ui/roles/manager/ForwardPopupView.fxml"));
            Parent popupRoot = loader.load();

            ForwardPopupViewController popupController = loader.getController();

            ArrayList<Comment> selectedComments = new ArrayList<>();
            for (Comment comment : commentTableView.getItems()) {
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
                logger.info("Comments successfully forwarded: {} comment(s)", selectedComments.size());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erfolg");
                alert.setHeaderText(null);
                alert.setContentText("Kommentare wurden erfolgreich weitergeleitet.");
                for (Comment comment : selectedComments ) {
                    try {
                        CommentUtil.updateComment(clientRequests, user, comment.getId(), "false");
                        logger.debug("Comment with ID {} marked as processed", comment.getId());
                    } catch (Exception e) {
                        logger.error("Error updating the comment with ID {}: {}", comment.getId(), e.getMessage(), e);
                        Util.showErrorDialog("Fehler beim Aktualisieren des Kommentars: " + e.getMessage());
                    }
                }
                popupStage.close();
                alert.showAndWait();
                loadData();
            } else {
                logger.warn("Error occurred while forwarding comments");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Fehler beim Weiterleiten der Kommentare.");
                alert.showAndWait();
            }
        } else {
            logger.info("No comment selected for forwarding");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Kein Kommentar ausgewählt");
            alert.setHeaderText(null);
            alert.setContentText("Bitte wählen Sie mindestens einen Kommentar aus, um ihn weiterzuleiten.");
            alert.showAndWait();
        }
    }

    /**
     * Loads the comments for the currently selected shuttle into the TableView.
     */
    public void loadData() {
        if (shuttleSelected == null) {
            logger.warn("No shuttle selected when loading comments");
            return;
        }
        try {
            commentTableView.setItems(FXCollections.observableArrayList(CommentUtil.getCommentsForShuttle(clientRequests, user, shuttleSelected.getId())));
            if (searchHelper != null) {
                searchHelper.setItems(commentTableView.getItems());
            }
            logger.info("Loaded comments for shuttle '{}'", shuttleSelected.getShuttleName());
        } catch (Exception e) {
            logger.error("Error loading comments: {}", e.getMessage(), e);
            Util.showErrorDialog("Fehler beim Laden der Kommentare: " + e.getMessage());
        }
    }

    /**
     * Sets the ClientRequests instance for server communication.
     *
     * @param clientRequests The ClientRequests instance to set.
     */
    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
        loadData();
        logger.debug("ClientRequests set in KommentarViewController");
    }

    /**
     * Sets the currently selected shuttle.
     *
     * @param shuttleSelected The Shuttle to set as selected.
     */
    public void setShuttle(Shuttle shuttleSelected) {
        this.shuttleSelected = shuttleSelected;
        logger.debug("Shuttle set in KommentarViewController: '{}'", shuttleSelected != null ? shuttleSelected.getShuttleName() : "null");
    }
}