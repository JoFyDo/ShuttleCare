package com.rocketshipcheckingtool.ui.datamodel;

public class Mechanic{
    private String name;
    private String role;
    private int id;

    public Mechanic(int id, String name, String role) {
        this.name = name;
        this.role = role;
        this.id = id;
    }

    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }

}
