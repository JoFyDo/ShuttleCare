package com.rocketshipcheckingtool.server.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a mechanic in the rocketship checking tool.
 * Contains details such as the mechanic's ID and name.
 * Provides methods to retrieve mechanic information and convert it to JSON format.
 */
public class Mechanic {
    private String name; // The name of the mechanic.
    private int id; // The unique identifier for the mechanic.
    private static final Logger logger = LoggerFactory.getLogger(Mechanic.class); // Logger for logging mechanic-related events.

    /**
     * Constructs a Mechanic object with the specified ID and name.
     *
     * @param id   The unique identifier for the mechanic.
     * @param name The name of the mechanic.
     */
    public Mechanic(int id, String name) {
        this.name = name;
        this.id = id;
        logger.debug("Created Mechanic: id={}, name='{}'", id, name);
    }

    /**
     * Converts the mechanic object to a JSON string representation.
     *
     * @return The JSON string representation of the mechanic.
     */
    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }

    /**
     * Gets the name of the mechanic.
     *
     * @return The mechanic's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the unique identifier of the mechanic.
     *
     * @return The mechanic's ID.
     */
    public int getId() {
        return id;
    }
}