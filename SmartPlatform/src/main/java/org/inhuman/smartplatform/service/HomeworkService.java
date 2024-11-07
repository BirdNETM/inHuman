package org.inhuman.smartplatform.service;


import org.inhuman.smartplatform.pojo.Homework;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface HomeworkService {

    List<Homework> getHomeworks(int id, int lessonId);

    ResponseEntity<Resource> downloadHomeworkFiles(int id, int homeworkId);

    Homework getHomeworksById(int id, int homeworkId);

    int publishHomework(int id, int lessonId, Homework homework);

    void publishHomeworkFile(int id, int homeworkId, MultipartFile file);

    void submitHomework(int id, int homeworkId, MultipartFile file) throws Exception;
}
