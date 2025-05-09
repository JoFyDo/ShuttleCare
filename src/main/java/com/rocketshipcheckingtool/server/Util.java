package com.rocketshipcheckingtool.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.domain.Manage;
import com.rocketshipcheckingtool.domain.Part;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Util {

    private static Gson gson = new Gson();

//    public static String combineJSONString(ArrayList<? extends Manage> items) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("[\n");
//
//        for (int i = 0; i < items.size(); i++) {
//            Manage item = items.get(i);
//            sb.append(item.toJson());
//            if (i < items.size() - 1) {
//                sb.append(",\n");
//            }
//        }
//        sb.append("\n]");
//        return sb.toString();
//
//
//    }

    public static Map<String, String> parseRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }

            Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();
            return gson.fromJson(body.toString(), mapType);
        }
    }

    public static Map<String, String> parseQueryParameters(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return result;
        }

        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public static String combineJSONString(List<?> items) {
        return gson.toJson(items);
    }

    public static void predictedDeployTimeUpdate(DatabaseConnection databaseConnection, int shuttleID, String status) {
        if (status.equals("Inspektion 1") || status.equals("Inspektion 2")) {
            ArrayList<Task> generalActiveTasks = databaseConnection.getGeneralTasksForShuttle(shuttleID);
            ArrayList<Task> additionalActiveTasks = databaseConnection.getActiveTaskByShuttleID(shuttleID);
            int timeNeeded = 0;
            for (Task task : generalActiveTasks) {
                timeNeeded += task.getTimeNeeded();
            }
            for (Task task : additionalActiveTasks) {
                timeNeeded += task.getTimeNeeded();
            }
            Calendar currentDeployTime = databaseConnection.getPredictedReleaseTime(shuttleID);
            if (currentDeployTime != null) {
                Shuttle shuttle = databaseConnection.getShuttle(shuttleID);

                Calendar landungTime = shuttle.getLandingTime();
                landungTime.add(Calendar.HOUR, timeNeeded);
                if (!landungTime.equals(currentDeployTime)) {
                    long timeDifferenceInHours = (currentDeployTime.getTimeInMillis() - landungTime.getTimeInMillis()) / (60 * 60 * 1000);
                    timeNeeded += (int) timeDifferenceInHours;
                }
            }
            databaseConnection.updatePredictedReleaseTime(shuttleID, timeNeeded);
        }
    }

    public static void orderPartDelayShuttle(DatabaseConnection databaseConnection, int shuttleID) {
        int delay = new Random().nextInt(49) + 24;
        databaseConnection.updatePredictedReleaseTime(shuttleID, delay);

    }


}
