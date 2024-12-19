package org.inhuman.smartplatform.service;

import org.inhuman.smartplatform.pojo.Tutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TutorService {
    List<Tutor> getTutors(int id, int lessonId);

    void addTutor(int id, String studentCode, int lessonId);

    void deleteTutor(int id, int tutorId, int lessonId);

    void updateTutor(int id, Tutor tutor);
}
