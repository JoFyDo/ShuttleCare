package com.rocketshipcheckingtool.server;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {
    private final static Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private final String database = "jdbc:sqlite:RocketDatabase.db";
    private Connection connection;

    public DatabaseConnection() {
        try {
            this.connection = connect();
        }catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<Shuttle> getShuttles() {
        try {
            ArrayList<Shuttle> shuttles = new ArrayList<>();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Shuttles");
            while (rs.next()) {
                shuttles.add(new Shuttle(rs.getInt("ID"), rs.getString("Name"), rs.getString("Status"), rs.getDate("Landung"), rs.getTime("Landung"), rs.getString("Mechaniker")));
            }
            return shuttles;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Shuttle getShuttle(String name) {
        try {
            String query = "SELECT * FROM Shuttles WHERE Name = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Shuttle(rs.getInt("ID"), rs.getString("Name"), rs.getString("Status"), rs.getDate("Landung"), rs.getTime("Landung"), rs.getString("Mechaniker"));
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
                return new Shuttle(rs.getInt("ID"), rs.getString("Name"), rs.getString("Status"), rs.getDate("Landung"), rs.getTime("Landung"), rs.getString("Mechaniker"));
            } else {
                return null;
            }
        }catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void changeShuttleStatus(Shuttle shuttle, String status) {
        try{
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("UPDATE Shuttles SET Status = '" + status + "' WHERE ID = " + shuttle.getId());
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
                tasks.add(new Task(rs.getString("Aufgabe"), rs.getString("Status"), rs.getString("Mechaniker"), rs.getString("ShuttleName"), rs.getInt("ID")));
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
                tasks.add(new Task(rs.getString("Aufgabe"), rs.getString("Status"), rs.getString("Mechaniker"), rs.getString("ShuttleName"), rs.getInt("ID")));
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
            String query = "INSERT INTO Tasks (Aufgabe, Status, ShuttleID, Mechaniker, Aktiv) VALUES (?, 'Offen', ?, ?, 'true')";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, description);
            stmt.setString(2, shuttleID);
            stmt.setString(3, mechanic);
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
                tasks.add(new Task(rs.getString("Aufgabe"), rs.getString("Status"), rs.getInt("ID"), rs.getString("ShuttleName")));
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

    public Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(database);
        logger.info("Connected to SQLite!");
        return c;
    }

    public void disconnect() throws SQLException {
        connection.close();
        logger.info("Disconnected from SQLite!");
    }
}
