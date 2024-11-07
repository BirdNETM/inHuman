package org.inhuman.smartplatform.service.impl;

import org.inhuman.smartplatform.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public Resource getImage(int id) throws MalformedURLException {
        Path filePath = Paths.get("D:/InHumanFile/pictures/" + id + ".jpg").toAbsolutePath().normalize();
        if (!filePath.toFile().exists()) {
            filePath = Paths.get("D:/InHumanFile/pictures/" + "default.jpg").toAbsolutePath().normalize();
        }
        return new UrlResource(filePath.toUri());
    }
}
