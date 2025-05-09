package com.rocketshipcheckingtool;

import com.rocketshipcheckingtool.server.DatabaseConnection;

import java.util.Scanner;

public class ResetShuttle {
    public static void main(String[] args) {
        // Create a new instance of the DatabaseConnection class
        DatabaseConnection databaseConnection = new DatabaseConnection();

        // Call the resetShuttle method to reset the shuttle status
        databaseConnection.getShuttles().forEach(shuttle -> {
            System.out.println(shuttle.getShuttleName() + " ID: " +shuttle.getId());
        });
        System.out.println("Bitte ID des Shuttles eingeben, das zurückgesetzt werden soll:");
        Scanner scanner = new Scanner(System.in);
        int shuttleId = 0;
        try {
            shuttleId = Integer.parseInt(scanner.nextLine());
            databaseConnection.changeShuttleStatus(shuttleId, "Gelandet");
            databaseConnection.updateAllTasksStatusBelongToShuttle(shuttleId, "Offen");
            databaseConnection.updateAllGeneralTasksStatusBelongToShuttle(shuttleId, "false");
            databaseConnection.setPredictedReleaseTime(shuttleId, null);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid shuttle ID.");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.exit(1);
        }

        // Print a message indicating that the shuttle has been reset
        System.out.println("Shuttle status has been reset.");

        System.out.println("Zusätzliche Aufgaben löschen?(Y/N)");
        try{
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                databaseConnection.updateAllTasksActivityBelongToShuttle(shuttleId, "false");
                System.out.println("Alle Aufgaben wurden gelöscht.");
            } else if (input.equalsIgnoreCase("N")) {
                System.out.println("Aufgaben wurden nicht gelöscht.");
            } else {
                System.out.println("Ungültige Eingabe. Bitte Y oder N eingeben.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
