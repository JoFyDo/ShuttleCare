package com.rocketshipcheckingtool.ui.technician;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;

public class Util {

    private final static Logger logger = LoggerFactory.getLogger(Util.class);

    public static ArrayList<Shuttle> getShuttles(ClientRequests clientRequests, String user) throws IOException {
        try {
            String tasks = clientRequests.request("/requestShuttles", user);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Shuttle>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static Shuttle getShuttle(ClientRequests clientRequests, String user, int i) throws IOException {
        try {
            String tasks = clientRequests.request("/requestShuttle", user, "Shuttle", String.valueOf(i));
            Gson gson = new Gson();
            Type shuttleType = new TypeToken<Shuttle>() {}.getType();
            return gson.fromJson(tasks, shuttleType);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static ArrayList<Task> getActiveTasks(ClientRequests clientRequests, String user) throws IOException {
        try {
            String tasks = clientRequests.request("/requestActiveTasks", user);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static ArrayList<Task> getActiveTasksByShuttleID(ClientRequests clientRequests, String user, int ShuttleID) throws IOException {
        try {
            String tasks = clientRequests.request("/requestActiveTaskForShuttle", user, "Shuttle", String.valueOf(ShuttleID));
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateTaskStatus(ClientRequests clientRequests, String user, int taskID, String status) throws IOException {
        try {
            clientRequests.request("/updateTask", user, "TaskID", String.valueOf(taskID), "status", String.valueOf(status));
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void createTask(ClientRequests clientRequests, String user, String mechanic, String description, int shuttleID) throws IOException {
        try {
            clientRequests.request("/createTask", user, "Mechanic", mechanic, "Description", description, "ShuttleID", String.valueOf(shuttleID));
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static ArrayList<Task> getGeneralTasksByShuttleID(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        try {
            String tasks = clientRequests.request("/requestGeneralTasksForShuttle", user, "ShuttleID", String.valueOf(shuttleID));
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateGeneralTask(ClientRequests clientRequests, String user, int taskID, String status) throws IOException {
        try {
            clientRequests.request("/updateGeneralTasksForShuttle", user, "TaskID", String.valueOf(taskID), "Status", status);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }
}