package com.rocketshipcheckingtool;

import com.rocketshipcheckingtool.server.datamodel.Shuttle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class AddShuttle {
    public static void main(String[] args) throws SQLException {
        try {
            Connection c = DriverManager.getConnection("jdbc:sqlite:RocketDatabase.db");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter shuttle name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Mechaniker: ");
            String mechaniker = scanner.nextLine();
            System.out.print("Enter Landung (YYYY-MM-DD HH:MM:SS): ");
            String landung = scanner.nextLine();
            String query = "INSERT INTO Shuttles (Name, Status, Landung, Mechaniker) VALUES (?, 'Gelandet', ?, ?)";
            try (var stmt = c.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, landung);
                stmt.setString(3, mechaniker);
                stmt.executeUpdate();
                System.out.println("Shuttle added successfully.");
            } catch (SQLException e) {
                System.out.println("Error adding shuttle: " + e.getMessage());
            } finally {
                c.close();
            }
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }


    }
}
