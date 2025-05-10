package com.rocketshipcheckingtool.ui.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.ui.datamodel.Notification;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

public class NotificationUtil {

    private final static Logger logger = LoggerFactory.getLogger(NotificationUtil.class);

    public static ArrayList<Notification> requestNotifications(ClientRequests clientRequests, String user) throws IOException {
        try {
            String tasks = clientRequests.getRequest("/requestNotifications", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Notification>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateNotification(ClientRequests clientRequests, String user, int notificationID, String status) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("NotificationID", String.valueOf(notificationID));
            params.put("Status", status);
            clientRequests.postRequest("/updateNotification", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static ArrayList<Notification> requestNotificationsByShuttle(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestNotificationsByShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Notification>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static boolean createNotification(ClientRequests clientRequests, String user, int shuttleID, String message, String sender, String comment) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            params.put("Message", message);
            params.put("Sender", sender);
            params.put("Comment", comment);
            clientRequests.postRequest("/createNotification", user, params);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }
}
