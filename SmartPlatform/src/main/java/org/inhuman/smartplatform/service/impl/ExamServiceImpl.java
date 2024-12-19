package org.inhuman.smartplatform.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.mapper.ExamMapper;
import org.inhuman.smartplatform.pojo.Classroom;
import org.inhuman.smartplatform.pojo.Exam;
import org.inhuman.smartplatform.service.ExamService;
import org.inhuman.smartplatform.utils.TutorPrivilegeCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamMapper examMapper;

    @Override
    public List<Exam> getExams(int id, int lessonId) {
        List<Exam> exams = examMapper.getExams(id, lessonId);
        for (Exam exam : exams) {
            List<Classroom> classrooms = examMapper.getClassroomByExamId(exam.getId());
            exam.setClassrooms(classrooms);
        }
        return exams;
    }

    @Override
    public List<Exam> addExam(int id, Exam exam) throws Exception {

        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,exam.getLessonId(),4);
        log.info("checked privilege");
        List<Exam> exams = getOccupiedRooms(exam);
        log.info("got exams");
        if (!exams.isEmpty()) {
            return exams;
        }
        examMapper.addExam(id,exam);
        log.info(String.valueOf(exam));
        String booking_description = exam.getExamName();

        for (Classroom classroom: exam.getClassrooms()) {
            examMapper.addExamBooking(id,exam.getId(),classroom.getClassroom_id(),exam.getExamStartTime(),exam.getExamEndTime(),exam.getLessonId(),booking_description);
        }
        return null;
    }

    @Override
    public List<Exam> updateExam(int id, Exam exam) throws Exception {
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,exam.getLessonId(),4);
        deleteExam(id, exam.getId());
        return addExam(id, exam);
    }

    @Override
    public void deleteExam(int id, int examId) throws Exception {
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,examId,4);
        examMapper.deleteExam(id,examId);
        examMapper.deleteExamBooking(id,examId);
    }

    @Override
    public List<Classroom> getClassrooms(int id) {
        return examMapper.getClassrooms(id);
    }

    private List<Exam> getOccupiedRooms(Exam exam) {
        List<Exam> exams = new ArrayList<>();
        for (int i = 0; i < exam.getClassrooms().size(); i++) {
            int classroom_id = exam.getClassrooms().get(i).getClassroom_id();
            List<Exam> exams_ = examMapper.getConflictExams(0, exam, classroom_id);
            if (exams_ != null) {
                for (Exam exam_ : exams_) {
                    if (exam_ != null) {
                        log.info(exam_.toString());
                        exam_.setClassrooms(Collections.singletonList(exam.getClassrooms().get(i)));
                        exams.add(exam_);
                    }
                }
            }

        }
        return exams;
    }
}
