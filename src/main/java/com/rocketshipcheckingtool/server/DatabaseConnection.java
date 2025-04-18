package com.rocketshipcheckingtool.server;

import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            ResultSet rs = stmt.executeQuery("SELECT Tasks.*, Shuttles.Name AS ShuttleName FROM Tasks INNER JOIN Shuttles ON Tasks.ShuttleID = Shuttles.ID WHERE Aktiv = 1");
            while (rs.next()) {
                tasks.add(new Task(rs.getString("Aufgabe"), rs.getString("Status"), rs.getString("Mechaniker"), rs.getString("ShuttleName")));
            }
            return tasks;
        }catch (SQLException e) {
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
