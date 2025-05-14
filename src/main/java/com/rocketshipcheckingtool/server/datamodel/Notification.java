package com.rocketshipcheckingtool.server.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a notification related to a shuttle.
 * Contains details such as the notification ID, message, shuttle ID, sender, and an optional comment.
 */
public class Notification {
    private int Id; // Unique identifier for the notification.
    private String message; // The message content of the notification.
    private int shuttleID; // The ID of the shuttle associated with the notification.
    private String sender; // The sender of the notification.
    private String comment; // An optional comment related to the notification.
    private static final Logger logger = LoggerFactory.getLogger(Notification.class); // Logger for logging notification-related events.

    /**
     * Constructs a Notification object with the specified details.
     *
     * @param Id        The unique identifier for the notification.
     * @param message   The message content of the notification.
     * @param shuttleID The ID of the shuttle associated with the notification.
     * @param sender    The sender of the notification.
     * @param comment   An optional comment related to the notification.
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
     * Gets the unique identifier of the notification.
     *
     * @return The notification ID.
     */
    public int getId() {
        logger.trace("getId called: {}", Id);
        return Id;
    }

    /**
     * Gets the message content of the notification.
     *
     * @return The notification message.
     */
    public String getMessage() {
        logger.trace("getMessage called: {}", message);
        return message;
    }

    /**
     * Gets the ID of the shuttle associated with the notification.
     *
     * @return The shuttle ID.
     */
    public int getShuttleID() {
        logger.trace("getShuttleID called: {}", shuttleID);
        return shuttleID;
    }

    /**
     * Gets the sender of the notification.
     *
     * @return The sender's name.
     */
    public String getSender() {
        logger.trace("getSender");
        return sender;
    }
}