package com.rocketshipcheckingtool.ui.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.ui.datamodel.QuestionnaireRating;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class for handling questionnaire-related operations.
 * Provides methods to retrieve and update questionnaire ratings for shuttles.
 */
public class QuestionnaireUtil {

    private final static Logger logger = LoggerFactory.getLogger(QuestionnaireUtil.class); // Logger instance for logging activities.

    /**
     * Retrieves the questionnaire ratings for a specific shuttle.
     *
     * @param clientRequests The ClientRequests instance for server communication.
     * @param user The username for whom the questionnaire is requested.
     * @param shuttleID The ID of the shuttle for which the questionnaire is requested.
     * @return A list of QuestionnaireRating objects.
     * @throws IOException If an error occurs during the request.
     */
    public static ArrayList<QuestionnaireRating> getQuestionnaireForShuttle(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        logger.info("Requesting questionnaire for shuttle ID {} and user '{}'", shuttleID, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestQuestionnaireForShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<QuestionnaireRating>>() {}.getType();
            ArrayList<QuestionnaireRating> ratings = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} questionnaire ratings for shuttle ID {}", ratings != null ? ratings.size() : 0, shuttleID);
            return ratings;
        } catch (Exception e) {
            logger.error("Failed to get questionnaire for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Updates the status of a specific questionnaire rating.
     *
     * @param clientRequests The ClientRequests instance for server communication.
     * @param user The username performing the update.
     * @param questionnaireRatingID The ID of the questionnaire rating to update.
     * @param status The new status to set for the questionnaire rating.
     * @return True if the update is successful.
     * @throws IOException If an error occurs during the update.
     */
    public static boolean updateQuestionnaireStatus(ClientRequests clientRequests, String user, int questionnaireRatingID, String status) throws IOException {
        logger.info("Updating questionnaire rating ID {} to status '{}' for user '{}'", questionnaireRatingID, status, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("QuestionnaireRatingID", String.valueOf(questionnaireRatingID));
            params.put("Status", status);
            clientRequests.postRequest("/updateQuestionnaire", user, params);
            logger.debug("Questionnaire rating ID {} updated to status '{}'", questionnaireRatingID, status);
            return true;
        } catch (Exception e) {
            logger.error("Failed to update questionnaire rating ID {}: {}", questionnaireRatingID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }
}