package com.rocketshipcheckingtool.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.server.datamodel.Shuttle;
import com.rocketshipcheckingtool.server.datamodel.Task;
import com.rocketshipcheckingtool.server.database.DatabaseFacade;
import com.sun.net.httpserver.HttpExchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    private static Gson gson = new Gson();

    public static Map<String, String> parseRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            logger.debug("Parsing request body: {}", body);
            Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();
            Map<String, String> result = gson.fromJson(body.toString(), mapType);
            logger.info("Parsed request body into map with {} entries", result != null ? result.size() : 0);
            return result;
        }
    }

    public static Map<String, String> parseQueryParameters(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) {
            logger.debug("No query parameters found");
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
        logger.info("Parsed query parameters: {}", result.keySet());
        return result;
    }

    public static String combineJSONString(List<?> items) {
        String json = gson.toJson(items);
        logger.debug("Converted list to JSON string: {}", json);
        return json;
    }

    public static void predictedReleaseTimeUpdate(DatabaseFacade databaseConnection, int shuttleID, String status) {
        logger.info("Updating predicted release time for shuttleID={} with status={}", shuttleID, status);
        if (status.equals("Gelandet") || status.equals("Inspektion 1") || status.equals("Inspektion 2")) {
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
                    logger.debug("Adjusted timeNeeded by time difference: {}", timeDifferenceInHours);
                }
            }
            databaseConnection.updatePredictedReleaseTime(shuttleID, timeNeeded);
            logger.info("Predicted release time updated for shuttleID={} with total timeNeeded={}", shuttleID, timeNeeded);
        } else {
            logger.debug("No predicted release time update needed for status={}", status);
        }
    }

    public static void orderPartDelayShuttle(DatabaseFacade databaseConnection, int shuttleID) {
        int delay = new Random().nextInt(49) + 24;
        logger.info("[Helper] Applying delay of {} hours to shuttleID={}", delay, shuttleID);
        Calendar predictedReleaseTime = databaseConnection.getPredictedReleaseTime(shuttleID);
        Shuttle shuttle = databaseConnection.getShuttle(shuttleID);
        long timeDifferenceInHours = (predictedReleaseTime.getTimeInMillis() - shuttle.getLandingTime().getTimeInMillis()) / (60 * 60 * 1000);
        delay += (int) timeDifferenceInHours;

        databaseConnection.updatePredictedReleaseTime(shuttleID, delay);
        logger.info("Predicted release time updated for shuttleID={} with delay={}", shuttleID, delay);
    }
}
