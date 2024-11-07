package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.inhuman.smartplatform.pojo.Homework;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HomeworkMapper {

    @Select("select * from homework where homework.lessonId = #{lessonId}")
    List<Homework> getHomeworks(int id, int lessonId);

    @Select("select homework.filesUrl from homework where id = #{homeworkId}")
    String getDocsUrlById(int id, int homeworkId);

    @Select("select homework.* from homework where id = #{homeworkId}")
    Homework getHomeworksById(int id, int homeworkId);

    @Insert("insert into homework (lessonid, title, content, publictime, overtime) values (#{lessonId},#{homework.title},#{homework.content},#{homework.publicTime},#{homework.overTime})")
    void publishHomework(int id, int lessonId, Homework homework);

    @Select("select last_insert_id()")
    int getLastInsertId();

    @Update("update homework set homework.filesUrl = #{filePath} where id = #{homeworkId}")
    void publishHomeworkFile(int id, int homeworkId, String filePath);

    @Insert("insert into homework_submit (studentId, homeworkId, fileUrl) values (#{id},#{homeworkId},#{filePath})")
    void submitHomework(int id, int homeworkId, String filePath);

    @Select("select homework.overTime from homework where id = #{homeworkId}")
    LocalDateTime getOverTime(int homeworkId);

    @Select("select homework_submit.fileUrl from homework_submit where homeworkId = #{homeworkId} and studentId  =#{id}")
    String getSubmittedUrl(int id, int homeworkId);

    @Update("update homework_submit set fileUrl = #{filePath} where studentId = #{id} and homeworkId = #{homeworkId}")
    void submitHomeworkAgain(int id, int homeworkId, String filePath);

    @Select("select teaching.user_id from teaching join user on teaching.user_id = user.id where user.position = 1 and teaching.lesson_id = #{lessonId}")
    List<Integer> getStudentsInLesson(int lessonId);

    @Insert("insert into homework_submit (studentId, homeworkId) values (#{student},#{homeworkId})")
    void homeworkToStudents(Integer student, int homeworkId);

    @Update("update homework_submit set status = '待批改' where studentId = #{id} and homeworkId = #{homeworkId}")
    void updateHomeworkStatus(int id, int homeworkId);
}
