package com.rocketshipcheckingtool.domain;

public class Mechanic implements Manage{
    private String name;
    private String role;
    private int id;

    public Mechanic(int id, String name, String role) {
        this.name = name;
        this.role = role;
        this.id = id;
    }

    @Override
    public String getByI(int index) {
        return "";
    }

    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }

    @Override
    public String toString() {
        return "Mechanic{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", id=" + id +
                '}';
    }
}
