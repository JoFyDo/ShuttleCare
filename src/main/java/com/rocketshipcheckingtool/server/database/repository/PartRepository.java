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
    }

    public ArrayList<Part> getParts() {
        try {
            String query = "SELECT * FROM Parts";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Part> parts = new ArrayList<>();
            while (rs.next()) {
                parts.add(new Part(rs.getInt("ID"), rs.getString("Name"), String.format("%.2f", (double) rs.getInt("Price") / 100), rs.getInt("Quantity")));
            }
            return parts;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Part getPart(int partID) {
        try {
            String query = "SELECT * FROM Parts WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(partID));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Part(rs.getInt("ID"), rs.getString("Name"), String.format("%.2f", (double) rs.getInt("Price") / 100), rs.getInt("Quantity"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean updatePartQuantity(Integer partID, Integer quantity) {
        try {
            String query = "UPDATE Parts SET Quantity = ? WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(quantity));
            stmt.setString(2, String.valueOf(partID));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}