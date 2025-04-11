package com.rocketshipcheckingtool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServerTest {
    public static void main(String[] args) {
        try {
            // URL to call (change this to your API or server)
            URL url = new URL("http://localhost:2104/requestShuttleOverview");

            // Open connection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Set headers if needed
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("User", "technician");

            // Read response
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream())
            );

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            in.close();

            // Output
            System.out.println("Response Body:");
            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
