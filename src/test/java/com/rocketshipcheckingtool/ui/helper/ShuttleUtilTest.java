package com.rocketshipcheckingtool.ui.helper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.rocketshipcheckingtool.ui.datamodel.Shuttle;
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

class ShuttleUtilTest {

    private ClientRequests mockClientRequests;
    private final String user = "technician";

    @BeforeEach
    void setUp() {
        mockClientRequests = mock(ClientRequests.class);
    }

    @Nested
    @DisplayName("getShuttles")
    class GetShuttles {

        @Test
        @DisplayName("Returns shuttles successfully")
        void returnsShuttlesSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            String jsonResponse = "[{\"id\":1,\"shuttleName\":\"Shuttle A\"},{\"id\":2,\"shuttleName\":\"Shuttle B\"}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenReturn(jsonResponse);

            ArrayList<Shuttle> shuttles = ShuttleUtil.getShuttles(mockClientRequests, user);

            assertNotNull(shuttles);
            assertEquals(2, shuttles.size());
            assertEquals("Shuttle A", shuttles.get(0).getShuttleName());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> ShuttleUtil.getShuttles(mockClientRequests, user));
        }
    }

    @Nested
    @DisplayName("getShuttle")
    class GetShuttle {

        @Test
        @DisplayName("Returns a specific shuttle successfully")
        void returnsSpecificShuttleSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            String jsonResponse = "{\"id\":1,\"shuttleName\":\"Shuttle A\"}";
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenReturn(jsonResponse);
            Shuttle shuttle = ShuttleUtil.getShuttle(mockClientRequests, user, shuttleID);

            assertNotNull(shuttle);
            assertEquals(1, shuttle.getId());
            assertEquals("Shuttle A", shuttle.getShuttleName());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> ShuttleUtil.getShuttle(mockClientRequests, user, shuttleID));
        }
    }

    @Nested
    @DisplayName("updateShuttleStatus")
    class UpdateShuttleStatus {

        @Test
        @DisplayName("Updates shuttle status successfully")
        void updatesShuttleStatusSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            String status = "active";
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");

            ShuttleUtil.updateShuttleStatus(mockClientRequests, user, shuttleID, status);

            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when update fails")
        void throwsConnectExceptionWhenUpdateFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            String status = "active";
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> ShuttleUtil.updateShuttleStatus(mockClientRequests, user, shuttleID, status));
        }
    }

    @Nested
    @DisplayName("updatePredictedReleaseTime")
    class UpdatePredictedReleaseTime {

        @Test
        @DisplayName("Updates predicted release time successfully")
        void updatesPredictedReleaseTimeSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            String status = "2023-12-31T23:59:59";
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");

            boolean result = ShuttleUtil.updatePredictedReleaseTime(mockClientRequests, user, shuttleID, status);

            assertTrue(result);
            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when update fails")
        void throwsConnectExceptionWhenUpdateFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            String status = "2023-12-31T23:59:59";
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> ShuttleUtil.updatePredictedReleaseTime(mockClientRequests, user, shuttleID, status));
        }
    }
}