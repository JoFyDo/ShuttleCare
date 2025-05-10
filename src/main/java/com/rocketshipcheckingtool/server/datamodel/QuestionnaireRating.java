package com.rocketshipcheckingtool.server.datamodel;

public class QuestionnaireRating {
    private int id;
    private String topic;
    private int rating;
    private int shuttleId;

    public QuestionnaireRating(int id, int rating, String topic, int shuttleId) {
        this.id = id;
        this.rating = rating;
        this.topic = topic;
        this.shuttleId = shuttleId;
    }

    public int getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public int getShuttleId() {
        return shuttleId;
    }

    public int getRating() {
        return rating;
    }

}
