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
    }

    public ArrayList<Comment> getComments() {
        try {
            String query = "SELECT * FROM Comments WHERE Active = 'true'";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Comment> comments = new ArrayList<>();
            while (rs.next()) {
                comments.add(new Comment(rs.getInt("ID"), rs.getString("Comment"), rs.getInt("ShuttleID")));
            }
            return comments;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Comment> getCommentsByShuttle(int shuttleID) {
        try {
            String query = "SELECT * FROM Comments WHERE ShuttleID = ? AND Active = 'true'";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(shuttleID));
            ResultSet rs = stmt.executeQuery();
            ArrayList<Comment> comments = new ArrayList<>();
            while (rs.next()) {
                comments.add(new Comment(rs.getInt("ID"), rs.getString("Comment"), rs.getInt("ShuttleID")));
            }
            return comments;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updateComment(int commentID, String status) {
        try {
            String query = "UPDATE Comments SET Active = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(commentID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}