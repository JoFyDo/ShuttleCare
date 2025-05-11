package com.rocketshipcheckingtool.ui.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

public class ShuttleUtil {

    private final static Logger logger = LoggerFactory.getLogger(ShuttleUtil.class);

    public static ArrayList<Shuttle> getShuttles(ClientRequests clientRequests, String user) throws IOException {
        logger.info("Requesting all shuttles for user '{}'", user);
        try {
            String tasks = clientRequests.getRequest("/requestShuttles", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Shuttle>>() {}.getType();
            ArrayList<Shuttle> shuttles = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} shuttles for user '{}'", shuttles != null ? shuttles.size() : 0, user);
            return shuttles;
        } catch (Exception e) {
            logger.error("Failed to get shuttles for user '{}': {}", user, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    public static Shuttle getShuttle(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        logger.info("Requesting shuttle with ID {} for user '{}'", shuttleID, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleType = new TypeToken<Shuttle>() {}.getType();
            Shuttle shuttle = gson.fromJson(tasks, shuttleType);
            logger.info("Received shuttle for ID {}: {}", shuttleID, shuttle != null ? "success" : "not found");
            return shuttle;
        } catch (Exception e) {
            logger.error("Failed to get shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateShuttleStatus(ClientRequests clientRequests, String user, int shuttleID, String status) throws IOException {
        logger.info("Updating shuttle ID {} to status '{}' for user '{}'", shuttleID, status, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            params.put("Status", status);
            clientRequests.postRequest("/updateShuttleStatus", user, params);
            logger.debug("Shuttle ID {} updated to status '{}'", shuttleID, status);
        } catch (Exception e) {
            logger.error("Failed to update shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    public static boolean updatePredictedReleaseTime(ClientRequests clientRequests, String user, int shuttleID, String status) throws IOException {
        logger.info("Updating predicted release time for shuttle ID {} to status '{}' for user '{}'", shuttleID, status, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            params.put("Status", status);
            clientRequests.postRequest("/updatePredictedReleaseTime", user, params);
            logger.debug("Predicted release time updated for shuttle ID {} to status '{}'", shuttleID, status);
            return true;
        } catch (Exception e) {
            logger.error("Failed to update predicted release time for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }
}
