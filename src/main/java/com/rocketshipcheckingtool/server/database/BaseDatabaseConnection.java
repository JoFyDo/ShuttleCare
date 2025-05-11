package com.rocketshipcheckingtool.server.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDatabaseConnection implements DatabaseConnector {
    private final static Logger logger = LoggerFactory.getLogger(BaseDatabaseConnection.class);
    private final String database = "jdbc:sqlite:RocketDatabase.db";
    private Connection connection;

    public BaseDatabaseConnection() {
        try {
            logger.info("Attempting to connect to SQLite database: {}", database);
            this.connection = connect();
            logger.info("Database connection established successfully.");
        } catch (SQLException e) {
            logger.error("Failed to connect to SQLite database: {}", e.getMessage(), e);
        }
    }

    @Override
    public Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(database);
        logger.info("Connected to SQLite database at '{}'", database);
        return c;
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            logger.info("Disconnected from SQLite database.");
        } else {
            logger.warn("Attempted to disconnect, but connection was already closed or null.");
        }
    }

    @Override
    public Connection getConnection() {
        if (connection == null) {
            logger.warn("getConnection called but connection is null.");
        }
        return connection;
    }
}
