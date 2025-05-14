package com.rocketshipcheckingtool.ui.datamodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a comment associated with a shuttle.
 * Provides properties for comment text, shuttle ID, selection state, and comment ID.
 * Includes logging for creation and property access.
 */
public class Comment {
    private int id;
    private String comment;
    private int shuttleId;
    private BooleanProperty selected;
    private static final Logger logger = LoggerFactory.getLogger(Comment.class);

    /**
     * Constructs a Comment object.
     *
     * @param id        the unique identifier of the comment
     * @param comment   the comment text
     * @param shuttleId the ID of the associated shuttle
     */
    public Comment(int id, String comment, int shuttleId) {
        this.id = id;
        this.comment = comment;
        this.shuttleId = shuttleId;
        logger.debug("Created Comment: id={}, shuttleId={}, comment='{}'", id, shuttleId, comment);
    }

    /**
     * Returns the unique identifier of the comment.
     *
     * @return the comment ID
     */
    public int getId() {
        logger.trace("getId called: {}", id);
        return id;
    }

    /**
     * Returns the comment text.
     *
     * @return the comment text
     */
    public String getComment() {
        logger.trace("getComment called: {}", comment);
        return comment;
    }

    /**
     * Returns the selection property for this comment.
     * Initializes the property if it is not already set.
     *
     * @return the BooleanProperty representing selection state
     */
    public BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty(false);
            logger.debug("Initialized selected property for comment id={}", id);
        }
        return selected;
    }

    /**
     * Returns the ID of the associated shuttle.
     *
     * @return the shuttle ID
     */
    public int getShuttleId() {
        logger.trace("getShuttleId called: {}", shuttleId);
        return shuttleId;
    }

    /**
     * Sets the selection state of this comment.
     *
     * @param selected true if selected, false otherwise
     */
    public void setSelected(boolean selected) {
        selectedProperty().set(selected);
        logger.debug("Set selected={} for comment id={}", selected, id);
    }
}

