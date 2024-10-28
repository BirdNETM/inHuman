package org.inhuman.smartplatform.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.mapper.HomeworkMapper;
import org.inhuman.smartplatform.pojo.Homework;
import org.inhuman.smartplatform.service.HomeworkService;
import org.inhuman.smartplatform.utils.DocsDownloadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    HomeworkMapper homeworkMapper;


    @Override
    public List<Homework> getHomeworks(int id, int lessonId) {
        return homeworkMapper.getHomeworks(id,lessonId);
    }

    @Override
    public ResponseEntity<Resource> downloadHomeworkFiles(int id, int homeworkId) {
        try {
            String url = homeworkMapper.getDocsUrlById(id, homeworkId);
            return DocsDownloadUtils.downloadDocsByUrl(url);
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Homework getHomeworksById(int id, int homeworkId) {
        return homeworkMapper.getHomeworksById(id,homeworkId);
    }
}
