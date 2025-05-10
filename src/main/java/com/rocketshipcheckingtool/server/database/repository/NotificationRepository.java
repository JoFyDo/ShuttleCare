package com.rocketshipcheckingtool.server.database.repository;

import com.rocketshipcheckingtool.server.datamodel.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NotificationRepository {
    private final Connection connection;
    private final static Logger logger = LoggerFactory.getLogger(NotificationRepository.class);

    public NotificationRepository(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<Notification> getNotifications(String user) {
        try {
            String query = "SELECT * FROM Notifications WHERE Aktiv = 'true' AND Absender != ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
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

    public ArrayList<Notification> getNotificationsByShuttle(String shuttleID, String user) {
        try {
            String query = "SELECT * FROM Notifications WHERE ShuttleID = ? AND Aktiv = 'true' AND Absender != ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, shuttleID);
            stmt.setString(2, user);
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

    public boolean createNotification(String message, String shuttleId, String sender, String comment) {
        try {
            String query = "INSERT INTO Notifications (Nachricht, ShuttleID, Absender, Kommentar, Aktiv) VALUES (?, ?, ?, ?, 'true')";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, message);
            stmt.setString(2, shuttleId);
            stmt.setString(3, sender);
            stmt.setString(4, comment);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}