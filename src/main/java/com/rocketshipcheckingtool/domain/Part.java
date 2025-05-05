package com.rocketshipcheckingtool.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Part implements Manage{
    private int id;
    private String name;
    private String price;
    private int quantity;
    private BooleanProperty selected;

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
        }
        return selected;
    }

    public boolean isSelected() {
        return selected != null && selected.get();
    }

    @Override
    public String getByI(int index) {
        return "";
    }

    @Override
    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }
}
