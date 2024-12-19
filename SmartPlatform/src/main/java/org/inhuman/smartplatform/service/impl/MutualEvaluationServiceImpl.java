package org.inhuman.smartplatform.service.impl;

import org.inhuman.smartplatform.mapper.MutualEvaluationMapper;
import org.inhuman.smartplatform.pojo.Mutual;
import org.inhuman.smartplatform.service.MutualEvaluationService;
import org.inhuman.smartplatform.utils.DocsDownloadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MutualEvaluationServiceImpl implements MutualEvaluationService {

    @Autowired
    private MutualEvaluationMapper mutualEvaluationMapper;
    @Autowired
    private View error;

    @Override
    public void receiveTasks(int homeWorkId, int reviewerId, int revieweeId, int score) {
        mutualEvaluationMapper.receiveReview(homeWorkId, reviewerId, revieweeId, score);
    }

    @Override
    public void assignTasks(int homeworkId, LocalDateTime deadLine) {
        mutualEvaluationMapper.assignTasks(homeworkId, deadLine);
        mutualEvaluationMapper.generatePeerReviewTasks(mutualEvaluationMapper.getLessonIdById(homeworkId), homeworkId);
    }

    @Override
    public List<Mutual> getSpecificMutual(int userId, int homeWorkId) {
        return mutualEvaluationMapper.getSpecificMutualTasks(userId, homeWorkId);
    }

    @Override
    public ResponseEntity<Resource> getHomeWork(int studentId, int homeWorkId) {
        try {
            String url = mutualEvaluationMapper.getDocsUrlById(studentId, homeWorkId);
            return DocsDownloadUtils.downloadDocsByUrl(url);
        }catch (Exception e){

        }
        return null;
    }
}
