package org.inhuman.smartplatform.service.impl;

import org.inhuman.smartplatform.mapper.TutorMapper;
import org.inhuman.smartplatform.pojo.Tutor;
import org.inhuman.smartplatform.service.TutorService;
import org.inhuman.smartplatform.utils.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorServiceImpl implements TutorService {
    @Autowired
    TutorMapper tutorMapper;

    @Override
    public List<Tutor> getTutors(int id, int lessonId) {
        return tutorMapper.getTutors(id,lessonId);
    }

    @Override
    public void addTutor(int id, String studentCode, int lessonId) {
        int tutor_id = DatabaseUtil.getIdByCode(studentCode);
        String tutor_name  = DatabaseUtil.getNameByCode(studentCode);
        tutorMapper.addTutor(id,tutor_id,tutor_name, lessonId);
    }

    @Override
    public void deleteTutor(int id, int tutorId, int lessonId) {
        tutorMapper.deleteTutor(id,tutorId, lessonId);
    }

    @Override
    public void updateTutor(int id, Tutor tutor) {
        tutorMapper.updateTutor(id,tutor);
    }
}
