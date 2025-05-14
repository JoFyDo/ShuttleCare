package com.rocketshipcheckingtool.server.datamodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a comment related to a shuttle.
 * Contains details such as the comment ID, content, associated shuttle ID, and selection status.
 * Provides methods to retrieve and modify comment information.
 */
public class Comment {
    private int id; // Unique identifier for the comment.
    private String comment; // The content of the comment.
    private int shuttleId; // The ID of the shuttle associated with the comment.
    private BooleanProperty selected; // Property indicating whether the comment is selected.
    private static final Logger logger = LoggerFactory.getLogger(Comment.class); // Logger for logging comment-related events.

    /**
     * Constructs a Comment object with the specified details.
     *
     * @param id        The unique identifier for the comment.
     * @param comment   The content of the comment.
     * @param shuttleId The ID of the shuttle associated with the comment.
     */
    public Comment(int id, String comment, int shuttleId) {
        this.id = id;
        this.comment = comment;
        this.shuttleId = shuttleId;
        logger.debug("Created Comment: id={}, shuttleId={}, comment='{}'", id, shuttleId, comment);
    }

    /**
     * Gets the unique identifier of the comment.
     *
     * @return The comment ID.
     */
    public int getId() {
        logger.trace("getId called: {}", id);
        return id;
    }

    /**
     * Gets the content of the comment.
     *
     * @return The comment content.
     */
    public String getComment() {
        logger.trace("getComment called: {}", comment);
        return comment;
    }

    /**
     * Gets the property indicating whether the comment is selected.
     * Initializes the property if it is not already initialized.
     *
     * @return The BooleanProperty representing the selection status.
     */
    public BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty(false);
            logger.debug("Initialized selected property for comment id={}", id);
        }
        return selected;
    }

    /**
     * Gets the ID of the shuttle associated with the comment.
     *
     * @return The shuttle ID.
     */
    public int getShuttleId() {
        logger.trace("getShuttleId called: {}", shuttleId);
        return shuttleId;
    }

    /**
     * Sets the selection status of the comment.
     *
     * @param selected The new selection status.
     */
    public void setSelected(boolean selected) {
        selectedProperty().set(selected);
        logger.debug("Set selected={} for comment id={}", selected, id);
    }
}