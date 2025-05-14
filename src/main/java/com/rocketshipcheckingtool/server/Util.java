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

/**
 * Utility class providing helper methods for server-side operations.
 * Includes methods for parsing request bodies, handling query parameters,
 * and updating shuttle-related data in the database.
 */
public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class); // Logger instance for logging.
    private static Gson gson = new Gson(); // Gson instance for JSON parsing and serialization.

    /**
     * Parses the request body of an HTTP exchange into a map of key-value pairs.
     *
     * @param exchange The HttpExchange object containing the request.
     * @return A map containing the parsed key-value pairs from the request body.
     * @throws IOException If the request body is not a valid JSON object or cannot be read.
     */
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
            if (result == null) {
                logger.warn("Parsed request body is null or not a valid JSON object: {}", body);
                throw new IOException("Request body is not a valid JSON object.");
            }
            logger.info("Parsed request body into map with {} entries", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Failed to parse request body", e);
            throw new IOException("Failed to parse request body.");
        }
    }

    /**
     * Parses query parameters from a URL query string into a map of key-value pairs.
     *
     * @param query The query string to parse.
     * @return A map containing the parsed query parameters.
     */
    public static Map<String, String> parseQueryParameters(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) {
            logger.debug("No query parameters found");
            return result;
        }

        try {
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                } else {
                    result.put(entry[0], "");
                }
            }
            logger.info("Parsed query parameters: {}", result.keySet());
        } catch (Exception e) {
            logger.error("Failed to parse query parameters: {}", query, e);
        }
        return result;
    }

    /**
     * Converts a list of objects into a JSON string.
     *
     * @param items The list of objects to convert.
     * @return A JSON string representation of the list.
     */
    public static String combineJSONString(List<?> items) {
        String json = gson.toJson(items);
        logger.debug("Converted list to JSON string: {}", json);
        return json;
    }

    /**
     * Updates the predicted release time of a shuttle based on its status and active tasks.
     *
     * @param databaseConnection The database connection to use for retrieving and updating data.
     * @param shuttleID The ID of the shuttle to update.
     * @param status The current status of the shuttle.
     */
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

    /**
     * Applies a random delay to the predicted release time of a shuttle.
     *
     * @param databaseConnection The database connection to use for retrieving and updating data.
     * @param shuttleID The ID of the shuttle to delay.
     */
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