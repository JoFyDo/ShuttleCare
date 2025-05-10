package com.rocketshipcheckingtool.ui.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rocketshipcheckingtool.ui.datamodel.QuestionnaireRating;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

public class QuestionnaireUtil {

    private final static Logger logger = LoggerFactory.getLogger(QuestionnaireUtil.class);

    public static ArrayList<QuestionnaireRating> getQuestionnaireForShuttle(ClientRequests clientRequests, String user, int shuttleID) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ShuttleID", String.valueOf(shuttleID));
            String tasks = clientRequests.getRequest("/requestQuestionnaireForShuttle", user, params);
            Gson gson = new Gson();
            Type shuttleListType = new TypeToken<ArrayList<QuestionnaireRating>>() {}.getType();
            return gson.fromJson(tasks, shuttleListType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }

    public static boolean updateQuestionnaireStatus(ClientRequests clientRequests, String user, int questionnaireRatingID, String status) throws IOException {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("QuestionnaireRatingID", String.valueOf(questionnaireRatingID));
            params.put("Status", status);
            clientRequests.postRequest("/updateQuestionnaire", user, params);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConnectException(e.getMessage());
        }
    }
}
