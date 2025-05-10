package com.rocketshipcheckingtool.ui.roles.technician;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class ClientRequests {
    private final String baseUrl;
    private final HttpClient client;
    private final Gson gson;

    public ClientRequests() {
        this.baseUrl = "http://localhost:2104";
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public String postRequest(String path, String user, Map<String, String> parameters)
            throws URISyntaxException, IOException, InterruptedException {
        // Convert to JSON
        String jsonBody = "{}"; // Default to empty JSON object
        if (parameters != null) {
            jsonBody = gson.toJson(parameters);
        }

        // Build the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .header("User", user)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Server responded with: " + response.statusCode());
        }

        return response.body();
    }

    public String getRequest(String path, String user, Map<String, String> parameters)
            throws URISyntaxException, IOException, InterruptedException {
        // Build query string from parameters
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

        // Build the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path + queryString))
                .header("User", user)
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Server responded with: " + response.statusCode());
        }

        return response.body();
    }

}