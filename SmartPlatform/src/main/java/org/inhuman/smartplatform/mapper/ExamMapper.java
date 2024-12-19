package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.inhuman.smartplatform.pojo.Classroom;
import org.inhuman.smartplatform.pojo.Exam;

import java.util.Date;
import java.util.List;

@Mapper
public interface ExamMapper {

    @Select("select exam.*,lesson.name as lessonName from exam join lesson on exam.lessonId = lesson.id where exam.lessonId = #{lessonId}")
    List<Exam> getExams(int id, int lessonId);


    @Insert("insert into exam (lessonId, examName, examStartTime, examEndTime, examDescription) values (#{exam.lessonId},#{exam.examName},#{exam.examStartTime},#{exam.examEndTime},#{exam.examDescription})")
    @Options(useGeneratedKeys = true, keyProperty = "exam.id", keyColumn = "id")
    void addExam(int id, @Param("exam") Exam exam);

    @Select("SELECT classroom.* " +
            "FROM classroom " +
            "JOIN classroom_booking ON classroom.classroom_id = classroom_booking.booking_classroom_id " +
            "WHERE classroom_booking.exam_id = #{id}")
    List<Classroom> getClassroomByExamId(int id);

    @Insert("INSERT INTO classroom_booking (exam_id, booking_classroom_id, booking_begin_time, booking_end_time, booking_user_id, booking_lesson_id, booking_description) " +
            "VALUES (#{examId}, #{classroomId}, #{examStartTime}, #{examEndTime}, #{userId}, #{lessonId}, #{bookingDescription})")
    void addExamBooking(int userId, int examId, int classroomId, Date examStartTime, Date examEndTime, int lessonId, String bookingDescription);

    @Select("""
    SELECT exam.*
    FROM exam
    JOIN classroom_booking ON exam.id = classroom_booking.exam_id
    WHERE (
        exam.examStartTime < #{exam.examEndTime} 
        AND exam.examEndTime > #{exam.examStartTime}
    ) 
    AND classroom_booking.booking_classroom_id = #{classroom_id}
    AND exam.id != #{id}
""")
    List<Exam> getConflictExams(int id, Exam exam, int classroom_id);


    @Delete("delete from exam where id = #{examId}")
    void deleteExam(int id, int examId);

    @Delete("delete from classroom_booking where exam_id = #{examId}")
    void deleteExamBooking(int id, int examId);

    @Select("SELECT classroom.* from classroom")
    List<Classroom> getClassrooms(int id);
}
