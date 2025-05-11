package com.rocketshipcheckingtool.server.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mechanic{
    private String name;
    private String role;
    private int id;
    private static final Logger logger = LoggerFactory.getLogger(Mechanic.class);

    public Mechanic(int id, String name, String role) {
        this.name = name;
        this.role = role;
        this.id = id;
        logger.debug("Created Mechanic: id={}, name='{}', role='{}'", id, name, role);
    }

    public String toJson() {
        String json = new com.google.gson.Gson().toJson(this);
        logger.debug("Serialized Mechanic with id {} to JSON: {}", id, json);
        return json;
    }

}
