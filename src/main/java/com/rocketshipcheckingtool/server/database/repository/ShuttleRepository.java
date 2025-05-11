package com.rocketshipcheckingtool.server.database.repository;

import com.rocketshipcheckingtool.server.datamodel.Shuttle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class ShuttleRepository {
    private final Connection connection;
    private final static Logger logger = LoggerFactory.getLogger(ShuttleRepository.class);

    public ShuttleRepository(Connection connection) {
        this.connection = connection;
        logger.debug("ShuttleRepository initialized with connection: {}", connection != null ? "OK" : "NULL");
    }

    public ArrayList<Shuttle> getShuttles() {
        try {
            ArrayList<Shuttle> shuttles = new ArrayList<>();
            Statement stmt = connection.createStatement();
            String query = "SELECT * FROM Shuttles WHERE Status != 'Verschrottet'";
            logger.debug("Executing query to get all shuttles: {}", query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                shuttles.add(new Shuttle(rs.getInt("ID"), rs.getString("Name"), rs.getString("Status"), rs.getString("Landung"), rs.getString("Mechaniker")));
            }
            logger.info("Fetched {} shuttles from database.", shuttles.size());
            return shuttles;
        } catch (SQLException e) {
            logger.error("Error fetching shuttles: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Shuttle getShuttle(String name) {
        try {
            String query = "SELECT * FROM Shuttles WHERE Name = ? AND Status != 'Verschrottet'";
            logger.debug("Executing query to get shuttle by name '{}': {}", name, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.info("Fetched shuttle '{}' from database.", name);
                return new Shuttle(rs.getInt("ID"), rs.getString("Name"), rs.getString("Status"), rs.getString("Landung"), rs.getString("Mechaniker"));
            } else {
                logger.warn("No shuttle found with name '{}'", name);
                return null;
            }
        }catch (SQLException e) {
            logger.error("Error fetching shuttle by name '{}': {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Shuttle getShuttle(int id) {
        try {
            String query = "SELECT * FROM Shuttles WHERE ID = ?";
            logger.debug("Executing query to get shuttle by ID {}: {}", id, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, Integer.toString(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.info("Fetched shuttle with ID {} from database.", id);
                return new Shuttle(rs.getInt("ID"), rs.getString("Name"), rs.getString("Status"), rs.getString("Landung"), rs.getString("Mechaniker"));
            } else {
                logger.warn("No shuttle found with ID {}", id);
                return null;
            }
        }catch (SQLException e) {
            logger.error("Error fetching shuttle by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void changeShuttleStatus(int shuttleID, String status) {
        try{
            String query = "UPDATE Shuttles SET Status = ? WHERE ID = ?";
            logger.debug("Changing status of shuttle ID {} to '{}'", shuttleID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, shuttleID);
            int updatedRows = stmt.executeUpdate();
            logger.info("Changed status of shuttle ID {} to '{}'. Rows affected: {}", shuttleID, status, updatedRows);
        } catch (SQLException e) {
            logger.error("Error changing shuttle status for ID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateShuttleStatus(int shuttleID, String status) {
        try {
            String query = "UPDATE Shuttles SET Status = ? WHERE ID = ?";
            logger.debug("Updating status of shuttle ID {} to '{}'", shuttleID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(shuttleID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated status of shuttle ID {} to '{}'. Rows affected: {}", shuttleID, status, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating shuttle status for ID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updatePredictedReleaseTime(int shuttleID, int time) {
        try {
            Shuttle shuttle = getShuttle(shuttleID);
            if (shuttle == null) {
                logger.warn("Cannot update predicted release time: shuttle with ID {} not found", shuttleID);
                return false;
            }
            // Get landing date and time from shuttle object
            Calendar calendar = shuttle.getLandingTime();

            // Add maintenance time (in hours)
            calendar.add(Calendar.HOUR, time);

            // Format timestamp for database
            java.sql.Timestamp predictedReleaseTime = new java.sql.Timestamp(calendar.getTimeInMillis());
            String predictedTimeStr = predictedReleaseTime.toString();

            logger.info("Updating predicted release time for shuttle ID {}: {} (Time Needed: {})", shuttleID, predictedTimeStr, time);

            String query = "UPDATE Shuttles SET VorFreigabeDatum = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(predictedTimeStr));
            stmt.setString(2, String.valueOf(shuttleID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Predicted release time updated for shuttle ID {}. Rows affected: {}", shuttleID, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating predicted release time for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Calendar getPredictedReleaseTime(int shuttleID) {
        try {
            String query = "SELECT VorFreigabeDatum FROM Shuttles WHERE ID = ?";
            logger.debug("Getting predicted release time for shuttle ID {}: {}", shuttleID, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(shuttleID));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                try{
                    java.sql.Timestamp predictedReleaseTime = rs.getTimestamp("VorFreigabeDatum");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(predictedReleaseTime);
                    logger.info("Fetched predicted release time for shuttle ID {}: {}", shuttleID, predictedReleaseTime);
                    return calendar;
                } catch (Exception e) {
                    logger.error("Failed to parse predicted release time for shuttle ID {}: {}", shuttleID, rs.getString("VorFreigabeDatum"), e);
                    return null;
                }
            } else {
                logger.warn("No predicted release time found for shuttle ID {}", shuttleID);
                return null;
            }
        } catch (SQLException e){
            logger.error("Error getting predicted release time for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean setPredictedReleaseTime(int shuttleID, String predictedReleaseTime) {
        try {
            String query = "UPDATE Shuttles SET VorFreigabeDatum = ? WHERE ID = ?";
            logger.debug("Setting predicted release time for shuttle ID {}: {}", shuttleID, predictedReleaseTime);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, predictedReleaseTime);
            stmt.setString(2, String.valueOf(shuttleID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Set predicted release time for shuttle ID {}. Rows affected: {}", shuttleID, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error setting predicted release time for shuttle ID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
