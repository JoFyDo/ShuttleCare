package com.rocketshipcheckingtool.ui.helper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.rocketshipcheckingtool.ui.datamodel.QuestionnaireRating;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

class QuestionnaireUtilTest {

    private ClientRequests mockClientRequests;
    private final String user = "technician";

    @BeforeEach
    void setUp() {
        mockClientRequests = mock(ClientRequests.class);
    }

    @Nested
    @DisplayName("getQuestionnaireForShuttle")
    class GetQuestionnaireForShuttle {

        @Test
        @DisplayName("Returns questionnaire ratings successfully")
        void returnsQuestionnaireRatingsSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            String jsonResponse = "[{\"id\":1,\"rating\":5}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenReturn(jsonResponse);

            ArrayList<QuestionnaireRating> ratings = QuestionnaireUtil.getQuestionnaireForShuttle(mockClientRequests, user, shuttleID);

            assertNotNull(ratings);
            assertEquals(1, ratings.size());
            assertEquals(5, ratings.get(0).getRating());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> QuestionnaireUtil.getQuestionnaireForShuttle(mockClientRequests, user, shuttleID));
        }
    }

    @Nested
    @DisplayName("updateQuestionnaireStatus")
    class UpdateQuestionnaireStatus {

        @Test
        @DisplayName("Updates questionnaire status successfully")
        void updatesQuestionnaireStatusSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int questionnaireRatingID = 1;
            String status = "completed";
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");

            boolean result = QuestionnaireUtil.updateQuestionnaireStatus(mockClientRequests, user, questionnaireRatingID, status);

            assertTrue(result);
            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when update fails")
        void throwsConnectExceptionWhenUpdateFails() throws IOException, URISyntaxException, InterruptedException {
            int questionnaireRatingID = 1;
            String status = "completed";
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> QuestionnaireUtil.updateQuestionnaireStatus(mockClientRequests, user, questionnaireRatingID, status));
        }
    }
}