package com.rocketshipcheckingtool.server;

import com.rocketshipcheckingtool.domain.Notification;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
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
        //Util.updateDatabase(databaseConnection);
        var request = exchange.getRequestURI();
        Map<String, List<String>> headers = exchange.getRequestHeaders();

        switch (request.getPath()) {
            case "/requestShuttles":
                if(headers.get("User").get(0).equals("technician")) {
                    ArrayList<Shuttle> shuttles = databaseConnection.getShuttles();
                    sendResponse(exchange, 200, Util.combineJSONString(shuttles));
                }
                break;
            case "/requestShuttle":
                if(headers.get("User").get(0).equals("technician")) {
                    Shuttle shuttle = databaseConnection.getShuttle(Integer.valueOf(headers.get("Shuttle").get(0)));
                    sendResponse(exchange, 200, shuttle.toJson());
                }
                break;
            case "/requestActiveTasks":
                if(headers.get("User").get(0).equals("technician")) {
                    ArrayList<Task> tasks = databaseConnection.getActiveTasks();
                    sendResponse(exchange, 200, Util.combineJSONString(tasks));
                }
                break;
            case "/requestActiveTaskForShuttle":
                if(headers.get("User").get(0).equals("technician")) {
                    ArrayList<Task> tasks = databaseConnection.getActiveTaskByShuttleID(Integer.valueOf(headers.get("Shuttle").get(0)));
                    sendResponse(exchange, 200, Util.combineJSONString(tasks));
                }
                break;
            case "/updateTask":
                if(headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, String.valueOf(databaseConnection.updateTask(Integer.valueOf(headers.get("TaskID").get(0)),headers.get("Status").get(0))));
                }
                break;
            case "/createTask":
                if(headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, String.valueOf(databaseConnection.createTask(headers.get("Mechanic").get(0), headers.get("Description").get(0), headers.get("ShuttleID").get(0))));
                }
                break;
            case "/requestGeneralTasksForShuttle":
                if(headers.get("User").get(0).equals("technician")) {
                    ArrayList<Task> tasks = databaseConnection.getGeneralTasksForShuttle(Integer.parseInt(headers.get("ShuttleID").get(0)));
                    sendResponse(exchange, 200, Util.combineJSONString(tasks));
                }
                break;
            case "/updateGeneralTasksForShuttle":
                if(headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, String.valueOf(databaseConnection.updateGeneralTask(Integer.parseInt(headers.get("TaskID").get(0)), headers.get("Status").get(0))));
                }
                break;
            case "/updateShuttleStatus":
                if(headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, String.valueOf(databaseConnection.updateShuttleStatus(Integer.valueOf(headers.get("ShuttleID").get(0)), headers.get("Status").get(0))));
                    Util.checkCurrentStatus(databaseConnection, Integer.valueOf(headers.get("ShuttleID").get(0)), headers.get("Status").get(0));
                }
                break;
            case "/updateAllTasksBelongToShuttle":
                if (headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, String.valueOf(databaseConnection.updateAllTasksActivityBelongToShuttle(Integer.valueOf(headers.get("ShuttleID").get(0)), headers.get("Status").get(0))));
                }
                break;
            case "/requestParts":
                if (headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, Util.combineJSONString(databaseConnection.getParts()));
                }
                break;
            case "/updatePartQuantity":
                if (headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, String.valueOf(databaseConnection.updatePartQuantity(Integer.valueOf(headers.get("PartID").get(0)), Integer.valueOf(headers.get("Quantity").get(0)))));
                }
                break;
            case "/requestNotifications":
                if (headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, Util.combineJSONString(databaseConnection.getNotifications()));
                }
                break;
            case "/updateNotification":
                if (headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, String.valueOf(databaseConnection.updateNotification(Integer.valueOf(headers.get("NotificationID").get(0)), headers.get("Status").get(0))));
                }
                break;
            case "/requestNotificationsByShuttle":
                if (headers.get("User").get(0).equals("technician")) {
                    ArrayList<Notification> notifications = databaseConnection.getNotificationsByShuttle(headers.get("ShuttleID").get(0));
                    sendResponse(exchange, 200, Util.combineJSONString(notifications));
                }
                break;
            case "/requestMechanics":
                if (headers.get("User").get(0).equals("technician")) {
                    sendResponse(exchange, 200, Util.combineJSONString(databaseConnection.getMechanics()));
                }
                break;
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
