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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Main server class for handling HTTP requests and managing the application's backend logic.
 * Initializes the server, processes incoming requests, and interacts with the database.
 */
public class Server {
    private final HttpServer server; // The HTTP server instance.
    private final DatabaseFacade databaseConnection; // Database connection for handling data operations.
    private final static Logger logger = LoggerFactory.getLogger(Server.class); // Logger for logging server events.
    private final int PORT = 2104; // Port number for the server.
    private final String HOST = "localhost"; // Host address for the server.

    /**
     * Constructor for the Server class.
     * Initializes the server and database connection.
     */
    public Server() {
        logger.info("Initializing server on {}:{}", HOST, PORT);
        this.server = initServer();
        databaseConnection = new DatabaseFacade();
    }

    public Server(String databasePath) {
        logger.info("Initializing server on {}:{}", HOST, PORT);
        this.server = initServer();
        databaseConnection = new DatabaseFacade(databasePath);

    }

    /**
     * Initializes the HTTP server.
     *
     * @return The initialized HttpServer instance.
     * @throws RuntimeException If the server fails to initialize.
     */
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

    /**
     * Handles incoming HTTP requests.
     *
     * @param exchange The HttpExchange object containing the request and response.
     * @throws IOException If an error occurs while processing the request.
     */
    public void handle(HttpExchange exchange) throws IOException {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        logger.debug("Request with ID {} received", requestId);

        long startTime = System.currentTimeMillis();
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Map<String, List<String>> headers = exchange.getRequestHeaders();
        String user = headers.containsKey("User") ? headers.get("User").get(0) : "";

        logger.info("Request {} started: {} {} from user {}", requestId, requestMethod, path, user);
        try {
            // Authenticate user
            logger.debug("Authenticating user: {}", user);
            if (!user.equals(UserRole.MANAGER.name().toLowerCase()) && !user.equals(UserRole.TECHNICIAN.name().toLowerCase())
                    && !user.equals(UserRole.BOOKING_AGENT.name().toLowerCase())) {
                logger.warn("Unauthorized access attempt from user: {}", user);
                sendResponse(exchange, 403, "Unauthorized user");
                return;
            }
            logger.debug("User {} authenticated successfully", user);

            // Parse parameters based on the request method
            Map<String, String> parameters;
            try {
                if ("POST".equals(requestMethod)) {
                    parameters = Util.parseRequestBody(exchange);
                } else {
                    parameters = Util.parseQueryParameters(exchange.getRequestURI().getQuery());
                }
            } catch (Exception e) {
                logger.error("Failed to parse parameters", e);
                sendResponse(exchange, 400, "Invalid request parameters.");
                return;
            }
            // Process based on path and method
            logger.debug("Processing request: {} {} with parameters: {}", requestMethod, path, parameters);
            processRequest(exchange, path, requestMethod, parameters, user);

        } catch (Exception e) {
            logger.error("Error handling request", e);
            sendResponse(exchange, 500, "Internal server error.");
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Request {} completed in {}ms", requestId, duration);
            MDC.remove("requestId");
        }
    }

    /**
     * Processes the request based on the path, method, and parameters.
     *
     * @param exchange      The HttpExchange object containing the request and response.
     * @param path          The request path.
     * @param requestMethod The HTTP method of the request.
     * @param parameters    The parsed parameters from the request.
     * @param user          The authenticated user making the request.
     * @throws IOException If an error occurs while processing the request.
     */
    private void processRequest(HttpExchange exchange, String path, String requestMethod,
                                Map<String, String> parameters, String user) throws IOException {
        logger.debug("Processing {} request for endpoint {} with parameters: {}", requestMethod, path, parameters);
        try {
            if (path.startsWith("/api") && user.equals(UserRole.BOOKING_AGENT.name().toLowerCase())) {
                switch (path) {
                    case "/api/requestShuttles":
                        if ("GET".equals(requestMethod)) {
                            ArrayList<Shuttle> shuttles = databaseConnection.getShuttles();
                            ArrayList<Map<String, Object>> simplifiedShuttles = new ArrayList<>();
                            for (Shuttle shuttle : shuttles) {
                                Map<String, Object> shuttleMap = new HashMap<>();
                                shuttleMap.put("id", shuttle.getId());
                                shuttleMap.put("shuttleName", shuttle.getShuttleName());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Calendar releaseTime = databaseConnection.getPredictedReleaseTime(shuttle.getId());
                                shuttleMap.put("predictedReleaseTime", releaseTime != null ? sdf.format(releaseTime.getTime()) : "Not available");
                                shuttleMap.put("predictedLandingTime", shuttle.getPredictedLandingTime());
                                simplifiedShuttles.add(shuttleMap);
                            }
                            logger.debug("Retrieved {} shuttles with release time", shuttles.size());
                            sendResponse(exchange, 200, Util.combineJSONString(simplifiedShuttles));
                        } else {
                            sendResponse(exchange, 405, "Method not allowed");
                        }
                        break;
                    case "/api/requestShuttle":
                        if ("GET".equals(requestMethod)) {
                            int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                            Shuttle shuttle = databaseConnection.getShuttle(shuttleId);
                            Map<String, Object> shuttleMap = new HashMap<>();
                            shuttleMap.put("id", shuttle.getId());
                            shuttleMap.put("shuttleName", shuttle.getShuttleName());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Calendar releaseTime = databaseConnection.getPredictedReleaseTime(shuttle.getId());
                            shuttleMap.put("predictedReleaseTime", releaseTime != null ? sdf.format(releaseTime.getTime()) : "Not available");
                            shuttleMap.put("predictedLandingTime", shuttle.getPredictedLandingTime());
                            sendResponse(exchange, 200, new com.google.gson.Gson().toJson(shuttleMap));
                        } else {
                            sendResponse(exchange, 405, "Method not allowed");
                        }
                        break;
                    case "/api/requestReadyShuttles":
                        if ("GET".equals(requestMethod)) {
                            ArrayList<Shuttle> shuttles = databaseConnection.getShuttles();
                            ArrayList<Map<String, Object>> simplifiedShuttles = new ArrayList<>();
                            for (Shuttle shuttle : shuttles) {
                                if (shuttle.getStatus().equals("Freigegeben")) {
                                    Map<String, Object> shuttleMap = new HashMap<>();
                                    shuttleMap.put("id", shuttle.getId());
                                    shuttleMap.put("shuttleName", shuttle.getShuttleName());
                                    simplifiedShuttles.add(shuttleMap);
                                }
                            }
                            logger.debug("Retrieved {} ready shuttles with release time", shuttles.size());
                            sendResponse(exchange, 200, Util.combineJSONString(simplifiedShuttles));
                        } else {
                            sendResponse(exchange, 405, "Method not allowed");
                        }
                        break;
                }
            }
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
                        try {
                            int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                            logger.debug("Retrieving shuttle with ID: {}", shuttleId);
                            Shuttle shuttle = databaseConnection.getShuttle(shuttleId);
                            logger.debug("Retrieved shuttle: {}", shuttle.getShuttleName());
                            sendResponse(exchange, 200, shuttle.toJson());
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid ShuttleID format: {}", parameters.get("ShuttleID"));
                            sendResponse(exchange, 400, "Invalid ShuttleID format.");
                        } catch (Exception e) {
                            logger.error("Failed to retrieve shuttle", e);
                            sendResponse(exchange, 500, "Failed to retrieve shuttle.");
                        }
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
                        try {
                            int taskId = Integer.parseInt(parameters.get("TaskID"));
                            String status = parameters.get("Status");
                            logger.debug("Updating task ID: {} to status: {}", taskId, status);
                            boolean success = databaseConnection.updateTask(taskId, status);
                            logger.info("Task {} update {} to status {}", taskId, success ? "successful" : "failed", status);
                            sendResponse(exchange, 200, String.valueOf(success));
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid TaskID format: {}", parameters.get("TaskID"));
                            sendResponse(exchange, 400, "Invalid TaskID format.");
                        } catch (Exception e) {
                            logger.error("Failed to update task", e);
                            sendResponse(exchange, 500, "Failed to update task.");
                        }
                    } else {
                        sendResponse(exchange, 405, "Method not allowed");
                    }
                    break;
                case "/createTask":
                    if ("POST".equals(requestMethod)) {
                        String mechanicId = parameters.get("MechanicID");
                        String description = parameters.get("Description");
                        String shuttleId = parameters.get("ShuttleID");
                        logger.debug("Creating new task for shuttle {}, assigned to {}", shuttleId, mechanicId);
                        boolean success = databaseConnection.createTask(mechanicId, description, shuttleId);
                        logger.info("Task creation for shuttle {} {}. Mechanic: {}", shuttleId, success ? "successful" : "failed", mechanicId);
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
                case "/updateAllGeneralTasksBelongToShuttle":
                    if ("POST".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        String status = parameters.get("Status");
                        logger.debug("Updating all general tasks for shuttle {} to status: {}", shuttleId, status);
                        boolean result = databaseConnection.updateAllGeneralTasksStatusBelongToShuttle(shuttleId, status);
                        logger.info("Update of all general tasks for shuttle {} to status {} {}", shuttleId, status, result ? "successful" : "failed");
                        sendResponse(exchange, 200, String.valueOf(result));
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
                case "/requestMechanic":
                    if ("GET".equals(requestMethod)) {
                        int mechanicId = Integer.parseInt(parameters.get("MechanicID"));
                        logger.debug("Retrieving mechanic with ID: {}", mechanicId);
                        Mechanic mechanic = databaseConnection.getMechanic(mechanicId);
                        logger.debug("Retrieved mechanic: {}", mechanic.getName());
                        sendResponse(exchange, 200, mechanic.toJson());
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
        } catch (Exception e) {
            logger.error("Error processing request", e);
            sendResponse(exchange, 500, "Internal server error.");
        }
    }

    /**
     * Sends a response to the client.
     *
     * @param exchange     The HttpExchange object containing the response.
     * @param responseCode The HTTP status code of the response.
     * @param message      The response message.
     * @throws IOException If an error occurs while sending the response.
     */
    private void sendResponse(HttpExchange exchange, int responseCode, String message) throws IOException {
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
        exchange.sendResponseHeaders(responseCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush();
        } catch (IOException e) {
            logger.error("Failed to write response to client", e);
            throw e;
        }

        if (responseCode >= 400 || logger.isDebugEnabled()) {
            logger.debug("Response sent: {} with status {}",
                    message.length() > 100 ? message.substring(0, 100) + "..." : message,
                    responseCode);
        }
    }

    /**
     * Stops the server.
     */
    public void stop() throws SQLException {
        logger.info("Stopping server on {}:{}...", HOST, PORT);
        server.stop(0);
        databaseConnection.disconnect();
        logger.info("Server shut down successfully");
    }

    /**
     * Starts the server.
     */
    public void start() {
        logger.info("Starting server on {}:{}", HOST, PORT);
        server.start();
        logger.info("Server started successfully at address: {}", server.getAddress());
        logger.info("Server ready to accept connections");
    }

    /**
     * Validates the presence of a required parameter in the request.
     *
     * @param parameters The map of request parameters.
     * @param paramName  The name of the required parameter.
     * @throws IOException If the parameter is missing or null.
     */
    private void validateParameter(Map<String, String> parameters, String paramName) throws IOException {
        logger.debug("Validating parameter: {}", paramName);
        if (!parameters.containsKey(paramName) || parameters.get(paramName) == null) {
            logger.warn("Missing required parameter: {}", paramName);
            throw new IOException("Missing required parameter: " + paramName);
        }
    }
}