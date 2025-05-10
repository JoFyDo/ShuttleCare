package com.rocketshipcheckingtool.server.datamodel;

public class Notification{
    private int Id;
    private String message;
    private int shuttleID;
    private String sender;
    private String comment;

    public Notification(int Id, String message, int shuttleID, String sender, String comment) {
        this.Id = Id;
        this.message = message;
        this.shuttleID = shuttleID;
        this.sender = sender;
        this.comment = comment;
    }

    public int getId() {
        return Id;
    }

    public String getMessage() {
        return message;
    }

    public int getShuttleID() {
        return shuttleID;
    }

    public String getSender() {
        return sender;
    }

    public String getComment() {
        return comment;
    }

}
