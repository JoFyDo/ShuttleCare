package com.rocketshipcheckingtool.ui.datamodel;

public class Task{
    private String task;
    private Boolean status;
    private String mechanic;
    private Shuttle shuttle;
    private String shuttleName;
    private int id;
    private int timeNeeded;

    public Task(String task, Boolean status, String mechanic, Shuttle shuttle, int id) {
        this.task = task;
        this.status = status;
        this.mechanic = mechanic;
        this.shuttle  = shuttle;
        this.id = id;
    }

    public Task(String task, Boolean status, String mechanic, String shuttleName, int id, int timeNeeded) {
        this.task = task;
        this.status = status;
        this.mechanic = mechanic;
        this.shuttleName = shuttleName;
        this.id = id;
        this.timeNeeded = timeNeeded;
    }

    public Task (String task, Boolean status, int id, String shuttleName, int timeNeeded) {
        this.task = task;
        this.status = status;
        this.id = id;
        this.shuttleName = shuttleName;
        this.timeNeeded = timeNeeded;
    }

    public String getByI(int index) {
        return switch (index) {
            case 0 -> task;
            case 2 -> mechanic;
            case 3 -> {
                if (status) {
                    yield "In Bearbeitung";
                } else {
                    yield "Fertig";
                }
            }
            case 1 -> shuttleName;
            default -> null;
        };
    }

    public String getTask() {
        return task;
    }

    public Boolean getStatus() {
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

    public int getTimeNeeded() {
        return timeNeeded;
    }

    public int getId() { return id; }
}
