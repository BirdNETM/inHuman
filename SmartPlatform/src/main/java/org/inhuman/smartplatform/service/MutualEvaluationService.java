package org.inhuman.smartplatform.service;

import org.inhuman.smartplatform.pojo.Mutual;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface MutualEvaluationService {
    void receiveTasks(int homeWorkId, int reviewerId, int revieweeId, int score);

    void assignTasks(int homeworkId, LocalDateTime deadLine);

    List<Mutual> getSpecificMutual(int userId, int homeWorkId);

    ResponseEntity<Resource> getHomeWork(int studentId, int homeWorkId);
}
