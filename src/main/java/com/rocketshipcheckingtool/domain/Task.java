package com.rocketshipcheckingtool.domain;

public class Task implements Manage {
    private String task;
    private String status;
    private String mechanic;
    private Shuttle shuttle;
    private String shuttleName;

    public Task(String task, String status, String mechanic, Shuttle shuttle) {
        this.task = task;
        this.status = status;
        this.mechanic = mechanic;
        this.shuttle  = shuttle;
    }

    public Task(String task, String status, String mechanic, String shuttleName) {
        this.task = task;
        this.status = status;
        this.mechanic = mechanic;
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
            case 1 -> mechanic;
            case 2 -> status;
            case 3 -> shuttleName;
            default -> null;
        };
    }
}
