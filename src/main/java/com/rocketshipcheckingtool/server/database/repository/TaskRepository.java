package com.rocketshipcheckingtool.server.database.repository;

import com.rocketshipcheckingtool.server.datamodel.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class TaskRepository {
    private final Connection connection;
    private final static Logger logger = LoggerFactory.getLogger(TaskRepository.class);

    public TaskRepository(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<Task> getActiveTasks() {
        try{
            ArrayList<Task> tasks = new ArrayList<>();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Tasks.*, Shuttles.Name AS ShuttleName FROM Tasks INNER JOIN Shuttles ON Tasks.ShuttleID = Shuttles.ID WHERE Aktiv = 'true'");
            while (rs.next()) {
                tasks.add(new Task(rs.getString("Aufgabe"), Boolean.valueOf(rs.getString("Status")), rs.getString("Mechaniker"), rs.getString("ShuttleName"), rs.getInt("ID"), rs.getInt("TimeNeeded")));
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
                tasks.add(new Task(rs.getString("Aufgabe"), Boolean.valueOf(rs.getString("Status")), rs.getString("Mechaniker"), rs.getString("ShuttleName"), rs.getInt("ID"), rs.getInt("TimeNeeded")));
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
            String query = "INSERT INTO Tasks (Aufgabe, Status, ShuttleID, Mechaniker, Aktiv, TimeNeeded) VALUES (?, 'false', ?, ?, 'true', ?)";
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
}