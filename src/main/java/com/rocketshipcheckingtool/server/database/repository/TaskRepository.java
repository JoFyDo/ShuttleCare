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
        logger.debug("TaskRepository initialized with connection: {}", connection != null ? "OK" : "NULL");
    }

    public ArrayList<Task> getActiveTasks() {
        try{
            ArrayList<Task> tasks = new ArrayList<>();
            String query = "SELECT Tasks.*, Shuttles.Name AS ShuttleName FROM Tasks INNER JOIN Shuttles ON Tasks.ShuttleID = Shuttles.ID WHERE Active = 'true'";
            logger.debug("Executing query to get all active tasks: {}", query);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                tasks.add(new Task(rs.getString("Task"), Boolean.valueOf(rs.getString("Status")), rs.getString("Mechanic"), rs.getString("ShuttleName"), rs.getInt("ID"), rs.getInt("TimeNeeded")));
            }
            logger.info("Fetched {} active tasks from database.", tasks.size());
            return tasks;
        }catch (SQLException e) {
            logger.error("Error fetching active tasks: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Task> getActiveTaskByShuttleID(int shuttleID) {
        try {
            String query = "SELECT Tasks.*, Shuttles.Name AS ShuttleName FROM Tasks INNER JOIN Shuttles ON Tasks.ShuttleID = Shuttles.ID WHERE Tasks.ShuttleID = ? AND Tasks.Active = 'true'";
            logger.debug("Executing query to get active tasks for shuttleID {}: {}", shuttleID, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, Integer.toString(shuttleID));
            ResultSet rs = stmt.executeQuery();
            ArrayList<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(new Task(rs.getString("Task"), Boolean.valueOf(rs.getString("Status")), rs.getString("Mechanic"), rs.getString("ShuttleName"), rs.getInt("ID"), rs.getInt("TimeNeeded")));
            }
            logger.info("Fetched {} active tasks for shuttleID {}.", tasks.size(), shuttleID);
            return tasks;
        }catch (SQLException e) {
            logger.error("Error fetching active tasks for shuttleID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateTask(int taskID, String status) {
        try {
            String query = "UPDATE Tasks SET Status = ? WHERE ID = ?";
            logger.debug("Updating task ID {} to status '{}'", taskID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(status));
            stmt.setString(2, Integer.toString(taskID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated task ID {} to status '{}'. Rows affected: {}", taskID, status, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating task ID {}: {}", taskID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean createTask(String mechanic, String description, String shuttleID) throws IOException {
        try {
            String query = "INSERT INTO Tasks (Task, Status, ShuttleID, Mechanic, Active, TimeNeeded) VALUES (?, 'false', ?, ?, 'true', ?)";
            logger.debug("Creating new task for shuttleID {}: mechanic='{}', description='{}'", shuttleID, mechanic, description);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, description);
            stmt.setString(2, shuttleID);
            stmt.setString(3, mechanic);
            stmt.setString(4, String.valueOf(((int)((Math.random() * 6) + 1))));
            int updatedRows = stmt.executeUpdate();
            logger.info("Created new task for shuttleID {}. Rows affected: {}", shuttleID, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error creating task for shuttleID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateAllTasksActivityBelongToShuttle(int shuttleID, String status) {
        try {
            String query = "UPDATE Tasks SET Active = ? WHERE ShuttleID = ?";
            logger.debug("Updating activity for all tasks of shuttleID {} to '{}'", shuttleID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(shuttleID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated activity for all tasks of shuttleID {} to '{}'. Rows affected: {}", shuttleID, status, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating activity for all tasks of shuttleID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateAllTasksStatusBelongToShuttle(int shuttleID, String status) {
        try {
            String query = "UPDATE Tasks SET Status = ? WHERE ShuttleID = ?";
            logger.debug("Updating status for all tasks of shuttleID {} to '{}'", shuttleID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(shuttleID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated status for all tasks of shuttleID {} to '{}'. Rows affected: {}", shuttleID, status, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating status for all tasks of shuttleID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
