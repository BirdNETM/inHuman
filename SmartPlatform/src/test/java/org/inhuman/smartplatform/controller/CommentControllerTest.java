package org.inhuman.smartplatform.controller;

import org.inhuman.smartplatform.pojo.Comment;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.CommentService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getComments() {
        // Mock data
        String mockToken = "mockAccessToken";
        int mockPostingId = 123;
        int mockUserId = 456;

        // Mock User and Comments
        User mockUser = new User(mockUserId, "TestUser");
        Comment comment1 = new Comment(1, mockPostingId, mockUserId, "This is comment 1");
        Comment comment2 = new Comment(2, mockPostingId, mockUserId, "This is comment 2");
        List<Comment> mockComments = Arrays.asList(comment1, comment2);

        // Mock JWT and service behavior
        when(JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);
        when(commentService.getComments(mockUserId, mockPostingId)).thenReturn(mockComments);

        // Call the method
        Result result = commentController.getComments(mockToken, mockPostingId);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(mockComments, result.getData());

        // Verify interactions
        verify(commentService, times(1)).getComments(mockUserId, mockPostingId);
    }

    @Test
    void setComments() {
        // Mock data
        String mockToken = "mockAccessToken";
        int mockUserId = 456;
        Comment mockComment = new Comment(1, 123, mockUserId, "New comment");

        // Mock User
        User mockUser = new User(mockUserId, "TestUser");

        // Mock JWT behavior
        when(JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);

        // Call the method
        Result result = commentController.setComments(mockToken, mockComment);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isSuccess());

        // Verify interactions
        verify(commentService, times(1)).setComments(mockUserId, mockComment);
    }
}
