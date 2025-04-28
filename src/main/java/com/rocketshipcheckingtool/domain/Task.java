package com.rocketshipcheckingtool.domain;

public class Task implements Manage {
    private String task;
    private String status;
    private String mechanic;
    private Shuttle shuttle;
    private String shuttleName;
    private int id;

    public Task(String task, String status, String mechanic, Shuttle shuttle, int id) {
        this.task = task;
        this.status = status;
        this.mechanic = mechanic;
        this.shuttle  = shuttle;
        this.id = id;
    }

    public Task(String task, String status, String mechanic, String shuttleName, int id) {
        this.task = task;
        this.status = status;
        this.mechanic = mechanic;
        this.shuttleName = shuttleName;
        this.id = id;
    }

    public Task (String task, String status, int id, String shuttleName) {
        this.task = task;
        this.status = status;
        this.id = id;
        this.shuttleName = shuttleName;
    }

    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }

    @Override
    public String toString() {
        return "Tasks{" +
                "task='" + task + '\'' +
                ", status='" + status + '\'' +
                ", mechanic='" + mechanic + '\'' +
                ", shuttle=" + shuttleName +
                '}';
    }

    @Override
    public String getByI(int index) {
        return switch (index) {
            case 0 -> task;
            case 2 -> mechanic;
            case 3 -> status;
            case 1 -> shuttleName;
            default -> null;
        };
    }

    public String getTask() {
        return task;
    }

    public String getStatus() {
        return status;
    }

    public String getMechanic() {
        return mechanic;
    }

    public Shuttle getShuttle() {
        return shuttle;
    }

    public String getShuttleName() {
        return shuttleName;
    }

    public int getId() { return id; }
}
