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
            this.connection = connect();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(database);
        logger.info("Connected to SQLite!");
        return c;
    }

    @Override
    public void disconnect() throws SQLException {
        connection.close();
        logger.info("Disconnected from SQLite!");
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}