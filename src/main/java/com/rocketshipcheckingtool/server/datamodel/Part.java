package com.rocketshipcheckingtool.server.datamodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Part {
    private int id;
    private String name;
    private String price;
    private int quantity;
    private BooleanProperty selected;
    private static final Logger logger = LoggerFactory.getLogger(Part.class);

    public Part(int id, String name, String price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty(false);
            logger.debug("Initialized selected property for part '{}'", name);
        }
        return selected;
    }

    public boolean isSelected() {
        boolean sel = selected != null && selected.get();
        logger.trace("Checked isSelected for part '{}': {}", name, sel);
        return sel;
    }

}
