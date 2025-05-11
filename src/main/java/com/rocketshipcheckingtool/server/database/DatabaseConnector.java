package com.rocketshipcheckingtool.server.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnector {
    Connection connect() throws SQLException;
    void disconnect() throws SQLException;
    Connection getConnection();
}