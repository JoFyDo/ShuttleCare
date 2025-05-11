package com.rocketshipcheckingtool.server.database.repository;

import com.rocketshipcheckingtool.server.datamodel.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PartRepository {
    private final Connection connection;
    private final static Logger logger = LoggerFactory.getLogger(PartRepository.class);

    public PartRepository(Connection connection) {
        this.connection = connection;
        logger.debug("PartRepository initialized with connection: {}", connection != null ? "OK" : "NULL");
    }

    public ArrayList<Part> getParts() {
        try {
            String query = "SELECT * FROM Parts";
            logger.debug("Executing query to get all parts: {}", query);
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Part> parts = new ArrayList<>();
            while (rs.next()) {
                parts.add(new Part(rs.getInt("ID"), rs.getString("Name"), String.format("%.2f", (double) rs.getInt("Price") / 100), rs.getInt("Quantity")));
            }
            logger.info("Fetched {} parts from database.", parts.size());
            return parts;
        } catch (SQLException e) {
            logger.error("Error fetching parts: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Part getPart(int partID) {
        try {
            String query = "SELECT * FROM Parts WHERE ID = ?";
            logger.debug("Executing query to get part with ID {}: {}", partID, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(partID));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.info("Fetched part with ID {} from database.", partID);
                return new Part(rs.getInt("ID"), rs.getString("Name"), String.format("%.2f", (double) rs.getInt("Price") / 100), rs.getInt("Quantity"));
            } else {
                logger.warn("No part found with ID {}", partID);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error fetching part with ID {}: {}", partID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean updatePartQuantity(Integer partID, Integer quantity) {
        try {
            String query = "UPDATE Parts SET Quantity = ? WHERE ID = ?";
            logger.debug("Executing update for partID {}: set Quantity = {}", partID, quantity);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(quantity));
            stmt.setString(2, String.valueOf(partID));
            int updatedRows = stmt.executeUpdate();
            logger.info("Updated partID {} to quantity {}. Rows affected: {}", partID, quantity, updatedRows);
            return true;
        } catch (SQLException e){
            logger.error("Error updating partID {}: {}", partID, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
