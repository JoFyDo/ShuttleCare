package com.rocketshipcheckingtool.ui.technician;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class ClientRequests {
    private URL url;
    private HttpURLConnection con;

    public ClientRequests() throws IOException {
        url = new URL("http://localhost:2104");
    }

    public String request(String path, String user) throws URISyntaxException, IOException {
        url = url.toURI().resolve(path).toURL();
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User", user);
        if (con.getResponseCode() != 200) {
            throw new IOException(con.getResponseMessage());
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "UTF-8")
        );

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append("\n");
        }
        in.close();
        con.disconnect();

        return response.toString();
    }
}
