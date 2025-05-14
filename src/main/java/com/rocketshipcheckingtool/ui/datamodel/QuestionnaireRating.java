package com.rocketshipcheckingtool.ui.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a questionnaire rating for a specific topic and shuttle.
 * Contains information about the rating's ID, topic, rating value, and associated shuttle ID.
 * Provides methods for property access and logging.
 */
public class QuestionnaireRating {
    private int id;
    private String topic;
    private int rating;
    private int shuttleId;
    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireRating.class);

    /**
     * Constructs a QuestionnaireRating object.
     *
     * @param id        the unique identifier of the questionnaire rating
     * @param rating    the rating value
     * @param topic     the topic of the questionnaire
     * @param shuttleId the ID of the associated shuttle
     */
    public QuestionnaireRating(int id, int rating, String topic, int shuttleId) {
        this.id = id;
        this.rating = rating;
        this.topic = topic;
        this.shuttleId = shuttleId;
        logger.debug("Created QuestionnaireRating: id={}, rating={}, topic='{}', shuttleId={}", id, rating, topic, shuttleId);
    }

    /**
     * Returns the unique identifier of the questionnaire rating.
     *
     * @return the rating ID
     */
    public int getId() {
        logger.trace("getId called: {}", id);
        return id;
    }

    /**
     * Returns the topic of the questionnaire rating.
     *
     * @return the topic
     */
    public String getTopic() {
        logger.trace("getTopic called: {}", topic);
        return topic;
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
     * Returns the rating value.
     *
     * @return the rating
     */
    public int getRating() {
        logger.trace("getRating called: {}", rating);
        return rating;
    }

}
