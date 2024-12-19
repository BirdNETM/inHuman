package org.inhuman.smartplatform.controller;

import org.inhuman.smartplatform.pojo.Exam;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.ExamService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class ExamControllerTest {

    @InjectMocks
    private ExamController examController;

    @Mock
    private ExamService examService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getExams() throws Exception {
        String mockToken = "mockAccessToken";
        int mockLessonId = 101;
        int mockUserId = 1;

        User mockUser = new User(mockUserId, "TestUser");
        Exam exam1 = new Exam(1, "Math Exam", "2024-12-01 10:00:00", 120);
        Exam exam2 = new Exam(2, "Physics Exam", "2024-12-02 14:00:00", 90);
        List<Exam> mockExams = Arrays.asList(exam1, exam2);

        try (var mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);
            when(examService.getExams(mockUserId, mockLessonId)).thenReturn(mockExams);

            Result result = examController.getExams(mockToken, mockLessonId);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(mockExams, result.getData());

            verify(examService, times(1)).getExams(mockUserId, mockLessonId);
        }
    }

    @Test
    void addExam() throws Exception {
        String mockToken = "mockAccessToken";
        int mockUserId = 1;
        Exam mockExam = new Exam(1, "Chemistry Exam", "2024-12-05 08:00:00", 180);

        User mockUser = new User(mockUserId, "TestUser");

        try (var mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);

            when(examService.addExam(mockUserId, mockExam)).thenReturn(Collections.emptyList());

            Result result = examController.addExam(mockToken, mockExam);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertNull(result.getData());

            verify(examService, times(1)).addExam(mockUserId, mockExam);
        }
    }

    @Test
    void deleteExam() throws Exception {
        String mockToken = "mockAccessToken";
        int mockUserId = 1;
        int mockExamId = 10;

        User mockUser = new User(mockUserId, "TestUser");

        try (var mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);

            doNothing().when(examService).deleteExam(mockUserId, mockExamId);

            Result result = examController.deleteExam(mockToken, mockExamId);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertNull(result.getData());

            verify(examService, times(1)).deleteExam(mockUserId, mockExamId);
        }
    }

    @Test
    void updateExam() throws Exception {
        String mockToken = "mockAccessToken";
        int mockUserId = 1;
        Exam mockExam = new Exam(1, "Updated Exam", "2024-12-06 10:00:00", 60);

        User mockUser = new User(mockUserId, "TestUser");
        List<Exam> mockConflicts = Collections.emptyList();

        try (var mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);
            when(examService.updateExam(mockUserId, mockExam)).thenReturn(mockConflicts);

            Result result = examController.updateExam(mockToken, mockExam);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertNull(result.getData());

            verify(examService, times(1)).updateExam(mockUserId, mockExam);
        }
    }
}
