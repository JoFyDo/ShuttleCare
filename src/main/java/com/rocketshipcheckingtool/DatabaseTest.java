package com.rocketshipcheckingtool;

import com.rocketshipcheckingtool.server.DatabaseConnection;

public class DatabaseTest {
    public static void main(String[] args) {
        DatabaseConnection db = new DatabaseConnection();
        System.out.println(db.getShuttles().toString());
        System.out.println(db.getTasks().toString());
    }
}
