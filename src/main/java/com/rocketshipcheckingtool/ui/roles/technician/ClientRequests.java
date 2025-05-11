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

public class ClientRequests {
    private static final Logger logger = LoggerFactory.getLogger(ClientRequests.class);
    private final String baseUrl;
    private final HttpClient client;
    private final Gson gson;

    public ClientRequests() {
        this.baseUrl = "http://localhost:2104";
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
        logger.debug("ClientRequests initialized with baseUrl={}", baseUrl);
    }

    public String postRequest(String path, String user, Map<String, String> parameters)
            throws URISyntaxException, IOException, InterruptedException {
        String jsonBody = "{}";
        if (parameters != null) {
            jsonBody = gson.toJson(parameters);
        }

        logger.info("POST request to '{}{}' by user '{}', body={}", baseUrl, path, user, jsonBody);

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
            logger.error("POST request failed with status: {}", response.statusCode());
            throw new IOException("Server responded with: " + response.statusCode());
        }

        return response.body();
    }

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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path + queryString))
                .header("User", user)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        logger.debug("GET response status: {}, body: {}", response.statusCode(), response.body());

        if (response.statusCode() != 200) {
            logger.error("GET request failed with status: {}", response.statusCode());
            throw new IOException("Server responded with: " + response.statusCode());
        }

        return response.body();
    }

}
