package org.inhuman.smartplatform.service;


import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

@Service
public interface ImageService {
    Resource getImage(int id) throws MalformedURLException;
}
