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

public class TaskUtil {

    private final static Logger logger = LoggerFactory.getLogger(TaskUtil.class);

    public static ArrayList<Task> getActiveTasks(ClientRequests clientRequests, String user) throws IOException {
        try {
            String tasks = clientRequests.getRequest("/requestActiveTasks", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static ArrayList<Task> getActiveTasksByShuttleID(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestActiveTaskForShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateTaskStatus(ClientRequests clientRequests, String user, int taskID, String status) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("TaskID", String.valueOf(taskID));
            params.put("Status", status);
            clientRequests.postRequest("/updateTask", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void createTask(ClientRequests clientRequests, String user, String mechanic, String description, int shuttleID) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("Mechanic", mechanic);
            params.put("Description", description);
            params.put("ShuttleID", String.valueOf(shuttleID));
            clientRequests.postRequest("/createTask", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateAllTasksBelongToShuttle(ClientRequests clientRequest, String user, int shuttleID, boolean status) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            params.put("Status", String.valueOf(status));
            clientRequest.postRequest("/updateAllTasksBelongToShuttle", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }
}
