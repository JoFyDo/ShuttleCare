package com.rocketshipcheckingtool.ui.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a notification related to a shuttle.
 * Contains information such as message, sender, comment, and associated shuttle ID.
 * Includes logging for creation and property access.
 */
public class Notification {
    private int Id;
    private String message;
    private int shuttleID;
    private String sender;
    private String comment;
    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    /**
     * Constructs a Notification object.
     *
     * @param Id        the unique identifier of the notification
     * @param message   the notification message
     * @param shuttleID the ID of the associated shuttle
     * @param sender    the sender of the notification
     * @param comment   an additional comment
     */
    public Notification(int Id, String message, int shuttleID, String sender, String comment) {
        this.Id = Id;
        this.message = message;
        this.shuttleID = shuttleID;
        this.sender = sender;
        this.comment = comment;
        logger.debug("Created Notification: id={}, shuttleID={}, sender='{}', message='{}'", Id, shuttleID, sender, message);
    }

    /**
     * Returns the unique identifier of the notification.
     *
     * @return the notification ID
     */
    public int getId() {
        logger.trace("getId called: {}", Id);
        return Id;
    }

    /**
     * Returns the notification message.
     *
     * @return the message
     */
    public String getMessage() {
        logger.trace("getMessage called: {}", message);
        return message;
    }

    /**
     * Returns the ID of the associated shuttle.
     *
     * @return the shuttle ID
     */
    public int getShuttleID() {
        logger.trace("getShuttleID called: {}", shuttleID);
        return shuttleID;
    }

    /**
     * Returns the additional comment.
     *
     * @return the comment
     */
    public String getComment() {
        logger.trace("getComment called: {}", comment);
        return comment;
    }

    /**
     * Returns the sender of the notification.
     *
     * @return the sender
     */
    public String getSender() {
        logger.trace("getSender");
        return sender;
    }
}
