package com.rocketshipcheckingtool.domain;

import java.sql.Time;
import java.sql.Date;

public class Shuttle implements Manage {
    private int id;
    private String shuttleName;
    private String status;
    private Date landungDate;
    private Time landungTime;
    private String mechanic;

    public Shuttle(int id, String shuttleName, String status, Date landungDate, Time landungTime, String mechanic) {
        this.id = id;
        this.shuttleName = shuttleName;
        this.status = status;
        this.landungDate = landungDate;
        this.landungTime = landungTime;
        this.mechanic = mechanic;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getByI(int i) {
        return switch (i) {
            case 0 -> shuttleName;
            case 1 -> status;
            case 2 -> (landungDate.toString() + " " + landungTime.toString());
            case 3 -> mechanic;
            default -> null;
        };
    }

    public String getName() {
        return shuttleName;
    }

    public String getStatus() {
        return status;
    }

    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }

    @Override
    public String toString() {
        return "Shuttle{" +
                "id=" + id +
                ", shuttleName='" + shuttleName + '\'' +
                ", status='" + status + '\'' +
                ", landunngDate=" + landungDate +
                ", landunngTime=" + landungTime +
                ", mechanic='" + mechanic + '\'' +
                '}';
    }
}
