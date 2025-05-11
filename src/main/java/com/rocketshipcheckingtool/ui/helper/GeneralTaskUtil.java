package com.rocketshipcheckingtool.ui.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.ui.datamodel.Task;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

public class GeneralTaskUtil {

    private final static Logger logger = LoggerFactory.getLogger(GeneralTaskUtil.class);

    public static ArrayList<Task> getGeneralTasksByShuttleID(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        logger.info("Requesting general tasks for shuttle ID {} and user '{}'", shuttleID, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestGeneralTasksForShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            ArrayList<Task> generalTasks = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} general tasks for shuttle ID {}", generalTasks != null ? generalTasks.size() : 0, shuttleID);
            return generalTasks;
        } catch (Exception e) {
            logger.error("Failed to get general tasks for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateGeneralTask(ClientRequests clientRequests, String user, int taskID, boolean status) throws IOException {
        logger.info("Updating general task ID {} to status '{}' for user '{}'", taskID, status, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("TaskID", String.valueOf(taskID));
            params.put("Status", String.valueOf(status));
            clientRequests.postRequest("/updateGeneralTasksForShuttle", user, params);
            logger.debug("General task ID {} updated to status '{}'", taskID, status);
        } catch (Exception e) {
            logger.error("Failed to update general task ID {}: {}", taskID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateAllGeneralTasksStatusBelongToShuttle(ClientRequests clientRequests, String user, int shuttleID, boolean status) throws IOException {
        logger.info("Updating all general tasks for shuttle ID {} to status '{}' for user '{}'", shuttleID, status, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            params.put("Status", String.valueOf(status));
            clientRequests.postRequest("/updateAllGeneralTasksBelongToShuttle", user, params);
            logger.debug("All general tasks for shuttle ID {} updated to status '{}'", shuttleID, status);
        } catch (Exception e) {
            logger.error("Failed to update all general tasks for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }
}
