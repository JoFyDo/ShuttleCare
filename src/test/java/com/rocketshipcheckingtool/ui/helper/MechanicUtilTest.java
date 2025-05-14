package com.rocketshipcheckingtool.ui.helper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.rocketshipcheckingtool.ui.datamodel.Mechanic;
import com.rocketshipcheckingtool.ui.helper.MechanicUtil;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.ArrayList;

class MechanicUtilTest {

    private ClientRequests mockClientRequests;
    private final String user = "technician";

    @BeforeEach
    void setUp() {
        mockClientRequests = mock(ClientRequests.class);
    }

    @Nested
    @DisplayName("getMechanics")
    class GetMechanics {

        @Test
        @DisplayName("Returns a list of mechanics successfully")
        void returnsListOfMechanicsSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            String jsonResponse = "[{\"id\":1,\"name\":\"John Doe\"},{\"id\":2,\"name\":\"Jane Smith\"}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenReturn(jsonResponse);

            ArrayList<Mechanic> mechanics = MechanicUtil.getMechanics(mockClientRequests, user);

            assertNotNull(mechanics);
            assertEquals(2, mechanics.size());
            assertEquals("John Doe", mechanics.get(0).getName());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> MechanicUtil.getMechanics(mockClientRequests, user));
        }
    }

    @Nested
    @DisplayName("getMechanic")
    class GetMechanic {

        @Test
        @DisplayName("Returns a mechanic by ID successfully")
        void returnsMechanicByIDSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            String jsonResponse = "{\"id\":1,\"name\":\"John Doe\"}";
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenReturn(jsonResponse);

            Mechanic mechanic = MechanicUtil.getMechanic(mockClientRequests, user, 1);

            assertNotNull(mechanic);
            assertEquals(1, mechanic.getId());
            assertEquals("John Doe", mechanic.getName());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            when(mockClientRequests.getRequest(anyString(), eq(user), isNull())).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> MechanicUtil.getMechanic(mockClientRequests, user, 1));
        }
    }
}