package com.rocketshipcheckingtool.ui.datamodel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Shuttle {
    private int id;
    private String shuttleName;
    private String status;
    private Calendar landingTime;
    private String mechanic;

    public Shuttle(int id, String shuttleName, String status, String landingTime, String mechanic) {
        this.id = id;
        this.shuttleName = shuttleName;
        this.status = status;
        this.mechanic = mechanic;

        this.landingTime = Calendar.getInstance();
        if (landingTime != null && !landingTime.isEmpty()) {
            try {
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(landingTime);
                this.landingTime.setTimeInMillis(timestamp.getTime());
            } catch (IllegalArgumentException e) {
                // Fallback to current time if parsing fails
                System.err.println("Failed to parse landing time: " + landingTime);
            }
        }


    }

    public int getId() {
        return id;
    }

    public String getByI(int i) {
        return switch (i) {
            case 0 -> shuttleName;
            case 1 -> status;
            case 2 -> (landingTime.toString());
            case 3 -> mechanic;
            default -> null;
        };
    }

    public String getShuttleName() {
        return shuttleName;
    }

    public String getStatus() {
        return status;
    }

    public String getLandingTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(landingTime.getTime());
    }

    public Calendar getLandingTime() {
        return landingTime;
    }

    public String getMechanic() {
        return mechanic;
    }
}
