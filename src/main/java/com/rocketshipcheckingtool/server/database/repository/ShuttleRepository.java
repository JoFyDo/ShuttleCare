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