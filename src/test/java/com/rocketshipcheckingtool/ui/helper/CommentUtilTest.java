package com.rocketshipcheckingtool.ui.helper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.rocketshipcheckingtool.ui.helper.CommentUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.rocketshipcheckingtool.ui.datamodel.Comment;
import com.rocketshipcheckingtool.ui.roles.technician.ClientRequests;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

class CommentUtilTest {

    private ClientRequests mockClientRequests;
    private final String user = "technician";

    @BeforeEach
    void setUp() {
        mockClientRequests = mock(ClientRequests.class);
    }

    @Nested
    @DisplayName("getCommentsForShuttle")
    class GetCommentsForShuttle {

        @Test
        @DisplayName("Returns comments for a valid shuttle ID")
        void returnsCommentsForValidShuttleID() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            String jsonResponse = "[{\"id\":1,\"comment\":\"Test Comment\",\"shuttleId\":1}]";
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenReturn(jsonResponse);

            ArrayList<Comment> comments = CommentUtil.getCommentsForShuttle(mockClientRequests, user, shuttleID);

            assertNotNull(comments);
            assertEquals(1, comments.size());
            assertEquals("Test Comment", comments.get(0).getComment());
        }

        @Test
        @DisplayName("Throws ConnectException when request fails")
        void throwsConnectExceptionWhenRequestFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            when(mockClientRequests.getRequest(anyString(), eq(user), any(HashMap.class))).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> CommentUtil.getCommentsForShuttle(mockClientRequests, user, shuttleID));
        }
    }

    @Nested
    @DisplayName("updateComment")
    class UpdateComment {

        @Test
        @DisplayName("Updates comment status successfully")
        void updatesCommentStatusSuccessfully() throws IOException, URISyntaxException, InterruptedException {
            int commentID = 1;
            String status = "true";
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("success");
            boolean result = CommentUtil.updateComment(mockClientRequests, user, commentID, status);

            assertTrue(result);
            verify(mockClientRequests, times(1)).postRequest(anyString(), eq(user), any(HashMap.class));
        }

        @Test
        @DisplayName("Throws ConnectException when update fails")
        void throwsConnectExceptionWhenUpdateFails() throws IOException, URISyntaxException, InterruptedException {
            int commentID = 1;
            String status = "Completed";
            doThrow(new IOException("Connection error")).when(mockClientRequests).postRequest(anyString(), eq(user), any(HashMap.class));

            assertThrows(ConnectException.class, () -> CommentUtil.updateComment(mockClientRequests, user, commentID, status));
        }
    }

    @Nested
    @DisplayName("allCommandsDone")
    class AllCommandsDone {

        @Test
        @DisplayName("Returns true when all commands are done")
        void returnsTrueWhenAllCommandsAreDone() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("true");

            boolean result = CommentUtil.allCommandsDone(mockClientRequests, user, shuttleID);

            assertTrue(result);
        }

        @Test
        @DisplayName("Returns false when not all commands are done")
        void returnsFalseWhenNotAllCommandsAreDone() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenReturn("false");

            boolean result = CommentUtil.allCommandsDone(mockClientRequests, user, shuttleID);

            assertFalse(result);
        }

        @Test
        @DisplayName("Throws ConnectException when check fails")
        void throwsConnectExceptionWhenCheckFails() throws IOException, URISyntaxException, InterruptedException {
            int shuttleID = 1;
            when(mockClientRequests.postRequest(anyString(), eq(user), any(HashMap.class))).thenThrow(new IOException("Connection error"));

            assertThrows(ConnectException.class, () -> CommentUtil.allCommandsDone(mockClientRequests, user, shuttleID));
        }
    }
}