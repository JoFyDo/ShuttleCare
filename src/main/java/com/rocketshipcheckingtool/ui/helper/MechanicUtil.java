package com.rocketshipcheckingtool.ui.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.ui.datamodel.Mechanic;
import com.rocketshipcheckingtool.ui.datamodel.Notification;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;

public class MechanicUtil {

    private final static Logger logger = LoggerFactory.getLogger(MechanicUtil.class);


    public static ArrayList<Mechanic> getMechanics(ClientRequests clientRequests, String user) throws IOException {
        logger.info("Requesting all mechanics for user '{}'", user);
        try {
            String tasks = clientRequests.getRequest("/requestMechanics", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Mechanic>>() {}.getType();
            ArrayList<Mechanic> mechanics = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} notifications for user '{}'", mechanics != null ? mechanics.size() : 0, user);
            return mechanics;
        } catch (Exception e) {
            logger.error("Failed to request notifications for user '{}': {}", user, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    public static Mechanic getMechanic(ClientRequests clientRequests, String user, int mechanicID) throws IOException {
        logger.info("Requesting mechanic with ID {} for user '{}'", mechanicID, user);
        try {
            String tasks = clientRequests.getRequest("/requestMechanic", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<Mechanic>() {}.getType();
            Mechanic mechanic = gson.fromJson(tasks, shuttleListType);
            logger.info("Received mechanic with ID {} for user '{}'", mechanic != null ? mechanic.getId() : 0, user);
            return mechanic;
        } catch (Exception e) {
            logger.error("Failed to request mechanic with ID {}: {}", mechanicID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

}

