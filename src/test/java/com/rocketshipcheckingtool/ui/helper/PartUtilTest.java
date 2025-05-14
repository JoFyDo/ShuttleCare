package com.rocketshipcheckingtool.ui.helper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.rocketshipcheckingtool.ui.datamodel.Part;
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

class PartUtilTest {

    private ClientRequests mockClientRequests;
    private final String user = "technician";

    @BeforeEach
    void setUp() {
        mockClientRequests = mock(ClientRequests.class);
    }

    @Nested
    @DisplayName("getParts")
    class GetParts {

        @Test
        @DisplayName("Returns parts successfully")
        void returnsPartsSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            String jsonResponse = "[{\"id\":1,\"name\":\"Part A\"},{\"id\":2,\"name\":\"Part B\"}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenReturn(jsonResponse);

            ArrayList<Part> parts = PartUtil.getParts(mockClientRequests, user);

            assertNotNull(parts);
            assertEquals(2, parts.size());
            assertEquals("Part A", parts.get(0).getName());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> PartUtil.getParts(mockClientRequests, user));
        }
    }

    @Nested
    @DisplayName("usePart")
    class UsePart {

        @Test
        @DisplayName("Uses part successfully")
        void usesPartSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");

            PartUtil.usePart(mockClientRequests, user, 1, 10);

            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when use fails")
        void throwsConnectExceptionWhenUseFails() throws IOException, URISyntaxException, InterruptedException {
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> PartUtil.usePart(mockClientRequests, user, 1, 10));
        }
    }

    @Nested
    @DisplayName("orderPart")
    class OrderPart {

        @Test
        @DisplayName("Orders part successfully")
        void ordersPartSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");

            PartUtil.orderPart(mockClientRequests, user, 1, 5, 100);

            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws RuntimeException when order fails")
        void throwsRuntimeExceptionWhenOrderFails() throws IOException, URISyntaxException, InterruptedException {
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(RuntimeException.class, () -> PartUtil.orderPart(mockClientRequests, user, 1, 5, 100));
        }
    }
}