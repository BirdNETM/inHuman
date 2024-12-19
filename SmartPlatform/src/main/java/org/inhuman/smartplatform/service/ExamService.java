package org.inhuman.smartplatform.service;


import org.inhuman.smartplatform.pojo.Classroom;
import org.inhuman.smartplatform.pojo.Exam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExamService {
    List<Exam> getExams(int id, int lessonId);

    List<Exam> addExam(int id, Exam exam) throws Exception;

    List<Exam> updateExam(int id, Exam exam) throws Exception;

    void deleteExam(int id, int examId) throws Exception;

    List<Classroom> getClassrooms(int id);
}
