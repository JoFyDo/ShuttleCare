package com.rocketshipcheckingtool.server.datamodel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.rocketshipcheckingtool.server.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a shuttle with its details such as ID, name, status, landing time, and assigned mechanic.
 * Provides methods to retrieve shuttle information and convert it to JSON format.
 */
public class Shuttle {
    private int id; // Unique identifier for the shuttle.
    private String shuttleName; // Name of the shuttle.
    private String status; // Current status of the shuttle.
    private Calendar landingTime; // Landing time of the shuttle.
    private String mechanic; // Name of the mechanic assigned to the shuttle.
    private static final Logger logger = LoggerFactory.getLogger(Shuttle.class); // Logger for logging shuttle-related events.

    /**
     * Constructs a Shuttle object with the specified details.
     *
     * @param id          The unique identifier for the shuttle.
     * @param shuttleName The name of the shuttle.
     * @param status      The current status of the shuttle.
     * @param landingTime The landing time of the shuttle as a string.
     * @param mechanic    The name of the mechanic assigned to the shuttle.
     */
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

    /**
     * Gets the unique identifier of the shuttle.
     *
     * @return The shuttle ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Converts the shuttle object to a JSON string representation.
     *
     * @return The JSON string representation of the shuttle.
     */
    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }

    /**
     * Gets the name of the shuttle.
     *
     * @return The shuttle name.
     */
    public String getShuttleName() {
        return shuttleName;
    }

    /**
     * Gets the current status of the shuttle.
     *
     * @return The shuttle status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the landing time of the shuttle.
     *
     * @return The landing time as a Calendar object.
     */
    public Calendar getLandingTime() {
        return landingTime;
    }

    /**
     * Gets the name of the mechanic assigned to the shuttle.
     *
     * @return The mechanic's name.
     */
    public String getMechanic() {
        return mechanic;
    }

    /**
     * Gets the predicted landing time of the shuttle as a formatted string.
     *
     * @return The predicted landing time in the format "yyyy-MM-dd HH:mm:ss".
     */
    public String getPredictedLandingTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(landingTime.getTime());
    }
}