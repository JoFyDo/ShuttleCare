package com.rocketshipcheckingtool.ui.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a mechanic with an ID and name.
 * Provides methods for JSON serialization and property access.
 * Includes logging for creation and serialization.
 */
public class Mechanic{
    private String name;
    private int id;
    private static final Logger logger = LoggerFactory.getLogger(Mechanic.class);

    /**
     * Constructs a Mechanic object.
     *
     * @param id   the unique identifier of the mechanic
     * @param name the name of the mechanic
     */
    public Mechanic(int id, String name) {
        this.name = name;
        this.id = id;
        logger.debug("Created Mechanic: id={}, name='{}', role='{}'", id, name);
    }

    /**
     * Serializes this Mechanic object to a JSON string.
     *
     * @return the JSON representation of this mechanic
     */
    public String toJson() {
        String json = new com.google.gson.Gson().toJson(this);
        logger.debug("Serialized Mechanic with id {} to JSON: {}", id, json);
        return json;
    }

    /**
     * Returns the name of the mechanic.
     *
     * @return the mechanic's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the unique identifier of the mechanic.
     *
     * @return the mechanic's ID
     */
    public int getId() {
        return id;
    }

}

