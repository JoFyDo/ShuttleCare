package com.rocketshipcheckingtool.ui.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.ui.datamodel.Mechanic;
import com.rocketshipcheckingtool.ui.datamodel.Task;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class for handling task-related operations for shuttles.
 * Provides methods to retrieve, update, and create tasks.
 */
public class TaskUtil {

    private final static Logger logger = LoggerFactory.getLogger(TaskUtil.class);

    /**
     * Retrieves all active tasks for a user.
     *
     * @param clientRequests the client requests handler
     * @param user the user performing the request
     * @return a list of active tasks
     * @throws IOException if the request fails
     */
    public static ArrayList<Task> getActiveTasks(ClientRequests clientRequests, String user) throws IOException {
        logger.info("Requesting all active tasks for user '{}'", user);
        try {
            String tasks = clientRequests.getRequest("/requestActiveTasks", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            ArrayList<Task> taskList = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} active tasks for user '{}'", taskList != null ? taskList.size() : 0, user);
            return taskList;
        } catch (Exception e) {
            logger.error("Failed to get active tasks for user '{}': {}", user, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Retrieves all active tasks for a specific shuttle.
     *
     * @param clientRequests the client requests handler
     * @param user the user performing the request
     * @param shuttleID the ID of the shuttle
     * @return a list of active tasks for the shuttle
     * @throws IOException if the request fails
     */
    public static ArrayList<Task> getActiveTasksByShuttleID(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        logger.info("Requesting active tasks for shuttle ID {} and user '{}'", shuttleID, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestActiveTaskForShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            ArrayList<Task> taskList = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} active tasks for shuttle ID {}", taskList != null ? taskList.size() : 0, shuttleID);
            return taskList;
        } catch (Exception e) {
            logger.error("Failed to get active tasks for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Updates the status of a task.
     *
     * @param clientRequests the client requests handler
     * @param user the user performing the update
     * @param taskID the ID of the task to update
     * @param status the new status for the task
     * @throws IOException if the update fails
     */
    public static void updateTaskStatus(ClientRequests clientRequests, String user, int taskID, String status) throws IOException {
        logger.info("Updating task ID {} to status '{}' for user '{}'", taskID, status, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("TaskID", String.valueOf(taskID));
            params.put("Status", status);
            clientRequests.postRequest("/updateTask", user, params);
            logger.debug("Task ID {} updated to status '{}'", taskID, status);
        } catch (Exception e) {
            logger.error("Failed to update task ID {}: {}", taskID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Creates a new task for a shuttle.
     *
     * @param clientRequests the client requests handler
     * @param user the user creating the task
     * @param mechanic the mechanic assigned to the task
     * @param description the description of the task
     * @param shuttleID the ID of the shuttle
     * @throws IOException if the creation fails
     */
    public static void createTask(ClientRequests clientRequests, String user, Mechanic mechanic, String description, int shuttleID) throws IOException {
        logger.info("Creating task for shuttle ID {} by user '{}', mechanic '{}', description '{}'", shuttleID, user, mechanic, description);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("MechanicID", String.valueOf(mechanic.getId()));
            params.put("Description", description);
            params.put("ShuttleID", String.valueOf(shuttleID));
            clientRequests.postRequest("/createTask", user, params);
            logger.debug("Task created for shuttle ID {} by mechanic '{}'", shuttleID, mechanic);
        } catch (Exception e) {
            logger.error("Failed to create task for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Updates the status of all tasks belonging to a specific shuttle.
     *
     * @param clientRequest the client requests handler
     * @param user the user performing the update
     * @param shuttleID the ID of the shuttle
     * @param status the new status for the tasks
     * @throws IOException if the update fails
     */
    public static void updateAllTasksBelongToShuttle(ClientRequests clientRequest, String user, int shuttleID, boolean status) throws IOException {
        logger.info("Updating all tasks for shuttle ID {} to status '{}' for user '{}'", shuttleID, status, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            params.put("Status", String.valueOf(status));
            clientRequest.postRequest("/updateAllTasksBelongToShuttle", user, params);
            logger.debug("All tasks for shuttle ID {} updated to status '{}'", shuttleID, status);
        } catch (Exception e) {
            logger.error("Failed to update all tasks for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }
}
