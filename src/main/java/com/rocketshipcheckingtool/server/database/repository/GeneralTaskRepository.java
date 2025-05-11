package com.rocketshipcheckingtool.server.database.repository;

import com.rocketshipcheckingtool.server.datamodel.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeneralTaskRepository {
    private final Connection connection;
    private final static Logger logger = LoggerFactory.getLogger(GeneralTaskRepository.class);

    public GeneralTaskRepository(Connection connection) {
        this.connection = connection;
        logger.debug("GeneralTaskRepository initialized with connection: {}", connection != null ? "OK" : "NULL");
    }

    public ArrayList<Task> getGeneralTasksForShuttle(int shuttleID) {
        try {
            String query = "SELECT GeneralTasks.*, Shuttles.Name AS ShuttleName FROM GeneralTasks INNER JOIN Shuttles ON GeneralTasks.ShuttleID = Shuttles.ID WHERE GeneralTasks.ShuttleID = ?";
            logger.debug("Executing query to get general tasks for shuttleID {}: {}", shuttleID, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(shuttleID));
            ResultSet rs = stmt.executeQuery();
            ArrayList<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(new Task(rs.getString("Aufgabe"), Boolean.valueOf(rs.getString("Status")), rs.getInt("ID"), rs.getString("ShuttleName"), rs.getInt("TimeNeeded")));
            }
            logger.info("Fetched {} general tasks for shuttleID {}.", tasks.size(), shuttleID);
            return tasks;
        } catch (SQLException e){
            logger.error("Error fetching general tasks for shuttleID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateGeneralTask(int taskID, String status) {
        try {
            String query = "UPDATE GeneralTasks SET Status = ? WHERE ID = ?";
            logger.debug("Executing update for general taskID {}: set Status = {}", taskID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(taskID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated general taskID {} to status '{}'. Rows affected: {}", taskID, status, updatedRows);
            return true;
        } catch(SQLException e){
            logger.error("Error updating general taskID {}: {}", taskID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateAllGeneralTasksStatusBelongToShuttle(int shuttleID, String status) {
        try {
            String query = "UPDATE GeneralTasks SET Status = ? WHERE ShuttleID = ?";
            logger.debug("Executing update for all general tasks of shuttleID {}: set Status = {}", shuttleID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(shuttleID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated all general tasks for shuttleID {} to status '{}'. Rows affected: {}", shuttleID, status, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating all general tasks for shuttleID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
