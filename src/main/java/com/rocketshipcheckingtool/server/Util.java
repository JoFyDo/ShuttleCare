package com.rocketshipcheckingtool.server;

import com.rocketshipcheckingtool.domain.Manage;

import java.util.ArrayList;

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
}
