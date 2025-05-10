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
        try {
            String tasks = clientRequests.getRequest("/requestShuttles", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Shuttle>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static Shuttle getShuttle(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleType = new TypeToken<Shuttle>() {}.getType();
            return gson.fromJson(tasks, shuttleType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateShuttleStatus(ClientRequests clientRequests, String user, int shuttleID, String status) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            params.put("Status", status);
            clientRequests.postRequest("/updateShuttleStatus", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static boolean updatePredictedReleaseTime(ClientRequests clientRequests, String user, int shuttleID, String status) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            params.put("Status", status);
            clientRequests.postRequest("/updatePredictedReleaseTime", user, params);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }
}
