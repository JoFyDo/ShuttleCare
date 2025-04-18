package com.rocketshipcheckingtool.server;

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
        Util.updateDatabase(databaseConnection);
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
