package org.inhuman.smartplatform.service.impl;

import org.apache.tomcat.util.codec.binary.Base64;
import org.inhuman.smartplatform.mapper.HomePageMapper;
import org.inhuman.smartplatform.pojo.HomePage;
import org.inhuman.smartplatform.service.HomePageService;
import org.inhuman.smartplatform.utils.TransmitFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class HomePageServiceImpl implements HomePageService {

    @Autowired
    HomePageMapper homePageMapper;

    @Override
    public HomePage getHomePage(int id) throws IOException {

        return homePageMapper.getHomePage(id);
    }

    @Override
    public void updateHomePage(int id, HomePage homePage) throws IOException {
        homePageMapper.updateHomePage(id,homePage);
    }

    @Override
    public void updateHomePageAvatar(int id, MultipartFile file) throws IOException {
        String url = TransmitFileUtils.storeFile(id,file);
        homePageMapper.updateHomePageAvatar(id,url);
    }

    @Override
    public String getHomePageAvatarAsBase64(int id) {
        try {
            Path filePath = Paths.get(homePageMapper.getHomePageAvatarAsBase64(id)).toAbsolutePath().normalize();
            byte[] imageBytes = Files.readAllBytes(filePath);
            return Base64.encodeBase64String(imageBytes);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public HomePage getHomePageOther(int id) {
        HomePage homePage =  homePageMapper.getHomePage(id);
        if(!homePage.getPrivacy()) {
            homePage.setBio("未公开");
            homePage.setGender("未公开");
            homePage.setFavorites("未公开");
        }
        return homePage;
    }

}
