package com.rocketshipcheckingtool.server;

import com.rocketshipcheckingtool.domain.Manage;
import com.rocketshipcheckingtool.domain.Shuttle;
import com.rocketshipcheckingtool.domain.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Util {


    public static String combineJSONString(ArrayList<? extends Manage> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < items.size(); i++) {
            Manage item = items.get(i);
            sb.append(item.toJson());
            if (i < items.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append("\n]");
        return sb.toString();


    }

    public static void checkCurrentStatus(DatabaseConnection databaseConnection, int shuttleID, String status) {
        Map<Integer, String> shuttleStatusMap = new HashMap<>();
        if (status.equals("Inspektion 1") || status.equals("Inspektion 2")) {
            ArrayList<Task> generalActiveTasks = databaseConnection.getGeneralTasksForShuttle(shuttleID);
            ArrayList<Task> additionalActiveTasks = databaseConnection.getActiveTaskByShuttleID(shuttleID);
            int timeNeeded = 0;
            for (Task task : generalActiveTasks) {
                timeNeeded += task.getTimeNeeded();
            }
            for (Task task : additionalActiveTasks) {
                timeNeeded += task.getTimeNeeded();
            }
            databaseConnection.updatePredictedTime(shuttleID, timeNeeded);
        }


    }
}
