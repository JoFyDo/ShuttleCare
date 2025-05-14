package com.rocketshipcheckingtool.ui.helper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.rocketshipcheckingtool.ui.datamodel.Notification;
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

class NotificationUtilTest {

    private ClientRequests mockClientRequests;
    private final String user = "technician";

    @BeforeEach
    void setUp() {
        mockClientRequests = mock(ClientRequests.class);
    }

    @Nested
    @DisplayName("requestNotifications")
    class RequestNotifications {

        @Test
        @DisplayName("Returns notifications successfully")
        void returnsNotificationsSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            String jsonResponse = "[{\"id\":1,\"message\":\"Test Notification\"}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenReturn(jsonResponse);

            ArrayList<Notification> notifications = NotificationUtil.requestNotifications(mockClientRequests, user);

            assertNotNull(notifications);
            assertEquals(1, notifications.size());
            assertEquals("Test Notification", notifications.get(0).getMessage());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> NotificationUtil.requestNotifications(mockClientRequests, user));
        }
    }

    @Nested
    @DisplayName("updateNotification")
    class UpdateNotification {

        @Test
        @DisplayName("Updates notification successfully")
        void updatesNotificationSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");

            NotificationUtil.updateNotification(mockClientRequests, user, 1, "read");

            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when update fails")
        void throwsConnectExceptionWhenUpdateFails() throws IOException, URISyntaxException, InterruptedException {
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> NotificationUtil.updateNotification(mockClientRequests, user, 1, "read"));
        }
    }

    @Nested
    @DisplayName("requestNotificationsByShuttle")
    class RequestNotificationsByShuttle {

        @Test
        @DisplayName("Returns notifications for a shuttle successfully")
        void returnsNotificationsForShuttleSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            String jsonResponse = "[{\"id\":1,\"message\":\"Shuttle Notification\"}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenReturn(jsonResponse);

            ArrayList<Notification> notifications = NotificationUtil.requestNotificationsByShuttle(mockClientRequests, user, 1);

            assertNotNull(notifications);
            assertEquals(1, notifications.size());
            assertEquals("Shuttle Notification", notifications.get(0).getMessage());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> NotificationUtil.requestNotificationsByShuttle(mockClientRequests, user, 1));
        }
    }

    @Nested
    @DisplayName("createNotification")
    class CreateNotification {

        @Test
        @DisplayName("Creates notification successfully")
        void createsNotificationSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");

            boolean result = NotificationUtil.createNotification(mockClientRequests, user, 1, "Test Message", "Sender", "Comment");

            assertTrue(result);
            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when creation fails")
        void throwsConnectExceptionWhenCreationFails() throws IOException, URISyntaxException, InterruptedException {
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> NotificationUtil.createNotification(mockClientRequests, user, 1, "Test Message", "Sender", "Comment"));
        }
    }
}