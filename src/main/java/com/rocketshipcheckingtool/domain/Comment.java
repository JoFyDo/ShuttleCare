package com.rocketshipcheckingtool.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Comment {
    private int id;
    private String comment;
    private int shuttleId;
    private BooleanProperty selected;

    public Comment(int id, String comment, int shuttleId) {
        this.id = id;
        this.comment = comment;
        this.shuttleId = shuttleId;
    }

    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty(false);
        }
        return selected;
    }

    public int getShuttleId() {
        return shuttleId;
    }

    public void setSelected(boolean selected) {}

    @Override
    public String toString() {
        return "QuestionnaireRating{" +
                "id=" + id +
                ", name='" + comment + '\'' +
                '}';
    }
}
