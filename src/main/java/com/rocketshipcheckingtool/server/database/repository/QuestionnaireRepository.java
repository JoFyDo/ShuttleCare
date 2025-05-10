package com.rocketshipcheckingtool.server.database.repository;

import com.rocketshipcheckingtool.server.datamodel.QuestionnaireRating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuestionnaireRepository {
    private final Connection connection;
    private final static Logger logger = LoggerFactory.getLogger(QuestionnaireRepository.class);

    public QuestionnaireRepository(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<QuestionnaireRating> getQuestionnaires() {
        try {
            String query = "SELECT * FROM QuestionnaireRatings WHERE Active = 'true'";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<QuestionnaireRating> questionnaires = new ArrayList<>();
            while (rs.next()) {
                questionnaires.add(new QuestionnaireRating(rs.getInt("ID"), rs.getInt("Rating"), rs.getString("Topic"), rs.getInt("ShuttleID")));
            }
            return questionnaires;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ArrayList<QuestionnaireRating> getQuestionnaireForShuttle(int shuttleID) {
        try {
            String query = "SELECT * FROM QuestionnaireRatings WHERE ShuttleID = ? AND Active = 'true'";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(shuttleID));
            ResultSet rs = stmt.executeQuery();
            ArrayList<QuestionnaireRating> questionnaires = new ArrayList<>();
            while (rs.next()) {
                questionnaires.add(new QuestionnaireRating(rs.getInt("ID"), rs.getInt("Rating"), rs.getString("Topic"), rs.getInt("ShuttleID")));
            }
            return questionnaires;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updateQuestionnaireRating(int questionnaireID, String status) {
        try {
            String query = "UPDATE QuestionnaireRatings SET Active = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(questionnaireID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}