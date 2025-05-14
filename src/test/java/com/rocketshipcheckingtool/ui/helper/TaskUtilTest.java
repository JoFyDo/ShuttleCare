package com.rocketshipcheckingtool.ui.helper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.rocketshipcheckingtool.ui.datamodel.Mechanic;
import com.rocketshipcheckingtool.ui.datamodel.Task;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

class TaskUtilTest {

    private ClientRequests mockClientRequests;
    private final String user = "technician";

    @BeforeEach
    void setUp() {
        mockClientRequests = mock(ClientRequests.class);
    }

    @Nested
    @DisplayName("getActiveTasks")
    class GetActiveTasks {

        @Test
        @DisplayName("Returns active tasks successfully")
        void returnsActiveTasksSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            String jsonResponse = "[{\"id\":1,\"task\":\"Task A\"},{\"id\":2,\"description\":\"Task B\"}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenReturn(jsonResponse);

            ArrayList<Task> tasks = TaskUtil.getActiveTasks(mockClientRequests, user);

            assertNotNull(tasks);
            assertEquals(2, tasks.size());
            assertEquals("Task A", tasks.get(0).getTask());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> TaskUtil.getActiveTasks(mockClientRequests, user));
        }
    }

    @Nested
    @DisplayName("getActiveTasksByShuttleID")
    class GetActiveTasksByShuttleID {

        @Test
        @DisplayName("Returns active tasks for a shuttle successfully")
        void returnsActiveTasksForShuttleSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            String jsonResponse = "[{\"id\":1,\"task\":\"Task A\"}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenReturn(jsonResponse);

            ArrayList<Task> tasks = TaskUtil.getActiveTasksByShuttleID(mockClientRequests, user, shuttleID);

            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertEquals("Task A", tasks.get(0).getTask());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> TaskUtil.getActiveTasksByShuttleID(mockClientRequests, user, shuttleID));
        }
    }

    @Nested
    @DisplayName("updateTaskStatus")
    class UpdateTaskStatus {

        @Test
        @DisplayName("Updates task status successfully")
        void updatesTaskStatusSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int taskID = 1;
            String status = "completed";
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");
            TaskUtil.updateTaskStatus(mockClientRequests, user, taskID, status);

            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when update fails")
        void throwsConnectExceptionWhenUpdateFails() throws IOException, URISyntaxException, InterruptedException {
            int taskID = 1;
            String status = "completed";
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> TaskUtil.updateTaskStatus(mockClientRequests, user, taskID, status));
        }
    }

    @Nested
    @DisplayName("createTask")
    class CreateTask {

        @Test
        @DisplayName("Creates a task successfully")
        void createsTaskSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            Mechanic mechanic = new Mechanic(1, "John Doe");
            String description = "Fix engine";
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");
            TaskUtil.createTask(mockClientRequests, user, mechanic, description, shuttleID);

            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when task creation fails")
        void throwsConnectExceptionWhenTaskCreationFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            Mechanic mechanic = new Mechanic(1, "John Doe");
            String description = "Fix engine";
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenThrow(new IOException("Connection error"));
            assertThrows(ConnectException.class, () -> TaskUtil.createTask(mockClientRequests, user, mechanic, description, shuttleID));
        }
    }
}