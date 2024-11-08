package org.inhuman.smartplatform.service;


import org.inhuman.smartplatform.pojo.Homework;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HomeworkService {

    List<Homework> getHomeworks(int id, int lessonId);

    ResponseEntity<Resource> downloadHomeworkFiles(int id, int homeworkId);

    Homework getHomeworksById(int id, int homeworkId);
}
