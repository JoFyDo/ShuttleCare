package com.rocketshipcheckingtool.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.domain.*;
import com.rocketshipcheckingtool.ui.technician.ClientRequests;
import com.rocketshipcheckingtool.ui.technician.NeueAufgabePopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

public class Util {

    private final static Logger logger = LoggerFactory.getLogger(Util.class);

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

    public static ArrayList<Part> getParts(ClientRequests clientRequests, String user) throws IOException {
        try {
            String tasks = clientRequests.getRequest("/requestParts", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Part>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void usePart(ClientRequests clientRequests, String user, int partID, int quantity) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("PartID", String.valueOf(partID));
            params.put("Quantity", String.valueOf(quantity));
            clientRequests.postRequest("/usePart", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void orderPart(ClientRequests clientRequests, String user, int partId, int quantity, Integer shuttleId) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("PartID", String.valueOf(partId));
            params.put("Quantity", String.valueOf(quantity));
            params.put("ShuttleID", String.valueOf(shuttleId));
            clientRequests.postRequest("/orderPart", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

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

    public static ArrayList<QuestionnaireRating> getQuestionnaireForShuttle(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestQuestionnaireForShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<QuestionnaireRating>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

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

    public static boolean updateQuestionnaireStatus(ClientRequests clientRequests, String user, int questionnaireRatingID, String status) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("QuestionnaireRatingID", String.valueOf(questionnaireRatingID));
            params.put("Status", status);
            clientRequests.postRequest("/updateQuestionnaire", user, params);
            return true;
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





    public static void newTaskForShuttle(ClientRequests clientRequests, String user, Shuttle shuttle, String preset) throws IOException {
        FXMLLoader loader = new FXMLLoader(Util.class.getResource("/com/rocketshipcheckingtool/ui/technician/NeueAufgabePopupView.fxml"));
        Parent popupRoot = loader.load();

        // New Stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Neue Aufgabe");
        popupStage.setScene(new Scene(popupRoot));
        popupStage.initModality(Modality.APPLICATION_MODAL);

        NeueAufgabePopupController popupController = loader.getController();
        if (preset != null) {
            popupController.setDescription(preset);
        }
        popupController.setStage(popupStage);
        popupController.initialize();
        popupStage.showAndWait();

        // Retrieve data from the popup
        String description = popupController.getDescription();
        String mechanic = popupController.getMechanic();
        if (!description.equals("") || !mechanic.equals("")) {
            description = description.strip();
            mechanic = mechanic.strip();
            Util.createTask(clientRequests, user, mechanic, description, shuttle.getId());
            if (shuttle.getStatus().equals("In Wartung")) {
                Util.updateShuttleStatus(clientRequests, user, shuttle.getId(), "Inspektion 1");
            }
        }
    }
}