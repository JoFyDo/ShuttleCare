package com.rocketshipcheckingtool.ui.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionnaireRating {
    private int id;
    private String topic;
    private int rating;
    private int shuttleId;
    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireRating.class);

    public QuestionnaireRating(int id, int rating, String topic, int shuttleId) {
        this.id = id;
        this.rating = rating;
        this.topic = topic;
        this.shuttleId = shuttleId;
        logger.debug("Created QuestionnaireRating: id={}, rating={}, topic='{}', shuttleId={}", id, rating, topic, shuttleId);
    }

    public int getId() {
        logger.trace("getId called: {}", id);
        return id;
    }

    public String getTopic() {
        logger.trace("getTopic called: {}", topic);
        return topic;
    }

    public int getShuttleId() {
        logger.trace("getShuttleId called: {}", shuttleId);
        return shuttleId;
    }

    public int getRating() {
        logger.trace("getRating called: {}", rating);
        return rating;
    }

}
