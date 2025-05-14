package com.rocketshipcheckingtool.ui.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.ui.datamodel.Comment;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class for handling comment-related operations such as retrieving, updating,
 * and checking the completion status of comments for shuttles.
 */
public class CommentUtil {

    private final static Logger logger = LoggerFactory.getLogger(CommentUtil.class);

    /**
     * Retrieves all comments for a specific shuttle.
     *
     * @param clientRequests the client requests handler
     * @param user the user performing the request
     * @param shuttleID the ID of the shuttle
     * @return a list of comments for the shuttle
     * @throws IOException if the request fails
     */
    public static ArrayList<Comment> getCommentsForShuttle(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        logger.info("Requesting comments for shuttle ID {} and user '{}'", shuttleID, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestCommentsForShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Comment>>() {}.getType();
            ArrayList<Comment> comments = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} comments for shuttle ID {}", comments != null ? comments.size() : 0, shuttleID);
            return comments;
        } catch (Exception e) {
            logger.error("Failed to get comments for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Updates the status of a comment.
     *
     * @param clientRequests the client requests handler
     * @param user the user performing the update
     * @param commentID the ID of the comment to update
     * @param status the new status for the comment
     * @return true if the update was successful
     * @throws IOException if the update fails
     */
    public static boolean updateComment(ClientRequests clientRequests, String user, int commentID, String status) throws IOException {
        logger.info("Updating comment ID {} to status '{}' for user '{}'", commentID, status, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("CommentID", String.valueOf(commentID));
            params.put("Status", status);
            clientRequests.postRequest("/updateComment", user, params);
            logger.debug("Comment ID {} updated to status '{}'", commentID, status);
            return true;
        } catch (Exception e) {
            logger.error("Failed to update comment ID {}: {}", commentID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Checks if all commands for a specific shuttle are done.
     *
     * @param clientRequests the client requests handler
     * @param user the user performing the check
     * @param shuttleID the ID of the shuttle
     * @return true if all commands are done, false otherwise
     * @throws IOException if the check fails
     */
    public static boolean allCommandsDone(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        logger.info("Checking if all commands are done for shuttle ID {} and user '{}'", shuttleID, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String done = clientRequests.postRequest("/allCommandsDone", user, params);
            boolean result = Boolean.parseBoolean(done);
            logger.info("All commands done for shuttle ID {}: {}", shuttleID, result);
            return result;
        } catch (Exception e) {
            logger.error("Failed to check all commands done for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }
}
