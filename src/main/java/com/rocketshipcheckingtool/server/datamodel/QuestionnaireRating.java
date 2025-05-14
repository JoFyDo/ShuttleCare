package com.rocketshipcheckingtool.server.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a rating for a questionnaire related to a shuttle.
 * Contains details such as the rating ID, topic, rating value, and associated shuttle ID.
 */
public class QuestionnaireRating {
    private int id; // Unique identifier for the questionnaire rating.
    private String topic; // The topic of the questionnaire.
    private int rating; // The rating value given in the questionnaire.
    private int shuttleId; // The ID of the shuttle associated with the questionnaire rating.
    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireRating.class); // Logger for logging events related to the class.

    /**
     * Constructs a QuestionnaireRating object with the specified details.
     *
     * @param id        The unique identifier for the questionnaire rating.
     * @param rating    The rating value given in the questionnaire.
     * @param topic     The topic of the questionnaire.
     * @param shuttleId The ID of the shuttle associated with the questionnaire rating.
     */
    public QuestionnaireRating(int id, int rating, String topic, int shuttleId) {
        this.id = id;
        this.rating = rating;
        this.topic = topic;
        this.shuttleId = shuttleId;
        logger.debug("Created QuestionnaireRating: id={}, rating={}, topic='{}', shuttleId={}", id, rating, topic, shuttleId);
    }

    /**
     * Gets the unique identifier of the questionnaire rating.
     *
     * @return The ID of the questionnaire rating.
     */
    public int getId() {
        logger.trace("getId called: {}", id);
        return id;
    }

    /**
     * Gets the topic of the questionnaire.
     *
     * @return The topic of the questionnaire.
     */
    public String getTopic() {
        logger.trace("getTopic called: {}", topic);
        return topic;
    }

    /**
     * Gets the ID of the shuttle associated with the questionnaire rating.
     *
     * @return The shuttle ID.
     */
    public int getShuttleId() {
        logger.trace("getShuttleId called: {}", shuttleId);
        return shuttleId;
    }

    /**
     * Gets the rating value given in the questionnaire.
     *
     * @return The rating value.
     */
    public int getRating() {
        logger.trace("getRating called: {}", rating);
        return rating;
    }
}