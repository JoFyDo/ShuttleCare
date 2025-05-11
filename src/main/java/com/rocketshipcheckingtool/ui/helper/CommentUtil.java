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

public class CommentUtil {

    private final static Logger logger = LoggerFactory.getLogger(CommentUtil.class);

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
