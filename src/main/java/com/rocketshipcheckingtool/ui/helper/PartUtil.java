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

public class PartUtil {

    private final static Logger logger = LoggerFactory.getLogger(PartUtil.class);

    public static ArrayList<Part> getParts(ClientRequests clientRequests, String user) throws IOException {
        try {
            String tasks = clientRequests.getRequest("/requestParts", user, null);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<Part>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void usePart(ClientRequests clientRequests, String user, int partID, int quantity) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("PartID", String.valueOf(partID));
            params.put("Quantity", String.valueOf(quantity));
            clientRequests.postRequest("/usePart", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static void orderPart(ClientRequests clientRequests, String user, int partId, int quantity, Integer shuttleId) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("PartID", String.valueOf(partId));
            params.put("Quantity", String.valueOf(quantity));
            params.put("ShuttleID", String.valueOf(shuttleId));
            clientRequests.postRequest("/orderPart", user, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
