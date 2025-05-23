package com.rocketshipcheckingtool.server.database.repository;

import com.rocketshipcheckingtool.server.datamodel.Mechanic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MechanicRepository {
    private final Connection connection;
    private final static Logger logger = LoggerFactory.getLogger(MechanicRepository.class);

    public MechanicRepository(Connection connection) {
        this.connection = connection;
        logger.debug("MechanicRepository initialized with connection: {}", connection != null ? "OK" : "NULL");
    }

    public ArrayList<Mechanic> getMechanics() {
        try {
            String query = "SELECT * FROM Mechanics";
            logger.debug("Executing query to get all mechanics: {}", query);
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Mechanic> mechanics = new ArrayList<>();
            while (rs.next()) {
                mechanics.add(new Mechanic(rs.getInt("ID"), rs.getString("Name")));
            }
            logger.info("Fetched {} mechanics from database.", mechanics.size());
            return mechanics;
        } catch (SQLException e) {
            logger.error("Error fetching mechanics: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Mechanic getMechanic(int mechanicId) {
        try {
            String query = "SELECT * FROM Mechanics WHERE ID = ?";
            logger.debug("Executing query to get mechanic with ID {}: {}", mechanicId, query);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(mechanicId));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.info("Fetched mechanic with ID {} from database.", mechanicId);
                return new Mechanic(rs.getInt("ID"), rs.getString("Name"));
            } else {
                logger.warn("No mechanic found with ID {}", mechanicId);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error fetching mechanic with ID {}: {}", mechanicId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
