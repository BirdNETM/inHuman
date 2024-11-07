package org.inhuman.smartplatform.service;

import org.inhuman.smartplatform.pojo.HomePage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface HomePageService {
    HomePage getHomePage(int id) throws IOException;

    void updateHomePage(int id, HomePage homePage) throws IOException;

    void updateHomePageAvatar(int id, MultipartFile file) throws IOException;

    String getHomePageAvatarAsBase64(int id);

    HomePage getHomePageOther(int id);
}
