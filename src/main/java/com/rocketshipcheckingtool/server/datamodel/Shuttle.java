package com.rocketshipcheckingtool.server.datamodel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.rocketshipcheckingtool.server.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shuttle {
    private int id;
    private String shuttleName;
    private String status;
    private Calendar landingTime;
    private String mechanic;
    private static final Logger logger = LoggerFactory.getLogger(Shuttle.class);

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
                logger.debug("Parsed landing time '{}' for shuttle '{}'", landingTime, shuttleName);
            } catch (IllegalArgumentException e) {
                // Fallback to current time if parsing fails
                logger.error("Failed to parse landing time '{}', using current time for shuttle '{}'", landingTime, shuttleName, e);
            }
        }
    }

    public int getId() {
        return id;
    }

    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }

    public String getShuttleName() {
        return shuttleName;
    }

    public String getStatus() {
        return status;
    }

    public Calendar getLandingTime() {
        return landingTime;
    }

    public String getMechanic() {
        return mechanic;
    }

    public String getPredictedLandingTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(landingTime.getTime());
    }

}
