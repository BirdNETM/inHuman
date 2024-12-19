package org.inhuman.smartplatform.mapper;

import lombok.Data;
import org.apache.ibatis.annotations.*;
import org.inhuman.smartplatform.pojo.Tutor;

import java.util.List;

@Mapper
public interface TutorMapper {

    @Select("select tutor_permissions.*,homepage.username,lesson.name as lessonName from tutor_permissions join homepage on tutor_permissions.tutor_id = homepage.id join lesson on tutor_permissions.lesson_id = lesson.id where lesson_id = #{lessonId}")
    List<Tutor> getTutors(int id, int lessonId);

    @Insert("insert into tutor_permissions (tutor_id, tutor_name, lesson_id) values (#{tutor_id}, #{tutor_name}, #{lessonId})")
    void addTutor(int id, int tutor_id, String tutor_name,int lessonId);

    @Delete("delete from tutor_permissions where tutor_id = #{tutorId} and lesson_id = #{lessonId}")
    void deleteTutor(int id, int tutorId, int lessonId);

    @Update("update tutor_permissions set homework_control = #{tutor.homework_control}, points_control = #{tutor.points_control}, docs_control = #{tutor.docs_control}, exam_control = #{tutor.exam_control} where tutor_id = #{tutor.tutor_id} and lesson_id = #{tutor.lesson_id}")
    void updateTutor(int id, Tutor tutor);
}
