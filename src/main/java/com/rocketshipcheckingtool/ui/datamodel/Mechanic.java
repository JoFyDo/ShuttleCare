package com.rocketshipcheckingtool.ui.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mechanic{
    private String name;
    private int id;
    private static final Logger logger = LoggerFactory.getLogger(Mechanic.class);

    public Mechanic(int id, String name) {
        this.name = name;
        this.id = id;
        logger.debug("Created Mechanic: id={}, name='{}', role='{}'", id, name);
    }

    public String toJson() {
        String json = new com.google.gson.Gson().toJson(this);
        logger.debug("Serialized Mechanic with id {} to JSON: {}", id, json);
        return json;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}
