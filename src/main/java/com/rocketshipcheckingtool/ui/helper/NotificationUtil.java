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
        logger.info("Requesting all notifications for user '{}'", user);
        try {
            String tasks = clientRequests.getRequest("/requestNotifications", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Notification>>() {}.getType();
            ArrayList<Notification> notifications = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} notifications for user '{}'", notifications != null ? notifications.size() : 0, user);
            return notifications;
        } catch (Exception e) {
            logger.error("Failed to request notifications for user '{}': {}", user, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    public static void updateNotification(ClientRequests clientRequests, String user, int notificationID, String status) throws IOException {
        logger.info("Updating notification ID {} to status '{}' for user '{}'", notificationID, status, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("NotificationID", String.valueOf(notificationID));
            params.put("Status", status);
            clientRequests.postRequest("/updateNotification", user, params);
            logger.debug("Notification ID {} updated to status '{}'", notificationID, status);
        } catch (Exception e) {
            logger.error("Failed to update notification ID {}: {}", notificationID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    public static ArrayList<Notification> requestNotificationsByShuttle(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        logger.info("Requesting notifications for shuttle ID {} and user '{}'", shuttleID, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestNotificationsByShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Notification>>() {}.getType();
            ArrayList<Notification> notifications = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} notifications for shuttle ID {}", notifications != null ? notifications.size() : 0, shuttleID);
            return notifications;
        } catch (Exception e) {
            logger.error("Failed to request notifications for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    public static boolean createNotification(ClientRequests clientRequests, String user, int shuttleID, String message, String sender, String comment) throws IOException {
        logger.info("Creating notification for shuttle ID {} by user '{}', sender '{}', message '{}'", shuttleID, user, sender, message);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            params.put("Message", message);
            params.put("Sender", sender);
            params.put("Comment", comment);
            clientRequests.postRequest("/createNotification", user, params);
            logger.info("Notification created for shuttle ID {} by user '{}'", shuttleID, user);
            return true;
        } catch (Exception e) {
            logger.error("Failed to create notification for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }
}
