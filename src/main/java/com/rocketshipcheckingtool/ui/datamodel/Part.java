package com.rocketshipcheckingtool.ui.datamodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a part in the inventory system.
 * Contains information about the part's ID, name, price, quantity, and selection state.
 * Provides methods for property access and logging.
 */
public class Part {
    private int id;
    private String name;
    private String price;
    private int quantity;
    private BooleanProperty selected;
    private static final Logger logger = LoggerFactory.getLogger(Part.class);

    /**
     * Constructs a Part object.
     *
     * @param id       the unique identifier of the part
     * @param name     the name of the part
     * @param price    the price of the part
     * @param quantity the available quantity of the part
     */
    public Part(int id, String name, String price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Returns the unique identifier of the part.
     *
     * @return the part ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the part.
     *
     * @return the part name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the price of the part.
     *
     * @return the part price
     */
    public String getPrice() {
        return price;
    }

    /**
     * Returns the available quantity of the part.
     *
     * @return the part quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns the selection property for this part.
     * Initializes the property if it is not already set.
     *
     * @return the BooleanProperty representing selection state
     */
    public BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty(false);
            logger.debug("Initialized selected property for part '{}'", name);
        }
        return selected;
    }

    /**
     * Checks if the part is selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        boolean sel = selected != null && selected.get();
        logger.trace("Checked isSelected for part '{}': {}", name, sel);
        return sel;
    }

}
