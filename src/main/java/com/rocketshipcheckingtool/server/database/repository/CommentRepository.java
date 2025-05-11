package com.rocketshipcheckingtool.server.database.repository;

import com.rocketshipcheckingtool.server.datamodel.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CommentRepository {
    private final Connection connection;
    private final static Logger logger = LoggerFactory.getLogger(CommentRepository.class);

    public CommentRepository(Connection connection) {
        this.connection = connection;
        logger.debug("CommentRepository initialized with connection: {}", connection != null ? "OK" : "NULL");
    }

    public ArrayList<Comment> getComments() {
        try {
            String query = "SELECT * FROM Comments WHERE Active = 'true'";
            logger.debug("Executing query to get all active comments: {}", query);
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Comment> comments = new ArrayList<>();
            while (rs.next()) {
                comments.add(new Comment(rs.getInt("ID"), rs.getString("Comment"), rs.getInt("ShuttleID")));
            }
            logger.info("Fetched {} active comments from database.", comments.size());
            return comments;
        } catch (SQLException e) {
            logger.error("Error fetching comments: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Comment> getCommentsByShuttle(int shuttleID) {
        try {
            String query = "SELECT * FROM Comments WHERE ShuttleID = ? AND Active = 'true'";
            logger.debug("Executing query to get comments for shuttleID {}: {}", shuttleID, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(shuttleID));
            ResultSet rs = stmt.executeQuery();
            ArrayList<Comment> comments = new ArrayList<>();
            while (rs.next()) {
                comments.add(new Comment(rs.getInt("ID"), rs.getString("Comment"), rs.getInt("ShuttleID")));
            }
            logger.info("Fetched {} comments for shuttleID {}.", comments.size(), shuttleID);
            return comments;
        } catch (SQLException e) {
            logger.error("Error fetching comments for shuttleID {}: {}", shuttleID, e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateComment(int commentID, String status) {
        try {
            String query = "UPDATE Comments SET Active = ? WHERE ID = ?";
            logger.debug("Executing update for commentID {}: set Active = {}", commentID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(commentID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated commentID {} to status '{}'. Rows affected: {}", commentID, status, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating commentID {}: {}", commentID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
