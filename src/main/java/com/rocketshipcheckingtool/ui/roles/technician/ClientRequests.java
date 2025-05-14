package com.rocketshipcheckingtool.ui.roles.technician;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Class for handling HTTP client requests.
 * Provides methods for sending GET and POST requests to a server.
 */
public class ClientRequests {
    private static final Logger logger = LoggerFactory.getLogger(ClientRequests.class); // Logger instance for logging activities.
    private final String baseUrl; // Base URL for the server.
    private final HttpClient client; // HTTP client for sending requests.
    private final Gson gson; // Gson instance for JSON serialization and deserialization.

    /**
     * Constructor for initializing the ClientRequests class.
     * Sets up the base URL, HTTP client, and Gson instance.
     */
    public ClientRequests() {
        this.baseUrl = "http://localhost:2104";
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
        logger.debug("ClientRequests initialized with baseUrl={}", baseUrl);
    }

    /**
     * Sends a POST request to the specified path with the given parameters.
     *
     * @param path The endpoint path for the POST request.
     * @param user The user making the request.
     * @param parameters A map of parameters to include in the request body.
     * @return The response body as a string.
     * @throws URISyntaxException If the URI is invalid.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    public String postRequest(String path, String user, Map<String, String> parameters)
            throws URISyntaxException, IOException, InterruptedException {
        String jsonBody = "{}";
        if (parameters != null) {
            jsonBody = gson.toJson(parameters);
        }

        logger.info("POST request to '{}{}' by user '{}', body={}", baseUrl, path, user, jsonBody);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Content-Type", "application/json")
                    .header("User", user)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            logger.debug("POST response status: {}, body: {}", response.statusCode(), response.body());

            if (response.statusCode() != 200) {
                logger.error("POST request to '{}{}' failed with status: {} and body: {}", baseUrl, path, response.statusCode(), response.body());
                throw new IOException("Server responded with: " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException | RuntimeException e) {
            logger.error("Exception during POST request to '{}{}': {}", baseUrl, path, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Sends a GET request to the specified path with the given parameters.
     *
     * @param path The endpoint path for the GET request.
     * @param user The user making the request.
     * @param parameters A map of parameters to include in the query string.
     * @return The response body as a string.
     * @throws URISyntaxException If the URI is invalid.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    public String getRequest(String path, String user, Map<String, String> parameters)
            throws URISyntaxException, IOException, InterruptedException {
        StringBuilder queryString = new StringBuilder();
        boolean first = true;
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (first) {
                    queryString.append("?");
                    first = false;
                } else {
                    queryString.append("&");
                }
                queryString.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }

        logger.info("GET request to '{}{}{}' by user '{}'", baseUrl, path, queryString, user);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path + queryString))
                    .header("User", user)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            logger.debug("GET response status: {}, body: {}", response.statusCode(), response.body());

            if (response.statusCode() != 200) {
                logger.error("GET request to '{}{}{}' failed with status: {} and body: {}", baseUrl, path, queryString, response.statusCode(), response.body());
                throw new IOException("Server responded with: " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException | RuntimeException e) {
            logger.error("Exception during GET request to '{}{}{}': {}", baseUrl, path, queryString, e.getMessage(), e);
            throw e;
        }
    }
}