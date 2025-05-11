package com.rocketshipcheckingtool.server;

import com.rocketshipcheckingtool.server.datamodel.*;
import com.rocketshipcheckingtool.server.database.DatabaseFacade;
import com.rocketshipcheckingtool.ui.auth.UserRole;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;

public class Server {
    private final HttpServer server;
    private final DatabaseFacade databaseConnection;
    private final static Logger logger = LoggerFactory.getLogger(Server.class);
    private final int PORT = 2104;
    private final String HOST = "localhost";

    public Server() {
        logger.info("Initializing server on {}:{}", HOST, PORT);
        this.server = initServer();
        databaseConnection = new DatabaseFacade();
    }

    private HttpServer initServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);
            String PATH = "/";
            server.createContext(PATH, this::handle);
            server.setExecutor(Executors.newFixedThreadPool(10));
            return server;
        } catch (Exception e) {
            logger.error("Failed to initialize server", e);
            throw new RuntimeException("Server initialization failed", e);
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        logger.debug("Request with ID {} received", requestId);

        long startTime = System.currentTimeMillis();
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Map<String, List<String>> headers = exchange.getRequestHeaders();
        String user = headers.containsKey("User") ? headers.get("User").getFirst() : "";

        logger.info("Request {} started: {} {} from user {}", requestId, requestMethod, path, user);
        try {
            // Authenticate user
            logger.debug("Authenticating user: {}", user);
            if (!user.equals(UserRole.MANAGER.name().toLowerCase()) && !user.equals(UserRole.TECHNICIAN.name().toLowerCase())) {
                logger.warn("Unauthorized access attempt from user: {}", user);
                sendResponse(exchange, 403, "Unauthorized user");
                return;
            }
            logger.debug("User {} authenticated successfully", user);

            // Parse parameters based on request method
            Map<String, String> parameters;
            try {
                if ("POST".equals(requestMethod)) {
                    parameters = Util.parseRequestBody(exchange);
                } else {
                    parameters = Util.parseQueryParameters(exchange.getRequestURI().getQuery());
                }
            } catch (Exception e) {
                logger.error("Failed to parse parameters: {}", e.getMessage());
                sendResponse(exchange, 400, "Invalid request parameters: " + e.getMessage());
                return;
            }
            // Process based on path and method
            logger.debug("Processing request: {} {} with parameters: {}", requestMethod, path, parameters);
            processRequest(exchange, path, requestMethod, parameters, user);

        } catch (Exception e) {
            logger.error("Error handling request: {}", e.getMessage(), e);
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Request {} completed in {}ms", requestId, duration);
            MDC.remove("requestId");
        }
    }

    private void processRequest(HttpExchange exchange, String path, String requestMethod,
                                Map<String, String> parameters, String user) throws IOException {
        logger.debug("Processing {} request for endpoint {} with parameters: {}", requestMethod, path, parameters);
        try {
            switch (path) {
                case "/requestShuttles":
                    if ("GET".equals(requestMethod)) {
                        logger.debug("Retrieving all shuttles from database");
                        ArrayList<Shuttle> shuttles = databaseConnection.getShuttles();
                        logger.debug("Retrieved {} shuttles", shuttles.size());
                        sendResponse(exchange, 200, Util.combineJSONString(shuttles));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestShuttle":
                    if ("GET".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        logger.debug("Retrieving shuttle with ID: {}", shuttleId);
                        Shuttle shuttle = databaseConnection.getShuttle(shuttleId);
                        logger.debug("Retrieved shuttle: {}", shuttle.getShuttleName());
                        sendResponse(exchange, 200, shuttle.toJson());
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestActiveTasks":
                    if ("GET".equals(requestMethod)) {
                        logger.debug("Retrieving all active tasks from database");
                        ArrayList<Task> tasks = databaseConnection.getActiveTasks();
                        logger.debug("Retrieved {} active tasks", tasks.size());
                        sendResponse(exchange, 200, Util.combineJSONString(tasks));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestActiveTaskForShuttle":
                    if ("GET".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        logger.debug("Retrieving active tasks for shuttle ID: {}", shuttleId);
                        ArrayList<Task> tasks = databaseConnection.getActiveTaskByShuttleID(shuttleId);
                        logger.debug("Retrieved {} active tasks for shuttle {}", tasks.size(), shuttleId);
                        sendResponse(exchange, 200, Util.combineJSONString(tasks));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/updateTask":
                    if ("POST".equals(requestMethod)) {
                        int taskId = Integer.parseInt(parameters.get("TaskID"));
                        String status = parameters.get("Status");
                        logger.debug("Updating task ID: {} to status: {}", taskId, status);
                        boolean success = databaseConnection.updateTask(taskId, status);
                        logger.info("Task {} update {} to status {}", taskId, success ? "successful" : "failed", status);
                        sendResponse(exchange, 200, String.valueOf(success));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/createTask":
                    if ("POST".equals(requestMethod)) {
                        String mechanic = parameters.get("Mechanic");
                        String description = parameters.get("Description");
                        String shuttleId = parameters.get("ShuttleID");
                        logger.debug("Creating new task for shuttle {}, assigned to {}", shuttleId, mechanic);
                        boolean success = databaseConnection.createTask(mechanic, description, shuttleId);
                        logger.info("Task creation for shuttle {} {}. Mechanic: {}", shuttleId, success ? "successful" : "failed", mechanic);
                        sendResponse(exchange, 200, String.valueOf(success));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestGeneralTasksForShuttle":
                    if ("GET".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        logger.debug("Retrieving general tasks for shuttle ID: {}", shuttleId);
                        ArrayList<Task> tasks = databaseConnection.getGeneralTasksForShuttle(shuttleId);
                        logger.debug("Retrieved {} general tasks for shuttle {}", tasks.size(), shuttleId);
                        sendResponse(exchange, 200, Util.combineJSONString(tasks));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/updateGeneralTasksForShuttle":
                    if ("POST".equals(requestMethod)) {
                        int taskId = Integer.parseInt(parameters.get("TaskID"));
                        String status = parameters.get("Status");
                        logger.debug("Updating general task ID: {} to status: {}", taskId, status);
                        boolean success = databaseConnection.updateGeneralTask(taskId, status);
                        logger.info("General task {} update {} to status {}", taskId, success ? "successful" : "failed", status);
                        sendResponse(exchange, 200, String.valueOf(success));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/updateShuttleStatus":
                    if ("POST".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        String status = parameters.get("Status");
                        logger.debug("Updating shuttle {} status to: {}", shuttleId, status);
                        boolean success = databaseConnection.updateShuttleStatus(shuttleId, status);
                        logger.info("Shuttle {} status update to {} {}", shuttleId, status, success ? "successful" : "failed");
                        Util.predictedReleaseTimeUpdate(databaseConnection, shuttleId, status);
                        sendResponse(exchange, 200, String.valueOf(success));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;

                case "/updateAllTasksBelongToShuttle":
                    if ("POST".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        boolean status = Boolean.parseBoolean(parameters.get("Status"));
                        logger.debug("Updating all tasks for shuttle {} to activity status: {}", shuttleId, status);
                        boolean result = databaseConnection.updateAllTasksActivityBelongToShuttle(shuttleId, String.valueOf(status));
                        logger.info("Update of all tasks for shuttle {} to status {} {}", shuttleId, status, result ? "successful" : "failed");
                        sendResponse(exchange, 200, String.valueOf(result));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;

                case "/requestParts":
                    if ("GET".equals(requestMethod)) {
                        sendResponse(exchange, 200, Util.combineJSONString(databaseConnection.getParts()));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;

                case "/usePart":
                    if ("POST".equals(requestMethod)) {
                        int partId = Integer.parseInt(parameters.get("PartID"));
                        int quantity = Integer.parseInt(parameters.get("Quantity"));
                        Part part = databaseConnection.getPart(partId);
                        sendResponse(exchange, 200, String.valueOf(
                                databaseConnection.updatePartQuantity(partId, part.getQuantity() - quantity)));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;

                case "/orderPart":
                    if ("POST".equals(requestMethod)) {
                        int partId = Integer.parseInt(parameters.get("PartID"));
                        int quantity = Integer.parseInt(parameters.get("Quantity"));
                        try {
                            if (parameters.containsKey("ShuttleID")) {
                                int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                                Util.orderPartDelayShuttle(databaseConnection, shuttleId);
                            }
                        } catch (Exception e) {
                            logger.warn("Failed to process shuttle delay: {}", e.getMessage());
                        }
                        Part part = databaseConnection.getPart(partId);
                        sendResponse(exchange, 200, String.valueOf(
                                databaseConnection.updatePartQuantity(partId, part.getQuantity() + quantity)));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestNotifications":
                    if ("GET".equals(requestMethod)) {
                        logger.debug("Retrieving notifications for user: {}", user);
                        ArrayList<Notification> notifications = databaseConnection.getNotifications(user);
                        logger.debug("Retrieved {} notifications for user {}", notifications.size(), user);
                        sendResponse(exchange, 200, Util.combineJSONString(notifications));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/updateNotification":
                    if ("POST".equals(requestMethod)) {
                        int notificationId = Integer.parseInt(parameters.get("NotificationID"));
                        String status = parameters.get("Status");
                        logger.debug("Updating notification ID: {} to status: {}", notificationId, status);
                        boolean success = databaseConnection.updateNotification(notificationId, status);
                        logger.info("Notification {} update {} to status {}", notificationId, success ? "successful" : "failed", status);
                        sendResponse(exchange, 200, String.valueOf(success));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestNotificationsByShuttle":
                    if ("GET".equals(requestMethod)) {
                        String shuttleID = parameters.get("ShuttleID");
                        logger.debug("Retrieving notifications for shuttle ID: {} and user: {}", shuttleID, user);
                        ArrayList<Notification> notifications =
                                databaseConnection.getNotificationsByShuttle(shuttleID, user);
                        logger.debug("Retrieved {} notifications for shuttle {} and user {}", notifications.size(), shuttleID, user);
                        sendResponse(exchange, 200, Util.combineJSONString(notifications));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestMechanics":
                    if ("GET".equals(requestMethod)) {
                        logger.debug("Retrieving all mechanics from database");
                        ArrayList<Mechanic> mechanics = databaseConnection.getMechanics();
                        logger.debug("Retrieved {} mechanics", mechanics.size());
                        sendResponse(exchange, 200, Util.combineJSONString(mechanics));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/updatePredictedReleaseTime":
                    if ("POST".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        String predictedReleaseTime = parameters.get("PredictedReleaseTime");
                        logger.debug("Updating predicted release time for shuttle ID: {} to: {}", shuttleId, predictedReleaseTime);
                        boolean success = databaseConnection.setPredictedReleaseTime(shuttleId, predictedReleaseTime);
                        logger.info("Predicted release time update for shuttle {} {} to {}",
                                shuttleId, success ? "successful" : "failed", predictedReleaseTime);
                        sendResponse(exchange, 200, String.valueOf(success));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestQuestionnaires":
                    if ("GET".equals(requestMethod)) {
                        logger.debug("Retrieving all questionnaires from database");
                        ArrayList<QuestionnaireRating> questionnaires = databaseConnection.getQuestionnaires();
                        logger.debug("Retrieved {} questionnaires", questionnaires.size());
                        sendResponse(exchange, 200, Util.combineJSONString(questionnaires));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestQuestionnaireForShuttle":
                    if ("GET".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        logger.debug("Retrieving questionnaire for shuttle ID: {}", shuttleId);
                        ArrayList<QuestionnaireRating> questionnaires = databaseConnection.getQuestionnaireForShuttle(shuttleId);
                        logger.debug("Retrieved {} questionnaire items for shuttle {}", questionnaires.size(), shuttleId);
                        sendResponse(exchange, 200, Util.combineJSONString(questionnaires));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/updateQuestionnaire":
                    if ("POST".equals(requestMethod)) {
                        int questionnaireId = Integer.parseInt(parameters.get("QuestionnaireID"));
                        String status = parameters.get("Status");
                        logger.debug("Updating questionnaire ID: {} to status: {}", questionnaireId, status);
                        boolean success = databaseConnection.updateQuestionnaireRating(questionnaireId, status);
                        logger.info("Questionnaire {} update {} to status {}", questionnaireId, success ? "successful" : "failed", status);
                        sendResponse(exchange, 200, String.valueOf(success));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/requestCommentsForShuttle":
                    if ("GET".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        logger.debug("Retrieving comments for shuttle ID: {}", shuttleId);
                        ArrayList<Comment> comments = databaseConnection.getCommentsByShuttle(shuttleId);
                        logger.debug("Retrieved {} comments for shuttle {}", comments.size(), shuttleId);
                        sendResponse(exchange, 200, Util.combineJSONString(comments));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/updateComment":
                    if ("POST".equals(requestMethod)) {
                        int commentId = Integer.parseInt(parameters.get("CommentID"));
                        String status = parameters.get("Status");
                        logger.debug("Updating comment ID: {} to status: {}", commentId, status);
                        boolean success = databaseConnection.updateComment(commentId, status);
                        logger.info("Comment {} update {} to status {}", commentId, success ? "successful" : "failed", status);
                        sendResponse(exchange, 200, String.valueOf(success));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/createNotification":
                    if ("POST".equals(requestMethod)) {
                        String message = parameters.get("Message");
                        String shuttleId = parameters.get("ShuttleID");
                        String sender = parameters.get("Sender");
                        String comment = parameters.getOrDefault("Comment", "");
                        logger.debug("Creating notification for shuttle {}, from sender {}", shuttleId, sender);
                        boolean success = databaseConnection.createNotification(message, shuttleId, sender, comment);
                        logger.info("Notification creation for shuttle {} {}. Sender: {}", shuttleId, success ? "successful" : "failed", sender);
                        sendResponse(exchange, 200, String.valueOf(success));
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/allCommandsDone":
                    if ("POST".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        logger.debug("Checking if all commands are done for shuttle ID: {}", shuttleId);
                        ArrayList<Comment> comments = databaseConnection.getCommentsByShuttle(shuttleId);
                        boolean allDone = comments.isEmpty();
                        logger.debug("All commands done status for shuttle {}: {}", shuttleId, allDone);
                        sendResponse(exchange, 200, allDone ? "true" : "false");
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                default:
                    logger.warn("Endpoint not found: {}", path);
                    sendResponse(exchange, 404, "Endpoint not found");
                    break;
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid number format in parameters: {}", e.getMessage());
            sendResponse(exchange, 400, "Invalid parameter format: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
        }
    }

    private void sendResponse(HttpExchange exchange, int responseCode, String message) throws IOException {
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
        exchange.sendResponseHeaders(responseCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush();
        }

        // Only log responses for errors or if debug is enabled
        if (responseCode >= 400 || logger.isDebugEnabled()) {
            logger.debug("Response sent: {} with status {}",
                message.length() > 100 ? message.substring(0, 100) + "..." : message,
                responseCode);
        }
    }

    public void stop() {
        logger.info("Stopping server on {}:{}...", HOST, PORT);
        server.stop(0);
        logger.info("Server shut down successfully");
    }

    public void start() {
        logger.info("Starting server on {}:{}", HOST, PORT);
        server.start();
        logger.info("Server started successfully at address: {}", server.getAddress());
        logger.info("Server ready to accept connections");
    }

    // Helper method to check for missing parameters
// Helper method to check for missing parameters
    private void validateParameter(Map<String, String> parameters, String paramName) throws IOException {
        logger.debug("Validating parameter: {}", paramName);
        if (!parameters.containsKey(paramName) || parameters.get(paramName) == null) {
            logger.warn("Missing required parameter: {}", paramName);
            throw new IOException("Missing required parameter: " + paramName);
        }
    }

    // Helper method to check multiple parameters
    private void validateParameters(Map<String, String> parameters, String... paramNames) throws IOException {
        for (String paramName : paramNames) {
            validateParameter(parameters, paramName);
        }
    }
}