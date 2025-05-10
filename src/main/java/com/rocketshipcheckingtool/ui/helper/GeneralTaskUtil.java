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
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestGeneralTasksForShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Task>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateGeneralTask(ClientRequests clientRequests, String user, int taskID, String status) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("TaskID", String.valueOf(taskID));
            params.put("Status", status);
            clientRequests.postRequest("/updateGeneralTasksForShuttle", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }
}
