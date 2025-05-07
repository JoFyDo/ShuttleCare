package com.rocketshipcheckingtool.domain;

public class Notification implements Manage{
    private int ID;
    private String message;
    private int shuttleID;
    private String sender;
    private String comment;

    public Notification(int ID, String message, int shuttleID, String sender, String comment) {
        this.ID = ID;
        this.message = message;
        this.shuttleID = shuttleID;
        this.sender = sender;
        this.comment = comment;
    }

    public int getID() {
        return ID;
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

    @Override
    public String getByI(int index) {
        return "";
    }

    @Override
    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }
}
