package com.rocketshipcheckingtool.ui.helper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.rocketshipcheckingtool.ui.datamodel.Task;
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

class GeneralTaskUtilTest {

    private ClientRequests mockClientRequests;
    private final String user = "technician";

    @BeforeEach
    void setUp() {
        mockClientRequests = mock(ClientRequests.class);
    }

    @Nested
    @DisplayName("getGeneralTasksByShuttleID")
    class GetGeneralTasksByShuttleID {

        @Test
        @DisplayName("Returns general tasks for a valid shuttle ID")
        void returnsGeneralTasksForValidShuttleID() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            String jsonResponse = "[{\"id\":1,\"task\":\"Test Task\",\"shuttleId\":1}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenReturn(jsonResponse);

            ArrayList<Task> tasks = GeneralTaskUtil.getGeneralTasksByShuttleID(mockClientRequests, user, shuttleID);

            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertEquals("Test Task", tasks.get(0).getTask());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> GeneralTaskUtil.getGeneralTasksByShuttleID(mockClientRequests, user, shuttleID));
        }
    }

    @Nested
    @DisplayName("updateGeneralTask")
    class UpdateGeneralTask {

        @Test
        @DisplayName("Updates general task status successfully")
        void updatesGeneralTaskStatusSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int taskID = 1;
            boolean status = true;
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");

            GeneralTaskUtil.updateGeneralTask(mockClientRequests, user, taskID, status);

            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when update fails")
        void throwsConnectExceptionWhenUpdateFails() throws IOException, URISyntaxException, InterruptedException {
            int taskID = 1;
            boolean status = true;
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> GeneralTaskUtil.updateGeneralTask(mockClientRequests, user, taskID, status));
        }
    }

    @Nested
    @DisplayName("updateAllGeneralTasksStatusBelongToShuttle")
    class UpdateAllGeneralTasksStatusBelongToShuttle {

        @Test
        @DisplayName("Updates all general tasks for a shuttle successfully")
        void updatesAllGeneralTasksForShuttleSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            boolean status = true;
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");

            GeneralTaskUtil.updateAllGeneralTasksStatusBelongToShuttle(mockClientRequests, user, shuttleID, status);

            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when update fails")
        void throwsConnectExceptionWhenUpdateFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            boolean status = true;
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> GeneralTaskUtil.updateAllGeneralTasksStatusBelongToShuttle(mockClientRequests, user, shuttleID, status));
        }
    }
}