package com.rocketshipcheckingtool.ui.technician;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ClientRequests {
    private URL url;
    private HttpURLConnection con;

    public ClientRequests() throws IOException {
        url = new URL("http://localhost:2104");
    }

    public String request(String path, String user, String key, String value, String key2, String value2, String key3, String value3) throws URISyntaxException, IOException, InterruptedException {
        url = url.toURI().resolve(path).toURL();
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User", user);
        con.setRequestProperty(key, value);
        con.setRequestProperty(key2, value2);
        con.setRequestProperty(key3, value3);

        int status = con.getResponseCode();
        if (status != 200) {
            throw new IOException("Server responded with: " + status + " - " + con.getResponseMessage());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        }

        con.disconnect();
        return response.toString();
    }

    public String request(String path, String user, String key, String value, String key2, String value2) throws URISyntaxException, IOException, InterruptedException {
        return request(path, user, key, value, key2, value2, "p", "p");
    }

    public String request(String path, String user, String key, String value) throws URISyntaxException, IOException, InterruptedException {
        return request(path, user, key, value, "p", "p");
    }

    public String request(String path, String user) throws URISyntaxException, IOException, InterruptedException {
        return request(path, user, "p", "p");
    }
}
