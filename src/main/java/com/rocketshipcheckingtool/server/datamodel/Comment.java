package com.rocketshipcheckingtool.server.datamodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Comment {
    private int id;
    private String comment;
    private int shuttleId;
    private BooleanProperty selected;
    private static final Logger logger = LoggerFactory.getLogger(Comment.class);

    public Comment(int id, String comment, int shuttleId) {
        this.id = id;
        this.comment = comment;
        this.shuttleId = shuttleId;
        logger.debug("Created Comment: id={}, shuttleId={}, comment='{}'", id, shuttleId, comment);
    }

    public int getId() {
        logger.trace("getId called: {}", id);
        return id;
    }

    public String getComment() {
        logger.trace("getComment called: {}", comment);
        return comment;
    }

    public BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty(false);
            logger.debug("Initialized selected property for comment id={}", id);
        }
        return selected;
    }

    public int getShuttleId() {
        logger.trace("getShuttleId called: {}", shuttleId);
        return shuttleId;
    }

    public void setSelected(boolean selected) {
        selectedProperty().set(selected);
        logger.debug("Set selected={} for comment id={}", selected, id);
    }
}
