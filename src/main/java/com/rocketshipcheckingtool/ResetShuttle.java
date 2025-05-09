package com.rocketshipcheckingtool;

import com.rocketshipcheckingtool.server.DatabaseConnection;

import java.util.ArrayList;
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
        ArrayList<Integer> shuttles = new ArrayList<>();
        try {
            shuttleId = Integer.parseInt(scanner.nextLine());
            if (shuttleId < 0) {
                databaseConnection.getShuttles().forEach(shuttle -> {shuttles.add(shuttle.getId());});
            } else {
                shuttles.add(shuttleId);
            }
            for (int i = 0; i < shuttles.size(); i++) {
                databaseConnection.changeShuttleStatus(shuttles.get(i), "Flug");
                databaseConnection.updateAllTasksStatusBelongToShuttle(shuttles.get(i), "Offen");
                databaseConnection.updateAllGeneralTasksStatusBelongToShuttle(shuttles.get(i), "false");
                databaseConnection.setPredictedReleaseTime(shuttles.get(i), null);
                System.out.println("Shuttle ID " + shuttles.get(i) + " wurde zurückgesetzt.");
            }
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
                for (int i = 0; i < shuttles.size(); i++) {
                    databaseConnection.updateAllTasksActivityBelongToShuttle(shuttles.get(i), "false");
                    System.out.println("Alle Aufgaben für Shuttle ID " + shuttles.get(i) + " wurden gelöscht.");
                }
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
