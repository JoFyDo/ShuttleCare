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
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestCommentsForShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Comment>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static boolean updateComment(ClientRequests clientRequests, String user, int commentID, String status) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("CommentID", String.valueOf(commentID));
            params.put("Status", status);
            clientRequests.postRequest("/updateComment", user, params);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static boolean allCommandsDone(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String done = clientRequests.postRequest("/allCommandsDone", user, params);
            return Boolean.parseBoolean(done);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }
}
