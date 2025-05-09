package com.rocketshipcheckingtool.server;

import com.rocketshipcheckingtool.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseConnection {
    private final static Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private final String database = "jdbc:sqlite:RocketDatabase.db";
    private Connection connection;

    public DatabaseConnection() {
        try {
            this.connection = connect();
        }catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(database);
        logger.info("Connected to SQLite!");
        return c;
    }

    public void disconnect() throws SQLException {
        connection.close();
        logger.info("Disconnected from SQLite!");
    }

    public ArrayList<Shuttle> getShuttles() {
        try {
            ArrayList<Shuttle> shuttles = new ArrayList<>();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Shuttles WHERE Status != 'Verschrottet'");
            while (rs.next()) {
                shuttles.add(new Shuttle(rs.getInt("ID"), rs.getString("Name"), rs.getString("Status"), rs.getString("Landung"), rs.getString("Mechaniker")));
            }
            return shuttles;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Shuttle getShuttle(String name) {
        try {
            String query = "SELECT * FROM Shuttles WHERE Name = ? AND Status != 'Verschrottet'";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Shuttle(rs.getInt("ID"), rs.getString("Name"), rs.getString("Status"), rs.getString("Landung"), rs.getString("Mechaniker"));
            } else {
                return null;
            }
        }catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Shuttle getShuttle(int id) {
        try {
            String query = "SELECT * FROM Shuttles WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, Integer.toString(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Shuttle(rs.getInt("ID"), rs.getString("Name"), rs.getString("Status"), rs.getString("Landung"), rs.getString("Mechaniker"));
            } else {
                return null;
            }
        }catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void changeShuttleStatus(int shuttleID, String status) {
        try{
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("UPDATE Shuttles SET Status = '" + status + "' WHERE ID = " + shuttleID);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Task> getActiveTasks() {
        try{
            ArrayList<Task> tasks = new ArrayList<>();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Tasks.*, Shuttles.Name AS ShuttleName FROM Tasks INNER JOIN Shuttles ON Tasks.ShuttleID = Shuttles.ID WHERE Aktiv = 'true'");
            while (rs.next()) {
                tasks.add(new Task(rs.getString("Aufgabe"), rs.getString("Status"), rs.getString("Mechaniker"), rs.getString("ShuttleName"), rs.getInt("ID"), rs.getInt("TimeNeeded")));
            }
            return tasks;
        }catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Task> getActiveTaskByShuttleID(int shuttleID) {
        try {
            String query = "SELECT Tasks.*, Shuttles.Name AS ShuttleName FROM Tasks INNER JOIN Shuttles ON Tasks.ShuttleID = Shuttles.ID WHERE Tasks.ShuttleID = ? AND Tasks.Aktiv = 'true'";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, Integer.toString(shuttleID));
            ResultSet rs = stmt.executeQuery();
            ArrayList<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(new Task(rs.getString("Aufgabe"), rs.getString("Status"), rs.getString("Mechaniker"), rs.getString("ShuttleName"), rs.getInt("ID"), rs.getInt("TimeNeeded")));
            }
            return tasks;
        }catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updateTask(int taskID, String status) {
        try {
            String query = "UPDATE Tasks SET Status = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(status));
            stmt.setString(2, Integer.toString(taskID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean createTask(String mechanic, String description, String shuttleID) throws IOException {
        try {
            String query = "INSERT INTO Tasks (Aufgabe, Status, ShuttleID, Mechaniker, Aktiv, TimeNeeded) VALUES (?, 'Offen', ?, ?, 'true', ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, description);
            stmt.setString(2, shuttleID);
            stmt.setString(3, mechanic);
            stmt.setString(4, String.valueOf(((int)((Math.random() * 6) + 1))));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Task> getGeneralTasksForShuttle(int shuttleID) {
        try {
            String query = "SELECT GeneralTasks.*, Shuttles.Name AS ShuttleName FROM GeneralTasks INNER JOIN Shuttles ON GeneralTasks.ShuttleID = Shuttles.ID WHERE GeneralTasks.ShuttleID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(shuttleID));
            ResultSet rs = stmt.executeQuery();
            ArrayList<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(new Task(rs.getString("Aufgabe"), rs.getString("Status"), rs.getInt("ID"), rs.getString("ShuttleName"), rs.getInt("TimeNeeded")));
            }
            return tasks;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updateGeneralTask(int taskID, String status) {
        try {
            String query = "UPDATE GeneralTasks SET Status = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(taskID));
            stmt.executeUpdate();
            return true;
        } catch(SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updateShuttleStatus(int shuttleID, String status) {
        try {
            String query = "UPDATE Shuttles SET Status = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(shuttleID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updateAllTasksActivityBelongToShuttle(int shuttleID, String status) {
        try {
            String query = "UPDATE Tasks SET Aktiv = ? WHERE ShuttleID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(shuttleID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updateAllTasksStatusBelongToShuttle(int shuttleID, String status) {
        try {
            String query = "UPDATE Tasks SET Status = ? WHERE ShuttleID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(shuttleID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updateAllGeneralTasksStatusBelongToShuttle(int shuttleID, String status) {
        try {
            String query = "UPDATE GeneralTasks SET Status = ? WHERE ShuttleID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(shuttleID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Part> getParts() {
        try {
            String query = "SELECT * FROM Parts";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Part> parts = new ArrayList<>();
            while (rs.next()) {
                parts.add(new Part(rs.getInt("ID"), rs.getString("Name"), String.format("%.2f", (double) rs.getInt("Price") / 100), rs.getInt("Quantity")));
            }
            return parts;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Part getPart(int partID) {
        try {
            String query = "SELECT * FROM Parts WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(partID));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Part(rs.getInt("ID"), rs.getString("Name"), String.format("%.2f", (double) rs.getInt("Price") / 100), rs.getInt("Quantity"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updatePartQuantity(Integer partID, Integer quantity) {
        try {
            String query = "UPDATE Parts SET Quantity = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(quantity));
            stmt.setString(2, String.valueOf(partID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Notification> getNotifications() {
        try {
            String query = "SELECT * FROM Notifications WHERE Aktiv = 'true'";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Notification> notifications = new ArrayList<>();
            while (rs.next()) {
                notifications.add(new Notification(rs.getInt("ID"), rs.getString("Nachricht"), rs.getInt("ShuttleID"), rs.getString("Absender"), rs.getString("Kommentar")));
            }
            return notifications;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updateNotification(int notificationID, String status) {
        try {
            String query = "UPDATE Notifications SET Aktiv = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(notificationID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Notification> getNotificationsByShuttle(String shuttleID) {
        try {
            String query = "SELECT * FROM Notifications WHERE ShuttleID = ? AND Aktiv = 'true'";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, shuttleID);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Notification> notifications = new ArrayList<>();
            while (rs.next()) {
                notifications.add(new Notification(rs.getInt("ID"), rs.getString("Nachricht"), rs.getInt("ShuttleID"), rs.getString("Absender"), rs.getString("Kommentar")));
            }
            return notifications;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Mechanic> getMechanics() {
        try {
            String query = "SELECT * FROM Mechanics";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Mechanic> mechanics = new ArrayList<>();
            while (rs.next()) {
                mechanics.add(new Mechanic(rs.getInt("ID"), rs.getString("Name"), rs.getString("Role")));
            }
            return mechanics;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updatePredictedReleaseTime(int shuttleID, int time) {
        try {
            Shuttle shuttle = getShuttle(shuttleID);
            if (shuttle == null) {
                return false;
            }
            // Get landing date and time from shuttle object
            Calendar calendar = shuttle.getLandingTime();

            // Add maintenance time (in hours)
            calendar.add(Calendar.HOUR, time);

            // Format timestamp for database
            java.sql.Timestamp predictedReleaseTime = new java.sql.Timestamp(calendar.getTimeInMillis());
            String predictedTimeStr = predictedReleaseTime.toString();

            System.out.println("Predicted Release Time: " + predictedTimeStr);
            System.out.println("Time Needed: " + time);

            String query = "UPDATE Shuttles SET VorFreigabeDatum = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(predictedTimeStr));
            stmt.setString(2, String.valueOf(shuttleID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Calendar getPredictedReleaseTime(int shuttleID) {
        try {
            String query = "SELECT VorFreigabeDatum FROM Shuttles WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(shuttleID));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                try{
                    java.sql.Timestamp predictedReleaseTime = rs.getTimestamp("VorFreigabeDatum");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(predictedReleaseTime);
                    return calendar;
                } catch (Exception e) {
                    System.err.println("Failed to parse predicted release time: " + rs.getString("VorFreigabeDatum"));
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean setPredictedReleaseTime(int shuttleID, String predictedReleaseTime) {
        try {
            String query = "UPDATE Shuttles SET VorFreigabeDatum = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, predictedReleaseTime);
            stmt.setString(2, String.valueOf(shuttleID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
