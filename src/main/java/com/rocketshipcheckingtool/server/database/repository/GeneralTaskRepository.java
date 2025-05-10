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
    }

    public ArrayList<Task> getGeneralTasksForShuttle(int shuttleID) {
        try {
            String query = "SELECT GeneralTasks.*, Shuttles.Name AS ShuttleName FROM GeneralTasks INNER JOIN Shuttles ON GeneralTasks.ShuttleID = Shuttles.ID WHERE GeneralTasks.ShuttleID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(shuttleID));
            ResultSet rs = stmt.executeQuery();
            ArrayList<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(new Task(rs.getString("Aufgabe"), Boolean.valueOf(rs.getString("Status")), rs.getInt("ID"), rs.getString("ShuttleName"), rs.getInt("TimeNeeded")));
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
}