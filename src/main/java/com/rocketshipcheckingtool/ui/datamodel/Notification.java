package com.rocketshipcheckingtool.ui.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Notification {
    private int Id;
    private String message;
    private int shuttleID;
    private String sender;
    private String comment;
    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    public Notification(int Id, String message, int shuttleID, String sender, String comment) {
        this.Id = Id;
        this.message = message;
        this.shuttleID = shuttleID;
        this.sender = sender;
        this.comment = comment;
        logger.debug("Created Notification: id={}, shuttleID={}, sender='{}', message='{}'", Id, shuttleID, sender, message);
    }

    public int getId() {
        logger.trace("getId called: {}", Id);
        return Id;
    }

    public String getMessage() {
        logger.trace("getMessage called: {}", message);
        return message;
    }

    public int getShuttleID() {
        logger.trace("getShuttleID called: {}", shuttleID);
        return shuttleID;
    }

    public String getComment() {
        logger.trace("getComment called: {}", comment);
        return comment;
    }

    public String getSender() {
        logger.trace("getSender");
        return sender;
    }
}