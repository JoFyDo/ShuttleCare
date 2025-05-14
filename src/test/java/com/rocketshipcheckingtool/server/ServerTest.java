package com.rocketshipcheckingtool.server;

import com.rocketshipcheckingtool.test.TestDatabaseSetup;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static org.junit.Assert.*;

public class ServerTest {
    private static final Logger logger = LoggerFactory.getLogger(ServerTest.class);
    private static final String TEST_DB_PATH = "src/test/resources/testdatabase.db";
    @ClassRule
    public static TestDatabaseSetup dbSetup = new TestDatabaseSetup(TEST_DB_PATH);
    HttpURLConnection con;

    private Server server;
    private int port;

    @Before
    public void setUp() throws Exception {
        port = 2104; // random port to avoid conflicts
        server = new Server("jdbc:sqlite:" + TEST_DB_PATH);
        server.start();
        // Optionally, wait a moment for the server to be ready
        Thread.sleep(500);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        Thread.sleep(200);
        System.gc();
    }

    private String httpGet(String path) throws Exception {
        URL url = new URL("http://localhost:" + port + path);
        con = (HttpURLConnection) url.openConnection();
        if (path.contains("api")){
            con.setRequestProperty("User", "booking_agent");
        } else {
            con.setRequestProperty("User", "technician");
        }
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        assertEquals(200, status);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            content.append(line);
        }
        in.close();
        con.disconnect();
        return content.toString();
    }

    private String httpPost(String path, String body) throws Exception {
        URL url = new URL("http://localhost:" + port + path);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("User", "technician");
        con.getOutputStream().write(body.getBytes());
        int status = con.getResponseCode();
        assertEquals(200, status);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            content.append(line);
        }
        in.close();
        con.disconnect();
        return content.toString();
    }

    @Test
    public void testGetShuttles() throws Exception {
        String response = httpGet("/requestShuttles");
        assertTrue(response.contains("Orion Drift-Shuttle"));
    }

    @Test
    public void testGetShuttleById() throws Exception {
        String response = httpGet("/requestShuttle?ShuttleID=0");
        assertTrue(response.contains("Orion Drift-Shuttle"));
    }

    @Test
    public void testGetTasks() throws Exception {
        String response = httpGet("/requestActiveTasks");
        assertTrue(response.contains("Bildschirme ersetzen"));
    }

    @Test
    public void testGetTaskById() throws Exception {
        String response = httpGet("/requestActiveTasks");
        assertTrue(response.contains("Sauerstofftanks ersetzen"));
    }

    @Test
    public void testGetParts() throws Exception {
        String response = httpGet("/requestParts");
        assertTrue(response.contains("Hochleistungs-Batterie"));
    }

    @Test
    public void testGetPartById() throws Exception {
        String response = httpGet("/requestParts");
        assertTrue(response.contains("Hochleistungs-Batterie"));
    }

    @Test
    public void testGetMechanics() throws Exception {
        String response = httpGet("/requestMechanics");
        assertTrue(response.contains("Andi"));
    }

    @Test
    public void testGetMechanicById() throws Exception {
        String response = httpGet("/requestMechanic?MechanicID=0");
        assertTrue(response.contains("Andi"));
    }

    @Test
    public void testGetNotifications() throws Exception {
        String response = httpGet("/requestNotifications");
        assertTrue(response.contains("Wiederholte Fehlfunktionen in einem Sensor"));
    }

    @Test
    public void testGetGeneralTasks() throws Exception {
        String response = httpGet("/requestGeneralTasksForShuttle?ShuttleID=0");
        assertTrue(response.contains("Triebwerkscheck"));
    }

    @Test
    public void testGetQuestionnaireRatings() throws Exception {
        String response = httpGet("/requestQuestionnaires");
        assertTrue(response.contains("Sicherheit"));
    }

    @Test
    public void testGetComments() throws Exception {
        String response = httpGet("/requestCommentsForShuttle?ShuttleID=1");
        assertTrue(response.contains("Störung des Betriebsablaufs durch Passagiere"));
    }

    @Test
    public void testApiRequestShuttles() throws Exception {
        String response = httpGet("/api/requestShuttles");
        assertTrue(response.contains("Orion Drift-Shuttle"));
    }

    @Test
    public void testApiRequestShuttleById() throws Exception {
        String response = httpGet("/api/requestShuttle?ShuttleID=0");
        assertTrue(response.contains("Orion Drift-Shuttle"));
    }

    @Test
    public void testApiRequestReadyShuttles() throws Exception {
        String response = httpGet("/api/requestReadyShuttles");
        assertTrue(response.contains("Pioneer Nova"));
    }

    @Test
    public void testRequestActiveTaskForShuttle() throws Exception {
        String response = httpGet("/requestActiveTaskForShuttle?ShuttleID=1");
        assertTrue(response.contains("Bildschirme ersetzen"));
    }

    @Test
    public void testRequestNotificationByShuttle() throws Exception {
        String response = httpGet("/requestNotificationsByShuttle?ShuttleID=2");
        assertTrue(response.contains("Wiederholte Fehlfunktionen in einem Sensor"));
    }

    @Test
    public void testRequestQuestionnaireRatingsByShuttle() throws Exception {
        String response = httpGet("/requestQuestionnaireForShuttle?ShuttleID=2");
        assertTrue(response.contains("Sicherheit"));
    }

    @Test
    public void testUpdateTask() throws Exception {
        String response = httpPost("/updateTask", "{\"TaskID\": 56, \"Status\": false}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testCreateTask() throws Exception {
        String response = httpPost("/createTask", "{\"Description\": \"Test Task\", \"MechanicID\": 0, \"ShuttleID\": 1}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testUpdateGeneralTasksForShuttle() throws Exception {
        String response = httpPost("/updateGeneralTasksForShuttle", "{\"TaskID\": 1, \"Status\": true}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testUpdateAllGeneralTasksBelongToShuttle() throws Exception {
        String response = httpPost("/updateAllGeneralTasksBelongToShuttle", "{\"ShuttleID\": 1, \"Status\": true}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testUpdateShuttleStatus() throws Exception {
        String response = httpPost("/updateShuttleStatus", "{\"ShuttleID\": 1, \"Status\": \"Inspektion 1\"}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testUpdateAllTasksBelongToShuttle() throws Exception {
        String response = httpPost("/updateAllTasksBelongToShuttle", "{\"ShuttleID\": 1, \"Status\": false}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testUsePart() throws Exception {
        String response = httpPost("/usePart", "{\"PartID\": 1, \"Quantity\": 1}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testOrderPart() throws Exception {
        String response = httpPost("/orderPart", "{\"PartID\": 1, \"Quantity\": 1}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testOrderPartShuttleID() throws Exception {
        String response = httpPost("/orderPart", "{\"PartID\": 1, \"Quantity\": 1, \"ShuttleID\": 1}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testUpdateNotification() throws Exception {
        String response = httpPost("/updateNotification", "{\"NotificationID\": 1, \"Status\": false}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testUpdatePredictedReleaseTime() throws Exception {
        String response = httpPost("/updatePredictedReleaseTime", "{\"ShuttleID\": 1, \"PredictedReleaseTime\": \"2023-10-01 12:00:00\"}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testupdateQuestionnaire() throws Exception {
        String response = httpPost("/updateQuestionnaire", "{\"QuestionnaireID\": 1, \"Status\": false}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testUpdateComment() throws Exception {
        String response = httpPost("/updateComment", "{\"CommentID\": 1, \"Status\": false}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testCreateNotification() throws Exception {
        String response = httpPost("/createNotification", "{\"ShuttleID\": 1, \"Message\": \"Test Notification\", Sender\": \"Technician\"}");
        assertTrue(response.contains("true"));
    }

    @Test
    public void testAllCommandsDone() throws Exception {
        String response = httpPost("/allCommandsDone", "{\"ShuttleID\": 2}");
        assertTrue(response.contains("true"));
    }

}
