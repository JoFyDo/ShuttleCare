package com.rocketshipcheckingtool.server.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for managing database connections.
 * Provides methods to establish, retrieve, and disconnect database connections.
 */
public interface DatabaseConnector {

    /**
     * Establishes a connection to the database.
     *
     * @return A Connection object representing the database connection.
     * @throws SQLException If an error occurs while connecting to the database.
     */
    Connection connect() throws SQLException;

    /**
     * Disconnects the current database connection.
     *
     * @throws SQLException If an error occurs while disconnecting from the database.
     */
    void disconnect() throws SQLException;

    /**
     * Retrieves the current database connection.
     *
     * @return A Connection object representing the current database connection.
     */
    Connection getConnection();
}