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
        logger.debug("NotificationRepository initialized with connection: {}", connection != null ? "OK" : "NULL");
    }

    public ArrayList<Notification> getNotifications(String user) {
        try {
            String query = "SELECT * FROM Notifications WHERE Active = 'true' AND Sender != ?";
            logger.debug("Executing query to get notifications for user '{}': {}", user, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Notification> notifications = new ArrayList<>();
            while (rs.next()) {
                notifications.add(new Notification(rs.getInt("ID"), rs.getString("Notification"), rs.getInt("ShuttleID"), rs.getString("Sender"), rs.getString("Comment")));
            }
            logger.info("Fetched {} notifications for user '{}'.", notifications.size(), user);
            return notifications;
        } catch (SQLException e) {
            logger.error("Error fetching notifications for user '{}': {}", user, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateNotification(int notificationID, String status) {
        try {
            String query = "UPDATE Notifications SET Active = ? WHERE ID = ?";
            logger.debug("Executing update for notificationID {}: set Active = {}", notificationID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(notificationID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated notificationID {} to status '{}'. Rows affected: {}", notificationID, status, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating notificationID {}: {}", notificationID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Notification> getNotificationsByShuttle(String shuttleID, String user) {
        try {
            String query = "SELECT * FROM Notifications WHERE ShuttleID = ? AND Active = 'true' AND Sender != ?";
            logger.debug("Executing query to get notifications for shuttleID {} and user '{}': {}", shuttleID, user, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, shuttleID);
            stmt.setString(2, user);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Notification> notifications = new ArrayList<>();
            while (rs.next()) {
                notifications.add(new Notification(rs.getInt("ID"), rs.getString("Notification"), rs.getInt("ShuttleID"), rs.getString("Sender"), rs.getString("Comment")));
            }
            logger.info("Fetched {} notifications for shuttleID {} and user '{}'.", notifications.size(), shuttleID, user);
            return notifications;
        } catch (SQLException e) {
            logger.error("Error fetching notifications for shuttleID {} and user '{}': {}", shuttleID, user, e);
            throw new RuntimeException(e);
        }
    }

    public boolean createNotification(String message, String shuttleId, String sender, String comment) {
        try {
            String query = "INSERT INTO Notifications (Notification, ShuttleID, Sender, Comment, Active) VALUES (?, ?, ?, ?, 'true')";
            logger.debug("Executing insert for new notification: message='{}', shuttleId={}, sender='{}', comment='{}'", message, shuttleId, sender, comment);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, message);
            stmt.setString(2, shuttleId);
            stmt.setString(3, sender);
            stmt.setString(4, comment);
            int updatedRows = stmt.executeUpdate();
            logger.info("Created new notification for shuttleId {}. Rows affected: {}", shuttleId, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error creating notification for shuttleId {}: {}", shuttleId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
