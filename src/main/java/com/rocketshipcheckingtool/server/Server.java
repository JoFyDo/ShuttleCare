package com.rocketshipcheckingtool.server;

import com.rocketshipcheckingtool.domain.Notification;
import com.rocketshipcheckingtool.domain.Part;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import com.rocketshipcheckingtool.ui.auth.UserRole;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {
    private HttpServer server;
    private DatabaseConnection databaseConnection;
    private final static Logger logger = LoggerFactory.getLogger(Server.class);
    private final int PORT = 2104;
    private final String HOST = "localhost";
    private final String PATH = "/";

    public Server() {
        this.server = initServer();
        databaseConnection = new DatabaseConnection();
    }

    private HttpServer initServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);
            server.createContext(PATH, this::handle);
            server.setExecutor(null);
            return server;
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Map<String, List<String>> headers = exchange.getRequestHeaders();
        String user = headers.containsKey("User") ? headers.get("User").getFirst() : "";

        // Only process requests from technician users
        if (!user.equals(UserRole.MANAGER.name().toLowerCase()) && !user.equals(UserRole.TECHNICIAN.name().toLowerCase())) {
            sendResponse(exchange, 403, "Unauthorized user");
            return;
        }

        try {
            // Parse parameters based on request method
            Map<String, String> parameters;
            if ("POST".equals(requestMethod)) {
                // Read request body and parse JSON
                parameters = Util.parseRequestBody(exchange);
            } else {
                // Parse query parameters from URL
                parameters = Util.parseQueryParameters(exchange.getRequestURI().getQuery());
            }

            switch (path) {
                case "/requestShuttles":
                    if ("GET".equals(requestMethod)) {
                        ArrayList<Shuttle> shuttles = databaseConnection.getShuttles();
                        sendResponse(exchange, 200, Util.combineJSONString(shuttles));
                    }
                    break;

                case "/requestShuttle":
                    if ("GET".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        Shuttle shuttle = databaseConnection.getShuttle(shuttleId);
                        sendResponse(exchange, 200, shuttle.toJson());
                    }
                    break;

                case "/requestActiveTasks":
                    if ("GET".equals(requestMethod)) {
                        ArrayList<Task> tasks = databaseConnection.getActiveTasks();
                        sendResponse(exchange, 200, Util.combineJSONString(tasks));
                    }
                    break;

                case "/requestActiveTaskForShuttle":
                    if ("GET".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        ArrayList<Task> tasks = databaseConnection.getActiveTaskByShuttleID(shuttleId);
                        sendResponse(exchange, 200, Util.combineJSONString(tasks));
                    }
                    break;

                case "/updateTask":
                    if ("POST".equals(requestMethod)) {
                        int taskId = Integer.parseInt(parameters.get("TaskID"));
                        String status = parameters.get("Status");
                        sendResponse(exchange, 200, String.valueOf(databaseConnection.updateTask(taskId, status)));
                    }
                    break;

                case "/createTask":
                    if ("POST".equals(requestMethod)) {
                        String mechanic = parameters.get("Mechanic");
                        String description = parameters.get("Description");
                        String shuttleId = parameters.get("ShuttleID");
                        sendResponse(exchange, 200, String.valueOf(databaseConnection.createTask(mechanic, description, shuttleId)));
                    }
                    break;

                case "/requestGeneralTasksForShuttle":
                    if ("GET".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        ArrayList<Task> tasks = databaseConnection.getGeneralTasksForShuttle(shuttleId);
                        sendResponse(exchange, 200, Util.combineJSONString(tasks));
                    }
                    break;

                case "/updateGeneralTasksForShuttle":
                    if ("POST".equals(requestMethod)) {
                        int taskId = Integer.parseInt(parameters.get("TaskID"));
                        String status = parameters.get("Status");
                        sendResponse(exchange, 200, String.valueOf(databaseConnection.updateGeneralTask(taskId, status)));
                    }
                    break;

                case "/updateShuttleStatus":
                    if ("POST".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        String status = parameters.get("Status");
                        sendResponse(exchange, 200, String.valueOf(databaseConnection.updateShuttleStatus(shuttleId, status)));
                        Util.predictedReleaseTimeUpdate(databaseConnection, shuttleId, status);
                    }
                    break;

                case "/updateAllTasksBelongToShuttle":
                    if ("POST".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        boolean status = Boolean.parseBoolean(parameters.get("Status"));
                        sendResponse(exchange, 200, String.valueOf(databaseConnection.updateAllTasksActivityBelongToShuttle(shuttleId, String.valueOf(status))));
                    }
                    break;

                case "/requestParts":
                    if ("GET".equals(requestMethod)) {
                        sendResponse(exchange, 200, Util.combineJSONString(databaseConnection.getParts()));
                    }
                    break;

                case "/usePart":
                    if ("POST".equals(requestMethod)) {
                        int partId = Integer.parseInt(parameters.get("PartID"));
                        int quantity = Integer.parseInt(parameters.get("Quantity"));
                        Part part = databaseConnection.getPart(partId);

                        sendResponse(exchange, 200, String.valueOf(
                                databaseConnection.updatePartQuantity(partId, part.getQuantity() - quantity)));
                    }
                    break;

                case "/orderPart":
                    if ("POST".equals(requestMethod)) {
                        int partId = Integer.parseInt(parameters.get("PartID"));
                        int quantity = Integer.parseInt(parameters.get("Quantity"));
                        try{
                            int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                            Util.orderPartDelayShuttle(databaseConnection, shuttleId);
                        }catch (Exception e){}
                        Part part = databaseConnection.getPart(partId);
                        sendResponse(exchange, 200, String.valueOf(databaseConnection.updatePartQuantity(partId, part.getQuantity() + quantity)));
                    }
                    break;

                case "/requestNotifications":
                    if ("GET".equals(requestMethod)) {
                        sendResponse(exchange, 200, Util.combineJSONString(databaseConnection.getNotifications()));
                    }
                    break;

                case "/updateNotification":
                    if ("POST".equals(requestMethod)) {
                        int notificationId = Integer.parseInt(parameters.get("NotificationID"));
                        String status = parameters.get("Status");
                        sendResponse(exchange, 200, String.valueOf(databaseConnection.updateNotification(notificationId, status)));
                    }
                    break;

                case "/requestNotificationsByShuttle":
                    if ("GET".equals(requestMethod)) {
                        String shuttleID = parameters.get("ShuttleID");
                        ArrayList<Notification> notifications = databaseConnection.getNotificationsByShuttle(shuttleID);
                        sendResponse(exchange, 200, Util.combineJSONString(notifications));
                    }
                    break;

                case "/requestMechanics":
                    if ("GET".equals(requestMethod)) {
                        sendResponse(exchange, 200, Util.combineJSONString(databaseConnection.getMechanics()));
                    }
                    break;
                case "/updatePredictedReleaseTime":
                    if ("POST".equals(requestMethod)) {
                        int shuttleId = Integer.parseInt(parameters.get("ShuttleID"));
                        String predictedReleaseTime = parameters.get("PredictedReleaseTime");
                        sendResponse(exchange, 200, String.valueOf(databaseConnection.setPredictedReleaseTime(shuttleId, predictedReleaseTime)));
                    }
                    break;

                default:
                    sendResponse(exchange, 404, "Endpoint not found");
                    break;
            }
        } catch (Exception e) {
            logger.error("Error handling request: {}", e.getMessage());
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
        }
    }

    private void sendResponse(HttpExchange exchange, int responseCode, String message) throws IOException {;
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(responseCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush();
        }

        //System.out.println("Response message: " + message);
        //System.out.println("Bytes length: " + message.getBytes(StandardCharsets.UTF_8).length);
    }

    public void stop( ) {
        server.stop( 0 );
        logger.info( "Server shut down" );
    }

    public void start( ) {
        server.start( );
        logger.info( "Server started: {}", server.getAddress( ) );
    }
}


