package com.rocketshipcheckingtool.ui.datamodel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a shuttle with its properties such as name, status, landing time, and assigned mechanic.
 * Provides methods for property access, formatted landing time, and logging.
 */
public class Shuttle {
    private int id;
    private String shuttleName;
    private String status;
    private Calendar landingTime;
    private String mechanic;
    private static final Logger logger = LoggerFactory.getLogger(Shuttle.class);

    /**
     * Constructs a Shuttle object.
     *
     * @param id          the unique identifier of the shuttle
     * @param shuttleName the name of the shuttle
     * @param status      the current status of the shuttle
     * @param landingTime the landing time as a string (parsed to Calendar)
     * @param mechanic    the name of the assigned mechanic
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
     * Returns the unique identifier of the shuttle.
     *
     * @return the shuttle ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns a property of the shuttle by index.
     *
     * @param i the index (0: name, 1: status, 2: landing time, 3: mechanic)
     * @return the property value as a string, or null if index is invalid
     */
    public String getByI(int i) {
        return switch (i) {
            case 0 -> shuttleName;
            case 1 -> status;
            case 2 -> (landingTime.toString());
            case 3 -> mechanic;
            default -> null;
        };
    }

    /**
     * Returns the name of the shuttle.
     *
     * @return the shuttle name
     */
    public String getShuttleName() {
        return shuttleName;
    }

    /**
     * Returns the current status of the shuttle.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the shuttle.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the landing time as a formatted string.
     *
     * @return the landing time in "dd/MM/yyyy HH:mm" format
     */
    public String getLandingTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(landingTime.getTime());
    }

    /**
     * Returns the landing time as a Calendar object.
     *
     * @return the landing time
     */
    public Calendar getLandingTime() {
        return landingTime;
    }

    /**
     * Returns the name of the assigned mechanic.
     *
     * @return the mechanic's name
     */
    public String getMechanic() {
        return mechanic;
    }
}
