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
        logger.debug("QuestionnaireRepository initialized with connection: {}", connection != null ? "OK" : "NULL");
    }

    public ArrayList<QuestionnaireRating> getQuestionnaires() {
        try {
            String query = "SELECT * FROM QuestionnaireRatings WHERE Active = 'true'";
            logger.debug("Executing query to get all active questionnaire ratings: {}", query);
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<QuestionnaireRating> questionnaires = new ArrayList<>();
            while (rs.next()) {
                questionnaires.add(new QuestionnaireRating(rs.getInt("ID"), rs.getInt("Rating"), rs.getString("Topic"), rs.getInt("ShuttleID")));
            }
            logger.info("Fetched {} active questionnaire ratings from database.", questionnaires.size());
            return questionnaires;
        } catch (SQLException e) {
            logger.error("Error fetching questionnaire ratings: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public ArrayList<QuestionnaireRating> getQuestionnaireForShuttle(int shuttleID) {
        try {
            String query = "SELECT * FROM QuestionnaireRatings WHERE ShuttleID = ? AND Active = 'true'";
            logger.debug("Executing query to get questionnaire ratings for shuttleID {}: {}", shuttleID, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(shuttleID));
            ResultSet rs = stmt.executeQuery();
            ArrayList<QuestionnaireRating> questionnaires = new ArrayList<>();
            while (rs.next()) {
                questionnaires.add(new QuestionnaireRating(rs.getInt("ID"), rs.getInt("Rating"), rs.getString("Topic"), rs.getInt("ShuttleID")));
            }
            logger.info("Fetched {} questionnaire ratings for shuttleID {}.", questionnaires.size(), shuttleID);
            return questionnaires;
        } catch (SQLException e) {
            logger.error("Error fetching questionnaire ratings for shuttleID {}: {}", shuttleID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateQuestionnaireRating(int questionnaireID, String status) {
        try {
            String query = "UPDATE QuestionnaireRatings SET Active = ? WHERE ID = ?";
            logger.debug("Executing update for questionnaireID {}: set Active = {}", questionnaireID, status);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(questionnaireID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated questionnaireID {} to status '{}'. Rows affected: {}", questionnaireID, status, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating questionnaireID {}: {}", questionnaireID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
