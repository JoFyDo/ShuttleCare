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

    public static void updateDatabase(DatabaseConnection databaseConnection) {
        ArrayList<Task> tasks = databaseConnection.getActiveTasks();
        Map<String, ArrayList<Task>> taskMap = new HashMap<String, ArrayList<Task>>();
        for (Task task : tasks) {
            if (taskMap.containsKey(task.getShuttleName())){
                ArrayList<Task> listTask =  taskMap.get(task.getShuttleName());
                listTask.add(task);
            } else {
                ArrayList<Task> listTask = new ArrayList<>();
                listTask.add(task);
                taskMap.put(task.getShuttleName(), listTask);
            }
        }

        for (Map.Entry<String, ArrayList<Task>> entry : taskMap.entrySet()) {
            ArrayList<Task> taskList = entry.getValue();
            int c = 0;
            for (Task task : taskList) {
                if (task.getStatus().equals("Erledigt")){
                    c++;
                }
                if (c == taskList.size()) {
                    Shuttle shuttle = databaseConnection.getShuttle(task.getShuttleName());
                    databaseConnection.changeShuttleStatus(shuttle, "Erledigt - Warte auf Freigabe");
                }
            }
        }
    }
}
