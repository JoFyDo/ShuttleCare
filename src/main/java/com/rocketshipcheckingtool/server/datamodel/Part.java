package com.rocketshipcheckingtool.server.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a part used in the rocketship checking tool.
 * Contains details such as the part's ID, name, price, quantity, and selection status.
 */
public class Part {
    private final int id; // Unique identifier for the part.
    private final String name; // Name of the part.
    private final String price; // Price of the part as a string.
    private int quantity; // Quantity of the part available.
    private static final Logger logger = LoggerFactory.getLogger(Part.class); // Logger for logging part-related events.

    /**
     * Constructs a Part object with the specified details.
     *
     * @param id       The unique identifier for the part.
     * @param name     The name of the part.
     * @param price    The price of the part.
     * @param quantity The quantity of the part available.
     */
    public Part(int id, String name, String price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Gets the unique identifier of the part.
     *
     * @return The part ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the part.
     *
     * @return The part name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the price of the part.
     *
     * @return The part price.
     */
    public String getPrice() {
        return price;
    }

    /**
     * Gets the quantity of the part available.
     *
     * @return The part quantity.
     */
    public int getQuantity() {
        return quantity;
    }
}