package org.inhuman.smartplatform.controller;

import org.inhuman.smartplatform.pojo.HomePage;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.HomePageService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class HomePageControllerTest {

    @InjectMocks
    private HomePageController homePageController;

    @Mock
    private HomePageService homePageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getHomePage() throws Exception {
        String mockToken = "mockAccessToken";
        int mockUserId = 1;

        User mockUser = new User(mockUserId, "TestUser");
        HomePage mockHomePage = new HomePage(mockUserId, "Test Bio", "Test Location");

        try (var mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);
            when(homePageService.getHomePage(mockUserId)).thenReturn(mockHomePage);

            Result result = homePageController.getHomePage(mockToken);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(mockHomePage, result.getData());

            verify(homePageService, times(1)).getHomePage(mockUserId);
        }
    }

    @Test
    void getHomePageAvatarAsBase64() throws Exception {
        String mockToken = "mockAccessToken";
        int mockUserId = 1;

        User mockUser = new User(mockUserId, "TestUser");
        String mockAvatarBase64 = "mockAvatarBase64String";

        try (var mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);
            when(homePageService.getHomePageAvatarAsBase64(mockUserId)).thenReturn(mockAvatarBase64);

            Result result = homePageController.getHomePageAvatarAsBase64(mockToken);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(mockAvatarBase64, result.getData());

            verify(homePageService, times(1)).getHomePageAvatarAsBase64(mockUserId);
        }
    }

    @Test
    void updateHomePage() throws Exception {
        String mockToken = "mockAccessToken";
        int mockUserId = 1;
        HomePage mockHomePage = new HomePage(mockUserId, "Updated Bio", "Updated Location");

        User mockUser = new User(mockUserId, "TestUser");

        try (var mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);

            doNothing().when(homePageService).updateHomePage(mockUserId, mockHomePage);

            Result result = homePageController.updateHomePage(mockToken, mockHomePage);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertNull(result.getData());

            verify(homePageService, times(1)).updateHomePage(mockUserId, mockHomePage);
        }
    }

    @Test
    void updateHomePageAvatar() throws Exception {
        String mockToken = "mockAccessToken";
        int mockUserId = 1;
        MockMultipartFile mockFile = new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

        User mockUser = new User(mockUserId, "TestUser");

        try (var mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);

            doNothing().when(homePageService).updateHomePageAvatar(mockUserId, mockFile);

            Result result = homePageController.updateHomePageAvatar(mockToken, mockFile);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertNull(result.getData());

            verify(homePageService, times(1)).updateHomePageAvatar(mockUserId, mockFile);
        }
    }

    @Test
    void getHomePageOther() throws Exception {
        String mockToken = "mockAccessToken";
        int otherUserId = 2;

        User mockUser = new User(1, "TestUser");
        HomePage mockHomePage = new HomePage(otherUserId, "Other Bio", "Other Location");

        try (var mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUserFromClaims(any())).thenReturn(mockUser);
            when(homePageService.getHomePageOther(otherUserId)).thenReturn(mockHomePage);

            Result result = homePageController.getHomePageOther(mockToken, otherUserId);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(mockHomePage, result.getData());

            verify(homePageService, times(1)).getHomePageOther(otherUserId);
        }
    }
}
