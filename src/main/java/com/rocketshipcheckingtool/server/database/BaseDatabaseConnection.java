package com.rocketshipcheckingtool.server.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementation of the DatabaseConnector interface for managing SQLite database connections.
 * Provides methods to establish, retrieve, and disconnect from the database.
 */
public class BaseDatabaseConnection implements DatabaseConnector {
    private final static Logger logger = LoggerFactory.getLogger(BaseDatabaseConnection.class); // Logger for logging database connection events.
    private final String database = "jdbc:sqlite:RocketDatabase.db"; // The SQLite database URL.
    private Connection connection; // The active database connection.

    /**
     * Constructs a BaseDatabaseConnection and attempts to establish a connection to the SQLite database.
     * Logs the connection status and throws a RuntimeException if the connection fails.
     */
    public BaseDatabaseConnection() {
        try {
            logger.info("Attempting to connect to SQLite database: {}", database);
            this.connection = connect();
            logger.info("Database connection established successfully.");
        } catch (SQLException e) {
            logger.error("Failed to connect to SQLite database: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return A Connection object representing the database connection.
     * @throws SQLException If an error occurs while connecting to the database.
     */
    @Override
    public Connection connect() throws SQLException {
        try {
            Connection c = DriverManager.getConnection(database);
            logger.info("Connected to SQLite database at '{}'", database);
            return c;
        } catch (SQLException e) {
            logger.error("Error connecting to SQLite database: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Disconnects the current database connection if it is open.
     * Logs the disconnection status and throws a SQLException if an error occurs during disconnection.
     *
     * @throws SQLException If an error occurs while disconnecting from the database.
     */
    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try {
                connection.close();
                logger.info("Disconnected from SQLite database.");
            } catch (SQLException e) {
                logger.error("Error disconnecting from SQLite database", e);
                throw e;
            }
        } else {
            logger.warn("Attempted to disconnect, but connection was already closed or null.");
        }
    }

    /**
     * Retrieves the current database connection.
     * Logs a warning if the connection is null.
     *
     * @return A Connection object representing the current database connection, or null if no connection exists.
     */
    @Override
    public Connection getConnection() {
        if (connection == null) {
            logger.warn("getConnection called but connection is null.");
        }
        return connection;
    }
}