package com.rocketshipcheckingtool.ui.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.ui.datamodel.Part;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class for handling part-related operations such as retrieving, using,
 * and ordering parts for shuttles.
 */
public class PartUtil {

    private final static Logger logger = LoggerFactory.getLogger(PartUtil.class);

    /**
     * Retrieves all parts for a user.
     *
     * @param clientRequests the client requests handler
     * @param user the user performing the request
     * @return a list of parts
     * @throws IOException if the request fails
     */
    public static ArrayList<Part> getParts(ClientRequests clientRequests, String user) throws IOException {
        logger.info("Requesting parts for user '{}'", user);
        try {
            String tasks = clientRequests.getRequest("/requestParts", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Part>>() {}.getType();
            ArrayList<Part> parts = gson.fromJson(tasks, shuttleListType);
            logger.info("Received {} parts for user '{}'", parts != null ? parts.size() : 0, user);
            return parts;
        } catch (Exception e) {
            logger.error("Failed to get parts for user '{}': {}", user, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Uses a specified quantity of a part.
     *
     * @param clientRequests the client requests handler
     * @param user the user performing the operation
     * @param partID the ID of the part to use
     * @param quantity the quantity to use
     * @throws IOException if the operation fails
     */
    public static void usePart(ClientRequests clientRequests, String user, int partID, int quantity) throws IOException {
        logger.info("Using part ID {} with quantity {} for user '{}'", partID, quantity, user);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("PartID", String.valueOf(partID));
            params.put("Quantity", String.valueOf(quantity));
            clientRequests.postRequest("/usePart", user, params);
            logger.debug("Part ID {} used with quantity {}", partID, quantity);
        } catch (Exception e) {
            logger.error("Failed to use part ID {}: {}", partID, e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Orders a specified quantity of a part for a shuttle.
     *
     * @param clientRequests the client requests handler
     * @param user the user performing the operation
     * @param partId the ID of the part to order
     * @param quantity the quantity to order
     * @param shuttleId the ID of the shuttle (nullable)
     * @throws IOException if the operation fails
     */
    public static void orderPart(ClientRequests clientRequests, String user, int partId, int quantity, Integer shuttleId) throws IOException {
        logger.info("Ordering part ID {} with quantity {} for user '{}', shuttle ID {}", partId, quantity, user, shuttleId);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("PartID", String.valueOf(partId));
            params.put("Quantity", String.valueOf(quantity));
            params.put("ShuttleID", String.valueOf(shuttleId));
            clientRequests.postRequest("/orderPart", user, params);
            logger.debug("Part ID {} ordered with quantity {} for shuttle ID {}", partId, quantity, shuttleId);
        } catch (Exception e) {
            logger.error("Failed to order part ID {}: {}", partId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
